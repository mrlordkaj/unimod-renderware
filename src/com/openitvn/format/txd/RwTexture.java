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
package com.openitvn.format.txd;

import com.openitvn.unicore.raster.ICubeMapHeader;
import com.openitvn.unicore.raster.IPixelFormat;
import com.openitvn.unicore.raster.IRaster;
import com.openitvn.unicore.world.resource.ITexture;
import com.openitvn.unicore.raster.TextureHelper;
import com.openitvn.engine.renderware.RpTextureNative;
import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public class RwTexture extends ITexture {
    
    private final RpTextureNative texture;
    
    public RwTexture(String name, RpTextureNative tex) {
        super(name);
        super.setUWrap(tex.getUWrap());
        super.setVWrap(tex.getVWrap());
        this.texture = tex;
    }

    @Override public int getWidth() { return texture.width; }
    @Override public int getHeight() { return texture.height; }
    @Override public int getFaceCount() { return 1; }
    @Override public int getMipCount() { return texture.mipCount; }
    @Override public ICubeMapHeader getCubeMapHeader() { return null; }
    @Override public IPixelFormat getPixelFormat() { return texture.getPixelFormat(); }
    @Override public boolean isMipMapUsed() { return true; }
    
    @Override
    public byte[] getImageBuffer(int face, int mip) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] compilePatch(ITexture customTexture) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void decodeImage(IRaster dst, int face, int mip) throws UnsupportedOperationException {
        ByteBuffer bb = (ByteBuffer) texture.nativeData.rewind();
        IPixelFormat fmt = texture.getPixelFormat();
        Dimension mipSize = TextureHelper.calcMipMapSize(texture.width, texture.height, mip);
        switch (fmt) {
            case D3DFMT_DXT1:
            case D3DFMT_DXT3:
            case D3DFMT_DXT5:
            case D3DFMT_L8:
            case D3DFMT_A8R8G8B8:
            case D3DFMT_X8R8G8B8:
                TextureHelper.decodeImage(dst, mipSize, fmt, sliceMipBuffer(bb, mip));
                break;
                
            case PALETTE4_RGBA8_OES:
            case PALETTE4_RGB8_OES:
                // TODO
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
    
    private static ByteBuffer sliceMipBuffer(ByteBuffer bb, int mip) {
        int pos = bb.position();
        int size;
        for (int i = 0; i < mip; i++) {
            size = bb.getInt();
            pos += size + 4;
            bb.position(pos);
        }
        size = bb.getInt();
        bb.limit(bb.position() + size);
        ByteBuffer rs = bb.slice().order(ByteOrder.LITTLE_ENDIAN);
        bb.limit(bb.capacity());
        return rs;
    }
}
