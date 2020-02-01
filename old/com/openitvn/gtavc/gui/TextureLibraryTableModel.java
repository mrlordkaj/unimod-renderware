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

import com.openitvn.gtavc.core.RwLoader;
import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.gtavc.core.entity.TextureLibraryEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham <mrlordkaj@gmail.com>
 */
public class TextureLibraryTableModel extends AbstractTableModel {
    private final String[] COLUMNS = {"ID", "Name"};
    public static final int COL_INDEX = 0;
    public static final int COL_NAME = 1;
    
    private final ArrayList<TextureLibraryEntry> entries = new ArrayList<>();
    
    public void bind(byte[] data) {
        try {
            RpTextureDictionary rwTexDic = (RpTextureDictionary)RwLoader.loadFromBuffer(data);
            bind(rwTexDic);
        } catch (IOException ex) {
            Logger.getLogger(TextureLibraryTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void bind(RpTextureDictionary texDic) {
        entries.clear();
        for (RpTextureNative tex : texDic.textures)
            entries.add(new TextureLibraryEntry(entries.size(), tex));
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
        switch(col) {
            case COL_INDEX:
                return entries.get(row).getIndex();
                
            case COL_NAME:
                return entries.get(row).getTextureName();
        }
        
        return null;
    }
    
    public TextureLibraryEntry getEntry(int index) {
        return entries.get(index);
    }
    
    public int getEntryCount() {
        return entries.size();
    }
}
