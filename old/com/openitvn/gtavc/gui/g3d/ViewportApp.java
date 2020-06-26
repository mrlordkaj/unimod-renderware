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

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.openitvn.engine.renderware.struct.RpFrame;
import com.openitvn.engine.renderware.RwGeometry;
import com.openitvn.engine.renderware.RpMaterial;
import com.openitvn.engine.renderware.RwClump;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.gtavc.core.GtaCollision;
import com.openitvn.gtavc.core.RwTextureHelper;
import com.openitvn.gtavc.core.item.CARSEntry;
import com.openitvn.gtavc.core.item.INSTEntry;
import com.openitvn.gtavc.core.item.OBJSEntry;
import com.openitvn.gtavc.core.item.NULLEntry;
import com.openitvn.gtavc.gui.Main;
import com.openitvn.gtavc.gui.VehicleTableModel;
import com.openitvn.gtavc.plugin.export.fbx.Fbx6100;
import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.plugin.gta.GameConfig;
import java.awt.Canvas;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JProgressBar;

/**
 *
 * @author Thinh Pham
 */
public class ViewportApp implements ApplicationListener {
    
    static final int GRID_WIDTH = 10;
    static final int GRID_HEIGHT = 10;
    
    private final LwjglCanvas canvas;
    private ViewportMode mode;
    
    public final VehicleTableModel vehicleModel = new VehicleTableModel();
    
    //single model mode
    private GtaModel gtaModel;
    private ModelInstance modInst;
    private PerspectiveCamera modCam;
    private CameraInputController modCtrl;
    
    //vehicle
    private GtaVehicleModel gtaVehicleModel;
    private GtaWheelModel gtaWheelModel;
    private ModelInstance wheelInstance;
    private final HashMap<Integer, String> wheelLib = new HashMap<>();
    
    // map mode
    final GWorldMap mapView = GWorldMap.getInstance();
    final GWorldModel modView = GWorldModel.getInstance();
    
    //renderers
    private ModelBatch mb;
    private Environment env;
    
    //singleton app
    private static ViewportApp instance;
    public static ViewportApp getInstance() {
        if (instance == null) {
            LwjglApplicationConfiguration.disableAudio = true;
            instance = new ViewportApp();
        }
        return instance;
    }
    
    private ViewportApp() {
        canvas = new LwjglCanvas(this);
    }
    
    public Canvas getCanvas() {
        return canvas.getCanvas();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Draw Grid">
    
    //http://stackoverflow.com/questions/24215500/healthy-way-of-drawing-grid-lines-in-libgdx
    private ImmediateModeRenderer20 lineRenderer;
    
    private void drawLine(float x1, float y1, float z1,
                            float x2, float y2, float z2,
                            Color c) {
        lineRenderer.color(c);
        lineRenderer.vertex(x1, y1, z1);
        lineRenderer.color(c);
        lineRenderer.vertex(x2, y2, z2);
    }
    
    private void drawGrid(PerspectiveCamera cam) {
        int startX = -GRID_WIDTH / 2;
        int startY = -GRID_HEIGHT / 2;
        
        lineRenderer.begin(cam.combined, GL20.GL_LINES);
        
        for (int x = 0; x <= GRID_WIDTH; x++) {
            // draw vertical
            drawLine(x + startX, 0, startY,
                    x + startX, 0, GRID_HEIGHT + startY,
                    Color.DARK_GRAY);
        }

        for (int y = 0; y <= GRID_HEIGHT; y++) {
            // draw horizontal
            drawLine(startX, 0, startY + y,
                    GRID_WIDTH + startX, 0, startY + y,
                    Color.DARK_GRAY);
        }
        
        lineRenderer.end();
    }
    
    //</editor-fold>
    
    @Override
    public void create() {
        lineRenderer = new ImmediateModeRenderer20(false, true, 0);
        mb = new ModelBatch();
        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
        
        // setup viewers
        mapView.init();
        modView.init();
        
        //setup single viewpot
        modCam = new PerspectiveCamera();
        modCam.fieldOfView = 45;
        modCam.near = 0.2f;
        modCam.far = 300f;
        modCam.position.set(GRID_WIDTH, Math.max(GRID_WIDTH, GRID_HEIGHT), GRID_HEIGHT);
        modCam.lookAt(0, 0, 0);
        modCtrl = new CameraInputController(modCam);
        
        Gdx.input.setInputProcessor(mapView.camCtrl);
    }
    
    @Override
    public void resize(int width, int height) {
        mapView.resize(width, height);
        modCam.viewportWidth = width;
        modCam.viewportHeight = height;
    }
    
    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        
        // update camera
        switch (mode) {
            case MapNormal:
            case MapDistance:
            case MapCollision:
                mapView.draw(mb, env);
                break;
                
            case SingleModel:
            case VehicleNormal:
            case VehicleDamaged:
            case VehicleDistance:
                modCam.update();
                modCtrl.update();
                drawGrid(modCam);
                mb.begin(modCam);
                switch (mode) {
                    case SingleModel:
                    case VehicleDistance:
                        if (modInst != null)
                            mb.render(modInst, env);
                        break;
                        
                    case VehicleNormal:
                    case VehicleDamaged:
                        if (modInst != null)
                            mb.render(modInst, env);
                        if (wheelInstance != null)
                            mb.render(wheelInstance, env);
                        break;
                }
                mb.end();
                break;
        }
    }
    
    @Override public void pause() { }

    @Override public void resume() { }
    
    @Override
    public void dispose() {
        mb.dispose();
        instance = null;
    }
    
    public void setSingleModel(String modName, String txdName) throws IOException {
        if (gtaModel != null)
            gtaModel.dispose();
        gtaModel = new GtaModel(modName, txdName, GtaModel.MeshType.AllMesh);
        modInst = new ModelInstance(gtaModel.getModel());
    }
    
    public void setVehicle(CARSEntry e, ViewportMode viewMode) throws IOException {
        if (gtaWheelModel == null)
            gtaWheelModel = new GtaWheelModel();
        if (gtaVehicleModel == null) {
            gtaVehicleModel = new GtaVehicleModel(e, viewMode);
        } else if (!gtaVehicleModel.modName.equals(e.modName)) {
            gtaVehicleModel.dispose();
            gtaVehicleModel = new GtaVehicleModel(e, viewMode);
        }
        if (viewMode != mode) {
            gtaVehicleModel.changeViewMode(viewMode);
            mode = viewMode;
        }
        modInst = new ModelInstance(gtaVehicleModel.getModel());
        switch (viewMode) {
            case VehicleNormal:
            case VehicleDamaged:
                gtaWheelModel.setWheel(gtaVehicleModel, wheelLib.get(e.wheelModelId));
                wheelInstance = new ModelInstance(gtaWheelModel.getModel());
                break;
        }
    }
    
    public ViewportMode getViewpotMode() {
        return mode;
    }
    
    public void setViewpotMode(ViewportMode mode) {
        this.mode = mode;
        switch (mode) {
            case MapNormal:
            case MapDistance:
            case MapCollision:
                Gdx.input.setInputProcessor(mapView.camCtrl);
                mapView.setViewportMode(mode);
                break;
                
            case SingleModel:
                modInst = (gtaModel == null) ? null : new ModelInstance(gtaModel.getModel());
                Gdx.input.setInputProcessor(modCtrl);
                break;
                
            case VehicleNormal:
            case VehicleDamaged:
            case VehicleDistance:
                modInst = (gtaVehicleModel == null) ? null : new ModelInstance(gtaVehicleModel.getModel());
                Gdx.input.setInputProcessor(modCtrl);
                break;
        }
    }
    
    public void addOBJS(OBJSEntry objs, GtaCollision col) throws Exception {
        int modId = objs.modelId;
        if (GameConfig.getWheelIds().contains(modId)) {
            //TODO: just a temporary method, need a better solution to determine wheel entries
            wheelLib.put(modId, objs.modName);
        } else {
            mapView.addOBJS(objs, col);
        }
    }
    
    public void removeOBJS(OBJSEntry e) {
        mapView.removeOBJS(e);
    }
    
    public void addCARS(CARSEntry e) {
        vehicleModel.add(e);
    }
    
    public void select(NULLEntry entry) {
        switch (entry.getType()) {
            case INST:
                INSTEntry inst = (INSTEntry)entry;
                mapView.moveCameraTo(inst.pos);
                break;
        }
    }
    
    public boolean exportImg, skipExistImg, skipUnusedImg;
        
    public void exportFBX(Fbx6100 fbx, File fbxOut) throws Exception {
        JProgressBar progBar = Main.getInstance().getProgressBar();
        int k = 0;
        
        if (exportImg) {
            // export textures as png
            Collection<GtaTexture> gTexs = new ArrayList<>();
            switch (mode) {
                case MapNormal:
                case MapDistance:
                    gTexs = mapView.exportTextures(skipUnusedImg);
                    break;
                    
                case SingleModel:
                    if (gtaModel != null) {
                        if (skipUnusedImg) {
                            ArrayList<String> mappedNames = new ArrayList<>();
                            String texDic = gtaModel.txdName;
                            for (RwGeometry rGeo : gtaModel.getGeometries()) {
                                for (RpMaterial rMat : rGeo.materials) {
                                    if (rMat.textured) {
                                        String nameMap = GtaTextureManager.getMapperName(texDic, rMat.getTextureName());
                                        if (!mappedNames.contains(nameMap))
                                            mappedNames.add(nameMap);
                                    }
                                }
                            }
                            gTexs = GtaTextureManager.getTexturesByMapperNames(mappedNames);
                        } else {
                            gTexs = GtaTextureManager.getTexturesByTexDicName(gtaModel.txdName);
                        }
                    }
                    break;
                    
                case VehicleNormal:
                case VehicleDamaged:
                case VehicleDistance:
                    if (skipUnusedImg) {
                        ArrayList<String> mappedNames = new ArrayList<>();
                        String texDic = gtaVehicleModel.txdName;
                        if (gtaVehicleModel != null) {
                            for (RwGeometry rGeo : gtaVehicleModel.getGeometries()) {
                                for (RpMaterial rMat : rGeo.materials) {
                                    if (rMat.textured) {
                                        String nameMap = GtaTextureManager.getMapperName(texDic, rMat.getTextureName());
                                        if (!mappedNames.contains(nameMap))
                                            mappedNames.add(nameMap);
                                    }
                                }
                            }
                        }
                        if (mode != ViewportMode.VehicleDistance && gtaWheelModel != null) {
                            for (RwGeometry rGeo : gtaWheelModel.getGeometries()) {
                                for (RpMaterial rMat : rGeo.materials) {
                                    if (rMat.textured) {
                                        String nameMap = GtaTextureManager.getMapperName(texDic, rMat.getTextureName());
                                        if (!mappedNames.contains(nameMap))
                                            mappedNames.add(nameMap);
                                    }
                                }
                            }
                        }
                        gTexs = GtaTextureManager.getTexturesByMapperNames(mappedNames);
                    } else {
                        ArrayList<String> txdNames = new ArrayList<>();
                        if (gtaVehicleModel != null)
                            txdNames.add(gtaVehicleModel.txdName);
                        if (mode != ViewportMode.VehicleDistance && gtaWheelModel != null)
                            txdNames.add(gtaWheelModel.txdName);
                        gTexs = GtaTextureManager.getTexturesByTexDicNames(txdNames);
                    }
                    break;
            }
            progBar.setMaximum(gTexs.size());
            progBar.setValue(k);
            String outPath = fbxOut.getParent() + File.separator;
            for (GtaTexture gTex : gTexs) {
                RpTextureNative rTex = gTex.rTex;
                if (rTex != null) {
                    String relName = "Textures/" + rTex.getMapperName() + ".png";
                    progBar.setString("Exporting: " + relName);
                    File out = new File(outPath + relName);
                    if (!skipExistImg || !Files.exists(out.toPath())) {
                        out.mkdirs();
                        out.createNewFile();
                        ImageIO.write(RwTextureHelper.toBufferedImage(gTex.rTex, 0), "png", out);
                    }
                }
                progBar.setValue(++k);
            }
        } else {
            progBar.setMaximum(1);
            progBar.setValue(k);
        }
        
        // export fbx file
        progBar.setString("Exporting: " + fbxOut.getName());
        switch (mode) {
            case MapNormal:
            case MapDistance:
            case MapCollision:
                for (GtaInstance gInst : mapView.instances) {
                    GtaModel gModel = gInst.gModel;
                    Vector3 pos = gInst.getFBXPosition();
                    Vector3 rot = gInst.getFBXRotation();
                    Vector3 scl = gInst.getScale();
                    fbx.addGtaModel(gModel.modName, gModel.txdName, gInst, pos, rot, scl);
                }
                break;
                
            case SingleModel:
                if (gtaModel != null) {
                    RwClump rClump = gtaModel.rClump;
                    ArrayList<RwGeometry> rGeos = rClump.geometries;
                    for (RwGeometry rGeo : rGeos) {
                        RpFrame frame = rGeo.frame;
                        RpFrame[] frmSeq = rClump.frameList.getFrameSequence(frame);
                        Matrix4 trn = GtaModel.createTransform(frmSeq);
                        Vector3[] fbxTrn = rw2FbxTransform(trn);
                        fbx.addGeometry(frame.name, gtaModel.txdName, rGeo, fbxTrn[0], fbxTrn[1], fbxTrn[2]);
                    }
                }
                break;
                
            case VehicleNormal:
            case VehicleDamaged:
                // export wheels model
                if (gtaVehicleModel != null && gtaWheelModel != null) {
                    RwClump rClump = gtaVehicleModel.rClump;
                    for (RwGeometry rGeo : gtaWheelModel.getGeometries()) {
                        RpFrame frame = rGeo.frame;
                        RpFrame[] frmSeq = rClump.frameList.getFrameSequence(frame);
                        Matrix4 trn = GtaModel.createTransform(frmSeq);
                        if (frame.name.startsWith("wheel_l"))
                            trn.rotate(0, 1, 0, 180);
                        trn.scl(gtaWheelModel.getScale());
                        Vector3[] fbxTrn = rw2FbxTransform(trn);
                        fbx.addGeometry(frame.name, gtaWheelModel.txdName, rGeo, fbxTrn[0], fbxTrn[1], fbxTrn[2]);
                    }
                }
                
            case VehicleDistance:
                //export vehicle main model
                if (gtaVehicleModel != null) {
                    RwClump rClump = gtaVehicleModel.rClump;
                    for (RwGeometry rGeo : gtaVehicleModel.getGeometries()) {
                        RpFrame frame = rGeo.frame;
                        RpFrame[] frmSeq = rClump.frameList.getFrameSequence(frame);
                        Matrix4 trn = GtaModel.createTransform(frmSeq);
                        Vector3[] fbxTrn = rw2FbxTransform(trn);
                        fbx.addGeometry(frame.name, gtaVehicleModel.txdName, rGeo, fbxTrn[0], fbxTrn[1], fbxTrn[2]);
                    }
                }
                break;
        }
        fbx.export(fbxOut);
        fbx.dispose();
        progBar.setValue(++k);
    }
    
    static Vector3[] gl2FbxTransform(Matrix4 trn) {
        Vector3 pos = new Vector3();
        Vector3 rot = new Vector3();
        Vector3 scl = new Vector3();
        
        trn.getTranslation(pos);
        trn.getScale(scl);
        Quaternion quat = trn.getRotation(new Quaternion());
        rot.x = quat.getRoll();
        rot.y = quat.getPitch();
        rot.z = quat.getYaw();
        
        return new Vector3[] { pos, rot, scl };
    }
    
    static Vector3[] rw2FbxTransform(Matrix4 trn) {
        Vector3 pos = new Vector3();
        Vector3 scl = new Vector3();
        
        // get scale
        trn.getScale(scl);
        
        // get position
        Vector3 tmp = trn.getTranslation(new Vector3());
        pos.set(tmp.x, tmp.z, -tmp.y);
        
        // get rotation
        Quaternion quat = trn.getRotation(new Quaternion());
        quat.w = -quat.w; // (-x, z, -y, w) -> [z-up to y-up] -> (-x, -y, -z, w) <=> (x, y, z, -w)
        Vector3 rot = toEuler(quat);
        
        return new Vector3[] { pos, rot, scl };
    }
    
    static Vector3 toEuler(Quaternion quat) {
        // https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
        Vector3 rot = new Vector3();
        float x = quat.x;
        float y = quat.y;
        float z = quat.z;
        float w = quat.w;
        double ysqr = y * y;
	double t0 = -2.0f * (ysqr + z * z) + 1.0f;
	double t1 = +2.0f * (x * y - w * z);
	double t2 = -2.0f * (x * z + w * y);
	double t3 = +2.0f * (y * z - w * x);
	double t4 = -2.0f * (x * x + ysqr) + 1.0f;
	t2 = t2 > 1.0f ? 1.0f : t2;
	t2 = t2 < -1.0f ? -1.0f : t2;
        rot.x = (float)Math.toDegrees(Math.atan2(t3, t4)); // roll
	rot.y = (float)Math.toDegrees(Math.asin(t2)); // pitch
	rot.z = (float)Math.toDegrees(Math.atan2(t1, t0)); // yaw
        return rot;
    }
}
