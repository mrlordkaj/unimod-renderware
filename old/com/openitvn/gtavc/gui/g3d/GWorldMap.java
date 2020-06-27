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

import com.badlogic.gdx.math.Vector3;
import com.openitvn.gtavc.core.item.ItemINST;
import com.openitvn.gtavc.core.item.ItemOBJS;
import com.openitvn.gtavc.core.item.ItemTOBJ;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
public class GWorldMap extends GWorld {
    
    private final ArrayList<GtaInstance>
            norIns = new ArrayList<>(),
            lodIns = new ArrayList<>();
    
    private static GWorldMap instance;
    
    public static GWorldMap getInstance() {
        if (instance == null)
            instance = new GWorldMap();
        return instance;
    }
    
    private GWorldMap() { }
    
    @Override
    void init() {
        super.init();
        cam.far = 10000;
        camCtrl.scrollFactor = camCtrl.pinchZoomFactor = -.8f;
        camCtrl.translateUnits = 100;
    }
    
    void setViewportMode(ViewportMode mode) {
        switch (mode) {
            case MapNormal:
                instances = norIns;
                break;
                
            case MapDistance:
                instances = lodIns;
                break;
        }
    }
    
    void addOBJS(ItemOBJS objs) throws Exception {
        if (objs instanceof ItemTOBJ) {
            int sceneTime = 12;
            ItemTOBJ tobj = (ItemTOBJ) objs;
            if (tobj.timeOn < tobj.timeOff) { // same day
                if (tobj.timeOn > sceneTime) return;
            } else { // next day
                if (tobj.timeOff < sceneTime) return;
            }
//            return; // test: igrone all TOBJs
        }
//        if (!(objs instanceof TOBJEntry)) return; // test

        // add models
        GtaModel mod = new GtaModel(objs);
        models.put(objs.modelId, mod);
    }
    
    void removeOBJS(ItemOBJS e) {
        int modId = e.modelId;
        if (models.containsKey(modId)) {
            models.get(modId).dispose();
            models.remove(modId);
        }
    }
    
    public void addINST(ItemINST e) {
        int objId = e.modId;
        if (models.containsKey(objId)) {
            GtaModel mod = models.get(objId);
            GtaInstance inst = new GtaInstance(e, mod);
            if (mod.drawDistance < 300)
                norIns.add(inst);
            else if (mod.drawDistance < 3000)
                lodIns.add(inst);
            moveCameraTo(inst.inst.transform.getTranslation(new Vector3()));
        }
    }
    
    public void removeINST(ItemINST e) {
        for (GtaInstance inst : norIns) {
            if (inst.define.equals(e)) {
                norIns.remove(inst);
                return;
            }
        }
        for (GtaInstance inst : lodIns) {
            if (inst.define.equals(e)) {
                lodIns.remove(inst);
                return;
            }
        }
    }
    
    public void moveCameraTo(Vector3 pos) {
        camCtrl.target.set(pos);
        cam.position.set(pos).add(20, 20, 20);
        cam.lookAt(camCtrl.target);
        cam.up.set(0, 1, 0);
    }
}
