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

import com.openitvn.unicore.Workspace;
import com.openitvn.unicore.plugin.gta.GameConfig;
import java.util.prefs.Preferences;

/**
 *
 * @author Thinh Pham
 */
public class Setting
{
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
    
    public void saveDirectory(String name, String dir) {
        switch (name) {
            case GameConfig.ALIAS_III:
                prefs.put(GTA3_LOCATION, dir);
                break;
            case GameConfig.ALIAS_VC:
                prefs.put(GTAVC_LOCATION, dir);
                break;
            case GameConfig.ALIAS_SA:
                prefs.put(GTASA_LOCATION, dir);
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
