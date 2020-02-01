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

package com.openitvn.gtavc.core.item;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import java.nio.ByteBuffer;

/**
 *
 * @author Thinh Pham
 */

/*
inst
# GTA III and Vice City
Id, ModelName, PosX, PosY, PosZ, ScaleX, ScaleY, ScaleZ, RotX, RotY, RotZ, RotW
# Vice City
Id, ModelName, Interior, PosX, PosY, PosZ, ScaleX, ScaleY, ScaleZ, RotX, RotY, RotZ, RotW
# San Andreas
Id, ModelName, Interior, PosX, PosY, PosZ, RotX, RotY, RotZ, RotW, LOD
end
*/
public class INSTEntry extends NULLEntry {
    
    public int modId;
    public String modelName;
    public int interior;
    public Vector3 pos;
    public Vector3 scl;
    public Quaternion rot = new Quaternion();
    public int lodId;
    
    public INSTEntry(String[] args, int groupId) {
        super(groupId);
        modId = Integer.parseInt(args[0]);
        modelName = args[1];
        switch (args.length) {
            case 12: //(III/VC)
                pos = new Vector3( Float.parseFloat(args[2]), Float.parseFloat(args[3]), Float.parseFloat(args[4]) );
                scl = new Vector3( Float.parseFloat(args[5]), Float.parseFloat(args[6]), Float.parseFloat(args[7]) );
                rot.x = Float.parseFloat(args[8]);
                rot.y = Float.parseFloat(args[9]);
                rot.z = Float.parseFloat(args[10]);
                rot.w = Float.parseFloat(args[11]);
                break;
                
            case 13: //(VC)
                interior = Integer.parseInt(args[2]);
                pos = new Vector3( Float.parseFloat(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]) );
                scl = new Vector3 ( Float.parseFloat(args[6]), Float.parseFloat(args[7]), Float.parseFloat(args[8]) );
                rot.x = Float.parseFloat(args[9]);
                rot.y = Float.parseFloat(args[10]);
                rot.z = Float.parseFloat(args[11]);
                rot.w = Float.parseFloat(args[12]);
                break;
                
            case 11: //(SA)
                interior = Integer.parseInt(args[2]);
                pos = new Vector3( Float.parseFloat(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]) );
                scl = new Vector3 ( 1, 1, 1 );
                rot.x = Float.parseFloat(args[6]);
                rot.y = Float.parseFloat(args[7]);
                rot.z = Float.parseFloat(args[8]);
                rot.w = Float.parseFloat(args[9]);
                lodId = Integer.parseInt(args[10]);
                break;
        }
    }
    
    // binary format, only SA
    public INSTEntry(ByteBuffer bb, int bbOffset, int groupId) {
        super(groupId);
        bb.position(bbOffset);
        pos = new Vector3( bb.getFloat(), bb.getFloat(), bb.getFloat() );
        scl = new Vector3 ( 1, 1, 1 );
        rot.x = bb.getFloat();
        rot.y = bb.getFloat();
        rot.z = bb.getFloat();
        rot.w = bb.getFloat();
        modId = bb.getInt();
        interior = bb.getInt();
        lodId = bb.getInt();
    }
    
    @Override
    public ItemType getType() {
        return ItemType.INST;
    }
    
    @Override
    public String toString() {
        return String.format("[%1$04d] %2$s", modId, modelName);
    }
}
