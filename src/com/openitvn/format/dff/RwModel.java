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
package com.openitvn.format.dff;

import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.world.IGeometry;
import com.openitvn.unicore.world.ILayer;
import com.openitvn.unicore.world.IWorld;
import com.openitvn.unicore.world.IWorldCoord;
import com.openitvn.engine.renderware.RpSection;
import com.openitvn.engine.renderware.RpClump;
import com.openitvn.engine.renderware.RpGeometry;
import com.openitvn.engine.renderware.RpMaterial;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.engine.renderware.struct.RpFrame;
import com.openitvn.format.txd.RwTexture;
import com.openitvn.unicore.world.IMesh;
import com.openitvn.unicore.world.INode;
import com.openitvn.unicore.world.IWorldUnit;
import com.openitvn.unicore.world.resource.IModel;
import com.openitvn.unicore.world.resource.ITexture;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class RwModel extends IWorld {
    
    public RwModel() {
        this(null);
    }
    
    public RwModel(String name) {
        super(name);
        // TODO: some dff use X-up coordinate, (eg. love.dff from GTA3)
        setCoordinate(IWorldCoord.Zup, IWorldUnit.Meters);
        setInfo(new String[] {
            "GTA III Model Viewer",
            "Unit: Meters",
            "Coord: Z-Up"
        });
    }
    
    @Override
    public void fromData(DataStream ds) {
        fromData(ds, true);
    }
    
    public Collection<INode> fromData(DataStream dff, boolean allClump) {
        // prepare texture native cache from resource manager
        HashMap<String, RpTextureNative> texNavMap = new HashMap<>();
        for (ITexture tex : resource.getTextures()) {
            if (tex instanceof RwTexture) {
                RpTextureNative texData = ((RwTexture)tex).getTextureData();
                texNavMap.put(texData.textureName.toLowerCase(), texData);
            }
        }
        
        HashMap<RpFrame, INode> frameMap = new HashMap<>();
        int clumpId = 0;
        RpSection grand;
        while (dff.hasRemaining() && (grand = RpSection.fromData(dff, null)) != null) {
            if (grand instanceof RpClump) {
                // create nodes by frames
                for (RpFrame frmData : ((RpClump)grand).frameList.frames) {
                    boolean isGeometry = frmData.geometry != null;
                    // crete node/geometry
                    INode node = isGeometry ? new IGeometry() : new INode();
                    node.setName(frmData.name);
                    node.transform.localMatrix.set(frmData.combineMatrix4());
                    if (allClump) {
                        node.setLayerIndex(clumpId);
                    }
                    frameMap.put(frmData, node);
                    // create model and materials
                    if (isGeometry) {
                        RpGeometry geoData = frmData.geometry;
                        IModel mod = new IModel(frmData.name);
                        // meshes = materials
                        for (short i = 0; i < geoData.materials.size(); i++) {
                            // material
                            RpMaterial matData = geoData.materials.get(i);
                            RpTextureNative texData = texNavMap.get(matData.getTextureName().toLowerCase());
                            String matName;
                            if (texData != null) {
                                // create material name by add "m"
                                matName = texData.getMapperName()+"m";
                                // if have alpha channel, add "a"
                                if (matData.isMasked() || matData.color.a < 255) {
                                    matName += "a";
                                }
                            } else {
                                // default if texture not found
                                matName = frmData.name + "_untex" + i;
                            }
                            if (resource.findMaterial(matName) == null) {
                                RwMaterial mat = new RwMaterial(matName, matData, texData);
                                resource.register(mat);
                            }
                            // mesh
                            IMesh mesh = new IMesh();
                            mesh.setVertices(geoData.numVerts, geoData.vertData, geoData.vertFmt);
                            mesh.setIndices(geoData.indexMap.get(i));
                            mesh.materialName = matName;
                            mod.meshes.add(mesh);
                        }
                        resource.register(mod);
                    }
                }
                // resolve hierarchy by frame
                for (HashMap.Entry<RpFrame, INode> e : frameMap.entrySet()) {
                    INode geo = frameMap.get(e.getKey().parent);
                    e.getValue().attach(geo == null ? this : geo);
                }
                // add new layer for clump
                if (allClump) {
                    layers.add(new ILayer(clumpId, "Clump " + clumpId));
                    clumpId++;
                } else {
                    break;
                }
            }
        }
        return frameMap.values(); // return for vehicle management
    }
    
    public void loadTextureLibrary(DataStream txd) {
        RpSection grand = RpSection.fromData(txd, null);
        for (RpTextureNative texData : grand.getChildren(RpTextureNative.class)) {
            String texName = texData.getMapperName();
            if (resource.findTexture(texName) == null) {
                RwTexture tex = new RwTexture(texName, texData);
                resource.register(tex);
            }
        }
    }
}
