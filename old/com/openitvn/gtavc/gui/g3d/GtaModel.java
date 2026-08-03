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

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
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
import com.openitvn.format.dff.RwMaterial;
import com.openitvn.format.txd.RwTexture;
import com.openitvn.gtavc.gui.Main;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.plugin.gta.ResourceModel;
import com.openitvn.unicore.plugin.gta.item.ItemOBJS;
import com.openitvn.unicore.world.resource.IMaterial;
import com.openitvn.unicore.world.resource.ResourceManager;
import java.io.IOException;
import java.util.ArrayList;

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
    
    public GtaModel(ItemOBJS objs) {
        this(objs.modName, objs.txdName, MeshType.OneMesh);
        drawDistance = objs.dd1;
    }
    
    public GtaModel(String modName, String txdName, MeshType meshType) {
        this.meshType = meshType;
        this.modName = modName;
        this.txdName = txdName;
        ResourceModel res = ResourceModel.getInstance();
        try (EntryStream ms = res.getEntryStream(modName, "dff")) {
            rClump = RpSection.loadRoot(ms, RpClump.class);
        } catch (IOException ex) {
            System.err.println("DFF not found: " + modName);
        }
    }
    
    public Model getModel() {
        if (model == null) {
            boolean bLocalTransform = (meshType == MeshType.AllMesh);
            model = createModel(this, bLocalTransform, new Vector3(1, 1, 1));
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
        if (model != null) {
            model.dispose();
        }
        // TODO: Cleanup unused textures
//        for (RpGeometry geoData : getGeometries()) {
//            RpSection matList = geoData.getFirstChild(RpType.MaterialList);
//            for (RpMaterial matData : matList.getChildren(RpMaterial.class)) {
//                if (matData.bTextured) {
//                    String texName = matData.getTextureName();
//                    GtaTextureManager.detach(txdName, texName, this);
//                }
//            }
//        }
    }
    
    private static Model createModel(GtaModel gMod, boolean bLocalTransform, Vector3 scale) {
        ResourceManager res = Main.getInstance().resource;
            
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        for (RpGeometry rGeo : gMod.getGeometries()) {
            // vertexFormat and vertexData are common data
            VertexAttribute[] verFmt = createVertexFormat(rGeo);
            float[] vertData = wrapVertexData(rGeo);
            
            // local transform
            Matrix4 trn = new Matrix4().rotate(-1, 0, 0, 90); //convert z-up to y-up
            if (bLocalTransform) {
                RpFrame[] frmSeq = gMod.rClump.frameList.getFrameSequence(rGeo.frame);
                trn.mul(createTransform(frmSeq));
                if (rGeo.frame.name.startsWith("wheel_l"))
                    trn.rotate(0, 1, 0, 180);
//                for(RwFrame frame : frameSequence)
//                    System.out.print(" > " + frame.name);
//                System.out.println();
            }
            
            // create meshes
            int k = 1;
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
                RpMaterial matData = rMats.get(i);
                
                RpTextureDictionary texDic = GtaTextureManager.getTexDic(gMod.txdName);
                String matName = "M_";
                RpTextureNative texNav = matData.bTextured ? texDic.findTexture(matData.getTextureName()) : null;
                if (texNav != null) {
                    String texName = texNav.getMapperName();
                    if (!res.containsTexture(texName)) {
                        RwTexture iTex = new RwTexture(texName, texNav);
                        res.register(iTex);
                    }
                    matName += texName;
                }
                else {
                    matName += gMod.modName + k;
                    k++;
                }
                IMaterial iMat = res.findMaterial(matName);
                if (iMat == null) {
                    iMat = new RwMaterial(matName, matData, texNav);
                    res.register(iMat);
                }
                Material mat = res.makeInstance(iMat, null); // TODO: model is not null
                
                mb.part(null, mesh, GL20.GL_TRIANGLES, mat);
            }
        }
        return mb.end();
    }
    
    private static Matrix4 createTransform(RpFrame[] frameSequence) {
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
