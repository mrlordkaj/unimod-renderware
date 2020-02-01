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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.openitvn.gtavc.core.RwLoader;
import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.gtavc.core.GtaAssetModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author Thinh Pham
 */
public class GtaTextureManager {
    
    private static final HashMap<String, RpTextureDictionary> TEXDIC_MAP = new HashMap<>();
    private static final HashMap<String, GtaTexture> TEXTURE_MAP = new HashMap<>();
    
    public static void removeTexture(GtaTexture tex) {
        for (Entry<String, GtaTexture> e : TEXTURE_MAP.entrySet()) {
            if (e.getValue().equals(tex)) {
                TEXTURE_MAP.remove(e.getKey());
                return;
            }
        }
    }
    
    public static Texture attach(String txdName, String texName, GtaModel gMod) {
        String mapperName = getMapperName(txdName, texName);
        GtaTexture gTex = TEXTURE_MAP.get(mapperName);
        if (gTex == null) {
            RpTextureDictionary rTexDic = getTexDic(txdName);
            if (rTexDic != null) {
                RpTextureNative rTex = rTexDic.findTexture(texName);
                gTex = new GtaTexture(texName, rTex, gMod);
                TEXTURE_MAP.put(mapperName, gTex);
            } else {
                return new Texture(0, 0, Pixmap.Format.RGBA8888);
            }
        }
        return gTex.getTexture(gMod);
    }
    
    public static void detach(String txdName, String texName, GtaModel gMod) {
        GtaTexture gTex = TEXTURE_MAP.get(getMapperName(txdName, texName));
        if (gTex != null)
            gTex.removeHolder(gMod);
    }
    
    public static Collection<GtaTexture> getAllTextures() {
        return TEXTURE_MAP.values();
    }
    
    public static Collection<GtaTexture> getTexturesByTexDicName(String txdName) {
        ArrayList<GtaTexture> rs = new ArrayList<>();
        RpTextureDictionary rTexDic = getTexDic(txdName);
        for (RpTextureNative rTex : rTexDic.textures) {
            String mapperName = rTex.getMapperName();
            GtaTexture gTex = TEXTURE_MAP.get(mapperName);
            if (gTex != null)
                rs.add(gTex);
        }
        return rs;
    }
    
    public static Collection<GtaTexture> getTexturesByTexDicNames(ArrayList<String> txdNames) {
        ArrayList<GtaTexture> rs = new ArrayList<>();
        for (String txdName : txdNames)
            rs.addAll(getTexturesByTexDicName(txdName));
        return rs;
    }
    
    public static Collection<GtaTexture> getTexturesByMapperNames(ArrayList<String> mapperNames) {
        ArrayList<GtaTexture> rs = new ArrayList<>();
        for (Entry<String, GtaTexture> e : TEXTURE_MAP.entrySet()) {
            if (mapperNames.contains(e.getKey()))
                rs.add(e.getValue());
        }
        return rs;
    }
    
    // TODO: cleanup unnecessary dictionaries when unload scene part
    public static RpTextureDictionary getTexDic(String txdName) {
        RpTextureDictionary rTexDic = TEXDIC_MAP.get(txdName);
        if (rTexDic == null) {
            try {
                byte[] data = GtaAssetModel.getInstance().extract(txdName + ".txd");
                rTexDic = (RpTextureDictionary) RwLoader.loadFromBuffer(data);
                TEXDIC_MAP.put(txdName, rTexDic);
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return rTexDic;
    }
    
    public static String getMapperName(String txdName, String texName) {
        RpTextureDictionary rTexDic = getTexDic(txdName);
        if (rTexDic != null) {
            RpTextureNative rTex = rTexDic.findTexture(texName);
            if (rTex != null)
                return rTex.getMapperName();
        }
        return txdName + "_" + texName;
    }
}
