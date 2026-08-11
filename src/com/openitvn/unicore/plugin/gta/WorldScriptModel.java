/*
 * Copyright (C) 2016 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.openitvn.unicore.plugin.gta;

import com.openitvn.maintain.Logger;
import com.openitvn.unicore.BackgroundTask;
import com.openitvn.unicore.data.EntryStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
class WorldScriptModel extends AbstractTableModel
{
    public static final String[] COLUMNS = { "", "Name", "Type" };
    public static final int COL_ACTIVE = 0;
    public static final int COL_NAME = 1;
    public static final int COL_TYPE = 2;
    
    private ArrayList<WorldScript> scripts = new ArrayList<>();
    private WorldPanel app;
    
    void bind(WorldPanel app, ResourceModel res) {
        this.app = app;
        this.scripts = res.scripts;
        fireTableDataChanged();
    }
    
    WorldScript findScript(String name) {
        for (WorldScript e : scripts) {
            if (e.name.equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }
    
    WorldScript getScript(int id) {
        return scripts.get(id);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Activate / Deactivate">
    
    private ArrayList<String> getActivatedIPLs() {
        ArrayList<String> rs = new ArrayList<>();
        for (WorldScript e : scripts) {
            if (e.type == WorldScript.Type.IPL && e.bActive) {
                rs.add(e.name.toLowerCase());
            }
        }
        return rs;
    }
    
    private ArrayList<String> getActivatedIDEs(ArrayList<String> ignores) {
        ArrayList<String> rs = new ArrayList<>();
        for (WorldScript e : scripts) {
            if (e.type == WorldScript.Type.IDE && e.bActive) {
                String name = e.name.toLowerCase();
                if (!ignores.contains(name)) {
                    rs.add(name);
                }
            }
        }
        return rs;
    }
    
    private void executeStreamScript(WorldScript script, boolean bActive) {
        if (script.type == WorldScript.Type.IPL) {
            // For SA only, IPLs may have extra streamed data inside archive
            String prefix = script.name.toLowerCase().replace(".ipl", "_stream");/* name.substring(0, name.length() - 4).concat("_stream");*/
            ResourceModel res = ResourceModel.getInstance();
            int i = 0;
            while (true) {
                try (EntryStream ds = res.getEntryStream(prefix + i, "ipl")) {
                    String name = ds.getLastPath();
                    String status = bActive ? "on" : "off";
                    Logger.printNotice("IPL triggered: %s [%s]", name, status);
                    app.executeINSTGroup(name, ds, bActive);
                    i++;
                } catch (IOException ex) {
                    break;
                }
            }
        }
    }
    
    private void executeScript(WorldScript script, boolean bActive) {
        if (script != null && script.bActive != bActive) {
            String status = bActive ? "on" : "off";
            Logger.printNotice("%s triggered: %s [%s]", script.type, script.name, status);
            // Normal file
            try (FileReader fr = new FileReader(script.file);
                BufferedReader br = new BufferedReader(fr)) {
                String line;
                while ((line = ScriptHelper.readLine(br)) != null) {
                    switch (line) {
                        case "objs":
                        case "tobj":
                            app.executeOBJSGroup(script.name, br, bActive);
                            break;
                            
                        case "inst":
                            app.executeINSTGroup(script.name, br, bActive);
                            break;
                            
                        case "path":
                            app.executePATHGroup(script.name, br, bActive);
                            break;
                    }
                }
            } catch (IOException ex) {
                Logger.printError("%1$s failed: %2$s [%3$s]", script.type, script.name, status);
                return;
            }
            // Stream file (SA only)
            executeStreamScript(script, bActive);
            script.bActive = bActive;
            System.gc();
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="JTable Model">
    
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }
    
    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COL_ACTIVE:
                return Boolean.class;
            case COL_NAME:
                return String.class;
            case COL_TYPE:
                return WorldScript.Type.class;
        }
        return Object.class;
    }
    
    @Override
    public int getRowCount() {
        return scripts.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case COL_ACTIVE:
                return scripts.get(row).bActive;
            case COL_NAME:
                return scripts.get(row).name;
            case COL_TYPE:
                return scripts.get(row).type;
        }
        return null;
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return (col == COL_ACTIVE) && scripts.get(row).type == WorldScript.Type.IPL;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        boolean active = (boolean)value;
        WorldScript e = scripts.get(row);
        if (e.bActive != active) {
            if (active) {
                // Activation need a async task because it take long time
                // in fact, deactivation is very fast, so it not need to
                final ArrayList<String> deps = GameConfig.getDependencies(e.name);
                BackgroundTask.run(new BackgroundTask(e.name, deps.size()+1) {
                    @Override
                    protected Void doInBackground() {
                        try {
                            int actived = 0;
                            app.prepareDispatcher();
                            // Activate dependecies
                            for (String dep : deps) {
                                executeScript(findScript(dep), true);
                                setProcessedCount(++actived);
                            }
                            // Activate target
                            executeScript(findScript(name), true);
                            setProcessedCount(++actived);
                        } catch (Exception ex) {
                            err = ex;
                        }
                        return null;
                    }
                    @Override
                    protected void done() {
                        app.executeDispatcher();
                    }
                });
            } else {
                // Deactivate target
                executeScript(findScript(e.name), false);
                // Deactivate dependencies
                ArrayList<String> IPLs = getActivatedIPLs();
                ArrayList<String> kept = GameConfig.getDependencies(IPLs);
                ArrayList<String> IDEs = getActivatedIDEs(kept);
                for (String IDE : IDEs) {
                    executeScript(findScript(IDE), false);
                }
            }
        }
    }
    
    //</editor-fold>
}
