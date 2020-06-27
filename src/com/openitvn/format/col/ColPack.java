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
package com.openitvn.format.col;

import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.IGeometry;
import com.openitvn.unicore.world.IWorld;
import com.openitvn.unicore.world.IWorldCoord;
import com.openitvn.unicore.world.IWorldUnit;

/**
 *
 * @author Thinh Pham
 */
public class ColPack extends IWorld {
    
    public ColPack() {
        setCoordinate(IWorldCoord.Zup, IWorldUnit.Meters);
    }
    
    @Override
    public void fromData(DataStream ds) {
        while (ds.hasRemaining()) {
            int fourCC = ds.getInt();
            if (fourCC != 0) {
                ColFile col = new ColFile(fourCC, ds);
                if (col.model != null) {
                    resource.register(col.model);
                    new IGeometry(col.objsName).attach(this);
                }
            } else {
                break;
            }
        }
    }
}
