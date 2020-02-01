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

package com.openitvn.gtavc.gui.g3d;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.gtavc.core.RwTextureHelper;
import com.openitvn.unicore.raster.RasterPixmap;
import com.openitvn.unicore.raster.TextureHelper;
import java.awt.Dimension;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Thinh Pham
 */
public class GtaTexture {
    
    public final String texName;
    public final RpTextureNative rTex;
    private final Texture tex;
    private final ArrayList<GtaModel> gMods = new ArrayList<>();
    
    public GtaTexture(String texName, RpTextureNative rTex, GtaModel gMod) {
        this.texName = texName;
        this.rTex = rTex;
        this.tex = createTexture(rTex, 0);
        this.gMods.add(gMod);
    }
    
    public void removeHolder(GtaModel gMod) {
        gMods.remove(gMod);
        if (gMods.isEmpty()) {
            tex.dispose();
            GtaTextureManager.removeTexture(this);
        }
    }
    
    public Texture getTexture(GtaModel gMod) {
        if (!gMods.contains(gMod))
            gMods.add(gMod);
        return tex;
    }
    
    private static Texture createTexture(RpTextureNative rTex, int mip) {
        if (rTex == null) {
            return new Texture(0, 0, Pixmap.Format.RGBA8888);
        } else {
            Dimension size = TextureHelper.calcMipMapSize(rTex.width, rTex.height, mip);
            RasterPixmap img = new RasterPixmap(size.width, size.height);
            RwTextureHelper.decodeTexture(img, rTex, mip);
            Texture tex = new Texture(img, true);
            // set wrap
            tex.bind();
            GL11.glTexParameterf(tex.glTarget, GL20.GL_TEXTURE_WRAP_S, rTex.getUWrap());
            GL11.glTexParameterf(tex.glTarget, GL20.GL_TEXTURE_WRAP_T, rTex.getVWrap());
            // set filter
            tex.setFilter(Texture.TextureFilter.MipMap, Texture.TextureFilter.Nearest);
            return tex;
        }
    }
}
