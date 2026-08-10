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

package com.openitvn.gtavc.gui.pref;

import com.openitvn.gtavc.gui.Main;
import java.util.prefs.Preferences;
import javax.swing.JFrame;

/**
 *
 * @author Thinh Pham
 */
public class MainState {
    private static final String PREFERENCE_NODE_NAME = "/com/openitvn/gtaviewer";
    private static final String WINDOW_EXTENDED_STATE = "window_extended_state";
    private static final String WINDOW_WIDTH = "window_width";
    private static final String WINDOW_HEIGHT = "window_height";
    private static final String WINDOW_TOP = "window_top";
    private static final String WINDOW_LEFT = "window_left";
    private static final String MAIN_SPLIT_LOCATION = "split_main_location";
    
    public int windowTop, windowLeft;
    public int windowWidth, windowHeight;
    public int windowExtendedState;
    public int dividerLocation;
    
    private final Preferences prefs = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
    
    private static MainState instance;
    public static MainState getInstance() {
        if (instance == null) {
            instance = new MainState();
        }
        return instance;
    }
    
    private MainState() {
        windowTop = prefs.getInt(WINDOW_TOP, 0);
        windowLeft = prefs.getInt(WINDOW_LEFT, 0);
        windowWidth = prefs.getInt(WINDOW_WIDTH, 800);
        windowHeight = prefs.getInt(WINDOW_HEIGHT, 600);
        windowExtendedState = prefs.getInt(WINDOW_EXTENDED_STATE, JFrame.NORMAL);
        dividerLocation = prefs.getInt(MAIN_SPLIT_LOCATION, -1);
    }
    
    public void saveWindowState(Main mainFrame) {
        windowExtendedState = mainFrame.getExtendedState();
        if (windowExtendedState == JFrame.NORMAL) {
            windowTop = mainFrame.getY();
            windowLeft = mainFrame.getX();
            windowWidth = mainFrame.getWidth();
            windowHeight = mainFrame.getHeight();
        }
        prefs.putInt(WINDOW_EXTENDED_STATE, windowExtendedState);
        prefs.putInt(WINDOW_TOP, windowTop);
        prefs.putInt(WINDOW_LEFT, windowLeft);
        prefs.putInt(WINDOW_WIDTH, windowWidth);
        prefs.putInt(WINDOW_HEIGHT, windowHeight);
        prefs.putInt(MAIN_SPLIT_LOCATION, mainFrame.getDividerLocation());
    }
}
