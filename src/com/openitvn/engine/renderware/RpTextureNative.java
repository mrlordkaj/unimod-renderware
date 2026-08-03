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

import com.badlogic.gdx.graphics.GL20;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.resource.IPixelFormat;
import com.openitvn.helper.StringHelper;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/Texture_Native_(RW_Section)
 */
public class RpTextureNative extends RpSection {
    
    // Addressing mode
    
    public static final byte WRAP_NONE   = 0x00;
    public static final byte WRAP_REPEAT = 0x01;
    public static final byte WRAP_MIRROR = 0x02;
    public static final byte WRAP_CLAMP  = 0x03;
    
    // Filtering mode
    
    public static final byte FILTER_NONE               = 0x00;
    public static final byte FILTER_NEAREST            = 0x01;
    public static final byte FILTER_LINEAR             = 0x02;
    public static final byte FILTER_MIP_NEAREST        = 0x03;
    public static final byte FILTER_MIP_LINEAR         = 0x04;
    public static final byte FILTER_LINEAR_MIP_NEAREST = 0x05;
    public static final byte FILTER_LINEAR_MIP_LINEAR  = 0x06;
    
    // Image formats
    
    public static final int FORMAT_DEFAULT         = 0x0000;
    public static final int FORMAT_1555            = 0x0100; //(1 bit alpha, RGB 5 bits each; also used for DXT1 with alpha)
    public static final int FORMAT_565             = 0x0200; //(5 bits red, 6 bits green, 5 bits blue; also used for DXT1 without alpha)
    public static final int FORMAT_4444            = 0x0300; //(RGBA 4 bits each; also used for DXT3)
    public static final int FORMAT_LUM8            = 0x0400; //(gray scale, D3DFMT_L8)
    public static final int FORMAT_8888            = 0x0500; //(RGBA 8 bits each)
    public static final int FORMAT_888             = 0x0600; //(RGB 8 bits each, D3DFMT_X8R8G8B8)
    public static final int FORMAT_555             = 0x0A00; //(RGB 5 bits each - rare, use 565 instead, D3DFMT_X1R5G5B5)
    public static final int FORMAT_EXT_AUTO_MIPMAP = 0x1000; //(RW generates mipmaps, see special section below)
    public static final int FORMAT_EXT_PAL8        = 0x2000; //(2^8 = 256 palette colors)
    public static final int FORMAT_EXT_PAL4        = 0x4000; //(2^4 = 16 palette colors)
    public static final int FORMAT_EXT_MIPMAP      = 0x8000; //(mipmaps included)
    
    // Compression
    
    public static final byte COMPRESSION_NONE = 0;
    public static final byte COMPRESSION_DXT1 = 1;
    public static final byte COMPRESSION_DXT3 = 3;
    public static final byte COMPRESSION_DXT5 = 5;
    public static final byte COMPRESSION_PAL4 = 4;
    public static final byte COMPRESSION_PAL8 = 8;
    
    // Platform
    
    public static final int PLATFORM_GTA_XBOX = 5;
    public static final int PLATFORM_GTA3_PC  = 8;
    public static final int PLATFORM_GTASA_PC = 9;
    public static final int PLATFORM_PS2      = StringHelper.makeFourCC("PS20");
    
    // TextureFormat; 72 bytes in total
    
    private final int platformId;
    private final byte filterMode;
    private final byte addressing;
    public final String textureName;
    public final String maskName;
    
    // RasterFormat; 16 bytes in total
    
    private final int rasterFormat;
    private final int alpha;
    public final short width;
    public final short height;
    public final byte colorDepth;
    public final byte mipCount;
    private final byte rasterType;
    
    public RpTextureNative(int size, int libId, RpSection parent, DataStream ds) {
        super(RpType.TextureNative, size, libId, parent, ds);
        ByteBuffer bb = getStruct();
        // TextureFormat; 72 bytes in total
        platformId = bb.getInt();
        filterMode = bb.get();
        addressing = bb.get();
        bb.position(bb.position() + 2); // pad
        textureName = RpHelper.readName(bb, 32);
        maskName = RpHelper.readName(bb, 32);
        // RasterFormat; 16 bytes in total
        rasterFormat = bb.getInt();
        alpha = bb.getInt();
        width = bb.getShort();
        height = bb.getShort();
        colorDepth = bb.get();
        mipCount = bb.get();
        bb.position(bb.position() + 1); // pad
        rasterType = bb.get();
    }
    
    public ByteBuffer getNativeData() {
        ByteBuffer bb = getStruct();
        bb.position(88);
        return bb.slice().order(ByteOrder.LITTLE_ENDIAN);
    }
    
    public String getMapperName() {
        String texName = (maskName.isEmpty() || !hasAlpha()) ? textureName : maskName;
//        texName = texName.replaceAll("@", "a")
//                .replaceAll("\\s+", "_");
        return texName;
    }
    
    public boolean hasAlpha() {
        return platformId == PLATFORM_GTASA_PC ?
                (rasterType & 0b1) != 0 :
                alpha != 0;
    }
    
    private static byte getCompression(int fmt, int ext) {
        if ((ext & FORMAT_EXT_PAL4) != 0) {
            return COMPRESSION_PAL4;
        }
        if ((ext & FORMAT_EXT_PAL8) != 0) {
            return COMPRESSION_PAL8;
        }
        switch (fmt) {
            case FORMAT_1555:
            case FORMAT_565:
            case FORMAT_555:
                return COMPRESSION_DXT1;
            case FORMAT_4444:
                return COMPRESSION_DXT3;
            case FORMAT_LUM8:
            case FORMAT_8888:
            case FORMAT_888:
                return COMPRESSION_NONE;
        }
        return COMPRESSION_NONE;
    }
    
    public IPixelFormat getPixelFormat() {
        int ext = rasterFormat & 0xf000;
        int fmt = rasterFormat & 0x0f00;
        boolean bCompressSource = (platformId == PLATFORM_GTASA_PC) || (rasterType == COMPRESSION_NONE);
        byte compress = bCompressSource ? getCompression(fmt, ext) : rasterType;
        switch (compress) {
            case COMPRESSION_NONE:
                switch (fmt) {
                    case FORMAT_LUM8:
                        return IPixelFormat.D3DFMT_L8;
                    case FORMAT_8888:
                        return IPixelFormat.D3DFMT_A8R8G8B8;
                    case FORMAT_888:
                        return IPixelFormat.D3DFMT_X8R8G8B8;
                }
                break;
            case COMPRESSION_DXT1:
                return IPixelFormat.D3DFMT_DXT1;
            case COMPRESSION_DXT3:
                return IPixelFormat.D3DFMT_DXT3;
            case COMPRESSION_DXT5:
                return IPixelFormat.D3DFMT_DXT5;
            case COMPRESSION_PAL4:
                switch (fmt) {
                    case FORMAT_8888:
                        return IPixelFormat.PALETTE4_RGBA8_OES;
                    case FORMAT_888:
                        return IPixelFormat.PALETTE4_RGB8_OES;
                }
                break;
            case COMPRESSION_PAL8:
                switch (fmt) {
                    case FORMAT_8888:
                        return IPixelFormat.PALETTE8_RGBA8_OES;
                    case FORMAT_888:
                        return IPixelFormat.PALETTE8_RGB8_OES;
                }
        }
        return IPixelFormat.D3DFMT_UNKNOW;
    }
    
    public int getUWrap() {
        byte u = (byte)((addressing & 0xf0) >> 4);
        return convertWrap(u);
    }
    
    public int getVWrap() {
        byte v = (byte)(addressing & 0x0f);
        return convertWrap(v);
    }
    
    private static int convertWrap(byte val) {
        switch (val) {
            case WRAP_REPEAT:
                return GL20.GL_REPEAT;
            case WRAP_MIRROR:
                return GL20.GL_MIRRORED_REPEAT; 
        }
        return GL20.GL_CLAMP_TO_EDGE;
    }
}
