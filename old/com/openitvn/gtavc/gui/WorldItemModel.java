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
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
class WorldItemModel extends AbstractTableModel
{
    public static final String[] COLUMNS = {"Description", "Type"};
    public static final int COL_DESC = 0;
    public static final int COL_TYPE = 1;
    
    ArrayList<ItemNULL> entries = new ArrayList<>();
    
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
            case COL_DESC:
                return String.class;
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
                return entries.get(row).toString();
            case COL_TYPE:
                return entries.get(row).getType();
        }
        return null;
    }
}
