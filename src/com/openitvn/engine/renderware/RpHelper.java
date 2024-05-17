/*
 * Copyright (C) 2024 Thinh Pham
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
package com.openitvn.engine.renderware;

import com.openitvn.unicore.data.DataStream;
import java.nio.ByteBuffer;

/**
 *
 * @author Thinh Pham
 */
public abstract class RpHelper {
    
    public static String readName(DataStream ds, int length) {
        StringBuilder sb = new StringBuilder();
        for (int c, j = 0; j < length; j++) {
            if ((c = ds.get()) == 0) {
                ds.skip(length - 1 - j);
                break;
            }
            sb.append((char)c);
        }
        return sb.toString();
    }
    
    public static String readName(ByteBuffer bb) {
        return readName(bb, -1);
    }
    
    public static String readName(ByteBuffer bb, int length) {
        if (length < 0) {
            length = bb.remaining();
        }
        StringBuilder sb = new StringBuilder();
        mainLoop:
        for (int i = 0; i < length; i++) {
            byte b = bb.get();
            switch (b) {
                case 0:
                    bb.position(bb.position() + length - i - 1);
                    break mainLoop;
                    
                case '@':
                    sb.append('a');
                    break;
                    
                case ' ':
                case '-':
                    sb.append('_');
                    break;
                    
                default:
                    sb.append((char)b);
            }
        }
        return sb.toString().trim();
    }
}
