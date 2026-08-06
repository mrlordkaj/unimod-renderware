/*
 * Copyright (C) 2017 Thinh Pham
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

import com.openitvn.unicore.archive.IArchive;
import com.openitvn.unicore.archive.ICompression;
import com.openitvn.unicore.data.FileStream;
import com.openitvn.helper.StringHelper;
import com.openitvn.unicore.archive.IArchiveEntry;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author Thinh Pham
 */
public final class RwArchive extends IArchive<IArchiveEntry> {
    
    private final int MAGIC_VER2 = StringHelper.makeFourCC("VER2");
    
    public RwArchive() {
        compression = ICompression.None;
    }
      
    @Override
    protected void parse(File in) throws IOException {
        // Directory is invalid archive
        if (in.isDirectory()) {
            return;
        }
        if (in.getName().toLowerCase().endsWith(".img")) {
            // If first 4 byte is "VER2", it is version 2
            try (FileStream fs = new FileStream(getFile())) {
                if (fs.getInt() == MAGIC_VER2) {
                    // build index table v2
                    int numEntries = fs.getInt();
                    for (int i = 0; i < numEntries; i++) {
                        int offset = fs.getInt() * 2048;
                        int size = (fs.getUShort()) * 2048;
                        fs.skip(2); // packed, always 0
                        String name = fs.readString(24);
                        IArchiveEntry entry = new IArchiveEntry(this, name, size, offset, size);
                        entries.add(entry);
                    }
                    return;
                }
            }
            // If found .dir, it is version 1
            String imgName = in.getAbsolutePath();
            File dirFile = new File(imgName.substring(0, imgName.length()-4).concat(".dir"));
            if (Files.exists(dirFile.toPath())) {
                // Build index table v1
                try (FileStream fs = new FileStream(dirFile)) {
                    while (fs.hasRemaining()) {
                        int offset = fs.getInt() * 2048;
                        int size = fs.getInt() * 2048;
                        String name = fs.readString(24);
                        IArchiveEntry entry = new IArchiveEntry(this, name, size, offset, size);
                        entries.add(entry);
                    }
                }
                return;
            }
        }
        // Other files are abstract archives with the file itself as only entry
        String name = in.getName();
        int size = (int) in.length();
        IArchiveEntry entry = new IArchiveEntry(this, name, size, 0, size);
        entries.add(entry);
    }
    
    @Override
    public void repack(File out) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
