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

import com.badlogic.gdx.math.Vector3;
import com.openitvn.engine.renderware.struct.RpFrame;
import com.openitvn.engine.renderware.RpGeometry;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
public class GtaWheelModel extends GtaModel {
    
    private final ArrayList<RpGeometry> rwGeometries = new ArrayList<>();
    private Vector3 scale;

    public GtaWheelModel() throws IOException {
        super("wheels", "misc", MeshType.AllMesh);
    }
    
    public void setWheel(GtaVehicleModel vehicleModel, String wheelName) {
        rwGeometries.clear();
        wheelName = wheelName + "_l0";
        scale = vehicleModel.getWheelScale();
        for (RpGeometry geo : rClump.geometries) {
            if (geo.frame.name.equals(wheelName)) {
                // if found matched wheel
                for (RpFrame wheelDummy : vehicleModel.rClump.frameList.frames) {
                    if (wheelDummy.name.startsWith("wheel_")) {
                        try {
                            RpGeometry newWheel = (RpGeometry)geo.clone();
                            newWheel.frame = wheelDummy;
                            rwGeometries.add(newWheel);
                        } catch (CloneNotSupportedException ex) { }
                    }
                }
                break;
            }
        }
        model = createModel(this, true, scale);
    }
    
    public Vector3 getScale() {
        return scale;
    }
    
    @Override
    public ArrayList<RpGeometry> getGeometries() {
        return rwGeometries;
    }
}
