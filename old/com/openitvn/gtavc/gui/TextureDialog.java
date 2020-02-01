/*
 * Copyright (C) 2016 Thinh Pham <mrlordkaj@gmail.com>
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

import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.format.img.RwArchiveEntry;
import com.openitvn.gtavc.core.entity.TextureLibraryEntry;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author thinh
 */
public class TextureDialog extends javax.swing.JDialog {
    
    private static TextureDialog _instance;
    public static TextureDialog getInstance() {
        if(_instance == null) {
            _instance = new TextureDialog(Main.getInstance());
            _instance.resetForm();
        }
        return _instance;
    }
    
    private Main main = null;
    private final TextureLibraryTableModel texLibModel = new TextureLibraryTableModel();
    private int selectedTexId = -1;
    private final ArrayList<BufferedImage> images = new ArrayList<>();
    
    private String fileName;
    
    private TextureDialog(Main main) {
        super(main);
        this.main = main;
        initComponents();
        if(this.main != null) main.setRwTextureVisible(true);
        initTableTextureLibrary();
    }
    
    private void initTableTextureLibrary() {
        TableColumnModel columnModel = tableTexLib.getColumnModel();
        columnModel.getColumn(TextureLibraryTableModel.COL_INDEX).setMinWidth(30);
        columnModel.getColumn(TextureLibraryTableModel.COL_INDEX).setMaxWidth(30);
    }
    
    @Override
    public void dispose() {
        if(main != null) main.setRwTextureVisible(false);
        super.dispose();
        _instance = null;
    }
    
    public void openFile(RwArchiveEntry entry) throws IOException {
        fileName = entry.getName();
        resetForm();
        
        byte[] bb = entry.getData();
        if (bb != null) {
            lblFileName.setText(fileName);
            texLibModel.bind(bb);
            if (texLibModel.getEntryCount() > 0) {
                tableTexLib.setRowSelectionInterval(0, 0);
                selectTexture(0);
            }
        }
    }
    
    public void openFile(String fileName, RpTextureDictionary rwTexDic) {
        this.fileName = fileName;
        resetForm();
        
        lblFileName.setText(fileName);
        texLibModel.bind(rwTexDic);
        if(texLibModel.getEntryCount() > 0) {
            tableTexLib.setRowSelectionInterval(0, 0);
            selectTexture(0);
        }
    }
    
    public void resetForm() {
        lblFileName.setText("");
        lblTextureName.setText("");
        lblSize.setText("");
        lblBitDepth.setText("");
        lblHasAlpha.setText("");
        lblMipmap.setText("");
        cboMipmap.removeAllItems();
        lblCompression.setText("");
        lblImage.setIcon(null);
        texLibModel.unbind();
        images.clear();
        selectedTexId = -1;
    }
    
    private void selectTexture(int rowId) {
        if(rowId == selectedTexId) return;
        
        TextureLibraryEntry texEntry = texLibModel.getEntry(rowId);
        lblTextureName.setText(texEntry.getTextureName());
        lblSize.setText(texEntry.getSize());
        lblBitDepth.setText(texEntry.getColorDepth());
        lblHasAlpha.setText(texEntry.hasAlpha());
        lblMipmap.setText(texEntry.getMipmapCount().toString());
        lblCompression.setText(texEntry.getCompression());
        createMipmapSelector(texEntry);

        selectedTexId = rowId;
    }
    
    private void createMipmapSelector(TextureLibraryEntry texEntry) {
        cboMipmap.removeAllItems();
        int mipmapCount = texEntry.getMipmapCount();
        images.clear();
        for (int i = 0; i < mipmapCount; i++) {
            images.add(texEntry.getBufferedImage(i));
            cboMipmap.addItem("Level " + i);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableTexLib = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblCompression = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblHasAlpha = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblSize = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblBitDepth = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblTextureName = new javax.swing.JLabel();
        cboMipmap = new javax.swing.JComboBox<>();
        lblMipmap = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblFileName = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lblImage = new javax.swing.JLabel();
        btnOpen = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("RenderWare Texture Viewer");
        setMinimumSize(new java.awt.Dimension(600, 360));

        tableTexLib.setAutoCreateRowSorter(true);
        tableTexLib.setModel(texLibModel);
        tableTexLib.setRowHeight(20);
        tableTexLib.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tableTexLib.setShowHorizontalLines(false);
        tableTexLib.setShowVerticalLines(false);
        tableTexLib.getTableHeader().setResizingAllowed(false);
        tableTexLib.getTableHeader().setReorderingAllowed(false);
        tableTexLib.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tableTexLibMouseReleased(evt);
            }
        });
        tableTexLib.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tableTexLibKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tableTexLib);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Compression:");

        lblCompression.setForeground(new java.awt.Color(51, 51, 255));
        lblCompression.setText("<code>");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setLabelFor(lblHasAlpha);
        jLabel6.setText("Alpha:");

        lblHasAlpha.setForeground(new java.awt.Color(51, 51, 255));
        lblHasAlpha.setText("<alpha>");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setLabelFor(cboMipmap);
        jLabel7.setText("Mipmaps:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setLabelFor(lblSize);
        jLabel4.setText("Size:");

        lblSize.setForeground(new java.awt.Color(51, 51, 255));
        lblSize.setText("<size>");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setLabelFor(lblBitDepth);
        jLabel5.setText("Depth:");

        lblBitDepth.setForeground(new java.awt.Color(51, 51, 255));
        lblBitDepth.setText("<depth>");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setLabelFor(lblTextureName);
        jLabel2.setText("Texture Name:");

        lblTextureName.setForeground(new java.awt.Color(51, 51, 255));
        lblTextureName.setText("<texture name>");

        cboMipmap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMipmapActionPerformed(evt);
            }
        });

        lblMipmap.setForeground(new java.awt.Color(51, 51, 255));
        lblMipmap.setText("<level>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTextureName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSize, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblHasAlpha, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCompression, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblBitDepth, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMipmap)
                        .addGap(36, 36, 36)
                        .addComponent(cboMipmap, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblTextureName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblSize)
                    .addComponent(jLabel6)
                    .addComponent(lblHasAlpha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblCompression)
                    .addComponent(jLabel5)
                    .addComponent(lblBitDepth))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblMipmap)
                    .addComponent(cboMipmap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setLabelFor(lblFileName);
        jLabel9.setText("File Name:");

        lblFileName.setForeground(new java.awt.Color(255, 51, 102));
        lblFileName.setText("<file name>");

        lblImage.setBackground(new java.awt.Color(204, 204, 204));
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setOpaque(true);
        jScrollPane2.setViewportView(lblImage);

        btnOpen.setText("Open...");

        btnSave.setText("Save...");
        btnSave.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblFileName))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 179, Short.MAX_VALUE)
                        .addComponent(btnOpen)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(lblFileName)
                    .addComponent(btnOpen)
                    .addComponent(btnSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableTexLibMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableTexLibMouseReleased
        selectTexture(tableTexLib.getSelectedRow());
    }//GEN-LAST:event_tableTexLibMouseReleased

    private void tableTexLibKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableTexLibKeyReleased
        selectTexture(tableTexLib.getSelectedRow());
    }//GEN-LAST:event_tableTexLibKeyReleased

    private void cboMipmapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMipmapActionPerformed
        int selectedMipmap = cboMipmap.getSelectedIndex();
        if(selectedMipmap < 0) return;
        
        ImageIcon icon = new ImageIcon(images.get(selectedMipmap));
        lblImage.setIcon(icon);
    }//GEN-LAST:event_cboMipmapActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cboMipmap;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblBitDepth;
    private javax.swing.JLabel lblCompression;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblHasAlpha;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblMipmap;
    private javax.swing.JLabel lblSize;
    private javax.swing.JLabel lblTextureName;
    private javax.swing.JTable tableTexLib;
    // End of variables declaration//GEN-END:variables
}
