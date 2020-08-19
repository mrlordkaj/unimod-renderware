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

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/Texture_(RW_Section)
 */

public class RpTexture extends RpSection {
    
    // texture filtering
    public static final byte FILTERNAFILTERMODE = 0; //filtering is disabled
    public static final byte FILTERNEAREST = 1; //Point sampled
    public static final byte FILTERLINEAR = 2; //Bilinear
    public static final byte FILTERMIPNEAREST = 3; //Point sampled per pixel mip map
    public static final byte FILTERMIPLINEAR = 4; //Bilinear per pixel mipmap)
    public static final byte FILTERLINEARMIPNEAREST = 5; //MipMap interp point sampled
    public static final byte FILTERLINEARMIPLINEAR = 6; //Trilinear
    // texture addressing
    public static final byte TEXTUREADDRESSNATEXTUREADDRESS = 0; //no tiling
    public static final byte TEXTUREADDRESSWRAP = 1; //tile in U or V direction
    public static final byte TEXTUREADDRESSMIRROR = 2; //mirror in U or V direction
    public static final byte TEXTUREADDRESSCLAMP = 3;
    public static final byte TEXTUREADDRESSBORDER = 4;
    
    // data
    public byte filtering;
    public boolean hasMipMap;
    public String textureName;
    public String maskName;
    
    public RpTexture(int size, int libId, RpSection parent, DataStream ds) {
        super(RpType.Texture, size, libId, parent, ds);
        textureName = bufferToString(children.get(1).data);
        maskName = bufferToString(children.get(2).data);
    }
    
    private static String bufferToString(ByteBuffer bb) {
        StringBuilder sb = new StringBuilder();
        byte c;
        while (bb.hasRemaining() && (c = bb.get()) != 0) {
            sb.append((char)c);
        }
        return sb.toString();
    }
}
