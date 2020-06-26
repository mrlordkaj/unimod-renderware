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
 * https://gtamods.com/wiki/RpGeometry
 */
public class RpSphere {
    
    public float x, y, z, radius; // float
    
    public RpSphere(ByteBuffer bb) {
        x = bb.getFloat();
        y = bb.getFloat();
        z = bb.getFloat();
        radius = bb.getFloat();
    }
}
