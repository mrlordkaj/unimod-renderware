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
package com.openitvn.unicore.plugin.gta.item;

import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/PATH_(IDE_Section)
 */
public class ItemPATHNode {
    
    public static final int TYPE_NULL = 0,
                            TYPE_PORT = 1,
                            TYPE_TURN = 2;
    
    public final int type;          // 0 = Null
                                    // 1 = External
                                    // 2 = Internal
    public final short nextId;      // -1 = Do not link to any other node in this group
                                    // 0 to 11 = Link to this node number in this group
    public final boolean isCross;   // Boolean determining if there is a cross road in the imaginary line joined by two nodes
    public final Vector3 position;  // X, Y, and Z coordinates relative to the center of the object * 16
    public final float laneWidth;   // Always 160
    public final int numLefts;      // Number of lanes left of the node (ignored for ped path nodes)
    public final int numRights;     // Number of lanes right of the node (ignored for ped path nodes)
    
    public ItemPATHNode(String[] args) {
        type = Integer.parseInt(args[0]);
        nextId = Short.parseShort(args[1]);
        isCross = Integer.parseInt(args[2]) != 0;
        position = new Vector3(
                Integer.parseInt(args[3]) * 0.0625f,
                Integer.parseInt(args[4]) * 0.0625f,
                Integer.parseInt(args[5]) * 0.0625f);
        laneWidth = Integer.parseInt(args[6]) * 0.0625f * 0.5f;
        numRights = Integer.parseInt(args[7]);
        numLefts = Integer.parseInt(args[8]);
    }
}
