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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public class RwSection extends RpSection {
    
    private static int libraryIDPack(int ver, int build) {
        if (ver <= 0x31000)
            return ver >> 8;
        return ((ver - 0x30000 & 0x3ff00) << 14) |
               ((ver           & 0x0003f) << 16) |
                (build         & 0x0ffff);
    }
    
    public static RpSection fromData(DataStream ds, RwSection parent) {
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
                return new RwClump(size, libId, parent, ds);
                
            case FrameList:
                return new RpFrameList(size, libId, parent, ds);
                
            case Atomic:
                return new RpAtomic(size, libId, parent, ds);
                
            case Geometry:
                return new RwGeometry(size, libId, parent, ds);

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
                return new RwSection(type, size, libId, parent, ds);
        }
    }
    
    public RwSection(RpType type, int size, int libId, RpSection parent, DataStream ds) {
        super(type, size, libId, parent, ds);
    }
    
    @Override
    protected void parseChildren(DataStream ds) {
        if (type.isContainer) {
            // parse children
            long endPos = ds.position() + size;
            while (ds.position() < endPos)
                children.add(fromData(ds, RwSection.this));
        } else {
            // get data
            data = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
            ds.get(data.array());
        }
    }
    
    public byte[] toData() throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(12 + size);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(type.id);
        bb.putInt(size);
        bb.putInt(libraryIDPack(version, build));
        bb.put(data.array());
        return bb.array();
    }
}
