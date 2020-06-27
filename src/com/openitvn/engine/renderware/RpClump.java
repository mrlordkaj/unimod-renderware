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

import com.openitvn.engine.renderware.struct.RpFrame;
import com.openitvn.unicore.data.DataStream;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/RpClump
 */
public class RpClump extends RpSection {
    
    public ArrayList<RpGeometry> geometries = new ArrayList<>();
    public RpFrameList frameList;
    
    public RpClump(int size, int libId, RpSection parent, DataStream ds) {
        super(RpType.Clump, size, libId, parent, ds);
        // parse data
        ArrayList<RpAtomic> atoms = getChildren(RpAtomic.class);
        if (!atoms.isEmpty()) {
            if (version < 0x30400) {
                for (RpAtomic atom : atoms) {
                    geometries.add(atom.getFirstChild(RpGeometry.class));
                }
            } else {
                geometries = getFirstChild(RpType.GeometryList).getChildren(RpGeometry.class);
            }
            frameList = getFirstChild(RpFrameList.class);
            for (RpAtomic atom : atoms) {
                RpGeometry geo = geometries.get(atom.geometryIndex);
                RpFrame frm = frameList.frames[atom.frameIndex];
                geo.frame = frm; // DEPRECATED: no longer neccessary
                frm.geometry = geo;
            }
        }
        // NOTE: if frameCount = atomicCount + 1, then frame 0 belong to the clump (this)
    }
    
    public RpGeometry getRootGeometry() {
        return geometries.isEmpty() ? null :
                geometries.get(geometries.size() - 1);
    }
}
