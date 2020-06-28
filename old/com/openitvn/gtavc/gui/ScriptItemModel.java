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
import com.openitvn.unicore.plugin.gta.item.ItemType;
import com.openitvn.unicore.plugin.gta.ScriptHelper;
import com.openitvn.unicore.plugin.gta.WorldScriptEntry;
import com.openitvn.unicore.plugin.gta.item.ItemCARS;
import com.openitvn.unicore.plugin.gta.item.ItemINST;
import com.openitvn.unicore.plugin.gta.item.ItemOBJS;
import com.openitvn.unicore.plugin.gta.item.ItemTOBJ;
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
    
    public static final int COL_DESC = 0;
    public static final int COL_TYPE = 1;
    public static final int COL_FILE = 2;
    
    public final ArrayList<ItemNULL> entries = new ArrayList<>();
    
    public void bind(ArrayList<WorldScriptEntry> scripts) {
        entries.clear();
        if (scripts != null) {
            for (WorldScriptEntry script : scripts) {
                String filePath = script.getAbsolutePath();
                defineFromFile(filePath, script.getIndex());
            }
        }
        fireTableDataChanged();
    }
    
    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case COL_DESC:
                return "Description";
                
            case COL_TYPE:
                return "Type";
                
            case COL_FILE:
                return "SID";
        }
        return null;
    }
    
    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COL_FILE:
                return Integer.class;

            case COL_DESC:
            case COL_TYPE:
                return String.class;
        }
        return String.class;
    }
    
    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch(col) {
            case COL_DESC:
                return entries.get(row);
                
            case COL_TYPE:
                return entries.get(row).getType();
                
            case COL_FILE:
                return entries.get(row).getGroupIndex();
        }
        return null;
    }
    
    private void defineFromFile(String fileName, int fileId) {
        try (FileReader fr = new FileReader(fileName);
                BufferedReader br = new BufferedReader(fr)) {
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
    
    public ArrayList<ItemNULL> getEntriesByGroup(int gid) {
        ArrayList<ItemNULL> rs = new ArrayList<>();
        for (ItemNULL e : entries) {
            if (e.getGroupIndex() == gid) {
                rs.add(e);
            }
        }
        return rs;
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
