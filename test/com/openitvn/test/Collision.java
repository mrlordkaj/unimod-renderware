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
package com.openitvn.test;

import com.openitvn.gtavc.core.GtaCollision;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Thinh Pham
 */
public class Collision {
    
    HashMap<String, GtaCollision> colFileMap = new HashMap<>();
    
    Collision() {
        File file = new File("F:/comNtop.col");
        try (FileInputStream fis = new FileInputStream(file)) {
            while (fis.available() > 0) {
                int offset = (int)fis.getChannel().position() + 8;
                ByteBuffer bb = ByteBuffer.allocate(30).order(ByteOrder.LITTLE_ENDIAN);
                fis.read(bb.array());
                int fourCC = bb.getInt();
                int size = bb.getInt();
                String name = readName(bb, 22);
                fis.skip(size - 22);
                if (fourCC == 0)
                    break;
                colFileMap.put(name, new GtaCollision(fourCC, file, offset, size));
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        
        for (Map.Entry<String, GtaCollision> e : colFileMap.entrySet()) {
            String name = e.getKey();
            GtaCollision col = e.getValue();
            col.decode();
            System.out.println(col.modId + " : " + name + " / " + col.vertices.length + " / " + col.faces.length);

        }
    }
    
    static String readName(ByteBuffer bb, int len) {
        StringBuilder sb = new StringBuilder();
        for (int c, j = 0; j < len; j++) {
            if ((c = bb.get()) == 0) {
                bb.position(bb.position() + len - 1 - j);
                break;
            }
            sb.append((char) c);
        }
        return sb.toString();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Collision();
    }
}
