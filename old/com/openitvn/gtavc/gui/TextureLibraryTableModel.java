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

import com.openitvn.engine.renderware.RpSection;
import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.format.txd.RwTexture;
import com.openitvn.unicore.data.DataStream;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
public class TextureLibraryTableModel extends AbstractTableModel {
    private final String[] COLUMNS = {"ID", "Name"};
    public static final int COL_INDEX = 0;
    public static final int COL_NAME = 1;
    
    private final ArrayList<RwTexture> entries = new ArrayList<>();
    
    public void bind(DataStream ds) {
        RpTextureDictionary txd = RpSection.loadRoot(ds, RpTextureDictionary.class);
        bind(txd);
    }
    
    public void bind(RpTextureDictionary texDic) {
        entries.clear();
        for (RpTextureNative texData : texDic.textures) {
            RwTexture tex = new RwTexture(texData.textureName, texData);
            entries.add(tex);
        }
        fireTableDataChanged();
    }
    
    public void unbind() {
        entries.clear();
        fireTableDataChanged();
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
    public String getColumnName(int col) {
        return COLUMNS[col];
    }
    
    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COL_INDEX:
                return Integer.class;

            case COL_NAME:
                return String.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
            case COL_INDEX:
                return row;
                
            case COL_NAME:
                return entries.get(row).getTextureName();
        }
        
        return null;
    }
    
    public RwTexture getEntry(int index) {
        return entries.get(index);
    }
    
    public int getEntryCount() {
        return entries.size();
    }
}
