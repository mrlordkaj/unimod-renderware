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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.openitvn.unicore.plugin.gta.item.ItemNULL;
import com.openitvn.unicore.plugin.gta.item.ItemINST;
import com.openitvn.unicore.plugin.gta.item.ItemOBJS;
import java.awt.Canvas;
import java.awt.Color;
import java.io.IOException;

/**
 *
 * @author Thinh Pham
 */
public class ViewportApp implements ApplicationListener
{
    private final LwjglCanvas canvas;
    private ViewportMode mode;
    
    // Map modes
    
    final GWorldMap mapView = GWorldMap.getInstance();
    final GWorldModel modView = GWorldModel.getInstance();
    
    // Renderers
    
    private ModelBatch mb;
    private Environment env;
    
    // Singleton
    
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
    
    public void setCanvasBackground(Color c) {
        float r = c.getRed() / 255f;
        float g = c.getGreen() / 255f;
        float b = c.getBlue() / 255f;
        float a = c.getAlpha() / 255f;
        Gdx.gl.glClearColor(r, g, b, a);
    }
    
    @Override
    public void create() {
        setCanvasBackground(new Color(0xff393939));
        mb = new ModelBatch();
        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1f));
        mapView.init();
        modView.init();
        Gdx.input.setInputProcessor(mapView.camCtrl);
    }
    
    @Override
    public void resize(int width, int height) {
        mapView.resize(width, height);
        modView.resize(width, height);
    }
    
    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        switch (mode) {
            case MapNormal:
            case MapDistance:
                mapView.draw(mb, env);
                break;
            case SingleModel:
                modView.draw(mb, env);
                break;
        }
    }
    
    @Override public void pause() { }

    @Override public void resume() { }
    
    @Override
    public void dispose() {
        mapView.dispose();
        modView.dispose();
        mb.dispose();
        instance = null;
    }
    
    public void setSingleModel(String modName, String txdName) throws IOException {
        modView.setModel(modName, txdName);
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
                Gdx.input.setInputProcessor(modView.camCtrl);
                break;
        }
    }
    
    public void addOBJS(ItemOBJS objs) throws Exception {
        mapView.addOBJS(objs);
    }
    
    public void removeOBJS(ItemOBJS e) {
        mapView.removeOBJS(e);
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
