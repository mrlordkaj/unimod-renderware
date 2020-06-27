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
import com.openitvn.engine.renderware.RpGeometry;
import com.openitvn.gtavc.core.item.CARSEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class GtaVehicleModel extends GtaModel {
    public static HashMap<Integer, String> wheelLib = new HashMap<>();
    
    private ViewportMode mode;
    private final Vector3 wheelScale;
    
    public GtaVehicleModel(CARSEntry e, ViewportMode mod) throws IOException {
        super(e.modName, e.txdName, MeshType.AllMesh);
        this.mode = mod;
        float scale = e.wheelScale;
        this.wheelScale = new Vector3(scale, scale, scale);
    }
    
    public void changeViewMode(ViewportMode viewMode) {
        switch(viewMode) {
            case VehicleNormal:
            case VehicleDamaged:
            case VehicleDistance:
                this.mode = viewMode;
                model = createModel(this, true, new Vector3(1, 1, 1));
                break;
        }
    }
    
    @Override
    public ArrayList<RpGeometry> getGeometries() {
        ArrayList<RpGeometry> rs = new ArrayList<>();
        for (RpGeometry geo : rClump.geometries) {
            String geoName = geo.frame.name;
            switch (mode) {
                case VehicleNormal:
                    if (!geoName.endsWith("_vlo") && !geoName.endsWith("_dam"))
                        rs.add(geo);
                    break;
                
                case VehicleDamaged:
                    if (!geoName.endsWith("_vlo") && !geoName.endsWith("_ok"))
                        rs.add(geo);
                    break;
                    
                case VehicleDistance:
                    if (geoName.endsWith("_vlo"))
                        rs.add(geo);
                    break;
            }
        }
        return rs;
    }
    
    public Vector3 getWheelScale() {
        return wheelScale;
    }
}
