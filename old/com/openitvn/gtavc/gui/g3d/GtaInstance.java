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

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.openitvn.gtavc.core.item.ItemINST;

/**
 *
 * @author Thinh Pham
 */
public class GtaInstance {
    
    // gta data
    public final ItemINST define;
    public final GtaModel gModel;
    
    // libgdx data
    public final ModelInstance inst;
    
    public GtaInstance(ItemINST def, GtaModel mod) {
        this.define = def;
        this.gModel = mod;
        this.inst = new ModelInstance(mod.getModel());
        
        // compute transform
        Vector3 pos = def.pos;
        Vector3 scl = def.scl;
        Quaternion rot = def.rot;
        inst.transform.translate(pos.x, pos.z, -pos.y);
        inst.transform.rotateRad(rot.x, rot.z, -rot.y, -2*(float)Math.acos(rot.w));
        inst.transform.scale(scl.x, scl.y, scl.z);
    }
    
    public GtaInstance(Matrix4 transform, GtaModel mod) {
        this.define = null;
        this.gModel = mod;
        this.inst = new ModelInstance(mod.getModel());
        inst.transform.set(transform);
    }
}
