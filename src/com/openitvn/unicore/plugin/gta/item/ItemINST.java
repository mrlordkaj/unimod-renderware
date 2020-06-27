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
package com.openitvn.unicore.plugin.gta.item;

import com.openitvn.unicore.data.DataStream;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/INST
 */
public class ItemINST extends ItemNULL {
    
    public int modId;
    public String modName;
    public float interior, posX, posY, posZ, rotX, rotY, rotZ, rotW;
//    public float sclX = 1, sclY = 1, sclZ = 1;
    public int lodId;
    
    public ItemINST(String[] args) throws IllegalArgumentException {
        modId = Integer.decode(args[0]);
        modName = args[1];
        switch (args.length) {
            case 11: // SA
                // Id, ModelName, Interior, PosX, PosY, PosZ, RotX, RotY, RotZ, RotW, LOD
                interior = Float.parseFloat(args[2]);
                posX = Float.parseFloat(args[3]);
                posY = Float.parseFloat(args[4]);
                posZ = Float.parseFloat(args[5]);
                rotX = Float.parseFloat(args[6]);
                rotY = Float.parseFloat(args[7]);
                rotZ = Float.parseFloat(args[8]);
                rotW = Float.parseFloat(args[9]);
                lodId = Integer.decode(args[10]);
                break;

            case 12: // GTA3, VC
                // Id, ModelName, PosX, PosY, PosZ, ScaleX, ScaleY, ScaleZ, RotX, RotY, RotZ, RotW
                posX = Float.parseFloat(args[2]);
                posY = Float.parseFloat(args[3]);
                posZ = Float.parseFloat(args[4]);
//                // By default each value is 1.0, which means the object is not scaled at all.
//                sclX = Float.parseFloat(args[5]);
//                sclY = Float.parseFloat(args[6]);
//                sclZ = Float.parseFloat(args[7]);
                rotX = Float.parseFloat(args[8]);
                rotY = Float.parseFloat(args[9]);
                rotZ = Float.parseFloat(args[10]);
                rotW = Float.parseFloat(args[11]);
                break;

            case 13: // VC
                // Id, ModelName, Interior, PosX, PosY, PosZ, ScaleX, ScaleY, ScaleZ, RotX, RotY, RotZ, RotW
                interior = Float.parseFloat(args[2]);
                posX = Float.parseFloat(args[3]);
                posY = Float.parseFloat(args[4]);
                posZ = Float.parseFloat(args[5]);
//                // By default each value is 1.0, which means the object is not scaled at all.
//                sclX = Float.parseFloat(args[6]);
//                sclY = Float.parseFloat(args[7]);
//                sclZ = Float.parseFloat(args[8]);
                rotX = Float.parseFloat(args[9]);
                rotY = Float.parseFloat(args[10]);
                rotZ = Float.parseFloat(args[11]);
                rotW = Float.parseFloat(args[12]);
                break;

            default:
                throw new IllegalArgumentException();
        }
    }
    
    public ItemINST(DataStream ds) {
        posX = ds.getFloat();
        posY = ds.getFloat();
        posZ = ds.getFloat();
        rotX = ds.getFloat();
        rotY = ds.getFloat();
        rotZ = ds.getFloat();
        rotW = ds.getFloat();
        modId = ds.getInt();
        interior = ds.getInt();
        lodId = ds.getInt();
    }
    
    @Deprecated
    public ItemINST(String[] args, int groupId) {
        this(args);
        this.groupIndex = groupId;
        this.itemType = ItemType.INST;
    }
    
    @Deprecated
    public ItemINST(DataStream ds, int groupId) {
        this(ds);
        this.groupIndex = groupId;
        this.itemType = ItemType.INST;
    }
    
    @Override
    public String toString() {
        return String.format("[%1$04d] %2$s", modId, modName);
    }
}
