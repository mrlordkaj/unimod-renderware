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

package com.openitvn.engine.renderware;

import com.openitvn.unicore.data.DataStream;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/RpClump
 */
public class RwClump extends RwSection {
    
    public RpFrameList frameList;
    public ArrayList<RwGeometry> geometries = new ArrayList<>();
    
    public RwClump(int size, int libId, RwSection parent, DataStream ds) {
        super(RpType.Clump, size, libId, parent, ds);
        ArrayList<RpAtomic> atoms = super.getChildren(RpAtomic.class);
        if (!atoms.isEmpty()) {
            geometries = super.getFirstChild(RpType.GeometryList).getChildren(RwGeometry.class);
            frameList = super.getFirstChild(RpFrameList.class);
            for (RpAtomic atom : atoms)
                geometries.get(atom.geometryIndex).frame = frameList.getFrame(atom.frameIndex);
        }
        //note: if frameCount = atomicCount + 1, then frame 0 belong to clump (this)
    }
    
    public RwGeometry getRootGeometry() {
        return geometries.isEmpty() ? null :
                geometries.get(geometries.size() - 1);
    }
}
