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

import com.openitvn.gtavc.core.item.ItemINST;
import com.openitvn.gtavc.core.item.ItemOBJS;
import com.openitvn.gtavc.core.item.ItemTOBJ;
import com.openitvn.unicore.plugin.gta.item.ItemNULL;
import com.openitvn.unicore.plugin.gta.item.ItemType;
import com.openitvn.unicore.plugin.gta.ScriptHelper;
import com.openitvn.unicore.plugin.gta.WorldScriptEntry;
import com.openitvn.unicore.plugin.gta.item.ItemCARS;
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
    private final String[] COLUMNS = {"Description", "Type", "SID"};
    public static final int COL_DESCRIPTION = 0;
    public static final int COL_TYPE = 1;
    public static final int COL_FILE = 2;
    

    private final ArrayList<ItemNULL> entries = new ArrayList<>();

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
                return entries.get(row).getGroupIndex();
        }

        return null;
    }
    
    public void bind(ArrayList<WorldScriptEntry> definitionGroups) {
        //reset data
        entries.clear();
        
        //bind new data
        for (WorldScriptEntry group : definitionGroups) {
            String filePath = group.getAbsolutePath();
            defineFromFile(filePath, group.getIndex());
        }
        
        fireTableDataChanged();
    }
    
    public void unbind() {
        entries.clear();
        fireTableDataChanged();
    }
    
    public void addEntry(ItemNULL newEntry) {
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
                            ItemNULL newItem = createItemEntry(line, curType, fileId);
                            entries.add(newItem);
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public ArrayList<ItemNULL> getEntries() {
        return entries;
    }
    
    public ArrayList<ItemNULL> getEntriesByGroup(int gid) {
        ArrayList<ItemNULL> rs = new ArrayList<>();
        for (ItemNULL e : entries) {
            if (e.getGroupIndex() == gid) {
                rs.add(e);
            }
        }
        return rs;
    }
    
    public ItemNULL getEntry(int id) {
        return entries.get(id);
    }
    
    private static ItemNULL createItemEntry(String line, ItemType itemType, int groupId) {
        String[] args = ScriptHelper.parseLineByComma(line);
        switch (itemType) {
            case OBJS:
                return new ItemOBJS(args, groupId);
                
            case TOBJ:
                return new ItemTOBJ(args, groupId);
                
            case INST:
                return new ItemINST(args, groupId);
                
            case CARS:
                return new ItemCARS(args, groupId);
                
            default:
                return new ItemNULL(groupId);
        }
    }
}
