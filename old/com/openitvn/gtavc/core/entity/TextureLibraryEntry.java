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

package com.openitvn.gtavc.core.entity;

import com.openitvn.gtavc.core.RwTextureHelper;
import com.openitvn.engine.renderware.RpTextureNative;
import java.awt.image.BufferedImage;

/**
 *
 * @author Thinh Pham
 */
public class TextureLibraryEntry {
    
    private final int index;
    private final RpTextureNative texNav;
    
    public TextureLibraryEntry(int index, RpTextureNative texData) {
        this.index = index;
        this.texNav = texData;
    }
    
    public int getIndex() {
        return index;
    }
    
    public String getTextureName() {
        return texNav.textureName;
    }
    
    public String getMaskName() {
        return texNav.maskName;
    }
    
    public String getSize() {
        return texNav.width + " x " + texNav.height;
    }
    
    public String getColorDepth() {
        return texNav.colorDepth + " bit";
    }
    
    public String hasAlpha() {
        return texNav.hasAlpha ? "Yes" : "No";
    }
    
    public Integer getMipmapCount() {
        return (int) texNav.mipCount;
    }
    
    public String getCompression() {
        return texNav.getCompressionName();
    }
    
    public BufferedImage getBufferedImage(int mip) {
        return RwTextureHelper.toBufferedImage(texNav, mip);
    }
}
