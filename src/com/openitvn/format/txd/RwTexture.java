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
    
    public final RpTextureNative textureNative;
    
    public RwTexture(String name, RpTextureNative texNav) {
        super(name);
        textureNative = texNav;
        width = texNav.width;
        height = texNav.height;
        numFaces = 1;
        numMips = texNav.mipCount;
        uwrap = texNav.getUWrap();
        vwrap = texNav.getVWrap();
        format = texNav.getPixelFormat();
        // Read palette
        ByteBuffer bb = texNav.getNativeData();
        bb.rewind();
        switch (format) {
            case PALETTE4_RGBA8_OES:
            case PALETTE4_RGB8_OES:
                palette = new byte[16][4];
                for (byte[] c : palette) {
                    bb.get(c);
                }
                break;
                
            case PALETTE8_RGBA8_OES:
            case PALETTE8_RGB8_OES:
                palette = new byte[256][4];
                for (byte[] c : palette) {
                    bb.get(c);
                }
                break;
        }
        // Read image buffers
        imageBuffers = new byte[1][texNav.mipCount][];
        for (int i = 0; i < texNav.mipCount; i++) {
            int size = bb.getInt();
            imageBuffers[0][i] = new byte[size];
            bb.get(imageBuffers[0][i]);
        }
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
