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
    
    public static void setWorkspace(Workspace newWorkspace) {
        if (newWorkspace != workspace) {
            workspace = newWorkspace;
            ResourceModel.getInstance().load(newWorkspace);
        }
    }
    
    public static String getAlias() {
        return (workspace == null) ? null : workspace.name;
    }
    
    public static String getDirectory() {
        return (workspace == null) ? null : workspace.location;
    }
    
    public static ArrayList<String> getMainArchives() {
        ArrayList<String> rs = new ArrayList<>();
        switch (workspace.name) {
            case ALIAS_III:
                rs.add("models/txd.img");
                break;
        }
        rs.add("models/gta3.img");
        return rs;
    }
    
    public static ArrayList<String> getLoaders() {
        ArrayList<String> rs = new ArrayList<>();
        rs.add("/data/default.dat");
        switch (workspace.name) {
            case ALIAS_III:
                rs.add("/data/gta3.dat");
                rs.add("/data/animviewer.dat");
                break;
                
            case ALIAS_VC:
                rs.add("/data/gta_vc.dat");
                break;
                
            case ALIAS_SA:
                rs.add("/data/gta.dat");
                break;
        }
        return rs;
    }
    
    public static ArrayList<String> getDependencies() {
        ArrayList<String> rs = new ArrayList<>();
//        switch (workspace.name) {
//            case ALIAS_III:
//                rs.add("default.ide");
//                break;
//                
//            case ALIAS_VC:
//                rs.add("default.ide");
//                break;
//                
//            case ALIAS_SA:
//                rs.add("default.ide");
//                rs.add("vehicles.ide");
//                rs.add("peds.ide");
//                break;
//        }
        return rs;
    }
    
    public static ArrayList<String> getDependencies(ArrayList<String> ipls) {
        ArrayList<String> rs = new ArrayList<>();
        for (String ipl : ipls) {
            for (String dp : GameConfig.getDependencies(ipl)) {
                if (!rs.contains(dp)) {
                    rs.add(dp);
                }
            }
        }
        return rs;
    }
    
    public static ArrayList<String> getDependencies(String ipl) {
        ArrayList<String> rs = new ArrayList<>();
        if (ipl.toLowerCase().endsWith(".ipl")) {
            switch (workspace.name) {
                case ALIAS_III:
                    rs.add("generic.ide");
                    rs.add("making.ide");
                    rs.add("temppart.ide");
                    rs.add("subroads.ide");
                    if (ipl.startsWith("COM"))
                        rs.add("comroad.ide");
                    if (ipl.startsWith("INDUST"))
                        rs.add("indroads.ide");
                    break;

                case ALIAS_VC:
                    rs.add("generic.ide");
                    break;

                case ALIAS_SA:
                    // load generic
                    String loc = workspace.location + "/data/maps/";
                    for (File f : new File(loc+"generic").listFiles())
                        rs.add(f.getName());
                    // load xref
                    if (ipl.startsWith("vega"))
                        rs.add("vegaxref.ide");
                    if (ipl.startsWith("coun"))
                        rs.add("counxref.ide");
                    if (ipl.startsWith("LA"))
                        rs.add("LAxref.ide");
                    if (ipl.startsWith("SF"))
                        rs.add("SFxref.ide");
                    break;
            }
            rs.add(ipl.substring(0, ipl.length() - 4) + ".ide"); // .ipl replaced by .ide
        }
        return rs;
    }
    
    public static File getVehicleScript() {
        String name = ALIAS_SA.equals(workspace.name) ? "vehicles.ide" : "default.ide";
        return new File(workspace.location + "/data/" + name);
    }
    
    public static HashMap<Integer, String> getWheelNameMap() {
        HashMap<Integer, String> rs = new HashMap<>();
        switch (workspace.name) {
            case ALIAS_III:
                rs.put(160, "wheel_sport");
                rs.put(161, "wheel_saloon");
                rs.put(162, "wheel_lightvan");
                rs.put(163, "wheel_classic");
                rs.put(164, "wheel_alloy");
                rs.put(165, "wheel_lighttruck");
                rs.put(166, "wheel_smallcar");
                break;
                
            case ALIAS_VC:
                rs.put(237, "wheel_rim");
                rs.put(238, "wheel_offroad");
                rs.put(239, "wheel_truck");
                rs.put(250, "wheel_sport");
                rs.put(251, "wheel_saloon");
                rs.put(252, "wheel_lightvan");
                rs.put(253, "wheel_classic");
                rs.put(254, "wheel_alloy");
                rs.put(255, "wheel_lighttruck");
                rs.put(256, "wheel_smallcar");
                break;
        }
        return rs;
    }
    
    public static String getWorldIgnorePattern() {
        switch (workspace.name) {
            case ALIAS_III:
                return "(shad)|(null)";
        }
        return null;
    }
    
    @Deprecated
    public static ArrayList<Integer> getWheelIds() {
        ArrayList<Integer> rs = new ArrayList<>();
        switch (workspace.name) {
            case ALIAS_III:
                for (int i = 160; i <= 169; i++)
                    rs.add(i);
                break;
                
            case ALIAS_VC:
                for (int i = 250; i <= 257; i++)
                    rs.add(i);
                break;
        }
        return rs;
    }
    
    @Deprecated
    public static void setWorkspaceOld(Workspace space) {
        if (space != workspace) {
            workspace = space;
            ResourceModel.getInstance().load(space);
            Workspace.setActive(space, 0x240691);
        }
    }
}
