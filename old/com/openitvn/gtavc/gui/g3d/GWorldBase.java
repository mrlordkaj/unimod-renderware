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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.openitvn.engine.renderware.RpSection;
import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.plugin.gta.ResourceModel;
import com.openitvn.unicore.world.resource.ResourceManager;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public abstract class GWorldBase
{
    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 10;
    
    final ResourceManager resource = new ResourceManager();
    final HashMap<String, RpTextureDictionary> texDic = new HashMap<>();
    
    protected ModelBatch mb;
    protected Environment env;
    protected PerspectiveCamera cam;
    protected CameraInputController camCtrl;
    
    void init() {
        mb = new ModelBatch();
        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
        cam = new PerspectiveCamera();
        cam.fieldOfView = 45;
        cam.near = 0.2f;
        cam.far = 300;
        cam.position.set(GRID_WIDTH,
                Math.max(GRID_WIDTH, GRID_HEIGHT),
                GRID_HEIGHT);
        cam.lookAt(0, 0, 0);
        camCtrl = new CameraInputController(cam);
    }
    
    void dispose() {
        mb.dispose();
    }
    
    void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
    }
    
    final void update() {
        cam.update();
        camCtrl.update();
        mb.begin(cam);
        draw();
        mb.end();
    }
    
    protected abstract void draw();
    
    // TODO: Cleanup unnecessary dictionaries when unload scene part
    public RpTextureDictionary getTexDic(String txdName) {
        RpTextureDictionary txd = texDic.get(txdName);
        if (txd == null) {
            ResourceModel res = ResourceModel.getInstance();
            try (EntryStream ds = res.getEntryStream(txdName, "txd")) {
                txd = RpSection.loadRoot(ds, RpTextureDictionary.class);
                texDic.put(txdName, txd);
            } catch (IOException ex) {
                System.err.println("TXD not found: " + txdName);
            }
        }
        return txd;
    }
}
