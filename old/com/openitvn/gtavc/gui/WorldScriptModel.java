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

package com.openitvn.gtavc.gui;

import com.openitvn.gtavc.gui.g3d.ViewportApp;
import com.openitvn.unicore.archive.IArchiveEntry;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.plugin.gta.GameConfig;
import com.openitvn.unicore.plugin.gta.ResourceModel;
import com.openitvn.unicore.plugin.gta.ScriptHelper;
import com.openitvn.unicore.plugin.gta.WorldScript;
import com.openitvn.unicore.plugin.gta.item.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
class WorldScriptModel extends AbstractTableModel
{
    public static final String[] COLUMNS = {"", "Name", "Type"};
    public static final int COL_ACTIVE = 0;
    public static final int COL_NAME = 1;
    public static final int COL_TYPE = 2;
    
    final ResourceModel resource = ResourceModel.getInstance();
    final HashMap<WorldScript, ArrayList<ItemNULL>> scriptItems = new HashMap<>(); // script, items
    
    public void reload() throws IOException {
        // Reload data
        scriptItems.clear();
        for (WorldScript script : resource.scripts) {
            String path = script.file.getAbsolutePath();
            // Normal file
            try (FileReader fr = new FileReader(path)) {
                BufferedReader br = new BufferedReader(fr);
                String line;
                mainLoop:
                while ((line = ScriptHelper.readLine(br)) != null) {
                    switch (line) {
                        case "objs":
                        case "tobj":
                        case "inst":
                            if (!readGroup(br, line, script)) {
                                break mainLoop;
                            }
                            break;
                    }
                }
            } catch (IOException ex) {}
            // Stream file (SA only)
            if (GameConfig.ALIAS_SA.equals(GameConfig.getAlias())
                    && script.getName().toLowerCase().endsWith(".ipl")) {
                // Read extra binary stream inside img file
                ArrayList<ItemNULL> items = getScriptItems(script);
                String name = script.getName();
                String prefix = name.substring(0, name.length() - 4).concat("_stream");
                int id = 0;
                IArchiveEntry ae;
                while ((ae = resource.findEntry(prefix+id+".ipl")) != null) {
                    try {
                        EntryStream es = new EntryStream(ae);
                        es.position(4); // Skip "bnry"
                        int instCount = es.getInt();
                        es.position(0x4C); // Offset of item instances, 0x4C by default
                        for (int i = 0; i < instCount; i++) {
                            ItemINST inst = new ItemINST(es);
                            items.add(inst);
                        }
                    }
                    catch (IOException ex) {}
                    id++;
                }
            }
        }
        fireTableDataChanged();
    }
    
    private boolean readGroup(BufferedReader br, String type, WorldScript script) {
        ArrayList<ItemNULL> items = getScriptItems(script);
        String line;
        while ((line = ScriptHelper.readLine(br)) != null) {
            if (line.length() == 0) continue; // Skip blank lines
            if (line.charAt(0) == '#') continue; // Skip comment lines
            if (line.equalsIgnoreCase("end")) {
                return true;
            }
            String[] args = ScriptHelper.parseLineByComma(line);
            switch (type) {
                case "objs":
                    items.add(new ItemOBJS(args));
                    break;
                case "tobj":
                    items.add(new ItemTOBJ(args));
                    break;
                case "inst":
                    items.add(new ItemINST(args));
                    break;
            }
        }
        return false;
    }
    
    public ArrayList<ItemNULL> getScriptItems(WorldScript script) {
        // Group found
        if (scriptItems.containsKey(script)) {
            return scriptItems.get(script);
        }
        // Not found
        ArrayList<ItemNULL> rs = new ArrayList<>();
        scriptItems.put(script, rs);
        return rs;
    }
        
    public WorldScript getScriptByName(String name) {
        for (WorldScript script : resource.scripts) {
            if (script.getName().equalsIgnoreCase(name)) {
                return script;
            }
        }
        return null;
    }
        
    //<editor-fold defaultstate="collapsed" desc="Activate / Deactivate">
    
    private void activateIPL(String ipl) {
        // Activate dependecies
        for (String dp : GameConfig.getDependencies(ipl)) {
            activateGroup(getScriptByName(dp));
        }
        // Activate target
        activateGroup(getScriptByName(ipl));
    }
    
    private void activateGroup(WorldScript script) {
        if (script != null && !script.bActive) {
            ViewportApp app = ViewportApp.getInstance();
            for (ItemNULL e : getScriptItems(script)) {
                switch (e.getType()) {
                    case "OBJS":
                    case "TOBJ":
                        app.worldView.addOBJS((ItemOBJS)e);
                        break;
                    case "INST":
                        app.worldView.addINST((ItemINST)e);
                        break;
                }
            }
            script.bActive = true;
        }
    }
    
    private ArrayList<String> getActivatedGroups(WorldScript.Type type) {
        ArrayList<String> rs = new ArrayList<>();
        for (WorldScript e : resource.scripts) {
            if (e.type == type && e.bActive) {
                rs.add(e.getName().toLowerCase());
            }
        }
        return rs;
    }
    
    private void deactivateIPL(String ipl) {
        // Deactivate target
        deactivateScript(getScriptByName(ipl));
        // Deactivate dependencies
        ArrayList<String> ipls = getActivatedGroups(WorldScript.Type.IPL);
        ArrayList<String> ides = getActivatedGroups(WorldScript.Type.IDE);
        ArrayList<String> kept = GameConfig.getDependencies(ipls);
        for (String ide : ides) {
            if (!kept.contains(ide)) {
                deactivateScript(getScriptByName(ide));
            }
        }
        System.gc();
    }
    
    private void deactivateScript(WorldScript script) {
        if (script != null && script.bActive) {
            ViewportApp app = ViewportApp.getInstance();
            for (ItemNULL e : getScriptItems(script)) {
                switch (e.getType()) {
                    case "OBJS":
                    case "TOBJ":
                        app.worldView.removeOBJS((ItemOBJS)e);
                        break;
                    case "INST":
                        app.worldView.removeINST((ItemINST)e);
                        break;
                }
            }
            script.bActive = false;
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
            case COL_TYPE:
                return WorldScript.Type.class;
        }
        return String.class;
    }
    
    @Override
    public int getRowCount() {
        return resource.scripts.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case COL_ACTIVE:
                return resource.scripts.get(row).bActive;
            case COL_NAME:
                return resource.scripts.get(row).getName();
            case COL_TYPE:
                return resource.scripts.get(row).type;
        }
        return null;
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return col == COL_ACTIVE &&
                resource.scripts.get(row).type == WorldScript.Type.IPL;
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == COL_ACTIVE) {
            boolean active = (boolean)value;
            WorldScript script = resource.scripts.get(row);
            if (script.bActive != active) {
                if (active) activateIPL(script.getName());
                else deactivateIPL(script.getName());
            }
        }
    }
    
    //</editor-fold>
}
