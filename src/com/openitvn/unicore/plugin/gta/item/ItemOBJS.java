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

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/OBJS
 */
public class ItemOBJS extends ItemNULL {
    
    public int modId, meshCount = 1, flags;
    public float dd1, dd2, dd3;
    public String modName, txdName;
    
    public ItemOBJS(String[] args) throws IllegalArgumentException {
        modId = Integer.decode(args[0]);
        modName = args[1];
        txdName = args[2];
        switch (args.length) {
            case 5: // SA
                // Id, ModelName, TxdName, DrawDistance, Flags
                dd1 = Float.parseFloat(args[3]);
                flags = Integer.decode(args[4]);
                break;

            case 6: // GTA3, VC
                // Id, ModelName, TxdName, MeshCount, DrawDistance, Flags
                meshCount = Integer.decode(args[3]);
                dd1 = Float.parseFloat(args[4]);
                flags = Integer.decode(args[5]);
                break;

            case 7: // VC
                // Id, ModelName, TxdName, MeshCount, DrawDistance1, DrawDistance2, Flags
                meshCount = Integer.decode(args[3]);
                dd1 = Float.parseFloat(args[4]);
                dd2 = Float.parseFloat(args[5]);
                flags = Integer.decode(args[6]);
                break;

            case 8:
                // Id, ModelName, TxdName, MeshCount, DrawDistance1, DrawDistance2, DrawDistance3, Flags
                meshCount = Integer.decode(args[3]);
                dd1 = Float.parseFloat(args[4]);
                dd2 = Float.parseFloat(args[5]);
                dd2 = Float.parseFloat(args[6]);
                flags = Integer.decode(args[7]);
                break;

            default:
                throw new IllegalArgumentException();
        }
    }
    
    @Deprecated
    public ItemOBJS(String[] args, int groupId) {
        this(args);
        this.groupIndex = groupId;
        this.itemType = ItemType.OBJS;
    }
    
    @Override
    public String toString() {
        return String.format("[%1$04d] %2$s", modId, modName);
    }
}
