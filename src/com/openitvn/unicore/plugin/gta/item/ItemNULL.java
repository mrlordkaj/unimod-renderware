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
 * @author Thinh Pham
 */
@Deprecated
public class ItemNULL {
    
    protected int groupIndex;
    protected ItemType itemType = ItemType.NULL;
    
    public ItemNULL() {
        this(0);
    }
    
    public ItemNULL(int groupIndex) {
        this.groupIndex = groupIndex;
    }
    
    public int getGroupIndex() {
        return groupIndex;
    }
    
    public ItemType getType() {
        return itemType;
    }
    
    @Override
    public String toString() { 
        return "[Unsupported Item]";
    }
}
