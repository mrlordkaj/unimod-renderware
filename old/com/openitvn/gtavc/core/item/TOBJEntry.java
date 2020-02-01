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
 * https://gtamods.com/wiki/TOBJ
 */
public class TOBJEntry extends OBJSEntry {
    
    public int timeOn;
    public int timeOff;
    
    public TOBJEntry(String[] args, int groupId) {
        super(args, groupId);
        switch (args.length) {
            case 8: // type1 (III/VC/SA)
                timeOn = Integer.parseInt(args[6]);
                timeOff = Integer.parseInt(args[7]);
                break;
                
            case 9: // type2 (III/VC/SA)
                timeOn = Integer.parseInt(args[7]);
                timeOff = Integer.parseInt(args[8]);
                break;
                
            case 10: // type3 (III/VC/SA)
                timeOn = Integer.parseInt(args[8]);
                timeOff = Integer.parseInt(args[9]);
                break;
                
            case 7: // type4 (SA)
                timeOn = Integer.parseInt(args[5]);
                timeOff = Integer.parseInt(args[6]);
                break;
        }
    }
    
    @Override
    public ItemType getType() {
        return ItemType.TOBJ;
    }
}
