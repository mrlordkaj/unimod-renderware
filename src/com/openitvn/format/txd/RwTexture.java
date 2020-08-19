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

import com.openitvn.unicore.world.resource.ITexture;
import com.openitvn.engine.renderware.RpTextureNative;
import java.nio.ByteBuffer;

/**
 *
 * @author Thinh Pham
 */
public class RwTexture extends ITexture {
    
    private final RpTextureNative texture;
    
    public RwTexture(String name, RpTextureNative texture) {
        super(name);
        this.texture = texture;
        width = texture.width;
        height = texture.height;
        numFaces = 1;
        numMips = texture.mipCount;
        uwrap = texture.getUWrap();
        vwrap = texture.getVWrap();
        format = texture.getPixelFormat();
        // read palette
        ByteBuffer bb = texture.nativeData;
        bb.rewind();
        switch (texture.getPixelFormat()) {
            case PALETTE4_RGBA8_OES:
            case PALETTE4_RGB8_OES:
                // TODO: build palette
                int skip = 32 * 2;
                bb.position(skip);
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
        imageBuffers = new byte[1][texture.mipCount][];
        for (int i = 0; i < texture.mipCount; i++) {
            int size = bb.getInt();
            imageBuffers[0][i] = new byte[size];
            bb.get(imageBuffers[0][i]);
        }
    }
    
    public RpTextureNative getTextureData() {
        return texture;
    }
    
    @Override
    public byte[] compileTexture(ITexture src) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public boolean isMipMapUsed() {
        return true;
    }
}
