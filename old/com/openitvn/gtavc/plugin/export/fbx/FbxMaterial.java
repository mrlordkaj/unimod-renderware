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

package com.openitvn.gtavc.plugin.export.fbx;

import com.badlogic.gdx.graphics.g3d.Material;
import com.openitvn.engine.renderware.RpMaterial;

/**
 *
 * @author Thinh Pham
 */
public class FbxMaterial {
    
    public String matName;
    public String texName;
    public final String texPath;
    public final boolean isAlpha;
    public String maskName;
    public final Material mat;
    public final RpMaterial rMat;
    
    public FbxMaterial(String texName, String texPath, int sameCount, Material mat, RpMaterial rMat) {
        this.texName = generateName(texName, sameCount);
        this.matName = generateName(texName+"m", sameCount);
        this.texPath = texPath;
        this.isAlpha = rMat.isMasked();
        if (isAlpha) {
            this.maskName = texName+"a";
            if (sameCount > 0)
                this.maskName += "_ncl1_" + sameCount;
        }
        this.mat = mat;
        this.rMat = rMat;
    }
    
    private String generateName(String name, int sameCount) {
        if (sameCount > 0) {
            if (name.endsWith("_untex"))
                name += sameCount;
            else
                name += "_ncl1_" + sameCount;
        }
        return name;
    }
}
