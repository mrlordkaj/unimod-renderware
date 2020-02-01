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
import java.nio.ByteBuffer;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/Atomic_(RW_Section)
 */

public class RpAtomic extends RpSection {
    
    protected static final byte ATOMICCOLLISIONTEST = 0x01; //A generic collision flag to indicate that the atomic should be considered in collision tests. It wasn't used in GTA games since they don't use RW collision system.
    protected static final byte ATOMICRENDER        = 0x04; //The atomic is rendered if it is in the view frustum. It's set to TRUE for all models by default.
    
    protected final int frameIndex;
    protected final int geometryIndex;

    public RpAtomic(int size, int libId, RpSection parent, DataStream ds) {
        super(RpType.Atomic, size, libId, parent, ds);
        // parse struct
        ByteBuffer bb = getStruct();
        frameIndex = bb.getInt();
        geometryIndex = version < 0x30400 ? 0 : bb.getInt(); // see https://gtamods.com/wiki/RpClump
        int flags = bb.getInt();
        int unused = bb.getInt();
    }
}
