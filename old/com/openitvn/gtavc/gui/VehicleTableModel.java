/*
 * Copyright (C) 2016 Thinh Pham <mrlordkaj@gmail.com>
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
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham <mrlordkaj@gmail.com>
 */
public class VehicleTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"ID", "Name", "Type"};
    public static final int COL_INDEX = 0;
    public static final int COL_NAME = 1;
    public static final int COL_TYPE = 2;
    
    private final ArrayList<CARSEntry> entries = new ArrayList<>();
    
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
        CARSEntry e = entries.get(row);
        switch (col) {
            case COL_INDEX:
                return e.id;
                
            case COL_NAME:
                return entries.get(row).gameName;
                
            case COL_TYPE:
                return entries.get(row).type;
        }
        
        return null;
    }
    
    //</editor-fold>
    
    public void add(CARSEntry entry) {
        entries.add(entry);
    }
    
    public CARSEntry get(int entryId) {
        return entries.get(entryId);
    }
}
