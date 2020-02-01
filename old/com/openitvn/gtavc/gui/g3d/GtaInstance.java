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
import com.openitvn.gtavc.core.item.INSTEntry;

/**
 *
 * @author Thinh Pham
 */
public class GtaInstance {
    
    // gta data
    public final INSTEntry define;
    public final GtaModel gModel;
    
    // libgdx data
    public final ModelInstance inst;
    
    public GtaInstance(INSTEntry def, GtaModel mod) {
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
    
    public Vector3 getFBXPosition() {
        // can be extracted from instance.transform.getTranslation()
        // the order of vector3 components are: (pos.x, -pos.z, pos.y)
        if (define == null) {
            return inst.transform.getTranslation(new Vector3());
        } else {
            Vector3 p = define.pos;
            return new Vector3(p.x, p.z, -p.y);
        }
    }
    
    public Vector3 getFBXRotation() {
        // can be extracted from instance.transform.getRotation()
        // the order of quaternion components are: (-rot.x, rot.z, -rot.y, rot.w)
        if (define == null) {
            Quaternion q = inst.transform.getRotation(new Quaternion());
            return toEulerAngles(-q.x, q.z, -q.y, q.w);
        } else {
            return toEulerAngles(define.rot);
        }
    }
    
    public Vector3 getScale() {
        return define.scl;
    }
    
    private static Vector3 toEulerAngles(float x, float y, float z, float w) {
        // https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
        // https://ipfs.io/ipfs/QmXoypizjW3WknFiJnKLwHCnL72vedxjQkDDP1mXWo6uco/wiki/Conversion_between_quaternions_and_Euler_angles.html
        Vector3 a = new Vector3();
        double ysqr = y * y;
	double t0 = -2.0f * (ysqr + z * z) + 1.0f;
	double t1 = +2.0f * (x * y - w * z);
	double t2 = -2.0f * (x * z + w * y);
	double t3 = +2.0f * (y * z - w * x);
	double t4 = -2.0f * (x * x + ysqr) + 1.0f;
	t2 = t2 > 1.0f ? 1.0f : t2;
	t2 = t2 < -1.0f ? -1.0f : t2;
        a.x = (float) Math.toDegrees(Math.atan2(t3, t4)); // roll
	a.y = (float) Math.toDegrees(Math.asin(t2)); // pitch
	a.z = (float) Math.toDegrees(Math.atan2(t1, t0)); // yaw
        return a;
    }
    
    private static Vector3 toEulerAngles(Quaternion q) {
        return toEulerAngles(q.x, q.y, q.z, q.w);
    }
}
