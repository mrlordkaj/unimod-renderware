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

package com.openitvn.gtavc.core.item;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/OBJS
 */

public class OBJSEntry extends NULLEntry {
    
    public static final int FLAG_WETEFFECT =           0b000000000001; //Wet effect (objects appear darker).
    public static final int FLAG_DONOTFADE =           0b000000000010; //Do not fade the object when it is being loaded into or out of view.
    public static final int FLAG_ALPHA =               0b000000000100; //Allow transparencies of other objects to be visible through this object.
    public static final int FLAG_ALPHA2 =              0b000000001000; //Alpha transparency 2.
    public static final int FLAG_OPPOSITE =            0b000000010000; //Opposite to flag 2.
    public static final int FLAG_ISARRAY =             0b000000100000; //Indicates an object to be used inside an interior.
    public static final int FLAG_DISABLESHADOW =       0b000001000000; //Disables the shadow mesh to project a shadow; allow transparencies of other objects, shadows, and lights to be visible through this object.
    public static final int FLAG_DONOTCULL =           0b000010000000; //Object surface will not be culled.
    public static final int FLAG_DISABLEDRAWDISTANCE = 0b000100000000; //Disables draw distance (Only used for LOD objects with an LOD value greater than 299).
    public static final int FLAG_BREAKABLE =           0b001000000000; //Object is breakable (like glass – additional parameters defined inside the object.dat file, otherwise there is no effect).
    public static final int FLAG_BREAKABLE2 =          0b010000000000; //Similar to flag 512: object first cracks on a strong collision, then it breaks (does also require object.dat registration).
    
    public int modelId;
    public String modName;
    public String txdName;
    public int meshCount; // dff geometries
    public float dd1, dd2, dd3; // draw distance
    public int flags;
    
    public OBJSEntry(String[] args, int groupId) {
        super(groupId);
        modelId = Integer.parseInt(args[0]);
        modName = args[1];
        txdName = args[2];
        switch (args.length) {
            case 6: //type1 (III/VC/SA)
                meshCount = Integer.parseInt(args[3]);
                dd1 = Float.parseFloat(args[4]);
                flags = Integer.parseInt(args[5]);
                break;
                
            case 7: //type2 (III/VC/SA)
                meshCount = Integer.parseInt(args[3]);
                dd1 = Float.parseFloat(args[4]);
                dd2 = Float.parseFloat(args[5]);
                flags = Integer.parseInt(args[6]);
                break;
                
            case 8: //type3 (III/VC/SA)
                meshCount = Integer.parseInt(args[3]);
                dd1 = Float.parseFloat(args[4]);
                dd2 = Float.parseFloat(args[5]);
                dd3 = Float.parseFloat(args[6]);
                flags = Integer.parseInt(args[7]);
                break;
                
            case 5: //type4 (SA)
                dd1 = Float.parseFloat(args[3]);
                flags = Integer.parseInt(args[4]);
                break;
        }
    }
    
    @Override
    public ItemType getType() {
        return ItemType.OBJS;
    }
    
    @Override
    public String toString() {
        return String.format("[%1$04d] %2$s", modelId, modName);
    }
}
