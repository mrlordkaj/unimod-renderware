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

import com.openitvn.format.img.RwArchiveEntry;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.BackgroundTask;
import com.openitvn.unicore.data.EntryStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
class WorldScriptModel extends AbstractTableModel {
    
    private static final String[] COLUMNS = {"", "Name", "Type"};
    public static final int COL_ACTIVE = 0;
    public static final int COL_NAME = 1;
    public static final int COL_TYPE = 2;
    
    private ArrayList<WorldScriptEntry> scripts = new ArrayList<>();
    private WorldPanel app;
    
    void bind(WorldPanel app, ResourceModel res) {
        this.app = app;
        this.scripts = res.scripts;
        // active default dependencies
        app.prepareDispatcher();
        for (String dp : GameConfig.getDependencies()) {
            for (WorldScriptEntry e : scripts) {
                if (dp.equalsIgnoreCase(e.getName())) {
                    executeScript(e, true);
                    break;
                }
            }
        }
        app.executeDispatcher();
        fireTableDataChanged();
    }
    
    WorldScriptEntry findScript(String name) {
        for (WorldScriptEntry e : scripts) {
            if (e.getName().equalsIgnoreCase(name))
                return e;
        }
        return null;
    }
    
    WorldScriptEntry getScript(int id) {
        return scripts.get(id);
    }
    
    //<editor-fold defaultstate="collapsed" desc="Active / Deactive">
    private ArrayList<String> getActivatedGroups(WorldScriptType type) {
        ArrayList<String> rs = new ArrayList<>();
        for (WorldScriptEntry e : scripts) {
            if (e.type == type && e.isActive)
                rs.add(e.getName().toLowerCase());
        }
        return rs;
    }
    
    private void executeStreamScript(WorldScriptEntry group, boolean active) {
        if (group.type == WorldScriptType.IPL) {
            // for SA only, IPLs may have extra streamed data inside archive
            String name = group.getName();
            String prefix = name.toLowerCase().replace(".ipl", "_stream");/* name.substring(0, name.length() - 4).concat("_stream");*/
            RwArchiveEntry e;
            int i = 0;
            while ((e = ResourceModel.getInstance().findEntry(prefix + i + ".ipl")) != null) {
                try (EntryStream ds = new EntryStream(e)) {
                    app.executeINSTGroup(e.getName(), ds, active);
                } catch (IOException ex) {
                    Logger.printNotice("IPL failed: %1$s", e.getName());
                    break;
                }
                Logger.printNotice("IPL executed: %1$s", e.getName());
                i++;
            }
        }
    }
    
    private void executeScript(WorldScriptEntry script, boolean active) {
        if (script != null && script.isActive != active) {
            String name = script.getName();
            String state = active ? "on" : "off";
            try (InputStream is = new FileInputStream(script.file);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = ScriptHelper.readLine(br)) != null) {
                    switch (line) {
                        case "objs":
                        case "tobj":
                            app.executeOBJSGroup(name, br, active);
                            break;
                            
                        case "inst":
                            app.executeINSTGroup(name, br, active);
                            break;
                            
                        case "path":
                            app.executePATHGroup(name, br, active);
                            break;
                    }
                }
            } catch (IOException ex) {
                Logger.printError("%1$s failed: %2$s [%3$s]", script.type, name, state);
                return;
            }
            executeStreamScript(script, active);
            script.isActive = active;
            System.gc();
            Logger.printNotice("%1$s executed: %2$s [%3$s]", script.type, name, state);
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="JTable Model">
    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    @Override
    public int getRowCount() {
        return scripts.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }
    
    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COL_ACTIVE:
                return Boolean.class;
                
            case COL_NAME:
            case COL_TYPE:
                return String.class;
        }
        return Object.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case COL_ACTIVE:
                return scripts.get(row).isActive;
                
            case COL_NAME:
                return scripts.get(row).getName();
                
            case COL_TYPE:
                return scripts.get(row).type.toString();
        }
        
        return null;
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return (col == COL_ACTIVE) && scripts.get(row).type.equals(WorldScriptType.IPL);
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        boolean active = (boolean) value;
        WorldScriptEntry e = scripts.get(row);
        if (e.isActive != active) {
            String name = e.getName();
            if (active) {
                // activation need a async task because it take long time
                // in fact, deactivation is very fast, so it not need to
                final ArrayList<String> deps = GameConfig.getDependencies(name);
                BackgroundTask.run(new BackgroundTask(name, deps.size()+1) {
                    @Override
                    protected Void doInBackground() {
                        try {
                            int actived = 0;
                            app.prepareDispatcher();
                            // active dependecies
                            for (String dp : deps) {
                                executeScript(findScript(dp), true);
                                setProcessedCount(++actived);
                            }
                            // active target
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
                // deactive target
                executeScript(findScript(name), false);
                // deactive no longer required dependencies
                ArrayList<String> keeps = GameConfig.getDependencies(getActivatedGroups(WorldScriptType.IPL));
                keeps.addAll(GameConfig.getDependencies());
                for (String ide : getActivatedGroups(WorldScriptType.IDE)) {
                    if (!keeps.contains(ide))
                        executeScript(findScript(ide), false);
                }
            }
        }
    }
    //</editor-fold>
}
