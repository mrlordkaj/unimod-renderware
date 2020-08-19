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
package com.openitvn.unicore.plugin.gta.item;

import com.openitvn.unicore.plugin.gta.GameConfig;

/**
 *
 * @author Thinh Pham
 * https://gtamods.com/wiki/CARS_(IDE_Section)
 */
public class ItemCARS extends ItemNULL {
    
    public int id;              // Unique object ID.
    public String modName;      // Name of the .dff model file without extension.
    public String txdName;      // Name of the .txd texture dictionary without extension.
    public String type;         // Type of vehicle, which includes car, boat, train, heli, plane, and bike.
                                // This data is related to hardcoded functions and must not be changed or else it might crash the game.
                                // The bike type is never used in the game but is documented here for sake of completion.
    public String handlingId;   // Name corresponding to its handling data in the handling.cfg file.
    public String gameName;     // Name corresponding to its GXT key, case sensitive and must be seven characters or less!
                                // Vehicles with an invalid name will show up as MODELNAME missing when entered
    public String anims;        // Appropriate animation file mainly used on bikes.
    public String clazz;        // Class of the vehicle.
                                // poorfamily, richfamily, executive, worker, special, big, taxi, ignore
    public int frq;             // Frequency of the vehicle spawning randomly on the streets.
    public int lvl;             // unknow
    public int compRules;       // Component rules, alters how vehicle models' "extras" behave, see below for list
    public int wheelModelId;    // car - Model index of wheel model.
    public int lodModel;        // plane - Model index of LOD model – can be any valid object.
    public int steeringAngle;   // bike - Steering angle (in degrees)
    public float wheelScale;    // car - Scale of wheel and collision models, 1.0 for original size of wheel and collision models
    public float wheelScaleRear;// Scale of rear wheels and collision models for types car, trailer, quad, mtruck, bmx and bike
    public float wheelUpgradeClass; // The wheel set this vehicle relates to, the ids are equivalent to the ones defined in carmods.dat
    
    public ItemCARS(String[] args, String gameAlias) {
        int i = 0;
        id = Integer.parseInt(args[i++]);
        modName = args[i++];
        txdName = args[i++];
        type = args[i++];
        handlingId = args[i++];
        gameName = args[i++];
        if (!GameConfig.ALIAS_III.equals(gameAlias)) {
            anims = args[i++];
        }
        clazz = args[i++];
        frq = Integer.parseInt(args[i++]);
        lvl = Integer.parseInt(args[i++]);
        compRules = Integer.parseInt(args[i++], 16);
        try {
            switch (type) {
                case "car":
                    wheelModelId = Integer.parseInt(args[i++]);
                    wheelScale = Float.parseFloat(args[i++]);
                    if (GameConfig.ALIAS_SA.equals(gameAlias)) {
                        wheelScaleRear = Float.parseFloat(args[i++]);
                        wheelUpgradeClass = Float.parseFloat(args[i++]);
                    }
                    break;

                case "bike":
                    steeringAngle = Integer.parseInt(args[i++]);
                    wheelScale = Float.parseFloat(args[i++]);
                    break;

                case "plane":
                    lodModel = Integer.parseInt(args[i++]);
                    if (GameConfig.ALIAS_SA.equals(gameAlias)) {
                        wheelScale = Float.parseFloat(args[i++]);
                        wheelScaleRear = Float.parseFloat(args[i++]);
                        wheelUpgradeClass = Float.parseFloat(args[i++]);
                    }
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException ex) { }
    }
        
    @Deprecated
    public ItemCARS(String[] args, int groupIndex) {
        this(args, GameConfig.getAlias());
        this.groupIndex = groupIndex;
        this.itemType = ItemType.CARS;
    }
}
