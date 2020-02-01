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
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/Texture_Dictionary_(RW_Section)
 */
public class RpTextureDictionary extends RpSection {
    
    public ArrayList<RpTextureNative> textures;
    
    public RpTextureDictionary(int size, int libId, RpSection parent, DataStream ds) {
        super(RpType.TextureDictionary, size, libId, parent, ds);
        textures = super.getChildren(RpTextureNative.class);
    }
    
    public RpTextureNative findTexture(String name) {
        name = name.toLowerCase();
        for (RpTextureNative tex : textures) {
            if (name.equals(tex.textureName.toLowerCase()))
                return tex;
        }
        return null;
    }
}
