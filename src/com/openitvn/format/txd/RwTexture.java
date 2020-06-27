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

import com.openitvn.unicore.world.resource.ICubeMap;
import com.openitvn.unicore.world.resource.IPixelFormat;
import com.openitvn.unicore.world.resource.ITexture;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.unicore.world.resource.BufferedRaster;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 *
 * @author Thinh Pham
 */
public class RwTexture extends ITexture {
    
    private final RpTextureNative texture;
    private final byte[][] imageBuffers;
    private byte[][] palette;
    
    public RwTexture(String name, RpTextureNative texture) {
        super(name);
        this.texture = texture;
        ByteBuffer bb = texture.nativeData;
        bb.rewind();
        // read palette
        switch (texture.getPixelFormat()) {
            case PALETTE4_RGBA8_OES:
            case PALETTE4_RGB8_OES:
                bb.position(bb.position() + 32 * 2);
                break;
                
            case PALETTE8_RGBA8_OES:
            case PALETTE8_RGB8_OES:
                palette = new byte[256][4];
                for (byte[] c : palette) {
                    c[0] = bb.get();
                    c[1] = bb.get();
                    c[2] = bb.get();
                    c[3] = bb.get();
                }
                break;
        }
        // read image buffers
        imageBuffers = new byte[texture.mipCount][];
        for (int i = 0; i < texture.mipCount; i++) {
            int size = bb.getInt();
            imageBuffers[i] = new byte[size];
            bb.get(imageBuffers[i]);
        }
    }
    
    public RpTextureNative getTextureData() {
        return texture;
    }
    
    @Override
    public byte[] compileTexture(ITexture src) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public byte[][] getPalette() {
        return palette;
    }
    
    @Override
    public byte[] getImageBuffer(int faceId, int mipLevel) throws UnsupportedOperationException {
        return imageBuffers[mipLevel];
    }
    
    @Override
    public int getWidth() {
        return texture.width;
    }
    
    @Override
    public int getHeight() {
        return texture.height;
    }
    
    @Override
    public int getFaceCount() {
        return 1;
    }
    
    @Override
    public int getMipCount() {
        return texture.mipCount;
    }
    
    @Override
    public int getUWrap() {
        return texture.getUWrap();
    }

    @Override
    public int getVWrap() {
        return texture.getVWrap();
    }
    
    @Override
    public ICubeMap getCubeMapHeader() {
        return null;
    }
    
    @Override
    public IPixelFormat getPixelFormat() {
        return texture.getPixelFormat();
    }
    
    @Override
    public boolean isMipMapUsed() {
        return true;
    }
    
    @Deprecated
    public BufferedImage toBufferedImage(int mip) {
        Dimension imgSize = ITexture.computeMipMapSize(texture.width, texture.height, mip);
        BufferedRaster img = new BufferedRaster(imgSize.width, imgSize.height);
        RwTexture tex = new RwTexture(texture.textureName, texture);
        tex.decodeImage(img, 0, 0);
        return img;
    }
}
