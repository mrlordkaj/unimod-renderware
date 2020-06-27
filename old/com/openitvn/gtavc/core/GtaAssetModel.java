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
package com.openitvn.gtavc.core;

import com.openitvn.format.img.RwArchive;
import com.openitvn.format.img.RwArchiveEntry;
import com.openitvn.unicore.data.BufferStream;
import com.openitvn.unicore.plugin.gta.GameConfig;
import java.io.IOException;
import java.util.LinkedHashMap;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
public class GtaAssetModel extends AbstractTableModel {
    
    private final String[] COLUMNS = {"ID", "Name", "Type", "Size"};
    public static final int COL_INDEX = 0;
    public static final int COL_NAME = 1;
    public static final int COL_TYPE = 2;
    public static final int COL_SIZE = 3;
    
    private static GtaAssetModel instance;
    
    public static GtaAssetModel getInstance() {
        if (instance == null)
            instance = new GtaAssetModel();
        return instance;
    }
    
    final LinkedHashMap<String, RwArchiveEntry> assetMap = new LinkedHashMap<>();
    
    private GtaAssetModel() { }
    
    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    @Override
    public int getRowCount() {
        return assetMap.size();
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
            case COL_SIZE:
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
                return getEntry(row).getName();

            case COL_TYPE:
                return getEntry(row).getExt();

            case COL_SIZE:
                long size = getEntry(row).getSize();
                return size / 1024 + " kB";
        }

        return null;
    }
    
    public RwArchiveEntry getEntry(int i) {
        return assetMap.values().stream().skip(i).findFirst().get();
    }
    
    public BufferStream extract(String name) throws IOException {
        RwArchiveEntry entry = assetMap.get(name.toLowerCase());
        if (entry != null) {
            return entry.toDataStream();
        }
        return null;
    }
    
    public void addArchive(String imgName) {
        try {
            RwArchive arc = new RwArchive();
            arc.open(GameConfig.getDirectory()+"/"+imgName);
            for (RwArchiveEntry e : arc.entries)
                assetMap.put(e.getName().toLowerCase(), e);
            fireTableDataChanged();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
