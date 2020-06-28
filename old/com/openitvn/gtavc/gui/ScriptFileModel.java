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

import com.openitvn.unicore.plugin.gta.item.ItemNULL;
import com.openitvn.gtavc.gui.g3d.GWorldMap;
import com.openitvn.gtavc.gui.g3d.ViewportApp;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.archive.IArchiveEntry;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.plugin.gta.GameConfig;
import com.openitvn.unicore.plugin.gta.ResourceModel;
import com.openitvn.unicore.plugin.gta.WorldScriptEntry;
import com.openitvn.unicore.plugin.gta.WorldScriptType;
import com.openitvn.unicore.plugin.gta.item.ItemCARS;
import com.openitvn.unicore.plugin.gta.item.ItemINST;
import com.openitvn.unicore.plugin.gta.item.ItemOBJS;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
public class ScriptFileModel extends AbstractTableModel {
    public static final String[] COLUMNS = {"", "ID", "Name", "Type"};
    public static final int COL_ACTIVE = 0;
    public static final int COL_INDEX = 1;
    public static final int COL_NAME = 2;
    public static final int COL_TYPE = 3;
    
    private final ScriptItemModel scriptItemModel = new ScriptItemModel();
    private final ArrayList<WorldScriptEntry> entries = new ArrayList<>();
    
    //<editor-fold defaultstate="collapsed" desc="JTable Model">
    
    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    @Override
    public int getRowCount() {
        return entries.size();
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
                
            case COL_INDEX:
                return Integer.class;

            case COL_NAME:
            case COL_TYPE:
                return String.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case COL_ACTIVE:
                return entries.get(row).isActive();
                
            case COL_INDEX:
                return entries.get(row).getIndex();
                
            case COL_NAME:
                return entries.get(row).getName();
                
            case COL_TYPE:
                return entries.get(row).getType();
        }
        return null;
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return (col == COL_ACTIVE) && entries.get(row).getType().equals(WorldScriptType.IPL);
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == COL_ACTIVE) {
            boolean active = (boolean)value;
            WorldScriptEntry entry = entries.get(row);
            if (entry.isActive() != active) {
                if (active) activeIPL(entry.getName());
                else deactiveIPL(entry.getName());
            }
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Data Management">
    public void reload() throws IOException {
        // reset data
        scriptItemModel.bind(null);
        entries.clear();
        // bind data from script file
        for (String loader : GameConfig.getLoaders()) {
            Logger.printNotice("Executing loader: %1$s", loader);
            try (InputStream is = new FileInputStream(GameConfig.getDirectory() + loader);
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] args = line.replaceAll("#.*$", "").trim().split("\\s+");
                    switch (args[0]) {
                        case "IDE":
                            entries.add(new WorldScriptEntry(entries.size(), args[1], WorldScriptType.IDE));
                            break;
                            
                        case "IPL":
                            entries.add(new WorldScriptEntry(entries.size(), args[1], WorldScriptType.IPL));
                            break;
                            
                        default:
                            continue;
                    }
                    Logger.printNormal(line);
                }
            } catch (IOException ex) {
                Logger.printError("Failed executing: %1$s", loader);
            }
        }

        scriptItemModel.bind(entries);
        // stream internal ipl from SA
        if (GameConfig.ALIAS_SA.equals(GameConfig.getAlias()))
            parseInternalScript();
        
        fireTableDataChanged();
    }
    
    static String readName(ByteBuffer bb, int len) {
        StringBuilder sb = new StringBuilder();
        for (int c, j = 0; j < len; j++) {
            if ((c = bb.get()) == 0) {
                bb.position(bb.position() + len - 1 - j);
                break;
            }
            sb.append((char) c);
        }
        return sb.toString();
    }
    
    public ArrayList<WorldScriptEntry> getEntries() {
        return entries;
    }
    
    public WorldScriptEntry getEntry(int groupId) {
        return (groupId < 0 || groupId >= entries.size()) ? null : entries.get(groupId);
    }
    
    public WorldScriptEntry getEntry(String entryName) {
        for (WorldScriptEntry e : entries) {
            if (e.getName().equalsIgnoreCase(entryName)) {
                return e;
            }
        }
        return null;
    }
    
    private ArrayList<String> getActiveGroups(WorldScriptType type) {
        ArrayList<String> rs = new ArrayList<>();
        for (WorldScriptEntry e : entries) {
            if (e.getType() == type && e.isActive()) {
                rs.add(e.getName().toLowerCase());
            }
        }
        return rs;
    }
    
    public ScriptItemModel getDefinitionItemModel() {
        return scriptItemModel;
    }
    
    private void parseInternalScript() {
        // for GTA SA only, read extra binary stream inside img file
        try {
            ArrayList<ItemNULL> items = scriptItemModel.entries;
            ResourceModel res = ResourceModel.getInstance();
            for (WorldScriptEntry group : entries) {
                if (group.getName().toLowerCase().endsWith(".ipl")) {
                    String name = group.getName();
                    String prefix = name.substring(0, name.length() - 4).concat("_stream");
                    int id = 0;
                    IArchiveEntry entry;
                    while ((entry = res.findEntry(prefix + id, "ipl")) != null) {
                        try (EntryStream es = new EntryStream(entry)) {
                            es.position(4); //skips "bnry"
                            int instCount = es.getInt();
                            es.position(0x4C); // offset of item instances, 0x4C by default
                            for (int i = 0; i < instCount; i++) {
                                ItemINST inst = new ItemINST(es, group.getIndex());
                                items.add(inst);
                            }
                        }
                        id++;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Active / Deactive">
    public void activeIPL(String ipl) {
        //active dependecies
        for (String dp : GameConfig.getDependencies(ipl))
            activeGroup(getEntry(dp));
        //active target
        activeGroup(getEntry(ipl));
    }
    
    private void activeGroup(WorldScriptEntry g) {
        if (g != null && !g.isActive()) {
            ViewportApp app = ViewportApp.getInstance();
            for (ItemNULL e : scriptItemModel.getEntriesByGroup(g.getIndex())) {
                try {
                    switch (e.getType()) {
                        case OBJS:
                        case TOBJ:
                            ItemOBJS objs = (ItemOBJS) e;
                            app.addOBJS(objs);
                            break;

                        case CARS:
                            app.addCARS((ItemCARS)e);
                            break;

                        case INST:
                            GWorldMap.getInstance().addINST((ItemINST)e);
                            break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
            g.setActive(true);
        }
    }
    
    public void deactiveIPL(String ipl) {
        // deactive target
        deactiveGroup(getEntry(ipl));
        // deactive no longer required dependencies
        ArrayList<String> keeps = GameConfig.getDependencies(getActiveGroups(WorldScriptType.IPL));
        keeps.addAll(GameConfig.getDependencies());
        for (String ide : getActiveGroups(WorldScriptType.IDE)) {
            if (!keeps.contains(ide))
                deactiveGroup(getEntry(ide));
        }
        System.gc();
    }
    
    private void deactiveGroup(WorldScriptEntry g) {
        if (g != null && g.isActive()) {
            ViewportApp app = ViewportApp.getInstance();
            for (ItemNULL e : scriptItemModel.getEntriesByGroup(g.getIndex())) {
                try {
                    switch (e.getType()) {
                        case OBJS:
                        case TOBJ:
                            app.removeOBJS((ItemOBJS)e);
                            break;

                        case INST:
                            GWorldMap.getInstance().removeINST((ItemINST)e);
                            break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
            g.setActive(false);
        }
    }
    //</editor-fold>
}
