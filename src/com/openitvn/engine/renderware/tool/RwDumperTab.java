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

package com.openitvn.engine.renderware.tool;

import java.awt.BorderLayout;

import javax.swing.tree.DefaultTreeModel;

import com.openitvn.unicore.data.DataStream;

/**
 *
 * @author Thinh Pham
 */
@SuppressWarnings("serial")
class RwDumperTab extends javax.swing.JPanel {
	
    RwDumperTab(String name, DataStream ds) {
        initComponents();
        RwDumperRoot root = new RwDumperRoot(name, ds);
        DefaultTreeModel model = new DefaultTreeModel(root);
        treeSection.setModel(model);
        super.setName(name);
    }
    
    private void initComponents() {

        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        treeSection = new javax.swing.JTree();

        setBorder(new javax.swing.border.EmptyBorder(4, 4, 4, 4));
        setLayout(new BorderLayout());

        jScrollPane1.setBorder(null);

        treeSection.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane1.setViewportView(treeSection);

        add(jScrollPane1, BorderLayout.CENTER);
    }
    
    private javax.swing.JTree treeSection;

}
