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

import com.openitvn.unicore.plugin.gta.item.ItemINST;
import com.openitvn.unicore.plugin.gta.item.ItemOBJS;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class GtaCanvasWorld extends GtaCanvas
{
    private final ViewportApp gdxApp;
    private final HashMap<Integer, GtaModel> modMap = new HashMap<>();
    private final HashMap<ItemINST, GtaInstance> instMap = new HashMap<>();
    private int time = 12;
    
    GtaCanvasWorld(ViewportApp gdxApp) {
        this.gdxApp = gdxApp;
    }
    
    @Override
    void init() {
        super.init();
        cam.far = 10000;
        camCtrl.scrollFactor = camCtrl.pinchZoomFactor = -.8f;
        camCtrl.translateUnits = 100;
    }
    
    @Override
    void dispose() {
        instMap.clear();
        for (GtaModel mod : modMap.values()) {
            mod.dispose();
        }
        modMap.clear();
        super.dispose();
    }
    
    @Override
    protected void draw() {
        for (GtaInstance inst : instMap.values()) {
            if (inst.bVisible) {
                mb.render(inst.inst, env);
            }
        }
    }
    
    public void setTime(int time) {
        this.time = time;
        updateVisibility();
    }
    
    void updateVisibility() {
        boolean bDistance = gdxApp.getViewpotMode() == ViewportMode.DistanceWorld;
        for (GtaInstance inst : instMap.values()) {
            inst.updateVisibility(bDistance, time);
        }
    }
    
    public void addOBJS(ItemOBJS objs) {
        GtaModel mod = new GtaModel(objs, this);
        modMap.put(objs.modId, mod);
    }
    
    public void removeOBJS(ItemOBJS e) {
        if (modMap.containsKey(e.modId)) {
            modMap.get(e.modId).dispose();
            modMap.remove(e.modId);
        }
    }
    
    public void addINST(ItemINST e) {
        if (instMap.containsKey(e) || !modMap.containsKey(e.modId)) {
            return;
        }
        GtaModel mod = modMap.get(e.modId);
        GtaInstance inst = new GtaInstance(mod);
        inst.setINST(e);
        boolean bDistance = gdxApp.getViewpotMode() == ViewportMode.DistanceWorld;
        inst.updateVisibility(bDistance, time);
        instMap.put(e, inst);
    }
    
    public void removeINST(ItemINST e) {
        if (!instMap.containsKey(e)) {
            return;
        }
        instMap.remove(e);
    }
    
    public void moveCameraTo(float x, float y, float z) {
        camCtrl.target.set(x, y, z);
        cam.position.set(x + 20, y + 20, z + 20);
        cam.lookAt(camCtrl.target);
        cam.up.set(0, 1, 0);
    }
}
