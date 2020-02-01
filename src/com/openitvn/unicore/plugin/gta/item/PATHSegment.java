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
package com.openitvn.unicore.plugin.gta.item;

import com.openitvn.unicore.plugin.gta.ScriptHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Thinh Pham
 */
public class PATHSegment {
    
    public static final int TYPE_PED = 0,
                            TYPE_CAR = 1;
    
    public int type;        // ped = pedestrian traffic
                            // car = road traffic
    public int modId;       // Associated to existing model index defined in OBJS or TOBJ section
    public String modName;  // Associated to existing object model name defined in OBJS or TOBJ section
    
    public final ArrayList<PATHNode> nodes = new ArrayList<>();
    
    public PATHSegment(int groupType, int modId, String modName, BufferedReader br) throws IOException {
        this.type = groupType;
        this.modId = modId;
        this.modName = modName;
        for (int i = 0; i < 12; i++) {
            String[] args = ScriptHelper.parseLineByComma(br);
            PATHNode node = new PATHNode(args);
            if (node.type != 0)
                nodes.add(node);
        }
    }
}
