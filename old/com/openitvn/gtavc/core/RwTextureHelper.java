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

package com.openitvn.gtavc.core;

import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.unicore.world.resource.BufferedRaster;
import com.openitvn.unicore.world.resource.IPixelFormat;
import com.openitvn.unicore.world.resource.IRaster;
import com.openitvn.unicore.world.resource.ITexture;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public abstract class RwTextureHelper {
    
    public static BufferedImage toBufferedImage(RpTextureNative texNav, int mip) {
        Dimension imgSize = ITexture.computeMipMapSize(texNav.width, texNav.height, mip);
        BufferedRaster img = new BufferedRaster(imgSize.width, imgSize.height);
        RwTextureHelper.decodeTexture(img, texNav, mip);
        return img;
    }
    
    public static void decodeTexture(IRaster dst, RpTextureNative texNav, int mip) {
        ByteBuffer bb = (ByteBuffer) texNav.nativeData.rewind();
        IPixelFormat fmt = texNav.getPixelFormat();
        Dimension mipSize = ITexture.computeMipMapSize(texNav.width, texNav.height, mip);
        switch (fmt) {
            case D3DFMT_DXT1:
            case D3DFMT_DXT3:
            case D3DFMT_DXT5:
            case D3DFMT_L8:
            case D3DFMT_A8R8G8B8:
            case D3DFMT_X8R8G8B8:
                fmt.decodeImage(dst, mipSize, sliceMipBuffer(bb, mip));
                break;
                
            case PALETTE4_RGBA8_OES:
            case PALETTE4_RGB8_OES:
                //TODO
                break;

            case PALETTE8_RGBA8_OES:
            case PALETTE8_RGB8_OES:
                byte[][] pal = new byte[256][4];
                for (byte[] c : pal) {
                    c[0] = bb.get();
                    c[1] = bb.get();
                    c[2] = bb.get();
                    c[3] = bb.get();
                }
                bb = sliceMipBuffer(bb, mip);
                for (int y = 0; y < dst.getHeight(); y++) {
                    for (int x = 0; x < dst.getWidth(); x++) {
                        int id = bb.get() & 0xff;
                        dst.setRGBA(x, y, pal[id]);
                    }
                }
                break;
        }
    }
    
    private static ByteBuffer sliceMipBuffer(ByteBuffer nativeData, int mipmapLevel) {
        int startPos = nativeData.position();
        int rasterSize;
        for (int i = 0; i < mipmapLevel; i++) {
            rasterSize = nativeData.getInt();
            startPos += rasterSize + 4;
            nativeData.position(startPos);
        }
        rasterSize = nativeData.getInt();
        nativeData.limit(nativeData.position() + rasterSize);
        ByteBuffer bb = nativeData.slice();
        bb.rewind();
        bb.order(ByteOrder.LITTLE_ENDIAN);
        nativeData.limit(nativeData.capacity());
        return bb;
    }
    
}
