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
package com.openitvn.format.dff;

import com.openitvn.unicore.world.resource.IMaterial;
import com.openitvn.engine.renderware.RpMaterial;
import com.openitvn.engine.renderware.RpTextureNative;

/**
 *
 * @author Thinh Pham
 */
public class RwMaterial extends IMaterial {
    
    public RwMaterial(String matName, RpMaterial matData) {
        super(matName);
        // precomputed alpha
        color.a = matData.color.a / 255f;
        // enable alpha test when needed
        if (matData.isMasked() || color.a < 1) {
            alphaBlend = true;
            cullFace = false;
            alphaTest = color.a - 0.01f;
        }
        // shadingn factors
        ambientFactor = matData.ambient;
        diffuseFactor = matData.diffuse;
        specularFactor = matData.specular;
        // texture or color
        if (!matData.textured) {
            color.b = matData.color.b / 255f;
            color.g = matData.color.g / 255f;
            color.r = matData.color.r / 255f;
        }
    }
    
    public void bindTextureData(RpTextureNative texData) {
        if (texData.hasAlpha) {
            alphaBlend = true;
            cullFace = false;
        }
        diffuseTexture = texData.getMapperName();
    }
}
