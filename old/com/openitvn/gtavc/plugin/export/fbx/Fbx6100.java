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

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.openitvn.engine.renderware.RpGeometry;
import com.openitvn.engine.renderware.struct.RpColor;
import com.openitvn.gtavc.core.GtaCollision;
import com.openitvn.gtavc.gui.g3d.GtaInstance;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class Fbx6100 implements Disposable {
    
    // settings
    public boolean exportGeos = true;
    public boolean exportCols = false;
    public boolean skipExistGeo = false;
    
    // content
    private final HashMap<String, FbxMaterial> materials = new HashMap<>(); // uniqueName, fbxMat
    private final ArrayList<FbxInstance> instances = new ArrayList<>();
    private final HashMap<String, Integer> instNameCounter = new HashMap<>();
    private final ArrayList<FbxCollision> collisions = new ArrayList<>();
    
    public void addGeometry(String modName, String txdName, RpGeometry rGeo, Vector3 pos, Vector3 rot, Vector3 scl) {
        int sameCount = 0;
        String uniqueName = modName;
        if (instNameCounter.containsKey(modName)) {
            if (skipExistGeo)
                return;
            sameCount = instNameCounter.get(modName) + 1;
            uniqueName += "_ncl1_" + sameCount;
        }
        instNameCounter.put(modName, sameCount);
        FbxInstance finst = new FbxInstance(uniqueName, txdName, null, rGeo, pos, rot, scl);
        finst.prepareMaterials();
        instances.add(finst);
        for (FbxMaterial fmat : finst.materials)
            materials.put(fmat.matName, fmat);
    }
    
    public void addGtaModel(String modName, String txdName, GtaInstance gInst, Vector3 pos, Vector3 rot, Vector3 scl) {
//        for (RwGeometry rGeo : gModel.getGeometries())
//            addGeometry(modName, txdName, rGeo, pos, rot, scl);
        
        int sameCount = 0;
        String uniqueName = modName;
        if (instNameCounter.containsKey(modName)) {
            if (skipExistGeo)
                return;
            sameCount = instNameCounter.get(modName) + 1;
            uniqueName += "_ncl1_" + sameCount;
        }
        instNameCounter.put(modName, sameCount);
        FbxInstance finst = new FbxInstance(uniqueName, txdName, gInst.inst, gInst.gModel.getGeometries().get(0), pos, rot, scl);
        finst.prepareMaterials();
        instances.add(finst);
        for (FbxMaterial fmat : finst.materials)
            materials.put(fmat.matName, fmat);
        
        GtaCollision col = gInst.gModel.gCol;
        if (col != null) {
            // export collision meshes
            if (col.faces.length > 0)
                collisions.add(new FbxCollision(uniqueName, col, pos, rot, scl));
            // TODO: export primitive collisions
        }
    }
    
    public void export(File out) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(out);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                PrintStream ps = new PrintStream(bos)) {
            writeHeader(ps);
            writeDefinitions(ps);
            ps.println("Objects: {");
            writeGeometries(ps);
            writeMaterials(ps);
            writeTextures(ps);
            writeGlobalSettings(ps);
            ps.println("}");
            writeConnection(ps);
            ps.flush();
        }
    }
    
    private void writeGlobalSettings(PrintStream ps) {
        ps.println("    GlobalSettings: {");
        ps.println("        Version: 1000");
        ps.println("        Properties60: {");
        ps.println("            Property: \"UnitScaleFactor\", \"double\", \"\", 100");
        ps.println("            Property: \"OriginalUnitScaleFactor\", \"double\", \"\", 1");
        ps.println("        }");
        ps.println("    }");
    }
    
    private void writeHeader(PrintStream ps) {
        Calendar now = Calendar.getInstance();
        ps.println("; FBX 6.1.0 project file");
        ps.println("FBXHeaderExtension: {");
        ps.println("	FBXHeaderVersion: 1003");
        ps.println("	FBXVersion: 6100");
        ps.println("	CreationTimeStamp: {");
        ps.println("		Version: 1000");
        ps.println("		Year: " + now.get(Calendar.YEAR));
        ps.println("		Month: " + now.get(Calendar.MONTH));
        ps.println("		Day: " + now.get(Calendar.DAY_OF_MONTH));
        ps.println("		Hour: " + now.get(Calendar.HOUR_OF_DAY));
        ps.println("		Minute: " + now.get(Calendar.MINUTE));
        ps.println("		Second: " + now.get(Calendar.SECOND));
        ps.println("		Millisecond: " + now.get(Calendar.MILLISECOND));
        ps.println("	}");
        ps.println("	Creator: \"FBX 2006 Exporter for GTA Viewer v1.0 - Author: Thinh Pham");
        ps.println("}");
    }
    
    private void writeDefinitions(PrintStream ps) {
        int numMods = 0;
        if (exportCols) numMods += collisions.size();
        if (exportGeos) numMods += instances.size();
        ps.println("Definitions: {");
        ps.println("	Version: 100");
        ps.println("	Count: " + (numMods + materials.size()*2));
        ps.println("	ObjectType: \"Model\" {");
        ps.println("		Count: " + numMods);
        ps.println("	}");
        ps.println("	ObjectType: \"Material\" {");
        ps.println("		Count: " + materials.size());
        ps.println("	}");
        ps.println("	ObjectType: \"Texture\" {");
        ps.println("		Count: " + materials.size());
        ps.println("	}");
        ps.println("}");
    }
    
    private void writeGeometries(PrintStream ps) {
        // write geometries
        Vector3 pos = new Vector3();
        Vector3 rot = new Vector3();
        Vector3 scl = new Vector3();
        for (FbxInstance inst : instances) {
            ps.println("	Model: \"Model::" + inst.modName + "\", \"Mesh\" {");
            ps.println("		Version: 232");
            ps.println("		Properties60: {");
            if (inst.modInst != null) {
                Matrix4 trn = inst.modInst.transform;
                trn.getTranslation(pos);
                trn.getScale(scl);
                getFbxRotation(trn.getRotation(new Quaternion()), rot);
                ps.println("			Property: \"Lcl Translation\", \"Lcl Translation\", \"A\", " + printVec3(pos));
                ps.println("			Property: \"Lcl Rotation\", \"Lcl Rotation\", \"A\", " + printVec3(rot));
                ps.println("			Property: \"Lcl Scaling\", \"Lcl Scaling\", \"A\", " + printVec3(scl));
            } else if (inst.rGeo != null) {
                ps.println("			Property: \"PreRotation\", \"Vector3D\", \"\", -90,0,0");
                ps.println("			Property: \"RotationActive\", \"bool\", \"\", 1");
                ps.println("			Property: \"Lcl Translation\", \"Lcl Translation\", \"A\", " + printVec3(inst.rPos));
                ps.println("			Property: \"Lcl Rotation\", \"Lcl Rotation\", \"A\", " + printVec3(inst.rRot));
                ps.println("			Property: \"Lcl Scaling\", \"Lcl Scaling\", \"A\", " + printVec3(inst.rScl));
            }
            ps.println("		}");
            ps.println("		Vertices: " + inst.getStringVertices());
            ps.println("		PolygonVertexIndex: " + inst.getStringIndices());
            writeLayerNormal(inst, ps);
            writeLayerColor(inst, ps);
            writeLayerMaterial(inst, ps);
            writeLayerTexture(inst, false, ps);
            writeLayerUV(inst, false, ps);
//            boolean isAlpha = inst.haveAlphaChannel();
//            if (isAlpha) {
//                writeLayerTexture(inst, true, ps);
//                writeLayerUV(inst, true, ps);
//            }
            // layer names
            ps.println("		Layer: 0 {");
            ps.println("			Version: 100");
            writeLayerName("LayerElementMaterial", ps);
            writeLayerName("LayerElementTexture", ps);
            if (inst.getTexCoordCount() > 0) writeLayerName("LayerElementUV", ps);
            // for alpha only
//            if (isAlpha) {
//                writeLayerName("LayerElementTransparentTextures", ps);
//            }
//            if (isAlpha && inst.getTexCoordCount() > 0) {
//                writeLayerName("LayerElementTransparentUV", ps);
//            }
            if (inst.rGeo.hasNormal()) writeLayerName("LayerElementNormal", ps);
            if (inst.rGeo.prelit != null) writeLayerName("LayerElementColor", ps);
            ps.println("		}");
            // end layer names
            ps.println("	}");
        }
        
        // write collisions
        if (exportCols) {
            for (FbxCollision col : collisions) {
                ps.println("	Model: \"Model::CLM_" + col.modName + "\", \"Mesh\" {");
                ps.println("		Version: 232");
                ps.println("		Properties60: {");
                ps.println("			Property: \"PreRotation\", \"Vector3D\", \"\", -90,0,0");
                ps.println("			Property: \"RotationActive\", \"bool\", \"\", 1");
                ps.println("			Property: \"Lcl Translation\", \"Lcl Translation\", \"A\", " + printVec3(col.rPos));
                ps.println("			Property: \"Lcl Rotation\", \"Lcl Rotation\", \"A\", " + printVec3(col.rRot));
                ps.println("			Property: \"Lcl Scaling\", \"Lcl Scaling\", \"A\", " + printVec3(col.rScl));
                ps.println("		}");
                ps.println("		Vertices: " + col.getStringVertices());
                ps.println("		PolygonVertexIndex: " + col.getStringIndices());
                ps.println("	}");
            }
        }
    }
    
    private static Vector3 getFbxRotation(Quaternion quat, Vector3 euler) {
        // https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
        // https://ipfs.io/ipfs/QmXoypizjW3WknFiJnKLwHCnL72vedxjQkDDP1mXWo6uco/wiki/Conversion_between_quaternions_and_Euler_angles.html
        float x = quat.x;
        float y = quat.y;
        float z = quat.z;
        float w = -quat.w;
        double ysqr = y * y;
        double t0 = -2.0f * (ysqr + z * z) + 1.0f;
        double t1 = +2.0f * (x * y - w * z);
        double t2 = -2.0f * (x * z + w * y);
        double t3 = +2.0f * (y * z - w * x);
        double t4 = -2.0f * (x * x + ysqr) + 1.0f;
        t2 = t2 > 1.0f ? 1.0f : t2;
        t2 = t2 < -1.0f ? -1.0f : t2;
        euler.x = (float) Math.toDegrees(Math.atan2(t3, t4)); // roll
        euler.y = (float) Math.toDegrees(Math.asin(t2)); // pitch
        euler.z = (float) Math.toDegrees(Math.atan2(t1, t0)); // yaw
        return euler;
    }
    
    private static String printVec3(Vector3 v) {
        return v.x + "," + v.y + "," + v.z;
    }
    
    private void writeLayerNormal(FbxInstance finst, PrintStream ps) {
        if (finst.rGeo.hasNormal()) {
            ps.println("		LayerElementNormal: 0 {");
            ps.println("			Version: 101");
            ps.println("			MappingInformationType: \"ByVertice\"");
            ps.println("			ReferenceInformationType: \"Direct\"");
            ps.println("			Normals: " + finst.getStringNormals());
            ps.println("		}");
        }
    }
    
    private void writeLayerColor(FbxInstance inst, PrintStream ps) {
        if (inst.rGeo.prelit != null) {
            ps.println("		LayerElementColor: 0 {");
            ps.println("			Version: 101");
            ps.println("			MappingInformationType: \"ByPolygonVertex\"");
            ps.println("			ReferenceInformationType: \"IndexToDirect\"");
            ps.println("			Colors: " + inst.getStringColors());
            ps.println("			ColorIndex: " + inst.getStringUVIndices());
            ps.println("		}");
        }
    }
    
    private void writeLayerMaterial(FbxInstance inst, PrintStream ps) {
        ps.println("		LayerElementMaterial: 0 {");
        ps.println("			Version: 101");
        ps.println("			MappingInformationType: \"ByPolygon\"");
        ps.println("			ReferenceInformationType: \"IndexToDirect\"");
        ps.println("			Materials: " + inst.getStringMaterials());
        ps.println("		}");
    }
    
    private void writeLayerTexture(FbxInstance inst, boolean isAlpha, PrintStream ps) {
        String name = isAlpha ? "LayerElementTransparentTextures" : "LayerElementTexture";
        ps.println("		" + name + ": 0 {");
        ps.println("			Version: 101");
        ps.println("			MappingInformationType: \"ByPolygon\"");
        ps.println("			ReferenceInformationType: \"IndexToDirect\"");
        ps.println("			TextureId: " + inst.getStringMaterials());
        ps.println("		}");
    }
    
    private void writeLayerUV(FbxInstance inst, boolean isAlpha, PrintStream ps) {
        if (inst.getTexCoordCount() > 0) {
            String name = isAlpha ? "LayerElementTransparentUV" : "LayerElementUV";
            ps.println("		" + name + ": 0 {");
            ps.println("			Version: 101");
            ps.println("			MappingInformationType: \"ByPolygonVertex\"");
            ps.println("			ReferenceInformationType: \"IndexToDirect\"");
            ps.println("			UV: " + inst.getStringTexCoord(0));
            ps.println("			UVIndex: " + inst.getStringUVIndices());
            ps.println("		}");
        }
    }
    
    private static void writeLayerName(String name, PrintStream ps) {
        ps.println("			LayerElement: {");
        ps.println("				Type: \""+name+"\"");
        ps.println("			}");
    }
    
    private void writeMaterials(PrintStream ps) {
        float f = 1 / 255f;
        for (FbxMaterial fmat : materials.values()) {
            RpColor c = fmat.rMat.color;
            String strColor = c.r*f + "," + c.g*f + "," + c.b*f;
            ps.println("	Material: \"Material::" + fmat.matName + "\", \"\" {");
            ps.println("		Version: 102");
            ps.println("		Properties60: {");
            ps.println("			Property: \"SpecularColor\", \"Color\", \"A\", " + strColor);
            if (!fmat.rMat.textured) {
                ps.println("			Property: \"AmbientColor\", \"Color\", \"A\", " + strColor);
                ps.println("			Property: \"AmbientFactor\", \"Number\", \"A\", " + fmat.rMat.ambient);
                ps.println("			Property: \"DiffuseColor\", \"Color\", \"A\", " + strColor);
                ps.println("			Property: \"DiffuseFactor\", \"Number\", \"A\", " + fmat.rMat.diffuse);
                ps.println("			Property: \"SpecularFactor\", \"Number\", \"A\", " + fmat.rMat.specular);
            } else {
                ps.println("			Property: \"SpecularFactor\", \"Number\", \"A\", 0");
            }
            if (c.a < 255) {
                float op = c.a*f;
                ps.println("			Property: \"TransparentColor\", \"Color\", \"A\", 1,1,1");
                ps.println("			Property: \"TransparencyFactor\", \"Number\", \"A\", " + (1-op));
                ps.println("			Property: \"Opacity\", \"double\", \"A\", " + op);
            }
            ps.println("		}");
            ps.println("	}");
        }
    }
    
    private void writeTextures(PrintStream ps) {
        for (FbxMaterial fmat : materials.values()) {
            ps.println("	Texture: \"Texture::" + fmat.texName + "\", \"\" {");
            ps.println("		Type: \"Texture\"");
            ps.println("		Version: 202");
            if (fmat.texPath != null)
                ps.println("		RelativeFilename: \"" + fmat.texPath + "\"");
            ps.println("	}");
//            if (fMat.isAlpha) {
//                ps.println("	Texture: \"Texture::" + fMat.maskName + "\", \"\" {");
//                ps.println("		Type: \"Texture\"");
//                ps.println("		Version: 202");
//                if (fMat.texPath != null)
//                    ps.println("		RelativeFilename: \"" + fMat.texPath + "\"");
//                ps.println("	}");
//            }
        }
    }
    
    private void writeConnection(PrintStream ps) {
        ps.println("Connections:  {");
        for (FbxInstance finst : instances) {
            ps.println("	Connect: \"OO\", \"Model::" + finst.modName + "\", \"Model::Scene\"");
            for (FbxMaterial fmat : finst.materials) {
                ps.println("	Connect: \"OO\", \"Material::" + fmat.matName + "\", \"Model::" + finst.modName + "\"");
                ps.println("	Connect: \"OO\", \"Texture::" + fmat.texName + "\", \"Model::" + finst.modName + "\"");
//                if (fMat.isAlpha)
//                    ps.println("    Connect: \"OO\", \"Texture::" + fMat.maskName + "\", \"Model::" + fInst.modName + "\"");
            }
        }
        if (exportCols) {
            for (FbxCollision fcol : collisions) {
                ps.println("	Connect: \"OO\", \"Model::CLM_" + fcol.modName + "\", \"Model::Scene\"");
            }
        }
        ps.println("}");
    }

    @Override
    public void dispose() {
        instances.clear();
        materials.clear();
        instNameCounter.clear();
    }
}
