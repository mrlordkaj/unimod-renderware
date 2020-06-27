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

import com.openitvn.engine.renderware.RpSection;
import com.openitvn.unicore.archive.IArchiveEntry;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.data.FileStream;
import java.io.IOException;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Thinh Pham
 */
public class RwDumperTab extends javax.swing.JPanel {
	
    public RwDumperTab() {
        initComponents();
    }
    
    public RwDumperTab(IArchiveEntry entry) {
        initComponents();
        try (EntryStream ds = new EntryStream(entry)) {
            RpSection root = RpSection.loadRoot(ds);
            DefaultTreeModel model = new DefaultTreeModel(root);
            treeSection.setModel(model);
            super.setName(ds.getFullPath());
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public RwDumperTab(String fileName) {
        initComponents();
        try (FileStream fs = new FileStream(fileName)) {
            RpSection root = RpSection.loadRoot(fs);
            DefaultTreeModel model = new DefaultTreeModel(root);
            treeSection.setModel(model);
            super.setName(fileName);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        treeSection = new javax.swing.JTree();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(null);

        treeSection.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane1.setViewportView(treeSection);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree treeSection;
    // End of variables declaration//GEN-END:variables

}
