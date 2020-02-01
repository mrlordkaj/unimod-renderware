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
package com.openitvn.unicore.plugin.gta;

import com.openitvn.unicore.Workspace;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Thinh Pham
 */
public abstract class GameConfig {
    
    public static final String  ALIAS_III = "gta3",
                                ALIAS_VC = "gtavc",
                                ALIAS_SA = "gtasa";
    
    private static Workspace workspace;
    
    public static void setWorkspace(Workspace space) {
        if (space != workspace) {
            workspace = space;
            ResourceModel.getInstance().load(space);
        }
    }
    
    public static String getAlias() {
        return (workspace == null) ? null : workspace.name;
    }
    
    public static String getDirectory() {
        return (workspace == null) ? null : workspace.location;
    }
    
    public static ArrayList<String> getMainArchives() {
        ArrayList<String> ret = new ArrayList<>();
        switch (workspace.name) {
            case ALIAS_III:
                ret.add("models/txd.img");
                break;
        }
        ret.add("models/gta3.img");
        return ret;
    }
    
    public static ArrayList<String> getLoaders() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add("/data/default.dat");
        switch (workspace.name) {
            case ALIAS_III:
                ret.add("/data/gta3.dat");
                ret.add("/data/animviewer.dat");
                break;
                
            case ALIAS_VC:
                ret.add("/data/gta_vc.dat");
                break;
                
            case ALIAS_SA:
                ret.add("/data/gta.dat");
                break;
        }
        return ret;
    }
    
    public static ArrayList<String> getDependencies() {
        ArrayList<String> ret = new ArrayList<>();
        switch (workspace.name) {
            case ALIAS_III:
                ret.add("default.ide");
//                ret.add("gta3.ide");
                break;
                
            case ALIAS_VC:
                ret.add("default.ide");
                break;
                
            case ALIAS_SA:
                ret.add("default.ide");
                ret.add("vehicles.ide");
                ret.add("peds.ide");
                break;
        }
        return ret;
    }
    
    public static ArrayList<String> getDependencies(ArrayList<String> ipls) {
        ArrayList<String> ret = new ArrayList<>();
        for (String ipl : ipls) {
            for (String dp : GameConfig.getDependencies(ipl)) {
                if (!ret.contains(dp))
                    ret.add(dp);
            }
        }
        return ret;
    }
    
    public static ArrayList<String> getDependencies(String ipl) {
        ArrayList<String> ret = new ArrayList<>();
        if (ipl.toLowerCase().endsWith(".ipl")) {
            switch (workspace.name) {
                case ALIAS_III:
                    ret.add("generic.ide");
                    ret.add("making.ide");
                    ret.add("temppart.ide");
                    ret.add("subroads.ide");
                    if (ipl.startsWith("COM"))
                        ret.add("comroad.ide");
                    if (ipl.startsWith("INDUST"))
                        ret.add("indroads.ide");
                    break;

                case ALIAS_VC:
                    ret.add("generic.ide");
                    break;

                case ALIAS_SA:
                    // load generic
                    String loc = workspace.location + "/data/maps/";
                    for (File f : new File(loc+"generic").listFiles())
                        ret.add(f.getName());
                    // load xref
                    if (ipl.startsWith("vega"))
                        ret.add("vegaxref.ide");
                    if (ipl.startsWith("coun"))
                        ret.add("counxref.ide");
                    if (ipl.startsWith("LA"))
                        ret.add("LAxref.ide");
                    if (ipl.startsWith("SF"))
                        ret.add("SFxref.ide");
                    break;
            }
            ret.add(ipl.substring(0, ipl.length() - 4) + ".ide"); // .ipl replaced by .ide
        }
        return ret;
    }
    
    public static File getVehicleScript() {
        String name = ALIAS_SA.equals(workspace.name) ? "vehicles.ide" : "default.ide";
        return new File(workspace.location + "/data/" + name);
    }
    
    public static HashMap<Integer, String> getWheelNameMap() {
        HashMap<Integer, String> ret = new HashMap<>();
        switch (workspace.name) {
            case ALIAS_III:
                ret.put(160, "wheel_sport");
                ret.put(161, "wheel_saloon");
                ret.put(162, "wheel_lightvan");
                ret.put(163, "wheel_classic");
                ret.put(164, "wheel_alloy");
                ret.put(165, "wheel_lighttruck");
                ret.put(166, "wheel_smallcar");
                break;
                
            case ALIAS_VC:
                ret.put(237, "wheel_rim");
                ret.put(238, "wheel_offroad");
                ret.put(239, "wheel_truck");
                ret.put(250, "wheel_sport");
                ret.put(251, "wheel_saloon");
                ret.put(252, "wheel_lightvan");
                ret.put(253, "wheel_classic");
                ret.put(254, "wheel_alloy");
                ret.put(255, "wheel_lighttruck");
                ret.put(256, "wheel_smallcar");
                break;
        }
        return ret;
    }
    
    @Deprecated
    public static ArrayList<Integer> getWheelIds() {
        ArrayList<Integer> ret = new ArrayList<>();
        switch (workspace.name) {
            case ALIAS_III:
                for (int i = 160; i <= 169; i++)
                    ret.add(i);
                break;
                
            case ALIAS_VC:
                for (int i = 250; i <= 257; i++)
                    ret.add(i);
                break;
        }
        return ret;
    }
    
    @Deprecated
    public static void setWorkspaceOld(Workspace space) {
        if (space != workspace) {
            workspace = space;
            Workspace.setActive(space, 0x240691);
        }
            
    }
}
