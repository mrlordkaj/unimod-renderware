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
//            frames[i].index = i;
        }
        // resolve hierarchy
        for (RpFrame frm : frames) {
            if (frm.parentIndex >= 0)
                frm.parent = frames[frm.parentIndex];
        }
//        for (RpFrame frm : frames)
//            System.out.printf("%1$s (%2$d)\n", frm.name, frm.getLevel());
    }
    
//    @Deprecated
//    public RpFrame[] getFrameSequence(String frameName) {
//        RpFrame frame = getFrame(frameName);
//        return getFrameSequence(frame);
//    }
    
//    @Deprecated
//    public RpFrame[] getFrameSequence(int frameIndex) {
//        RpFrame frame = getFrame(frameIndex);
//        return getFrameSequence(frame);
//    }
    
    @Deprecated
    public RpFrame[] getFrameSequence(RpFrame frame) {
        ArrayList<RpFrame> frameSequence = new ArrayList<>();
        //add target frame
        frameSequence.add(frame);
        //add parents sequence
        RpFrame curFrame = frame;
        while (curFrame.hasParent()) {
            curFrame = getParentFrame(curFrame);
            frameSequence.add(0, curFrame);
        }
        return frameSequence.toArray(new RpFrame[frameSequence.size()]);
    }
    
    @Deprecated
    public RpFrame getFrame(String frameName) {
        for (RpFrame frame : frames) {
            if (frame.name.equals(frameName))
                return frame;
        }
        return null;
    }
    
//    @Deprecated
//    public RpFrame[] getChildFrames(RpFrame parentFrame) {
//        return getChildFrames(parentFrame.index);
//    }
    
    @Deprecated
    public RpFrame[] getChildFrames(int parentIndex) {
        ArrayList<RpFrame> result = new ArrayList<>();
        for (RpFrame frame : frames) {
            if (frame.parentIndex == parentIndex)
                result.add(frame);
        }
        return result.toArray(new RpFrame[result.size()]);
    }
    
    @Deprecated
    public RpFrame getParentFrame(RpFrame childFrame) {
         int parentIndex = childFrame.parentIndex;
        return frames[parentIndex];
    }
    
//    @Deprecated
//    public RpFrame getParentFrame(int childIndex) {
//        int parentIndex = frames[childIndex].parentIndex;
//        return frames[parentIndex];
//    }
}
