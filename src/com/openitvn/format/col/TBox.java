/*
 * Copyright (C) 2024 Thinh Pham
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
package com.openitvn.format.col;

import com.badlogic.gdx.math.Vector3;
import com.openitvn.unicore.data.DataStream;

/**
 *
 * @author Thinh Pham
 */
public class TBox {
    public final Vector3 min = new Vector3();
    public final Vector3 max = new Vector3();
    
    public void read(DataStream ds) {
        ds.getVector3(min);
        ds.getVector3(max);
    }
     
    public Vector3 getCenter() {
        return min.cpy().add(max).scl(0.5f);
    }
    
    public Vector3 getScale() {
        return max.cpy().sub(min);
    }
}
