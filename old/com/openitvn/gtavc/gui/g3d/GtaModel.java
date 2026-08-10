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
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.openitvn.engine.renderware.*;
import com.openitvn.engine.renderware.struct.RpFrame;
import com.openitvn.format.dff.RwMaterial;
import com.openitvn.format.txd.RwTexture;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.plugin.gta.ResourceModel;
import com.openitvn.unicore.plugin.gta.item.ItemOBJS;
import com.openitvn.unicore.world.IMesh;
import com.openitvn.unicore.world.resource.IMaterial;
import com.openitvn.unicore.world.resource.IModel;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
public class GtaModel extends IModel
{
    enum MeshType { Single, Multiple }
    
    final MeshType meshType;
    final String modName, txdName;
    final GtaCanvas world;
    ItemOBJS objs;
    Model model;
    
    final ArrayList<RpGeometry> geometries = new ArrayList<>();
    
    GtaModel(ItemOBJS objs, GtaCanvas world) {
        this(objs.modName, objs.txdName, MeshType.Single, world);
        this.objs = objs;
    }
    
    GtaModel(String modName, String txdName, MeshType meshType, GtaCanvas world) {
        this.meshType = meshType;
        this.modName = modName;
        this.txdName = txdName;
        this.world = world;
    }
    
    float getDrawDistance() {
        return objs != null ? objs.dd1 : 0;
    }
    
    public Model getModel() {
        if (model != null) {
            return model;
        }
        try (EntryStream ds = ResourceModel.getInstance().getEntryStream(modName, "dff")) {
            // Load clump and collect geometries
            RpClump clump = RpSection.loadRoot(ds, RpClump.class);
            if (meshType == MeshType.Multiple) {
                geometries.addAll(clump.geometries);
            }
            else {
                RpGeometry root = clump.getRootGeometry();
                if (root != null) {
                    geometries.add(root);
                }
            }
            // Build model
            ModelBuilder mb = new ModelBuilder();
            mb.begin();
            for (RpGeometry geoData : geometries) {
                // Convert z-up to y-up
                Matrix4 trn = new Matrix4().rotate(-1, 0, 0, 90);
                // Apply local transform
                if (meshType == MeshType.Multiple) {
                    trn.mul(createTransform(clump.frameList, geoData.frame));
                    if (geoData.frame.name.startsWith("wheel_l")) {
                        trn.rotate(0, 1, 0, 180);
                    }
                }
                // Create meshes
                int k = 1;
                for (int i = 0; i < geoData.materials.size(); i++) {
                    // Prepare material for new meshpart
                    RpMaterial matData = geoData.materials.get(i);
                    String matName = "M_";
                    RpTextureDictionary texDic = world.getTexDic(txdName);
                    RpTextureNative texNav = null;
                    if (texDic != null) {
                        texNav = matData.bTextured ? texDic.findTexture(matData.getTextureName()) : null;
                    }
                    if (texNav != null) {
                        String texName = texNav.getMapperName();
                        if (!world.resource.containsTexture(texName)) {
                            RwTexture iTex = new RwTexture(texName, texNav);
                            world.resource.register(iTex);
                        }
                        matName += texName;
                    }
                    else {
                        matName += modName + k;
                        k++;
                    }
                    IMaterial iMat = world.resource.findMaterial(matName);
                    if (iMat == null) {
                        iMat = new RwMaterial(matName, matData, texNav);
                        world.resource.register(iMat);
                    }
                    Material mat = world.resource.makeInstance(iMat, null); // TODO: model is not null
                    // Create and init transform new part
                    IMesh iMesh = new IMesh();
                    iMesh.setVertices(geoData.numVerts, geoData.vertData, geoData.vertFmt);
                    iMesh.setIndices(geoData.indexMap.get(i));
                    iMesh.materialName = matName;
                    Mesh mesh = iMesh.rebuild();
                    mesh.transform(trn);
                    // Add meshpart to final model
                    mb.part(null, mesh, GL20.GL_TRIANGLES, mat);
                }
            }
            model = mb.end();
        } catch (IOException ex) {
            Logger.printWarning("DFF not found: " + modName);
        }
        return model;
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
    
    private static Matrix4 createTransform(RpFrameList list, RpFrame target) {
        Matrix4 trn = new Matrix4(target.combineMatrix4());
        while (target.hasParent()) {
            target = list.findParent(target);
            trn.mulLeft(new Matrix4(target.combineMatrix4()));
        }
        return trn;
    }
}
