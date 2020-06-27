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

import com.openitvn.engine.renderware.struct.RpColor;
import com.openitvn.unicore.data.DataStream;
import java.nio.ByteBuffer;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/RpMaterial
 */
public class RpMaterial extends RpSection {
    
    // data
    public RpColor color;
    public boolean textured;
    public float ambient;
    public float specular;
    public float diffuse;
    
    // reference
    private RpTexture texture;
    
    public RpMaterial(int size, int libId, RpSection parent, DataStream ds) {
        super(RpType.Material, size, libId, parent, ds);
        ByteBuffer bb = getStruct();
        int flags = bb.getInt();
        color = new RpColor(bb);
        int unused = bb.getInt();
        textured = bb.getInt() != 0;
        if (textured)
            texture = getFirstChild(RpTexture.class);
        if (version > 0x30400) {
            ambient = bb.getFloat();
            specular = bb.getFloat();
            diffuse = bb.getFloat();
        } // else set from geometry
        // NOTE: hos_man.dff, worker1.dff (III) have backface out
    }
    
    public boolean isMasked() {
        return textured && !texture.maskName.isEmpty();
    }
    
    public String getTextureName() {
        return textured ? texture.textureName : "";
    }
}
