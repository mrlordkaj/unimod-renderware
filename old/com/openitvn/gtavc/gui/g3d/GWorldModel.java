/*
 * Copyright (C) 2019 Thinh Pham
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

import com.openitvn.unicore.world.IGrid;
import com.openitvn.unicore.world.IWorldCoord;
import java.io.IOException;

/**
 *
 * @author Thinh Pham
 */
public class GWorldModel extends GWorldBase
{
    private IGrid grid;
    private GtaInstance inst;
    
    @Override
    void init() {
        super.init();
        grid = new IGrid();
        grid.rebuild(IWorldCoord.Zup);
    }
    
    @Override
    void dispose() {
        if (inst != null) {
            inst.mod.dispose();
            inst = null;
        }
        super.dispose();
        grid.dispose();
        
    }
    
    @Override
    protected void draw() {
        grid.draw(mb);
        if (inst != null) {
            mb.render(inst.inst, env);
        }
    }
    
    public void openModel(String modName, String txdName) throws IOException {
        // Clean current stuff
        if (inst != null) {
            inst.mod.dispose();
        }
        // Set new model
        GtaModel mod = new GtaModel(modName, txdName, GtaModel.MeshType.Multiple, this);
        inst = new GtaInstance(mod);
    }
}
