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
package com.openitvn.gtavc.core.item;

/**
 *
 * @author Thinh Pham
 */
public enum ItemType {
    
    NULL,
    //IDE
    OBJS, //Most important section: defines objects for the map. These objects can be placed into the world through the INST section of the item placement files.
    TOBJ, //Functions similarly to OBJS but has two additional parameters defining the ingame time range the object can get rendered. These objects can be placed into the world through the INST section of the item placement files.
    HIER, //Used to define objects for use in cutscenes.
    CARS, //Used to define vehicles.
    PEDS, //Used to define pedestrians (random NPC's).
    PATH, //Used to create waypoints for random NPC spawns (Paths).
    DDFX, //Used to add particle effects and simple ped behaviors to defined objects.
    WEAP, //Used to define weapons.
    ANIM, //Functions similarly to OBJS, but it has one additional parameter indicating an IFP or WAD animation file to assign an animation to the object.
          //These objects are placed through the INST section of the item placement files.
    TXDP, //Used to virtually extend texture dictionaries.
    TANM, //Used to combine TOBJ and ANIM sections.
    TREE,
    VNOD,
    LINK,
    MLO, //Used to create interiors. This section does also contain information about portals (previous ENEX connections) and dimensions of the interior which influences certain aspects, like the weather, for example. All objects are placed relative to an offset placed using MLO+ inside the IPL or WPL file.
    AMAT, //This is Audio Materials. Possible Used to make a sound effect at the model. Usually used for dynamic objects.
    LODM,
    AGRPS,
    HAND,
    
    //IPL
    INST, //Most important section: places objects defined in OBJS, TOBJ, ANIM or TANM in the world.
    ZONE, //Creates ingame regions.
    CULL, //Creates zones with special behaviour and influences to e.g. NPC's or weather.
    PICK, //Creates weapon pickups.
    //PATH,
    OCCL, //Creates occlusion zones for separated rendering.
    MULT,
    GRGE, //Creates zones for in-game garages.
    ENEX, //Creates entrance and exit markers.
    //CARS, //Creates car generators.
    JUMP, //Creates unique stunt jumps.
    TCYC, //Creates timecycle modifiers.
    AUZO, //Creates zones playing an audio stream if the user enters it.
    MZON,
    //VNOD, //Extended format of PATH – apparently only used for multiplayer mode.
    //LINK,
    BLOK, //Apparently ignored by the game – appears to be used to allocate responsibilities during the development stage of GTA IV, but also could be used to place decision makers.
    MLOP, //MLO placement – Used to place offsets for GTA IV's interiors. The interiors themselfes get placed inside the IDE file section MLO.
    //DDFX, //Used to make many game effects, for example - Particle.
    //LODM,
    SLOW; //Unknown for what it is responsible, used very rare and only in two extended .WPL. Section contains a box.
    
    @Override
    public String toString() {
        switch (this) {
            case DDFX:
                return "2DFX";
                
            case MLOP:
                return "MLO+";
        }
        return super.toString();
    }
    
    public static ItemType fromLine(String line) {
        line = line.toUpperCase();
        switch (line) {
            case "2DFX":
                return DDFX;
                
            case "MLO+":
                return MLOP;
                
            default:
                for (ItemType v : values()) {
                    if (v.toString().equals(line))
                        return v;
                }
        }
        return null;
    }
}
