/*
 * Copyright (C) 2020 Thinh
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
package com.openitvn.engine.renderware.tool;

import com.openitvn.engine.renderware.RpSection;
import com.openitvn.unicore.data.DataStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Thinh
 */
public class RwDumperRoot implements TreeNode {
    
    final String name;
    final ArrayList<RpSection> grands = new ArrayList<>();
    
    RwDumperRoot(String name, DataStream ds) {
        this.name = name;
        RpSection grand;
        while ((grand = RpSection.loadRoot(ds)) != null) {
            grands.add(grand);
        }
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return grands.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return grands.size();
    }

    @Override
    public TreeNode getParent() {
        return null;
    }

    @Override
    public int getIndex(TreeNode node) {
        for (int i = 0; i < grands.size(); i++) {
            if (grands.get(i).equals(node)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Enumeration children() {
        return Collections.enumeration(grands);
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
