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

package com.openitvn.gtavc.gui;

import com.openitvn.control.UCFileChooser;
import com.openitvn.engine.renderware.RwSection;
import com.openitvn.gtavc.core.RwLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Thinh Pham
 */
public class DumperTab extends javax.swing.JPanel {

    private final DefaultTreeModel sectionModel = new DefaultTreeModel(null);
    private String entryName = "";
    private String fileUri = "";
    
    public DumperTab(String entryName, byte[] data) throws IOException {
        initComponents();
        
        this.entryName = entryName;
        sectionModel.setRoot(RwLoader.loadFromBuffer(data));
    }
    
    public DumperTab(String entryName, String fileUri) throws IOException {
        initComponents();
        
        this.entryName = entryName;
        this.fileUri = fileUri;
        sectionModel.setRoot(RwLoader.loadFromFile(fileUri));
    }
    
    public String getFullTitle() {
        return fileUri.equals("")? entryName : fileUri;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mnuSection = new javax.swing.JPopupMenu();
        mnuExtract = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeSection = new javax.swing.JTree();

        mnuExtract.setText("Extract Data...");
        mnuExtract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExtractActionPerformed(evt);
            }
        });
        mnuSection.add(mnuExtract);

        jScrollPane1.setBorder(null);

        treeSection.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        treeSection.setModel(sectionModel);
        treeSection.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treePopupTrigger(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                treePopupTrigger(evt);
            }
        });
        jScrollPane1.setViewportView(treeSection);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void mnuExtractActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExtractActionPerformed
        RwSection sec = (RwSection)treeSection.getSelectionPath().getLastPathComponent();
        String suggestName = entryName;
        if (!suggestName.substring(suggestName.length() - 4).equals(".dat"))
            suggestName += ".dat";
        
        UCFileChooser fcSave = new UCFileChooser(fileUri);
        fcSave.setSelectedFile(new File(suggestName));
        if (fcSave.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileOutputStream fos = new FileOutputStream(fcSave.getSelectedFile())) {
                fos.write(sec.toData());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_mnuExtractActionPerformed

    private void treePopupTrigger(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treePopupTrigger
        int x = evt.getX();
        int y = evt.getY();
        int selRow = treeSection.getRowForLocation(x, y);
        treeSection.setSelectionRow(selRow);
        if(evt.isPopupTrigger() && (treeSection.getSelectionCount() == 1)) {
            mnuSection.show(evt.getComponent(), x, y);
        }
    }//GEN-LAST:event_treePopupTrigger


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem mnuExtract;
    private javax.swing.JPopupMenu mnuSection;
    private javax.swing.JTree treeSection;
    // End of variables declaration//GEN-END:variables

}
