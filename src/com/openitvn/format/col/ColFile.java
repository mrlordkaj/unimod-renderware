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
package com.openitvn.format.col;

import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.IMesh;
import com.openitvn.unicore.world.IVertex;
import com.openitvn.unicore.world.resource.IModel;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
public class ColFile {
    
    public final String objsName;
    public final int objsId; // uint16
    public IModel model;
    public ArrayList<ColSphere> spheres = new ArrayList();
    public ArrayList<ColBox> boxes = new ArrayList();
    
    public ColFile(int fourCC, DataStream ds) {
        int size = ds.getInt();
        int offset = (int) ds.position();
        objsName = ds.readFixedString(22);
        objsId = ds.getShort();
        ds.skip(40); // TODO: bounding
        switch (fourCC) {
            case 1280069443: // COLL
                decodeV1(ds);
                break;

            case 843861827: // COL2
            case 860639043: // COL3
                ds.skip(size - 64);
                break;

            case 877416259: // COL4
                ds.skip(size - 64);
                break;
        }
        // make sure correct pointer for the next
        ds.position(offset+size);
    }
    
    private void decodeV1(DataStream ds) {
        // TODO: read spheres
        int numSpheres = ds.getInt();
//        ds.skip(numSpheres * 20);
        spheres = new ArrayList(numSpheres);
        for (int i = 0; i < numSpheres; i++) {
            spheres.add(new ColSphere(ds));
        }
        
        int unk = ds.getInt();
        
        // TODO: read boxes
        int numBoxes = ds.getInt();
//        ds.skip(numBoxes * 28);
        boxes = new ArrayList(numBoxes);
        for (int i = 0; i < numBoxes; i++) {
            boxes.add(new ColBox(ds));
        }
        
        // read vertices
        int numVerts = ds.getInt();
        IVertex[] vertices = new IVertex[numVerts];
        for (int i = 0; i < numVerts; i++) {
            float x = ds.getFloat();
            float y = ds.getFloat();
            float z = ds.getFloat();
            vertices[i] = new IVertex(x, y, z);
        }
        
        // read faces
        int numFaces = ds.getInt();
        if (numFaces > 0) {
            ShortBuffer indices = ShortBuffer.allocate(numFaces * 3);
            for (int i = 0; i < numFaces; i++) {
                int i3 = ds.getInt();
                int i2 = ds.getInt();
                int i1 = ds.getInt();
                indices.put((short)i1);
                indices.put((short)i2);
                indices.put((short)i3);
                ds.getInt(); // surface
            }
            IMesh mesh = new IMesh();
            mesh.setVertices(vertices);
            mesh.setIndices(indices);
            model = new IModel("CLM_"+objsName);
            model.meshes.add(mesh);
        }
    }
}
