/*
 * Copyright (C) 2024 Thinh Pham
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
package com.openitvn.unicore.plugin.gta;

import com.openitvn.engine.renderware.RpClump;
import com.openitvn.engine.renderware.RpGeometry;
import com.openitvn.engine.renderware.RpMaterial;
import com.openitvn.engine.renderware.RpSection;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.format.col.ColFile;
import com.openitvn.format.dff.RwMaterial;
import com.openitvn.format.dff.RwWorld;
import com.openitvn.format.txd.RwTexture;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.plugin.gta.item.ItemOBJS;
import com.openitvn.unicore.plugin.gta.item.ItemPATHSegment;
import com.openitvn.unicore.world.IMesh;
import com.openitvn.unicore.world.resource.IMaterial;
import com.openitvn.unicore.world.resource.IModel;
import com.openitvn.unicore.world.resource.ITexture;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 *
 * @author Thinh Pham
 */
public class GWorld extends RwWorld {
    public static final int LAYER_NORMAL = 0,
                            LAYER_DISTANCE = 1,
                            LAYER_COLLISION = 2,
                            LAYER_CAR_PATH = 3;
    
    public final HashMap<Integer, String> modelNameMap = new HashMap<>(); // inst find
    public final HashMap<String, ColFile> collisionMap = new HashMap<>(); // inst find
    public final HashMap<Integer, Integer> modelLayerMap = new HashMap<>(); // layer find - objs.id, layer.id
    
    // store all resource names used by groups,
    // for quickly delete when deactive a group
    public final HashMap<String, GroupRegistry> groupRegistryMap = new HashMap<>(); // groupName, registry
    
    public GWorld(String name) {
        super(name);
        addLayer(LAYER_NORMAL, "Normal Map");
        addLayer(LAYER_DISTANCE, "Distance Map", false);
        addLayer(LAYER_COLLISION, "Collision Map", false);
        addLayer(LAYER_CAR_PATH, "Vehicle Path", false);
        // add blank material for default
        IMaterial mat = new IMaterial("M_Blank");
        resource.register(mat);
    }
    
    @Override
    public void destruct() {
        super.destruct();
        modelNameMap.clear();
        collisionMap.clear();
        modelLayerMap.clear();
        groupRegistryMap.clear();
    }
    
    public void addOBJS(ItemOBJS objs, GroupRegistry reg) {
        // check ignore pattern
        String regex = GameConfig.getWorldIgnorePattern();
        if (regex != null) {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(objs.modName).find()) {
                return;
            }
        }
        
        ResourceModel res = ResourceModel.getInstance();
        // load textures
        try (EntryStream ts = res.getEntryStream(objs.txdName, "txd")) {
            loadTexDic(ts);
        } catch (IOException ex) {
            Logger.printWarning("TXD not found: " + objs.txdName);
        }
        // prepare texture native cache from resource manager
        HashMap<String, RpTextureNative> texNavMap = new HashMap<>();
        for (ITexture tex : resource.getTextures()) {
            if (tex instanceof RwTexture) {
                RpTextureNative texData = ((RwTexture)tex).getTextureData();
                if (!texData.textureName.isEmpty()) {
                    texNavMap.put(texData.textureName.toLowerCase(), texData);
                }
                if (!texData.maskName.isEmpty()) {
                    texNavMap.put(texData.maskName.toLowerCase(), texData);
                }
            }
        }
        // load model
        try (EntryStream ms = res.getEntryStream(objs.modName, "dff")) {
            RpClump clump = RpSection.loadRoot(ms, RpClump.class);
            if (clump != null) {
                // only load root geometry as model
                RpGeometry geoData = clump.getRootGeometry();
                if (geoData != null) {
                    IModel mod = new IModel("SM_"+objs.modName);
                    reg.modNames.add(objs.modName);
                    // meshes = materials
                    int k = 1;
                    for (short i = 0; i < geoData.materials.size(); i++) {
                        RpMaterial matData = geoData.materials.get(i);
                        // search texture by name which defined in material
                        String texName = matData.getMaskName();
                        RpTextureNative texNav = texNavMap.get(texName.toLowerCase());
                        if (texNav == null) {
                            texName = matData.getTextureName();
                            texNav = texNavMap.get(texName.toLowerCase());
                        }
                        // create material
                        String matName = "M_";
                        if (texName.isEmpty()) {
                            matName += objs.modName + k;
                            k++;
                        } else {
                            matName += texName;
                            if (texNav == null) {
                                Logger.printWarning("Texture not found: %s (%s.txd)", texName, objs.txdName);
                            }
                        }
                        // register new material when missing
                        if (!resource.containsMaterial(matName)) {
                            RwMaterial mat = new RwMaterial(matName, matData, texNav);
                            resource.register(mat);
                            reg.matNames.add(matName);
                        }
                        // mesh
                        IMesh mesh = new IMesh();
                        mesh.setVertices(geoData.numVerts, geoData.vertData, geoData.vertFmt);
                        mesh.setIndices(geoData.indexMap.get(i));
                        mesh.materialName = matName;
                        mod.meshes.add(mesh);
                    }
                    resource.register(mod);
                    modelNameMap.put(objs.modId, objs.modName);
                }
            }
        } catch (IOException ex) {
            Logger.printWarning("DFF not found: " + objs.modName);
        }
    }
    
    void executeOBJSGroup(String groupName, BufferedReader br, boolean bActive) {
        GroupRegistry reg = groupRegistryMap.get(groupName);
        if (bActive) {
            // register new reg for this group
            if (reg == null) {
                reg = new GroupRegistry();
                groupRegistryMap.put(groupName, reg);
            }
            // parse collision list
            ResourceModel res = ResourceModel.getInstance();
            String colFile = groupName.substring(0, groupName.length() - 4);
            try (EntryStream cs = res.getEntryStream(colFile, "col")) {
                int fourCC;
                while (cs.remaining() > 4 && (fourCC = cs.getInt()) != 0) {
                    ColFile col = new ColFile(fourCC, cs);
                    if (col.model != null) {
                        resource.register(col.model);
                        reg.modNames.add(col.model.getName());
                        collisionMap.put(col.objsName, col);
                        reg.colNames.add(col.objsName);
                    }
                }
            } catch (IOException ex) { }
            // parse objs list
            String[] args;
            while ((args = ScriptHelper.parseLineByComma(br)) != null) {
                try {
                    ItemOBJS objs = new ItemOBJS(args);
                    // cancel special objects
                    switch (GameConfig.getAlias()) {
                        case GameConfig.ALIAS_III:
                            if ((160 <= objs.modId && objs.modId <= 169) || // wheels
                                (170 <= objs.modId && objs.modId <= 184) || // weapons
                                (196 <= objs.modId && objs.modId <= 199)) { // misc
                                continue;
                            }
                            break;
                            
                        case GameConfig.ALIAS_VC:
                            if ((130 <= objs.modId && objs.modId <= 239) || // vehicles
                                (250 <= objs.modId && objs.modId <= 257)) { // wheels
                                continue;
                            }
                            break;
                    }
                    addOBJS(objs, reg);
                    int layer = objs.isLOD() ? LAYER_DISTANCE : LAYER_NORMAL;
                    modelLayerMap.put(objs.modId, layer);
                } catch (IllegalArgumentException ex) { }
            }
        } else if (reg != null) {
            // null registry means the group already unregistered
            resource.deleteModels(reg.modNames, true);
            resource.deleteMaterials(reg.matNames, true);
            resource.deleteTextures(reg.texNames, true);
            for (String colName : reg.colNames) {
                collisionMap.remove(colName);
            }
            groupRegistryMap.remove(groupName);
        }
    }
    
    public String getModNameById(int id) {
        return modelNameMap.get(id);
    }
}
