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
package com.openitvn.gtavc.plugin.export.fbx;

import com.badlogic.gdx.math.Vector3;
import com.openitvn.gtavc.core.GtaCollision;

/**
 *
 * @author Thinh Pham
 */
public class FbxCollision {
    
    public final String modName;
    public final GtaCollision gCol;
    public final Vector3 rPos;
    public final Vector3 rRot;
    public final Vector3 rScl;
    
    public FbxCollision(String modName, GtaCollision gCol, Vector3 pos, Vector3 rot, Vector3 scl) {
        this.modName = modName;
        this.gCol = gCol;
        this.rPos = pos;
        this.rRot = rot;
        this.rScl = scl;
    }
    
    public String getStringVertices() {
        StringBuilder sb = new StringBuilder();
        for (Vector3 v : gCol.vertices) {
            sb.append(v.x).append(",")
                    .append(v.y).append(",")
                    .append(v.z).append(",");
        }
        return sb.deleteCharAt(sb.length()-1).toString();
    }
    
    public String getStringIndices() {
        StringBuilder sb = new StringBuilder();
        for (Vector3 tri : gCol.faces) {
            sb.append((int)tri.x).append(",")
                    .append((int)tri.y).append(",")
                    .append(~(int)tri.z).append(",");
        }
        return sb.deleteCharAt(sb.length()-1).toString();
    }
}
