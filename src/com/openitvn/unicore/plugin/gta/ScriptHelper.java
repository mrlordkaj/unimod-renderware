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

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author Thinh Pham
 */
public abstract class ScriptHelper {
    
    /**
     * Read the next line, remove comment, then split up by comma.
     * @return arguments or null.
     */
    public static String[] parseLineByComma(BufferedReader br) {
        try {
            String line = br.readLine();
            return parseLineByComma(line);
        } catch (IOException ex) {
            return null;
        }
    }
    
    /**
     * Remove comment, then split up by comma.
     * @return arguments or null.
     */
    @Deprecated
    public static String[] parseLineByComma(String line) {
        if (line != null) {
            line = line.replaceAll("#.*$", "").trim();
            if (!line.equalsIgnoreCase("end")) {
                return line.split("\\s*\\,\\s*");
            }
        }
        return null;
    }
    
    /**
     * Read the next line, remove comment, then split up by space.
     * @return arguments or null.
     */
    static String[] parseLineBySpace(BufferedReader br) throws IOException {
        String line = br.readLine();
        if (line != null) {
            line = line.replaceAll("#.*$", "").trim();
            if (!line.equalsIgnoreCase("end"))
                return line.split("\\s+");
        }
        return null;
    }
    
    /**
     * Read the next line, remove comment, then transform to lowercase.
     * @return command line or null.
     */
    static String readLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        return line == null ? null :
                line.replaceAll("#.*$", "").trim().toLowerCase();
    }
}
