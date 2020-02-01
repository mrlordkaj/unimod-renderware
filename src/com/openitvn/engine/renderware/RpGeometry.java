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
package com.openitvn.engine.renderware;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.IVertex;
import com.openitvn.engine.renderware.struct.RpSphere;
import com.openitvn.engine.renderware.struct.RpColor;
import com.openitvn.maintain.Logger;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/RpGeometry
 */

public class RpGeometry extends RpSection {
    
    // flags
    private static final int GEOMETRYTRISTRIP =                 0x00000001; //is triangle strip (if disabled it will be an triangle list)
    private static final int GEOMETRYPOSITIONS =                0x00000002; //vertex translation
    private static final int GEOMETRYTEXTURED =                 0x00000004; //texture coordinates
    private static final int GEOMETRYPRELIT =                   0x00000008; //vertex colors
    private static final int GEOMETRYNORMALS =                  0x00000010; //store normals
    private static final int GEOMETRYLIGHT =                    0x00000020; //geometry is lit (dynamic and static)
    private static final int GEOMETRYMODULATEMATERIALCOLOR =    0x00000040; //modulate material color
    private static final int GEOMETRYTEXTURED2 =                0x00000080; //texture coordinates 2
    private static final int GEOMETRYNATIVE =                   0x01000000; //native geometry
    
    // data
    public final int numVerts;
    
    // references
    public final ArrayList<RpMaterial> materials;
    public final FloatBuffer vertData;
    public final VertexAttributes vertFmt;
    public ArrayList<ShortBuffer> indexMap;
    
    public RpGeometry(int size, int libId, RpSection parent, DataStream ds) {
        super(RpType.Geometry, size, libId, parent, ds);
        materials = getFirstChild(RpType.MaterialList).getChildren(RpMaterial.class);
        //System.out.println(String.format("%08X", version));
        ByteBuffer bb = getStruct();
        int fmt = bb.getInt();
        int numFaces = bb.getInt();
        numVerts = bb.getInt();
        int numMorphs = bb.getInt(); // (mesh) morphing is not used in GTA series, so this is always 1
        if (version < 0x34000) {
            float ambient = bb.getFloat();
            float specular = bb.getFloat();
            float diffuse = bb.getFloat();
            for (RpMaterial mat : materials) {
                mat.ambient = ambient;
                mat.specular = specular;
                mat.diffuse = diffuse;
            }
        }
        // prepare vertices
        IVertex[] verts = new IVertex[numVerts];
        for (int i = 0; i < numVerts; i++)
            verts[i] = new IVertex();
        HashMap<Short, ArrayList<Short>> idxMap = new HashMap<>();
        // vertices data
        if ((fmt & GEOMETRYNATIVE) == 0) {
            // prelit
            if ((fmt & GEOMETRYPRELIT) != 0) {
                for (IVertex v : verts) {
                    RpColor c = new RpColor(bb);
                    v.setColor(c.r, c.g, c.b, c.a);
                }
            }
            // texCoords
            if ((fmt & (GEOMETRYTEXTURED | GEOMETRYTEXTURED2)) != 0) {
                int numTexCoords = (fmt & 0x00ff0000) >> 16;
                if (numTexCoords == 0)
                    numTexCoords = ((fmt & GEOMETRYTEXTURED) != 0) ? 1 : 2;
                for (int i = 0; i < numTexCoords; i++) {
                    for (IVertex v : verts)
                        v.addTexCoord(bb.getFloat(), bb.getFloat());
                }
            }
            // indices and matId
            for (int i = 0; i < numFaces; i++) {
                short b = bb.getShort();
                short a = bb.getShort();
                short m = bb.getShort();
                short c = bb.getShort();
                ArrayList<Short> ids = idxMap.get(m);
                if (ids == null)
                    idxMap.put(m, (ids = new ArrayList<>()));
                ids.add(a);
                ids.add(b);
                ids.add(c);
            }
        }
        // repeat by morphTargetCount (always 1 in GTA series)
        RpSphere bounding = new RpSphere(bb); // boundingSphere
        int hasVertex = bb.getInt();
        int hasNormal = bb.getInt();
        // position
        for (IVertex v : verts)
            v.set(bb.getFloat(), bb.getFloat(), bb.getFloat());
        // normal
        if ((fmt & GEOMETRYNORMALS) != 0) {
            for (IVertex v : verts)
                v.setNormal(bb.getFloat(), bb.getFloat(), bb.getFloat());
        }
        if (bb.hasRemaining()) {
            Logger.printWarning("Remaining %1$d bytes (%2$d processed)", bb.remaining(), bb.position());
            Logger.printWarning("|- Vertices: %1$d", numVerts);
            Logger.printWarning("|- Triangles: %1$d", numFaces);
        }
        vertData = IVertex.createVertexBuffer(verts);
        vertFmt = verts[0].generateVertexFormat();
        // end repeat by morphTargetCount
        // materials and meshes
        indexMap = new ArrayList<>();
        for (short i = 0; i < materials.size(); i++) {
            ArrayList<Short> ids = idxMap.get(i);
            ShortBuffer sb = ShortBuffer.allocate(ids.size());
            for (short id : ids)
                sb.put(id);
            indexMap.add(sb);
        }
    }
    
//    @Deprecated
//    public RpFrame frame;
//    
//    @Override
//    @Deprecated
//    public Object clone() throws CloneNotSupportedException{  
//        return super.clone();  
//    }
//    
//    @Deprecated
//    public int getTexCoordCount() {
//        return (texCoords == null) ? 0 : texCoords.length;
//    }
//    
//    @Deprecated
//    public Vector2[][] getTexCoords() {
//        return texCoords;
//    }
//    
//    @Deprecated
//    public Vector2[] getTexCoord(int setId) {
//        return (setId < texCoords.length) ? texCoords[setId] : null;
//    }
//    
//    @Deprecated
//    public int getVertexCount() {
//        return vertices.length;
//    }
//    
//    @Deprecated
//    public Vector3[] getVertices() {
//        return vertices;
//    }
//    
//    @Deprecated
//    public boolean hasNormal() {
//        return normals != null;
//    }
//    
//    @Deprecated
//    public Vector3[] getNormals() {
//        return normals;
//    }
//    
//    @Deprecated
//    public boolean hasVertexColor() {
//        return prelit != null;
//    }
//    
//    @Deprecated
//    public RpColor[] getVertexColor() {
//        return prelit;
//    }
//    
//    @Deprecated
//    public int getIndexCount() {
//        return triangles.length * 3;
//    }
//    
//    @Deprecated
//    public short[] getIndices(int matId) {
//        ArrayList<Short> ids = new ArrayList<>();
//        for (RwTriangle tri : triangles) {
//            if (tri.materialId == matId) {
//                ids.add(tri.vertex1);
//                ids.add(tri.vertex2);
//                ids.add(tri.vertex3);
//            }
//        }
//        short[] rs = new short[ids.size()];
//        for(int i = 0; i < rs.length; i++)
//            rs[i] = ids.get(i);
//        return rs;
//    }
//    
//    @Deprecated
//    public RwTriangle[] getTriangles() {
//        return triangles;
//    }
//    
//    @Deprecated
//    public RwTriangle[] getTriangles(int matId) {
//        ArrayList<RwTriangle> rs = new ArrayList<>();
//        for (RwTriangle tri : triangles) {
//            if (tri.materialId == matId)
//                rs.add(tri);
//        }
//        return rs.toArray(new RwTriangle[rs.size()]);
//    }
}
