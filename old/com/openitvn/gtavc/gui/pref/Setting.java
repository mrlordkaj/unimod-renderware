/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openitvn.gtavc.gui.pref;

import com.openitvn.unicore.Workspace;
import com.openitvn.unicore.plugin.gta.GameConfig;
import java.util.prefs.Preferences;

/**
 *
 * @author Thinh Pham <mrlordkaj@gmail.com>
 */
public class Setting {

    private static final String PREFERENCE_NODE_NAME = "/com/openitvn/gtaviewer";
    private static final String GTA3_LOCATION = "gta3_location";
    private static final String GTAVC_LOCATION = "gtavc_location";
    private static final String GTASA_LOCATION = "gtasa_location";

    private final Preferences prefs;

    private static Setting _instance;

    public static Setting getInstance() {
        if (_instance == null) {
            _instance = new Setting();
        }
        return _instance;
    }

    private Setting() {
        prefs = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
    }
    
    public void saveDirectory(Workspace space) {
        switch (space.name) {
            case GameConfig.ALIAS_III:
                prefs.put(GTA3_LOCATION, space.location);
                break;
                
            case GameConfig.ALIAS_VC:
                prefs.put(GTAVC_LOCATION, space.location);
                break;
                
            case GameConfig.ALIAS_SA:
                prefs.put(GTASA_LOCATION, space.location);
                break;
        }
    }
    
    public String getGta3Location() {
        return prefs.get(GTA3_LOCATION, "");
    }
    
    public String getGtaVcLocation() {
        return prefs.get(GTAVC_LOCATION, "");
    }

    public String getGtaSaLocation() {
        return prefs.get(GTASA_LOCATION, "");
    }
}
