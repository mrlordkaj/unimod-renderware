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
package com.openitvn.engine.renderware.tool;

import com.openitvn.engine.renderware.RpSection;
import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.format.txd.RwTexture;
import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.archive.IArchiveEntry;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.data.FileStream;
import com.openitvn.unicore.world.resource.BufferedRaster;
import com.openitvn.unicore.world.resource.ITexture;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;

/**
 *
 * @author Thinh Pham
 */
public final class RwTexer extends javax.swing.JDialog
{
    private static final String DIALOG_TITLE = "RW Texture Viewer";
    
    private static RwTexer instance;
    public static RwTexer getInstance() {
        if (instance == null) {
            instance = new RwTexer();
        }
        return instance;
    }
    
    private final RwTexerModel texLibModel = new RwTexerModel();
    private RwTexture texture;
    private File curFile;
    
    private final ListSelectionListener texLibTableSelectionListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            cboMipmap.removeAllItems();
            int row = texLibTable.getSelectedRow();
            if (row >= 0) {
                int id = texLibTable.convertRowIndexToModel(row);
                texture = texLibModel.entries.get(id);
                RpTextureNative texData = texture.textureNative;
                lblFormat.setText(texture.getPixelFormat().toString());
                lblFormat.setToolTipText(String.format("Depth: %d bit", texData.colorDepth));
                for (int i = 0; i < texture.getMipCount(); i++) {
                    Dimension size = ITexture.computeMipMapSize(texData.width, texData.height, i);
                    String str = String.format("Level %d (%d x %d)", i, size.width, size.height);
                    cboMipmap.addItem(str);
                }
            } else {
                lblFormat.setText("");
                lblFormat.setToolTipText(null);
                texture = null;
            }
            mnuExport.setEnabled(texture != null);
        }
    };

    /**
     * Creates new form RwTexer
     */
    private RwTexer() {
        super(Unicore.getMainFrame(), false);
        initComponents();
        loadTexDic(null, null);
    }
    
    @Override
    public void dispose() {
        instance = null;
        super.dispose();
    }
    
    public void openEntry(IArchiveEntry entry) {
        try (EntryStream es = new EntryStream(entry)) {
            RpTextureDictionary txd = RpSection.loadRoot(es, RpTextureDictionary.class);
            loadTexDic(es.getFullPath(), txd);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    public void loadTexDic(String name, RpTextureDictionary txd) {
        // Update title
        StringBuilder sb = new StringBuilder(DIALOG_TITLE);
        if (name != null && !name.isEmpty()) {
            sb.append(" - ").append(name);
        }
        setTitle(sb.toString());
        // Bind texDic
        texLibModel.bindTexDic(txd);
        if (texLibModel.entries.isEmpty()) {
            mnuExportAll.setEnabled(false);
            lblImage.setIcon(null);
        } else {
            mnuExportAll.setEnabled(true);
            texLibTable.setRowSelectionInterval(0, 0);
        }
    }
    
    private BufferedRaster exportImage(RwTexture texture, int mipLevel) {
        if (texture != null) {
            Dimension imgSize = ITexture.computeMipMapSize(texture.getWidth(), texture.getHeight(), mipLevel);
            BufferedRaster img = new BufferedRaster(imgSize.width, imgSize.height);
            texture.decodeImage(img, 0, mipLevel);
            return img;
        }
        return null;
    }
    
    private void exportImage(RwTexture tex, File out) {
        try {
            BufferedRaster img = exportImage(tex, 0);
            ImageIO.write(img, "png", out);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        texLibTable = new javax.swing.JTable();
        cboMipmap = new javax.swing.JComboBox<>();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        lblFormat = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        lblImage = new javax.swing.JLabel();
        javax.swing.JMenuBar jMenuBar1 = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu1 = new javax.swing.JMenu();
        mnuOpen = new javax.swing.JMenuItem();
        mnuExport = new javax.swing.JMenuItem();
        mnuExportAll = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuExit = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(DIALOG_TITLE);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(600, 360));

        texLibTable.setAutoCreateRowSorter(true);
        texLibTable.setModel(texLibModel);
        texLibTable.setRowHeight(20);
        texLibTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        texLibTable.setShowHorizontalLines(false);
        texLibTable.setShowVerticalLines(false);
        texLibTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(texLibTable);
        // Set fixed width on alpha column
        TableColumn colAlpha = texLibTable.getColumnModel().getColumn(RwTexerModel.COL_ALPHA);
        colAlpha.setMinWidth(20);
        colAlpha.setMaxWidth(20);
        // Add selection event
        texLibTable.getSelectionModel().addListSelectionListener(texLibTableSelectionListener);

        cboMipmap.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboMipmapItemStateChanged(evt);
            }
        });

        jLabel1.setText("Mipmap");

        jLabel3.setText("Format");

        lblImage.setBackground(new java.awt.Color(204, 204, 204));
        lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImage.setOpaque(true);
        jScrollPane2.setViewportView(lblImage);

        jMenu1.setText("File");

        mnuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuOpen.setText("Open...");
        mnuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenActionPerformed(evt);
            }
        });
        jMenu1.add(mnuOpen);

        mnuExport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuExport.setText("Export...");
        mnuExport.setEnabled(false);
        mnuExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportActionPerformed(evt);
            }
        });
        jMenu1.add(mnuExport);

        mnuExportAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuExportAll.setText("Export All...");
        mnuExportAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportAllActionPerformed(evt);
            }
        });
        jMenu1.add(mnuExportAll);
        jMenu1.add(jSeparator1);

        mnuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuExit.setText("Exit");
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        jMenu1.add(mnuExit);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFormat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboMipmap, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFormat)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboMipmap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboMipmap, lblFormat});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        dispose();
    }//GEN-LAST:event_mnuExitActionPerformed

    private void mnuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOpenActionPerformed
        JFileChooser fc = new JFileChooser(curFile);
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Renderware Textures (txd)","txd"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            curFile = fc.getSelectedFile();
            try (FileStream fs = new FileStream(curFile)) {
                RpTextureDictionary txd = RpSection.loadRoot(fs, RpTextureDictionary.class);
                loadTexDic(fs.getFullPath(), txd);
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }//GEN-LAST:event_mnuOpenActionPerformed

    private void cboMipmapItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboMipmapItemStateChanged
        if (texture != null && evt.getStateChange() == ItemEvent.SELECTED) {
            int mip = cboMipmap.getSelectedIndex();
            if (mip >= 0) {
                BufferedRaster img = exportImage(texture, mip);
                ImageIcon ico = new ImageIcon(img);
                lblImage.setIcon(ico);
            }
        }
    }//GEN-LAST:event_cboMipmapItemStateChanged

    private void mnuExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportActionPerformed
        JFileChooser fc = new JFileChooser(curFile);
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Portable Network Graphics (png)","png"));
        fc.setSelectedFile(new File(texture.textureNative.getMapperName()+".png"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            curFile = fc.getSelectedFile();
            exportImage(texture, curFile);
        }
    }//GEN-LAST:event_mnuExportActionPerformed

    private void mnuExportAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportAllActionPerformed
        JFileChooser fc = new JFileChooser(curFile);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            curFile = fc.getSelectedFile();
            for (RwTexture tex : texLibModel.entries) {
                String fileName = tex.textureNative.getMapperName() + ".png";
                File out = new File(curFile.getPath() + "/" + fileName);
                exportImage(tex, out);
            }
        }
    }//GEN-LAST:event_mnuExportAllActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Unicore.loadDefaultStyle();
                RwTexer app = new RwTexer();
                app.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboMipmap;
    private javax.swing.JLabel lblFormat;
    private javax.swing.JLabel lblImage;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenuItem mnuExport;
    private javax.swing.JMenuItem mnuExportAll;
    private javax.swing.JMenuItem mnuOpen;
    private javax.swing.JTable texLibTable;
    // End of variables declaration//GEN-END:variables
}
