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
package com.openitvn.format.img;

import com.openitvn.unicore.archive.IArchiveEntry;
import com.openitvn.unicore.data.BufferStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Thinh Pham
 */
public class RwArchiveEntry extends IArchiveEntry {
    
    RwArchiveEntry(RwArchive arc, String name, int offset, int size) {
        super(arc, name, size, offset, size);
    }
    
    public BufferStream toDataStream() {
        try (FileInputStream fis = new FileInputStream(getArchive().getFile())) {
            byte[] data = new byte[(int)getSize()];
            fis.skip(getOffset());
            fis.read(data);
            return new BufferStream(data);
        } catch (IOException ex) { }
        return null;
    }
    
    @Deprecated
    public byte[] getData() {
        byte[] ret = new byte[0];
        try (FileInputStream fis = new FileInputStream(getArchive().getFile())) {
            ret = new byte[(int)getSize()];
            fis.skip(getOffset());
            fis.read(ret);
        } catch (IOException ex) { }
        return ret;
    }
    
    @Deprecated
    public String getType() {
        return getExt().toUpperCase();
    }
}
