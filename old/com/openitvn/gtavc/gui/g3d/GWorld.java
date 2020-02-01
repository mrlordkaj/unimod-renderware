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

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.openitvn.engine.renderware.RpMaterial;
import com.openitvn.engine.renderware.RwGeometry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public abstract class GWorld {
    
    final HashMap<Integer, GtaModel> models = new HashMap<>();
    PerspectiveCamera cam;
    CameraInputController camCtrl;
    
    ArrayList<GtaInstance> instances = new ArrayList<>();
    
    void init() {
        cam = new PerspectiveCamera();
        cam.fieldOfView = 45;
        cam.near = 0.2f;
        cam.far = 300;
        cam.position.set(ViewportApp.GRID_WIDTH,
                Math.max(ViewportApp.GRID_WIDTH, ViewportApp.GRID_HEIGHT),
                ViewportApp.GRID_HEIGHT);
        cam.lookAt(0, 0, 0);
        camCtrl = new CameraInputController(cam);
    }
    
    void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
    }
    
    void draw(ModelBatch mb, Environment env) {
        cam.update();
        camCtrl.update();
        mb.begin(cam);
        for (GtaInstance ginst : instances)
            mb.render(ginst.inst, env);
        mb.end();
    }
    
    Collection<GtaTexture> exportTextures(boolean skipUnused) {
        if (skipUnused) {
            ArrayList<String> nameMap = new ArrayList<>();
            for (GtaInstance gInst : instances) {
                String texDic = gInst.gModel.txdName;
                for (RwGeometry rGeo : gInst.gModel.getGeometries()) {
                    for (RpMaterial rMat : rGeo.materials) {
                        if (rMat.textured) {
                            String name = GtaTextureManager.getMapperName(texDic, rMat.getTextureName());
                            if (!nameMap.contains(name))
                                nameMap.add(name);
                        }
                    }
                }
            }
            return GtaTextureManager.getTexturesByMapperNames(nameMap);
        } else {
            return GtaTextureManager.getAllTextures();
        }
    }
}
