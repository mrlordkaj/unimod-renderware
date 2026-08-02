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

import java.nio.ByteBuffer;

/**
 *
 * @author Thinh Pham
 */
public abstract class RpHelper {
    
    public static String readName(ByteBuffer bb) {
        return readName(bb, -1);
    }
    
    public static String readName(ByteBuffer bb, int len) {
        if (len < 0) {
            len = bb.remaining();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            byte b = bb.get();
            if (b == 0) {
                bb.position(bb.position() + len - i - 1);
                break;
            }
            sb.append((char)b);
        }
        return sb.toString().trim();
    }
}
