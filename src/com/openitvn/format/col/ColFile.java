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
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
public class ColFile {
    
    private static final int FLAG_CONE = 1; // collision uses cones instead of lines (flag forced to false by engine upon loading)
    private static final int FLAG_NOT_EMPTY = 2; // not empty (collision model has spheres or boxes or a mesh)
    private static final int FLAG_FACE_GROUP = 8; // has face groups (if not empty)
    private static final int FLAG_SHADOW_MESH = 16; // has shadow mesh (col 3)
    
    private static final float[] sphereVertices = new float[] { 0.0f,0.0f,1.0f,0.894427180290222f,0.0f,0.447213590145111f,0.276393175125122f,0.85065084695816f,0.447213590145111f,-0.723606824874878f,0.525731027126312f,0.447213590145111f,-0.723606765270233f,-0.525731205940247f,0.447213590145111f,0.276393324136734f,-0.850650787353516f,0.447213590145111f,0.723606824874878f,0.525731086730957f,-0.447213590145111f,-0.276393234729767f,0.850650787353516f,-0.447213590145111f,-0.894427180290222f,-7.81933096050125E-8f,-0.447213590145111f,-0.276392877101898f,-0.85065084695816f,-0.447213590145111f,0.723606765270233f,-0.525731146335602f,-0.447213590145111f,0.0f,0.0f,-1.0f,0.525731086730957f,0.0f,0.850650787353516f,0.162459820508957f,0.5f,0.850650787353516f,-0.42532542347908f,0.309016942977905f,0.850650787353516f,-0.425325393676758f,-0.309017032384872f,0.850650787353516f,0.162459924817085f,-0.499999970197678f,0.850650787353516f,0.688190877437592f,0.5f,0.525731086730957f,-0.262865602970123f,0.809016942977905f,0.525731086730957f,-0.85065084695816f,-9.99600189288685E-8f,0.525731086730957f,-0.262865453958511f,-0.80901700258255f,0.525731086730957f,0.688190996646881f,-0.499999970197678f,0.525731086730957f,0.951056480407715f,0.309016972780228f,0.0f,-3.33200098623365E-8f,1.0f,0.0f,-0.951056480407715f,0.309016913175583f,0.0f,-0.587785124778748f,-0.80901712179184f,0.0f,0.587785303592682f,-0.809016942977905f,0.0f,0.95105642080307f,-0.30901700258255f,0.0f,0.587785243988037f,0.809016942977905f,0.0f,-0.587785303592682f,0.809016942977905f,0.0f,-0.95105642080307f,-0.309017062187195f,0.0f,2.66560078898692E-7f,-1.0f,0.0f,0.262865543365479f,0.809016942977905f,-0.525731086730957f,-0.688190937042236f,0.499999940395355f,-0.525731086730957f,-0.688190817832947f,-0.50000011920929f,-0.525731146335602f,0.26286569237709f,-0.809016942977905f,-0.525731027126312f,0.85065084695816f,-3.33200063096228E-8f,-0.525731086730957f,0.42532542347908f,0.309016972780228f,-0.850650787353516f,-0.16245986521244f,0.499999970197678f,-0.850650787353516f,-0.525731086730957f,-4.59608742175988E-8f,-0.850650787353516f,-0.162459656596184f,-0.5f,-0.850650787353516f,0.425325393676758f,-0.30901700258255f,-0.850650787353516f };
    private static final short[] sphereIndices = new short[] { 0,12,13,12,1,17,12,17,13,13,17,2,0,13,14,13,2,18,13,18,14,14,18,3,0,14,15,14,3,19,14,19,15,15,19,4,0,15,16,15,4,20,15,20,16,16,20,5,0,16,12,16,5,21,16,21,12,12,21,1,1,27,22,27,10,36,27,36,22,22,36,6,2,28,23,28,6,32,28,32,23,23,32,7,3,29,24,29,7,33,29,33,24,24,33,8,4,30,25,30,8,34,30,34,25,25,34,9,5,31,26,31,9,35,31,35,26,26,35,10,6,28,22,28,2,17,28,17,22,22,17,1,7,29,23,29,3,18,29,18,23,23,18,2,8,30,24,30,4,19,30,19,24,24,19,3,9,31,25,31,5,20,31,20,25,25,20,4,10,27,26,27,1,21,27,21,26,26,21,5,11,38,37,38,7,32,38,32,37,37,32,6,11,39,38,39,8,33,39,33,38,38,33,7,11,40,39,40,9,34,40,34,39,39,34,8,11,41,40,41,10,35,41,35,40,40,35,9,11,37,41,37,6,36,37,36,41,41,36,10 };
    private static final float[] boxVertices = new float[] { -0.5f,-0.5f,-0.5f,0.5f,-0.5f,-0.5f,-0.5f,0.5f,-0.5f,0.5f,0.5f,-0.5f,-0.5f,-0.5f,0.5f,0.5f,-0.5f,0.5f,-0.5f,0.5f,0.5f,0.5f,0.5f,0.5f };
    private static final short[] boxIndices = new short[] { 0,2,3,3,1,0,4,5,7,7,6,4,0,1,5,5,4,0,1,3,7,7,5,1,3,2,6,6,7,3,2,0,4,4,6,2 };
    
    public final String objsName;
    public final int objsId; // uint16
    public IModel model;
    
    class Header {
        // version >= 2
        int numSpheres, numBoxes, numFaces;
        short numLines;
        int flags;
        long offsetSpheres, offsetBoxes, offsetLines;
        long offsetVertices, offsetFaces, offsetPlanes;
        // version >= 3
        int numShadowFaces;
        long offsetShadowVertices, offsetShadowFaces;
    }
    
    public ColFile(int fourCC, DataStream ds) {
        long offset = ds.position();
        int size = ds.getInt();
        objsName = ds.readFixedString(22);
        objsId = ds.getShort();
        ds.skip(40); // TODO: bounding
        int version = 1;
        switch (fourCC) {
            case 843861827: // COL2
                version = 2;
                break;
            case 860639043: // COL3
                version = 3;
                break;
            case 877416259: // COL4
                version = 4;
                break;
        }
        // decode header
        Header header = new Header();
        if (version >= 2) {
            header.numSpheres = ds.getShort();
            header.numBoxes = ds.getShort();
            header.numFaces = ds.getShort();
            header.numLines = ds.get();
            ds.skip(1);
            header.flags = ds.getInt();
            header.offsetSpheres = offset + ds.getInt();
            header.offsetBoxes = offset + ds.getInt();
            header.offsetLines = offset + ds.getInt();
            header.offsetVertices = offset + ds.getInt();
            header.offsetFaces = offset + ds.getInt();
            header.offsetPlanes = offset + ds.getInt();
        }
        if (version >= 3) {
            header.numShadowFaces = ds.getInt();
            header.offsetShadowVertices = ds.getInt();
            header.offsetShadowFaces = ds.getInt();
        }
        if (version == 4) {
            ds.skip(4);
        }
        // decode body
        ArrayList<IVertex> vertices = new ArrayList<>();
        ArrayList<Short> indices = new ArrayList<>(); // TODO: replace with ShortBuffer
        if (version >= 2) {
            if ((header.flags & FLAG_NOT_EMPTY) != 0) {
                decodeV2(ds, header, vertices, indices);
            }
        } else {
            decodeV1(ds, vertices, indices);
        }
        // create model
        addMeshToLibrary(vertices, indices);
        // make sure correct pointer for the next
        ds.position(offset+size+4);
    }
    
    private void decodeV1(DataStream ds, ArrayList<IVertex> vertices, ArrayList<Short> indices) {
        // read spheres
        int numSpheres = ds.getInt();
        for (int i = 0; i < numSpheres; i++) {
            float r = ds.getFloat();
            float x = ds.getFloat();
            float y = ds.getFloat();
            float z = ds.getFloat();
            ds.skip(4); // surface
            generateSphere(vertices, indices, x, y, z, r);
        }
        
        // unknow
        ds.skip(4);
        
        // read boxes
        int numBoxes = ds.getInt();
        for (int i = 0; i < numBoxes; i++) {
            float x1 = ds.getFloat();
            float y1 = ds.getFloat();
            float z1 = ds.getFloat();
            float x2 = ds.getFloat();
            float y2 = ds.getFloat();
            float z2 = ds.getFloat();
            ds.skip(4); // surface
            generateBox(vertices, indices, x1, y1, z1, x2, y2, z2);
        }
        
        // update vertices offset
        short kv = (short)vertices.size();
        // read vertices
        int numVerts = ds.getInt();
        for (int i = 0; i < numVerts; i++) {
            float x = ds.getFloat();
            float y = ds.getFloat();
            float z = ds.getFloat();
            IVertex v = new IVertex(x, y, z);
            vertices.add(v);
        }
        // read faces
        int numFaces = ds.getInt();
        if (numFaces > 0) {
            for (int i = 0; i < numFaces; i++) {
                int i3 = ds.getInt();
                int i2 = ds.getInt();
                int i1 = ds.getInt();
                ds.skip(4); // surface
                indices.add((short)(i1 + kv));
                indices.add((short)(i2 + kv));
                indices.add((short)(i3 + kv));
            }
        }
    }
    
    private void decodeV2(DataStream ds, Header header, ArrayList<IVertex> vertices, ArrayList<Short> indices) {
        // read spheres
        if (header.numSpheres > 0) {
            ds.position(header.offsetSpheres);
            for (int i = 0; i < header.numSpheres; i++) {
                float x = ds.getFloat();
                float y = ds.getFloat();
                float z = ds.getFloat();
                float r = ds.getFloat();
                ds.skip(4); // surface
                generateSphere(vertices, indices, x, y, z, r);
            }
        }
        
        if (header.numLines > 0) {
            System.out.printf("Col %s, Capsules %d\n", objsName, header.numLines);
        }
        
        // read boxes
        if (header.numBoxes > 0) {
            ds.position(header.offsetBoxes);
            for (int i = 0; i < header.numBoxes; i++) {
                float x1 = ds.getFloat();
                float y1 = ds.getFloat();
                float z1 = ds.getFloat();
                float x2 = ds.getFloat();
                float y2 = ds.getFloat();
                float z2 = ds.getFloat();
                ds.skip(4); // surface
                generateBox(vertices, indices, x1, y1, z1, x2, y2, z2);
            }
        }
        
        if (header.numFaces > 0) {
            // update vertices offset
            short kv = (short)vertices.size();
            // read vertices
            long offset = header.offsetFaces;
            if ((header.flags & FLAG_FACE_GROUP) != 0) {
                offset -= 4;
                ds.position(offset);
                int numFaceGroups = ds.getInt();
                offset -= (numFaceGroups * 28);
            }
            int numVerts = (int)(offset - header.offsetBoxes) / 6;
            ds.position(header.offsetVertices);
            for (int i = 0; i < numVerts; i++) {
                float x = ds.getShort() / 128.f;
                float y = ds.getShort() / 128.f;
                float z = ds.getShort() / 128.f;
                IVertex v = new IVertex(x, y, z);
                vertices.add(v);
            }
            // read faces
            ds.position(header.offsetFaces);
            for (int i = 0; i < header.numFaces; i++) {
                short i3 = ds.getShort();
                short i2 = ds.getShort();
                short i1 = ds.getShort();
                ds.skip(2); // material(1) + light(1)
                indices.add((short)(i1 + kv));
                indices.add((short)(i2 + kv));
                indices.add((short)(i3 + kv));
            }
        }
    }
    
    private void generateSphere(ArrayList<IVertex> vertices, ArrayList<Short> indices, float x, float y, float z, float r) {
        short kv = (short)vertices.size();
        // create vertices
        int k = 0;
        for (int j = 0; j < sphereVertices.length / 3; j++) {
            float vx = sphereVertices[k++] * r + x;
            float vy = sphereVertices[k++] * r + y;
            float vz = sphereVertices[k++] * r + z;
            IVertex v = new IVertex(vx, vy, vz);
            vertices.add(v);
        }
        // create indices
        for (int j = 0; j < sphereIndices.length; j++) {
            short ki = (short)(sphereIndices[j] + kv);
            indices.add(ki);
        }
    }
    
    private void generateBox(ArrayList<IVertex> vertices, ArrayList<Short> indices, float x1, float y1, float z1, float x2, float y2, float z2) {
        // update vertices offset
        short kv = (short)vertices.size();
        // create vertices
        float cx = (x1 + x2) * 0.5f;
        float cy = (y1 + y2) * 0.5f;
        float cz = (z1 + z2) * 0.5f;
        float sx = Math.abs(x2 - x1);
        float sy = Math.abs(y2 - y1);
        float sz = Math.abs(z2 - z1);
        int k = 0;
        for (int j = 0; j < boxVertices.length / 3; j++) {
            float vx = boxVertices[k++] * sx + cx;
            float vy = boxVertices[k++] * sy + cy;
            float vz = boxVertices[k++] * sz + cz;
            IVertex v = new IVertex(vx, vy, vz);
            vertices.add(v);
        }
        // create indices
        for (int j = 0; j < boxIndices.length; j++) {
            short ki = (short)(boxIndices[j] + kv);
            indices.add(ki);
        }
    }
    
    private void addMeshToLibrary(ArrayList<IVertex> vertices, ArrayList<Short> indices) {
        if (vertices.size() >= 3) {
            model = new IModel("CM_"+objsName);
            IMesh mesh = new IMesh();
            IVertex[] vertexData = new IVertex[vertices.size()];
            for (int i = 0; i < vertexData.length; i++) {
                vertexData[i] = vertices.get(i);
            }
            mesh.setVertices(vertexData);
            short[] indexData = new short[indices.size()];
            for (int i = 0; i < indexData.length; i++) {
                indexData[i] = indices.get(i);
            }
            mesh.setIndices(indexData);
            model.meshes.add(mesh);
        }
    }
    
    public boolean isEmpty() {
        return (model == null);
    }
}
