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
import com.openitvn.engine.renderware.struct.RpFrame;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/Frame_List_(RW_Section)
 */
public class RpFrameList extends RpSection {
    
    public RpFrame[] frames;

    public RpFrameList(int size, int libId, RpSection parent, DataStream ds) {
        super(RpType.FrameList, size, libId, parent, ds);
        ByteBuffer struct = getStruct();
        int frameCount = struct.getInt();
        frames = new RpFrame[frameCount];
        ArrayList<RpSection> exts = getChildren(RpType.Extension);
        for (int i = 0; i < frameCount; i++) {
            ByteBuffer bb = exts.get(i).getFirstChild(RpType.NodeName).data;
            String name = new String(bb.array());
            frames[i] = new RpFrame(name, struct);
        }
        // resolve hierarchy
        for (RpFrame frm : frames) {
            if (frm.parentIndex >= 0)
                frm.parent = frames[frm.parentIndex];
        }
    }
    
    public RpFrame findParent(RpFrame child) {
        int parentIndex = child.parentIndex;
        return frames[parentIndex];
    }
}
