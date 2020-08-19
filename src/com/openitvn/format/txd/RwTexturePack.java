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

import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.resource.ITexturePack;
import com.openitvn.engine.renderware.RpSection;
import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.maintain.DumpEntry;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Thinh Pham
 */
public class RwTexturePack extends ITexturePack {
    
    @Override
    public void decode(DataStream ds) {
        RpTextureDictionary texDic = RpSection.loadRoot(ds, RpTextureDictionary.class);
        for (RpTextureNative texData : texDic.textures) {
            String texName = texData.getMapperName();
            RwTexture tex = new RwTexture(texName, texData);
            textures.add(tex);
        }
    }
    
    @Override
    public byte[] encode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Collection<DumpEntry> dump(DataStream ds) {
        return new ArrayList();
    }
}
