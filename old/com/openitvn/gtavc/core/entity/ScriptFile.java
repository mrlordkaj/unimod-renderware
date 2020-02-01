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

package com.openitvn.gtavc.core.entity;

import com.openitvn.unicore.plugin.gta.GameConfig;

/**
 *
 * @author Thinh Pham
 */
public class ScriptFile {
    
    public enum Type { IDE, IPL }
    
    public final int index;
    public final String revPath;
    public final Type type;
    public boolean isActive = false;
    
    public ScriptFile(int index, String revPath, Type type) {
        this.index = index;
        this.revPath = revPath;
        this.type = type;
    }
    
    public String getName() {
        return getFileName();
    }
    
    public String getFileName() {
        String path = revPath;
        String[] elements = path.split("\\\\");
        return elements[elements.length-1];
    }
    
    public String getAbsolutePath() {
        return GameConfig.getDirectory() + "/" + revPath;
    }
}
