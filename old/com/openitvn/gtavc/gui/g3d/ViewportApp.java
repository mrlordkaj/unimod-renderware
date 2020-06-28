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
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector3;
import com.openitvn.unicore.plugin.gta.item.ItemNULL;
import com.openitvn.unicore.plugin.gta.GameConfig;
import com.openitvn.unicore.plugin.gta.VehicleModel;
import com.openitvn.unicore.plugin.gta.item.ItemCARS;
import com.openitvn.unicore.plugin.gta.item.ItemINST;
import com.openitvn.unicore.plugin.gta.item.ItemOBJS;
import java.awt.Canvas;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public class ViewportApp implements ApplicationListener {
    
    static final int GRID_WIDTH = 10;
    static final int GRID_HEIGHT = 10;
    
    private final LwjglCanvas canvas;
    private ViewportMode mode;
    
    public final VehicleModel vehicleModel = new VehicleModel();
    
    //single model mode
    private GtaModel gtaModel;
    private ModelInstance modInst;
    private PerspectiveCamera modCam;
    private CameraInputController modCtrl;
    
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
                mapView.draw(mb, env);
                break;
                
            case SingleModel:
                modCam.update();
                modCtrl.update();
                drawGrid(modCam);
                mb.begin(modCam);
                if (modInst != null) {
                    mb.render(modInst, env);
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
    
    public ViewportMode getViewpotMode() {
        return mode;
    }
    
    public void setViewpotMode(ViewportMode mode) {
        this.mode = mode;
        switch (mode) {
            case MapNormal:
            case MapDistance:
                Gdx.input.setInputProcessor(mapView.camCtrl);
                mapView.setViewportMode(mode);
                break;
                
            case SingleModel:
                modInst = (gtaModel == null) ? null : new ModelInstance(gtaModel.getModel());
                Gdx.input.setInputProcessor(modCtrl);
                break;
        }
    }
    
    public void addOBJS(ItemOBJS objs) throws Exception {
        mapView.addOBJS(objs);
    }
    
    public void removeOBJS(ItemOBJS e) {
        mapView.removeOBJS(e);
    }
    
    public void addCARS(ItemCARS e) {
        vehicleModel.entries().add(e);
    }
    
    public void select(ItemNULL entry) {
        switch (entry.getType()) {
            case INST:
                ItemINST inst = (ItemINST)entry;
                mapView.moveCameraTo(new Vector3(inst.posX, inst.posY, inst.posZ));
                break;
        }
    }
}
