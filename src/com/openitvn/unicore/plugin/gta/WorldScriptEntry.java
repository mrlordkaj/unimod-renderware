/*
 * Copyright (C) 2019 Thinh Pham
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

import java.io.File;

/**
 *
 * @author Thinh Pham
 */
public class WorldScriptEntry {
    
    final String path;
    final File file;
    final WorldScriptType type;
    boolean isActive = false;

    WorldScriptEntry(String path, WorldScriptType type) {
        this.path = path;
        this.type = type;
        this.file = new File(GameConfig.getDirectory() + "/" + path);
    }

    public String getName() {
        return file.getName();
    }
    
    @Deprecated
    private int index;
    
    @Deprecated
    public WorldScriptEntry(int index, String path, WorldScriptType type) {
        this(path, type);
        this.index = index;
    }
    
    @Deprecated
    public int getIndex() {
        return index;
    }
    
    @Deprecated
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }
    
    @Deprecated
    public boolean isActive() {
        return isActive;
    }
    
    @Deprecated
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    @Deprecated
    public WorldScriptType getType() {
        return type;
    }
}
