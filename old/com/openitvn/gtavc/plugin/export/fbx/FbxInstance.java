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

package com.openitvn.gtavc.plugin.export.fbx;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.openitvn.engine.renderware.struct.RpTriangle;
import com.openitvn.engine.renderware.RwGeometry;
import com.openitvn.engine.renderware.RpMaterial;
import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.engine.renderware.struct.RpColor;
import com.openitvn.gtavc.gui.g3d.GtaTextureManager;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class FbxInstance {
    
    public final String modName;
    public final String txdName;
    public final RwGeometry rGeo;
    
    public final Vector3 rPos;
    public final Vector3 rRot;
    public final Vector3 rScl;
    
    final ModelInstance modInst;
    int numVerts, vertSize;
    float[] vertices;
    
    final ArrayList<FbxMaterial> materials = new ArrayList<>();
    
    public FbxInstance(String modName, String txdName, ModelInstance modInst, RwGeometry rGeo, Vector3 pos, Vector3 rot, Vector3 scl) {
        this.modName = modName;
        this.txdName = txdName;
        this.rGeo = rGeo;
        this.modInst = modInst;
        this.rPos = pos;
        this.rRot = rot;
        this.rScl = scl;
        
        if (modInst != null) {
            Mesh m = modInst.model.meshes.first();
            numVerts = m.getNumVertices();
            vertSize = m.getVertexSize() / 4;
            vertices = new float[numVerts * vertSize];
            m.getVertices(vertices);
        }
    }
    
    void prepareMaterials() {
        materials.clear();
        HashMap<String, Integer> matNames = new HashMap<>();
        RpTextureDictionary rTexDic;
        RpTextureNative rTex;
        /*if (modInst != null) {
            for (RpMaterial rMat : rGeo.materials) {
                String texName = rMat.getTextureName();
                if ((rTexDic = GtaTextureManager.getTexDic(txdName)) != null &&
                        (rTex = rTexDic.findTexture(texName)) != null) {
                    texName = rTex.getMapperName();
                }
                String matName = rMat.textured ? texName : modName+"_untex";
                int sameCount = 0;
                if (matNames.containsKey(matName))
                    sameCount = matNames.get(matName) + 1;
                matNames.put(matName, sameCount);
                // uniqueName
                String relPath = rMat.textured ? "Textures\\" + texName + ".png" : null;
                materials.add(new FbxMaterial(matName, relPath, sameCount, null, rMat));
            }
        } else */if (rGeo != null) {
            for (RpMaterial rMat : rGeo.materials) {
                String texName = rMat.getTextureName();
                if ((rTexDic = GtaTextureManager.getTexDic(txdName)) != null &&
                        (rTex = rTexDic.findTexture(texName)) != null) {
                    texName = rTex.getMapperName();
                }
                String matName = rMat.textured ? texName : modName+"_untex";
                int sameCount = 0;
                if (matNames.containsKey(matName))
                    sameCount = matNames.get(matName) + 1;
                matNames.put(matName, sameCount);
                // uniqueName
                String relPath = rMat.textured ? "Textures\\" + texName + ".png" : null;
                materials.add(new FbxMaterial(matName, relPath, sameCount, null, rMat));
            }
        }
    }
    
    public boolean haveAlphaChannel() {
        for (FbxMaterial mat : materials) {
            if (mat.isAlpha)
                return true;
        }
        return false;
    }
    
    public String getStringVertices() {
        StringBuilder sb = new StringBuilder();
        if (modInst != null) {
            VertexAttribute attr = modInst.model.meshes.first()
                    .getVertexAttribute(VertexAttributes.Usage.Position);
            for (int i = 0; i < numVerts; i++) {
                int k = i * vertSize + attr.offset / 4;
                float x = vertices[k++];
                float y = vertices[k++];
                float z = vertices[k++];
                sb.append(x).append(",")
                        .append(y).append(",")
                        .append(z).append(",");
            }
        } else if (rGeo != null) {
            for (Vector3 v : rGeo.getVertices()) {
                sb.append(v.x).append(",")
                        .append(v.y).append(",")
                        .append(v.z).append(",");
            }
        }
        return sb.deleteCharAt(sb.length()-1).toString();
    }
    
    public String getStringIndices() {
        StringBuilder sb = new StringBuilder();
        if (modInst != null) {
            for (Mesh m : modInst.model.meshes) {
                short[] indices = new short[m.getNumIndices()];
                m.getIndices(indices);
                for (int i = 0; i < indices.length / 3; i++) {
                    int k = i * 3;
                    short v1 = indices[k++];
                    short v2 = indices[k++];
                    short v3 = indices[k++];
                    sb.append(v1).append(",")
                            .append(v2).append(",")
                            .append(~v3).append(",");
                }
            }
        } else if (rGeo != null) {
            for (RpTriangle face : rGeo.getTriangles()) {
                sb.append(face.v1).append(",")
                        .append(face.v2).append(",")
                        .append(~face.v3).append(",");
            }
        }
        return sb.deleteCharAt(sb.length()-1).toString();
    }
    
    public String getStringUVIndices() {
        StringBuilder sb = new StringBuilder();
        for (RpTriangle face : rGeo.getTriangles()) {
            sb.append(face.v1).append(",")
                    .append(face.v2).append(",")
                    .append(face.v3).append(",");
        }
        return sb.deleteCharAt(sb.length()-1).toString();
    }
    
    public String getStringNormals() {
        Vector3[] normals = rGeo.getNormals();
        if (normals != null) {
            String[] strs = new String[normals.length];
            for (int i = 0; i < normals.length; i++) {
                Vector3 n = normals[i];
                strs[i] = String.format("%1$f,%2$f,%3$f", n.x, n.y, n.z);
            }
            return String.join(",", strs);
        }
        return "";
    }
    
    public String getStringColors() {
        RpColor[] colors = rGeo.prelit;
        if (colors != null) {
            String[] strs = new String[colors.length];
            float f = 1 / 255f;
            for (int i = 0; i < colors.length; i++) {
                RpColor c = colors[i];
                strs[i] = String.format("%1$f,%2$f,%3$f,%4$f", c.r*f, c.g*f, c.b*f, c.a*f);
            }
            return String.join(",", strs);
        }
        return "";
    }
    
    public String getStringMaterials() {
        RpTriangle[] faces = rGeo.getTriangles();
        String[] matIds = new String[faces.length];
        for (int i = 0; i < faces.length; i++)
            matIds[i] = faces[i].materialIndex + "";
        return String.join(",", matIds);
    }
    
    public int getTexCoordCount() {
        return rGeo.getTexCoordCount();
    }
    
    public String getStringTexCoord(int setId) {
        Vector2[] uvs = rGeo.getTexCoord(setId);
        String[] wrappedUV = new String[uvs.length];
        for(int i = 0; i < wrappedUV.length; i++) {
            Vector2 uv = uvs[i];
            float flipV = 1f - uv.y; //fix vertical flipped texture
            wrappedUV[i] = String.format("%1$f,%2$f", uv.x, flipV);
        }
        return String.join(",", wrappedUV);
    }
}
