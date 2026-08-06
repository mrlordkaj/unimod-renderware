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
import com.openitvn.unicore.plugin.gta.item.ItemINST;
import com.openitvn.unicore.plugin.gta.item.ItemTOBJ;

/**
 *
 * @author Thinh Pham
 */
class GtaInstance
{
    final GtaModel mod;
    final ModelInstance inst;
    boolean bVisible = true;
    
    GtaInstance(GtaModel mod) {
        this.mod = mod;
        this.inst = new ModelInstance(mod.getModel());
    }
    
    void setINST(ItemINST e) {
        inst.transform.translate(e.posX, e.posZ, -e.posY);
        inst.transform.rotateRad(e.rotX, e.rotZ, -e.rotY, -2*(float)Math.acos(e.rotW));
        inst.transform.scale(e.sclX, e.sclZ, e.sclY);
    }
    
    void updateVisibility(boolean bDistanceMode, int time) {
        // By distance
        float dd = mod.getDrawDistance();
        bVisible = bDistanceMode ? dd > 300 : dd <= 300;
        // By time
        if (bVisible && mod.objs instanceof ItemTOBJ) {
            ItemTOBJ tobj = (ItemTOBJ)mod.objs;
            // Night object
            bVisible = (time >= tobj.timeOn || time < tobj.timeOff);
            if (tobj.timeOn < tobj.timeOff) {
                System.err.println(tobj.modId + " " + tobj.modName + " " + tobj.timeOn + " " + tobj.timeOff);
            }
        }
    }
}
