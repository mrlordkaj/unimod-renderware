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

package com.openitvn.gtavc.core;

import com.openitvn.engine.renderware.RpSection;
import com.openitvn.engine.renderware.RwSection;
import com.openitvn.unicore.data.BufferStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public class RwLoader {
    
    public static RpSection loadFromFile(String fileName) throws IOException {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            byte[] data = new byte[fis.available()];
            fis.read(data);
            return loadFromBuffer(data);
        }
    }
    
    public static RpSection loadFromBuffer(byte[] data) throws IOException {
        if (data != null) {
            BufferStream bb = new BufferStream(data);
//            ByteBuffer bb = ByteBuffer.wrap(data);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            return RwSection.fromData(bb, null);
        }
        return null;
    }
}
