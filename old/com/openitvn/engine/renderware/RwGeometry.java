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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.openitvn.engine.renderware.struct.RpSphere;
import com.openitvn.engine.renderware.struct.RpColor;
import com.openitvn.engine.renderware.struct.RpFrame;
import com.openitvn.engine.renderware.struct.RpTriangle;
import com.openitvn.unicore.data.DataStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/RpGeometry
 */
public class RwGeometry extends RwSection implements Cloneable {
    //rpGeometry format flags definition
    private static final int GEOMETRYTRISTRIP =                 0x00000001; //is triangle strip (if disabled it will be an triangle list)
    private static final int GEOMETRYPOSITIONS =                0x00000002; //vertex translation
    private static final int GEOMETRYTEXTURED =                 0x00000004; //texture coordinates
    private static final int GEOMETRYPRELIT =                   0x00000008; //vertex colors
    private static final int GEOMETRYNORMALS =                  0x00000010; //store normals
    private static final int GEOMETRYLIGHT =                    0x00000020; //geometry is lit (dynamic and static)
    private static final int GEOMETRYMODULATEMATERIALCOLOR =    0x00000040; //modulate material color
    private static final int GEOMETRYTEXTURED2 =                0x00000080; //texture coordinates 2
    private static final int GEOMETRYNATIVE =                   0x01000000; //native Geometry
    
    // data
    public RpColor[] prelit;
    private Vector2[][] texCoords;
    private RpTriangle[] triangles;
    private Vector3[] vertices;
    private Vector3[] normals;
    
    // references
    public RpFrame frame;
    public ArrayList<RpMaterial> materials;
    
    public RwGeometry(int size, int libId, RwSection parent, DataStream ds) {
        super(RpType.Geometry, size, libId, parent, ds);
        //System.out.println(String.format("%08X", version));
        ByteBuffer bb = super.getStruct();
        // format
        int fmt = bb.getInt();
        int numFaces = bb.getInt();
        int numVerts = bb.getInt();
        int numMorphs = bb.getInt();  // (mesh) morphing is not used in GTA series, so this is always 1
        // version < 0x34000
        float ambient = 0, specular = 0, diffuse = 0;
        if (version < 0x34000) {
            ambient = bb.getFloat();
            specular = bb.getFloat();
            diffuse = bb.getFloat();
        }
        // verties attributes
        if ((fmt & GEOMETRYNATIVE) == 0) {
            // prelit
            if ((fmt & GEOMETRYPRELIT) != 0) {
                prelit = new RpColor[numVerts];
                for (int i = 0; i < numVerts; i++) {
                    prelit[i] = new RpColor(bb);
                }
            }
            // texCoords
            if ((fmt & (GEOMETRYTEXTURED | GEOMETRYTEXTURED2)) != 0) {
                int numTexSets = (fmt & 0x00FF0000) >> 16;
                if (numTexSets == 0)
                    numTexSets = ((fmt & GEOMETRYTEXTURED) != 0) ? 1 : 2;
                texCoords = new Vector2[numTexSets][numVerts];
                for (int i = 0; i < numTexSets; i++) {
                    for (int j = 0; j < numVerts; j++)
                        texCoords[i][j] = new Vector2(bb.getFloat(), bb.getFloat());
                }
            }
            // indicies and matId
            triangles = new RpTriangle[numFaces];
            for (int i = 0; i < numFaces; i++) {
                triangles[i] = new RpTriangle(bb);
            }
        }
        // bounding
        RpSphere bounding = new RpSphere(bb);
        int hasVertex = bb.getInt();
        int hasNormal = bb.getInt();
        // vertices position
        vertices = new Vector3[numVerts];
        for (int i = 0; i < numVerts; i++)
            vertices[i] = new Vector3(bb.getFloat(), bb.getFloat(), bb.getFloat());
        // vertices normal
        if ((fmt & GEOMETRYNORMALS) != 0) {
            normals = new Vector3[numVerts];
            for (int i = 0; i < numVerts; i++)
                normals[i] = new Vector3(bb.getFloat(), bb.getFloat(), bb.getFloat());
        }
        if (bb.hasRemaining()) {
            String template =
                      "remaining {0} bytes (proccessed {1} bytes)"
                    + "\n|- vertices: " + numVerts
                    + "\n|- triangles: " + numFaces;
            Logger.getLogger(RwGeometry.class.getName()).log(Level.SEVERE, template, new Object[]{bb.remaining(), bb.position()});
        }
//        testObj();
        // materials
        materials = super.getFirstChild(RpType.MaterialList).getChildren(RpMaterial.class);
        if (version < 0x34000) {
            for (RpMaterial mat : materials) {
                mat.ambient = ambient;
                mat.specular = specular;
                mat.diffuse = diffuse;
            }
        }
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException{  
        return super.clone();  
    }
    
//    private void testObj() {
//        for (RwVector3 vertice : vertices) {
//            System.out.println(String.format("v %1$f %2$f %3$f", vertice.x, vertice.y, vertice.z));
//        }
//        if(normals != null) {
//            for(RwVector3 normal : normals) {
//                System.out.println(String.format("vn %1$f %2$f %3$f", normal.x, normal.y, normal.z));
//            }
//        }
//        if(texCoords != null) {
//            for (RwTexCoord texCoord : texCoords[0]) {
//                System.out.println(String.format("vt %1$f %2$f", texCoord.u, texCoord.v));
//            }
//        }
//        for (RwTriangle triangle : triangles) {
//            int vertex1 = triangle.vertex1 + 1;
//            int vertex2 = triangle.vertex2 + 1;
//            int vertex3 = triangle.vertex3 + 1;
//            StringBuilder v1 = new StringBuilder();
//            StringBuilder v2 = new StringBuilder();
//            StringBuilder v3 = new StringBuilder();
//            v1.append(vertex1);
//            v2.append(vertex2);
//            v3.append(vertex3);
//            if(texCoords != null) {
//                v1.append("/").append(vertex1);
//                v2.append("/").append(vertex2);
//                v3.append("/").append(vertex3);
//            }
//            if(normals != null) {
//                v1.append("/").append(vertex1);
//                v2.append("/").append(vertex2);
//                v3.append("/").append(vertex3);
//            }
//            System.out.println(String.format("f %1$s %2$s %3$s", v1, v2, v3));
//        }
//    }
    
    //<editor-fold defaultstate="collapsed" desc="TexCoords">
    public int getTexCoordCount() {
        return (texCoords == null) ? 0 : texCoords.length;
    }
    
    public Vector2[][] getTexCoords() {
        return texCoords;
    }
    
    public Vector2[] getTexCoord(int setId) {
        return (setId < texCoords.length) ? texCoords[setId] : null;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Vertices, Normals, and VertexColors">
    public int getVertexCount() {
        return vertices.length;
    }
    
    public Vector3[] getVertices() {
        return vertices;
    }
    
    public boolean hasNormal() {
        return normals != null;
    }
    
    public Vector3[] getNormals() {
        return normals;
    }
    
    public boolean hasVertexColor() {
        return prelit != null;
    }
    
    public RpColor[] getVertexColor() {
        return prelit;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Indices and Triangles">
    public int getIndexCount() {
        return triangles.length * 3;
    }
    
    public short[] getIndices(int matId) {
        ArrayList<Short> ids = new ArrayList<>();
        for (RpTriangle tri : triangles) {
            if (tri.materialIndex == matId) {
                ids.add(tri.v1);
                ids.add(tri.v2);
                ids.add(tri.v3);
            }
        }
        short[] rs = new short[ids.size()];
        for(int i = 0; i < rs.length; i++)
            rs[i] = ids.get(i);
        return rs;
    }
    
    public RpTriangle[] getTriangles() {
        return triangles;
    }
    
    public RpTriangle[] getTriangles(int matId) {
        ArrayList<RpTriangle> rs = new ArrayList<>();
        for (RpTriangle tri : triangles) {
            if (tri.materialIndex == matId)
                rs.add(tri);
        }
        return rs.toArray(new RpTriangle[rs.size()]);
    }
    //</editor-fold>
}
