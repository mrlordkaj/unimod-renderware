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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.openitvn.engine.renderware.struct.RpTriangle;
import com.openitvn.engine.renderware.struct.RpFrame;
import com.openitvn.engine.renderware.RpType;
import com.openitvn.engine.renderware.RpGeometry;
import com.openitvn.engine.renderware.RpMaterial;
import com.openitvn.engine.renderware.RpClump;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.openitvn.engine.renderware.RpSection;
import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.engine.renderware.struct.RpColor;
import com.openitvn.gtavc.core.GtaAssetModel;
import com.openitvn.gtavc.core.GtaCollision;
import com.openitvn.gtavc.core.item.OBJSEntry;
import com.openitvn.unicore.data.BufferStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class GtaModel {
    
    public enum MeshType { OneMesh, AllMesh }
    
    final MeshType meshType;
    final String modName, txdName;
    float drawDistance;
    RpClump rClump;
    Model model;
    public GtaCollision gCol;
    
    public GtaModel(OBJSEntry objs) throws IOException {
        this(objs.modName, objs.txdName, MeshType.OneMesh);
        drawDistance = objs.dd1;
    }
    
    public GtaModel(String modName, String txdName, MeshType meshType) throws IOException {
        this.meshType = meshType;
        this.modName = modName;
        this.txdName = txdName;
        try (BufferStream bs = GtaAssetModel.getInstance().extract(modName + ".dff")) {
            rClump = RpSection.loadRoot(bs, RpClump.class);
        } catch (NullPointerException ex) { }
    }
    
    public Model getModel() {
        if (model == null) {
            boolean lclTrn = (meshType == MeshType.AllMesh);
            model = createModel(this, lclTrn, new Vector3(1, 1, 1));
        }
        return model;
    }
    
    public ArrayList<RpGeometry> getGeometries() {
        ArrayList<RpGeometry> ret = new ArrayList<>();
        if (rClump != null) {
            switch (meshType) {
                case AllMesh:
                    return rClump.geometries;
                    
                case OneMesh:
                    RpGeometry root = rClump.getRootGeometry();
                    if (root != null)
                        ret.add(root);
                    break;
            }
        }
        return ret;
    }
    
    public void dispose() {
        if (model != null)
            model.dispose();
        // cleanup unused textures
        for (RpGeometry rGeo : getGeometries()) {
            for (RpMaterial rMat : rGeo.getFirstChild(RpType.MaterialList).getChildren(RpMaterial.class)) {
                if (rMat.textured) {
                    String texName = rMat.getTextureName();
                    GtaTextureManager.detach(txdName, texName, this);
                }
            }
        }
    }
    
    public static HashMap<String, Material> MATERIAL_MAP = new HashMap<>();
    public static HashMap<String, String> TEXTURE_USAGE_MAP = new HashMap<>(); // matName, texName
    
    public static Model createModel(GtaModel gMod, boolean applyLocalTransform, Vector3 scale) {
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        for (RpGeometry rGeo : gMod.getGeometries()) {
            // vertexFormat and vertexData are common data
            VertexAttribute[] verFmt = createVertexFormat(rGeo);
            float[] vertData = wrapVertexData(rGeo);
            
            // local transform
            Matrix4 trn = new Matrix4().rotate(-1, 0, 0, 90); //convert z-up to y-up
            if (applyLocalTransform) {
                RpFrame[] frmSeq = gMod.rClump.frameList.getFrameSequence(rGeo.frame);
                trn.mul(createTransform(frmSeq));
                if (rGeo.frame.name.startsWith("wheel_l"))
                    trn.rotate(0, 1, 0, 180);
//                for(RwFrame frame : frameSequence)
//                    System.out.print(" > " + frame.name);
//                System.out.println();
            }
            
            // create meshes
            ArrayList<RpMaterial> rMats = rGeo.getFirstChild(RpType.MaterialList).getChildren(RpMaterial.class);
            for (int i = 0; i < rMats.size(); i++) {
                // create and init transform new part
                short[] idxData = wrapIndexData(rGeo.getTriangles(i));
                Mesh mesh = new Mesh(true, rGeo.getVertexCount(), idxData.length, verFmt);
                mesh.setVertices(vertData);
                mesh.setIndices(idxData);
                mesh.scale(scale.x, scale.y, scale.z);
                mesh.transform(trn);
                // prepare material for new meshpart
                RpMaterial rMat = rMats.get(i);
                Material mat = new Material();
                boolean hasAlpha;
                if (rMat.textured) {
                    String texName = rMat.getTextureName();
                    Texture tex = GtaTextureManager.attach(gMod.txdName, texName, gMod);
                    mat.set(TextureAttribute.createDiffuse(tex));
                    // check alpha
                    try {
                        RpTextureDictionary rTexDic = GtaTextureManager.getTexDic(gMod.txdName);
                        RpTextureNative rTex = rTexDic.findTexture(texName);
                        hasAlpha = rTex != null && rTex.hasAlpha;
                    } catch (NullPointerException ex) {
                        hasAlpha = rMat.textured && rMat.isMasked();
                    }
                } else {
                    hasAlpha = rMat.color.a < 255;
                    mat.set(ColorAttribute.createDiffuse(combineColor(rMat.color, rMat.diffuse)));
                    mat.set(ColorAttribute.createAmbient(combineColor(rMat.color, rMat.ambient)));
                    mat.set(ColorAttribute.createSpecular(combineColor(rMat.color, rMat.specular)));
                }
                // enable alpha test
                if (hasAlpha) {
                    mat.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
                    mat.set(FloatAttribute.createAlphaTest(.5f));
                    mat.set(IntAttribute.createCullFace(GL20.GL_NONE));
                }
                mb.part(null, mesh, GL20.GL_TRIANGLES, mat);
            }
        }
        return mb.end();
    }
    
    private static Color combineColor(RpColor rColor, float f) {
        Color c = new Color();
        c.r = rColor.r * f / 255;
        c.g = rColor.g * f / 255;
        c.b = rColor.b * f / 255;
        c.a = rColor.a * f / 255;
        return c;
    }
    
    public static Matrix4 createTransform(RpFrame[] frameSequence) {
        Matrix4 trn = new Matrix4();
        for (RpFrame frm : frameSequence)
            trn.mul(new Matrix4(frm.combineMatrix4()));
        return trn;
    }
    
    private static VertexAttribute[] createVertexFormat(RpGeometry rGeo) {
        ArrayList<VertexAttribute> attrs = new ArrayList<>();
        attrs.add(VertexAttribute.Position());
        if (rGeo.hasNormal())
            attrs.add(VertexAttribute.Normal());
        for (int i = 0; i < rGeo.getTexCoordCount(); i++)
            attrs.add(VertexAttribute.TexCoords(i));
        return attrs.toArray(new VertexAttribute[attrs.size()]);
    }
    
    private static float[] wrapVertexData(RpGeometry rGeo) {
        boolean hasNormal = rGeo.hasNormal();
        Vector3[] normals = rGeo.getNormals();
        
        int numTexCoords = rGeo.getTexCoordCount();
        Vector2[][] texCoords = rGeo.getTexCoords();
        
        //begin create new vertex data
        int numVerts = rGeo.getVertexCount();
        int vertStride = 3;
        if (hasNormal)
            vertStride += 3;
        vertStride += numTexCoords * 2;
        float[] vertData = new float[numVerts * vertStride];
        //end create new vertex data
        
        //begin wrap vertex data
        Vector3[] verts = rGeo.getVertices();
        int k = 0;
        for (int i = 0; i < numVerts; i++) {
            vertData[k++] = verts[i].x;
            vertData[k++] = verts[i].y;
            vertData[k++] = verts[i].z;
            if (hasNormal) {
                vertData[k++] = normals[i].x;
                vertData[k++] = normals[i].y;
                vertData[k++] = normals[i].z;
            }
            for (int j = 0; j < numTexCoords; j++) {
                vertData[k++] = texCoords[j][i].x;
                vertData[k++] = texCoords[j][i].y;
            }
        }
        //end wrap vertex data
        
        return vertData;
    }
    
    private static short[] wrapIndexData(RpTriangle[] faces) {
        short[] data = new short[faces.length * 3];
        int i = 0;
        for (RpTriangle face : faces) {
            data[i] = face.v1; i++;
            data[i] = face.v2; i++;
            data[i] = face.v3; i++;
        }
        return data;
    }
}
