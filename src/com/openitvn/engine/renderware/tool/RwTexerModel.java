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

package com.openitvn.engine.renderware.tool;

import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.format.txd.RwTexture;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
@SuppressWarnings("serial")
class RwTexerModel extends AbstractTableModel {
    
    static final String[] COLUMNS = { "Name", "Mask", "A" };
    static final int COL_NAME = 0;
    static final int COL_MASK = 1;
    static final int COL_ALPHA = 2;
    
    final ArrayList<RwTexture> entries = new ArrayList<>();
    
    void bindTexDic(RpTextureDictionary texDic) {
        entries.clear();
        if (texDic != null) {
            for (RpTextureNative texData : texDic.textures) {
                RwTexture tex = new RwTexture(texData.textureName, texData);
                entries.add(tex);
            }
        }
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
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case COL_ALPHA:
                return Boolean.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        RpTextureNative texData = entries.get(row).textureNative;
        switch (col) {
            case COL_NAME:
                return texData.textureName;
                
            case COL_MASK:
                return texData.maskName;
                
            case COL_ALPHA:
                return texData.hasAlpha();
        }
        return null;
    }
}
