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

package com.openitvn.engine.renderware.struct;

import com.openitvn.engine.renderware.RpGeometry;
import java.nio.ByteBuffer;

/**
 *
 * @author Thinh Pham
 */
public class RpFrame {
    
    // data
    public final float[] rotation = new float[9]; // matrix3
    public final float[] position = new float[3]; // vector3
    public final int parentIndex;
    public final int matrixFlags;
    
    // reference
    public final String name;
    public RpFrame parent;
    public RpGeometry geometry;
//    @Deprecated public int index;
    
    public RpFrame(String name, ByteBuffer bb) {
        this.name = name;
        for (int i = 0; i < 9; i++)
            rotation[i] = bb.getFloat();
        for (int i = 0; i < 3; i++)
            position[i] = bb.getFloat();
        parentIndex = bb.getInt();
        matrixFlags = bb.getInt();
    }
    
    public float[] combineMatrix4() {
        float[] r = rotation;
        float[] t = position;
        return new float[] {
            r[0], r[1], r[2], 0,
            r[3], r[4], r[5], 0,
            r[6], r[7], r[8], 0,
            t[0], t[1], t[2], 1
        };
    }
    
//    public int getLevel() {
//        int lvl = 0;
//        for (RpFrame p = this; p != null; p = p.parent)
//            lvl++;
//        return lvl;
//    }
    
    @Deprecated
    public boolean hasParent() {
        return parentIndex >= 0;
    }
}
