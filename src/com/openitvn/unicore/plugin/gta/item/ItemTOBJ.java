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

/**
 *
 * @author Thinh 
 * https://gtamods.com/wiki/TOBJ
 */
public class ItemTOBJ extends ItemOBJS {
    
    public int timeOn;
    public int timeOff;

    public ItemTOBJ(String[] args) throws IllegalArgumentException {
        super(extractOBJS(args));
        int length = args.length;
        timeOff = Integer.parseInt(args[length - 1]);
        timeOn = Integer.parseInt(args[length - 2]);
    }
    
    private static String[] extractOBJS(String[] args) {
        int length = args.length - 2;
        String[] rs = new String[length];
        System.arraycopy(args, 0, rs, 0, length);
        return rs;
    }
}
