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

import java.nio.ByteBuffer;

/**
 *
 * @author Thinh Pham
 */
public class RpTriangle {
    
    public final short v1, v2, v3; // int16
    public final short materialIndex; // int16
    
    public RpTriangle(ByteBuffer bb) {
        v2 = bb.getShort();
        v1 = bb.getShort();
        materialIndex = bb.getShort();
        v3 = bb.getShort();
    }
}
