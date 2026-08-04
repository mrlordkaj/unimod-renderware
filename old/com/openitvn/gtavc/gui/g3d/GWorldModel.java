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

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.openitvn.unicore.world.IGrid;
import com.openitvn.unicore.world.IWorldCoord;
import java.io.IOException;

/**
 *
 * @author Thinh Pham
 */
public class GWorldModel extends GWorldBase {
    
    private static GWorldModel instance;
    
    public static GWorldModel getInstance() {
        if (instance == null)
            instance = new GWorldModel();
        return instance;
    }
    
    private IGrid grid;
    
    @Override
    void init() {
        super.init();
        grid = new IGrid();
        grid.rebuild(IWorldCoord.Zup);
    }
    
    @Override
    void dispose() {
        super.dispose();
        grid.dispose();
    }
    
    @Override
    void draw(ModelBatch mb, Environment env) {
        super.draw(mb, env);
        grid.draw(mb);
    }
    
    public void setModel(String modName, String txdName) throws IOException {
        // Clean current stuff
        instances.clear();
        for (GtaModel mod : models.values()) {
            mod.dispose();
        }
        models.clear();
        // Set new model
        GtaModel mod = new GtaModel(modName, txdName, GtaModel.MeshType.AllMesh, this);
        models.put(0, mod);
        instances.add(new GtaInstance(new Matrix4(), mod));
    }
}
