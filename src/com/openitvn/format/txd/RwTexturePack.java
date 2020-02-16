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
    public void fromSource(ITexturePack source) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void decode(DataStream ds) {
        RpSection grand = RpSection.fromData(ds, null);
        for (RpTextureNative tex : grand.getChildren(RpTextureNative.class))
            addTexture(new RwTexture(tex.textureName, tex));
    }
    
    @Override
    public byte[] encode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Collection<DumpEntry> dump(DataStream ds) {
        return new ArrayList();
    }

    @Override
    public byte[] unwrap() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEditable() {
        return false;
    }
}
