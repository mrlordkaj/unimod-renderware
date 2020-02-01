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
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/RenderWare_binary_stream_file
 */
public class RpSection implements TreeNode {
    
    private static int libraryIDPack(int ver, int build) {
        if (ver <= 0x31000)
            return ver >> 8;
        return ((ver - 0x30000 & 0x3ff00) << 14) |
               ((ver           & 0x0003f) << 16) |
                (build         & 0x0ffff);
    }
    
    private static int libraryIDUnpackVersion(int libId) {
        if ((libId & 0xffff0000) != 0)
            return ((libId >> 14) & 0x3ff00) + 0x30000 |
                   ((libId >> 16) & 0x0003f);
	return libId << 8;
    }
    
    private static int libraryIDUnpackBuild(int libId) {
        if ((libId & 0xffff0000) != 0)
            return libId & 0xffff;
        return 0;
    }
    
    public static RpSection fromData(DataStream ds, RpSection parent) {
        if (ds.remaining() < 12)
            return null; // section below 12 bytes makes no sense
        int typeId = ds.getInt();
        if (typeId == 0)
            return null; // file padding with 0
        int size = ds.getInt();
        int libId = ds.getInt();
        RpType type = RpType.getType(typeId);
        switch (type) {
            case Clump:
                return new RpClump(size, libId, parent, ds);
                
            case FrameList:
                return new RpFrameList(size, libId, parent, ds);
                
            case Atomic:
                return new RpAtomic(size, libId, parent, ds);
                
            case Geometry:
                return new RpGeometry(size, libId, parent, ds);
                
            case Material:
                return new RpMaterial(size, libId, parent, ds);
                
            case Texture:
                return new RpTexture(size, libId, parent, ds);
                
            case TextureDictionary:
                return new RpTextureDictionary(size, libId, parent, ds);
                
            case TextureNative:
                return new RpTextureNative(size, libId, parent, ds);
                
            case Null:
                return null;
                
            default:
                return new RpSection(type, size, libId, parent, ds);
        }
    }
    
    // data
    public RpType type;
    public int size;
    public int version;
    public int build;
    public ByteBuffer data;
    // references
    public final RpSection parent;
    public final ArrayList<RpSection> children = new ArrayList<>();
    public final int level;
    
    protected RpSection(RpType type, int size, int libId, RpSection parent, DataStream ds) {
        this.parent = parent;
        this.type = type;
        this.version = libraryIDUnpackVersion(libId);
        this.build = libraryIDUnpackBuild(libId);
        this.level = (parent == null) ? 0 : parent.level + 1;
        // with version 0x31000 or lower,
        // such as mak_billboardsrvc.dff, or lamppost2.dff, or sam.dff (GTA3)
        // we must plus 12*(atomicCount-1) bytes to grand section's size
        if (version <= 0x31000 && level == 0 && ds.remaining() >= 16) {
            long mark = ds.position();
            ds.position(mark + 12);
            int atomicCount = ds.getInt();
            size += 12 * (atomicCount - 1);
            ds.position(mark);
        }
        this.size = size;
        
//        //DO NOT REMOVE - for dump when neccessary
//        System.out.printf("0x%1$05x\n", version);
//        StringBuilder dumpName = new StringBuilder();
//        for(int i = 0; i < getLevel(); i++) dumpName.append("|--");
//        dumpName.append(toString());
//        System.out.println(dumpName.toString());
        
        parseChildren(ds);
    }
    
    /**
     * TODO: Adapt with previous application,
     * will be removed in futher release.
     * @param ds 
     */
    @Deprecated
    protected void parseChildren(DataStream ds) {
        if (type.isContainer) {
            // parse children
            long endPos = ds.position() + size;
            while (ds.position() < endPos)
                children.add(fromData(ds, RpSection.this));
        } else {
            // get data
            data = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
            ds.get(data.array());
        }
    }
    
    protected final ByteBuffer getStruct() {
        return getFirstChild(RpType.Struct).data;
    }
    
    public final ArrayList<RpSection> getChildren() {
        return children;
    }
    
    public final ArrayList<RpSection> getChildren(RpType type) {
        ArrayList<RpSection> rs = new ArrayList<>();
        for (RpSection child : children) {
            if (child.type == type)
                rs.add(child);
        }
        return rs;
    }
    
    public final <T> ArrayList<T> getChildren(Class<T> childClass) {
        ArrayList<T> rs = new ArrayList<>();
        for (RpSection child : children) {
            if (child.getClass().equals(childClass))
                rs.add((T)child);
        }
        return rs;
    }
    
    public final RpSection getFirstChild(RpType type) {
        for (RpSection child : children) {
            if (child.type == type)
                return child;
        }
        return null;
    }
    
    public final <T> T getFirstChild(Class<T> childClass) {
        for (RpSection child : children) {
            if (child.getClass().equals(childClass))
                return (T)child;
        }
        return null;
    }
    
    @Override
    public final String toString() {
        return String.format("%1$s (%2$d)", type.name, size + 12);
    }
    
    @Override
    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).equals(node))
                return i;
        }
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return type.isContainer;
    }

    @Override
    public boolean isLeaf() {
        return !type.isContainer;
    }

    @Override
    public Enumeration children() {
        return Collections.enumeration(children);
    }
}