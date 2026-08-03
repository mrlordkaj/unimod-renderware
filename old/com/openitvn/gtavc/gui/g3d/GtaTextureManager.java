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

import com.openitvn.engine.renderware.RpSection;
import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.plugin.gta.ResourceModel;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class GtaTextureManager {
    
    private static final HashMap<String, RpTextureDictionary> TEXDIC_MAP = new HashMap<>();
    
    // TODO: Cleanup unnecessary dictionaries when unload scene part
    public static RpTextureDictionary getTexDic(String txdName) {
        RpTextureDictionary txd = TEXDIC_MAP.get(txdName);
        if (txd == null) {
            ResourceModel res = ResourceModel.getInstance();
            try (EntryStream ds = res.getEntryStream(txdName, "txd")) {
                txd = RpSection.loadRoot(ds, RpTextureDictionary.class);
                TEXDIC_MAP.put(txdName, txd);
            } catch (IOException ex) {
                System.err.println("TXD not found: " + txdName);
            }
        }
        return txd;
    }
}
