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
package com.openitvn.gtavc.core;

import com.badlogic.gdx.math.Vector3;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Thinh Pham
 */
public class GtaCollision {
    
    // entry
    private final File file;
    private final int offset, size;
    private boolean decoded;
    
    // header
    public int fourCC;
    public short modId; // uint16
    
    // body
    public Vector3[] vertices;
    public Vector3[] faces;
    
    public GtaCollision(int fourCC, File file, int offset, int size) {
        this.fourCC = fourCC;
        this.file = file;
        this.offset = offset;
        this.size = size;
    }
    
    public void decode() {
        if (!decoded) {
            try (FileInputStream fis = new FileInputStream(file)) {
                ByteBuffer bb = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
                fis.skip(offset);
                fis.read(bb.array());
                bb.position(22); // skip name (22)
                modId = bb.getShort();
                bb.position(64); // skip name (22) + modId (2) + bounding (40)
                switch (fourCC) {
                    case 1280069443: // COLL
                        decodeV1(bb);
                        break;

                    case 843861827: // COL2
                    case 860639043: // COL3
                        
                        break;

                    case 877416259: // COL4
                        
                        break;
                }
                decoded = true;
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
    
    public void decodeV1(ByteBuffer bb) {
        // TODO: read spheres
        int numSpheres = bb.getInt();
        bb.position(bb.position() + numSpheres * 20);
        
        int unk = bb.getInt();
        
        // TODO: read boxes
        int numBoxes = bb.getInt();
        bb.position(bb.position() + numBoxes * 28);
        
        // read vertices
        int numVerts = bb.getInt();
        vertices = new Vector3[numVerts];
        for (int i = 0; i < numVerts; i++) {
            vertices[i] = new Vector3(bb.getFloat(), bb.getFloat(), bb.getFloat());
        }
        
        // read faces
        int numFaces = bb.getInt();
        faces = new Vector3[numFaces];
        for (int i = 0; i < numFaces; i++) {
            int i3 = bb.getInt();
            int i2 = bb.getInt();
            int i1 = bb.getInt();
            faces[i] = new Vector3(i1, i2, i3);
            bb.getInt(); // surface
        }
    }
}
