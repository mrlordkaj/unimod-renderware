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

import com.openitvn.gtavc.core.item.CARSEntry;
import com.openitvn.gtavc.core.item.INSTEntry;
import com.openitvn.gtavc.core.item.OBJSEntry;
import com.openitvn.gtavc.core.item.TOBJEntry;
import com.openitvn.gtavc.core.item.NULLEntry;
import com.openitvn.gtavc.core.entity.ScriptFile;
import com.openitvn.gtavc.core.item.ItemType;
import com.openitvn.unicore.plugin.gta.ScriptHelper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
public class ScriptItemModel extends AbstractTableModel {
    private final String[] COLUMNS = {"Description", "Type", "File ID"};
    public static final int COL_DESCRIPTION = 0;
    public static final int COL_TYPE = 1;
    public static final int COL_FILE = 2;
    

    private final ArrayList<NULLEntry> entries = new ArrayList<>();

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
            case COL_FILE:
                return Integer.class;

            case COL_DESCRIPTION:
            case COL_TYPE:
                return String.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch(col) {
            case COL_DESCRIPTION:
                return entries.get(row);
                
            case COL_TYPE:
                return entries.get(row).getType();
                
            case COL_FILE:
                return entries.get(row).groupId;
        }

        return null;
    }
    
    public void bind(ArrayList<ScriptFile> definitionGroups) {
        //reset data
        entries.clear();
        
        //bind new data
        for(ScriptFile group : definitionGroups) {
            String filePath = group.getAbsolutePath();
            defineFromFile(filePath, group.index);
        }
        
        fireTableDataChanged();
    }
    
    public void unbind() {
        entries.clear();
        fireTableDataChanged();
    }
    
    public void addEntry(NULLEntry newEntry) {
        entries.add(newEntry);
    }
    
    private void defineFromFile(String fileName, int fileId) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            ItemType curType = ItemType.NULL; // read definition by group of types
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                ItemType newType = ItemType.fromLine(line);
                if (newType != null) {
                    curType = newType;
                } else if(line.toLowerCase().equals("end")) {
                    curType = ItemType.NULL;
                } else {
                    switch(curType) {
                        case NULL:
                            break;

                        case PATH:
                            //read path data by block
                            break;

                        default:
                            if (line.length() == 0) continue; //skip blank lines
                            if (line.charAt(0) == '#') continue; //skip comment lines
                            //read other data by line
                            NULLEntry newItem = createItemEntry(line, curType, fileId);
                            entries.add(newItem);
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public ArrayList<NULLEntry> getEntries() {
        return entries;
    }
    
    public ArrayList<NULLEntry> getEntriesByGroup(int groupId) {
        ArrayList<NULLEntry> rs = new ArrayList<>();
        for (NULLEntry e : entries) {
            if (e.groupId == groupId)
                rs.add(e);
        }
        return rs;
    }
    
    public NULLEntry getEntry(int id) {
        return entries.get(id);
    }
    
    private static NULLEntry createItemEntry(String line, ItemType itemType, int groupId) {
        String[] args = ScriptHelper.parseLineByComma(line);
        switch (itemType) {
            case OBJS:
                return new OBJSEntry(args, groupId);
                
            case TOBJ:
                return new TOBJEntry(args, groupId);
                
            case INST:
                return new INSTEntry(args, groupId);
                
            case CARS:
                return new CARSEntry(args, groupId);
                
            default:
                return new NULLEntry(groupId);
        }
    }
}
