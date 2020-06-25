/*
 * Copyright (C) 2019 Thinh Pham
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
package com.openitvn.unicore.plugin.gta;

import com.openitvn.format.dff.RwModel;
import com.openitvn.format.txd.RwTexturePack;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.data.FileStream;
import com.openitvn.unicore.plugin.gta.item.CARSEntry;
import com.openitvn.unicore.world.resource.IMaterial;
import com.openitvn.unicore.world.resource.IModel;
import com.openitvn.unicore.world.resource.ITexture;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
public class VehicleModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"", "Name", "Type"};
    static final int COL_INDEX = 0;
    static final int COL_NAME = 1;
    static final int COL_TYPE = 2;
    
    ArrayList<CARSEntry> entries = new ArrayList<>();
    ArrayList<ITexture> comTexLib;
    ArrayList<IMaterial> comMatLib;
    ArrayList<IModel> comModLib;
    
    void bind(ResourceModel res) {
        // build wheelLib
        comTexLib = new ArrayList<>();
        comMatLib = new ArrayList<>();
        comModLib = new ArrayList<>();
        switch (GameConfig.getAlias()) {
            case GameConfig.ALIAS_III:
            case GameConfig.ALIAS_VC:
                RwModel extraLib = new RwModel("wheels");
                res.extractModel(extraLib);
                comTexLib.addAll(extraLib.resource.getTextures());
                comMatLib.addAll(extraLib.resource.getMaterials());
                comModLib.addAll(extraLib.resource.getModels());
                break;
                
            case GameConfig.ALIAS_SA:
                String extraTxd = GameConfig.getDirectory()+"/models/generic/vehicle.txd";
                try (FileStream fs = new FileStream(extraTxd)) {
                    RwTexturePack texDic = new RwTexturePack();
                    texDic.decode(fs);
                    comTexLib.addAll(texDic.textures);
                } catch (IOException ex) {
                    Logger.printError(ex.getMessage());
                }
                break;
        }
        // build vehicle list
        entries.clear();
        String alias = GameConfig.getAlias();
        try (FileInputStream is = new FileInputStream(GameConfig.getVehicleScript());
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr)) {
            String line;
            String[] args;
            while ((line = ScriptHelper.readLine(br)) != null) {
                switch (line) {
                    case "cars":
                        while ((args = ScriptHelper.parseLineByComma(br)) != null) {
                            if (args.length >= 10)
                                entries.add(new CARSEntry(args, alias));
                        }
                        break;
                }
            }
        } catch (IOException ex) { }
        fireTableDataChanged();
    }
    
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
        return Object.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        CARSEntry e = entries.get(row);
        switch (col) {
            case COL_INDEX:
                return e.id;
                
            case COL_NAME:
                return e.modName;
                
            case COL_TYPE:
                return e.type;
        }
        return null;
    }
    //</editor-fold>
}
