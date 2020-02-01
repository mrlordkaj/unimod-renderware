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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Thinh Pham <mrlordkaj@gmail.com>
 */
public class DumperDialog extends javax.swing.JDialog {
    private static final String FORM_TITLE = "RenderWare Dumper";
    
    private File currentFile; //for file chooser default location
    private final HashMap<String, DumperTab> entryList = new HashMap<>();
    private final HashMap<String, DumperTab> fileList = new HashMap<>();
    
    private static DumperDialog _instance;
    public static DumperDialog getInstance() {
        if(_instance == null) _instance = new DumperDialog();
        return _instance;
    }
    
    private DumperDialog() {
        super(Main.getInstance());
        initComponents();
    }
    
    @Override
    public void dispose() {
        removeAllTabs();
        super.dispose();
        _instance = null;
    }
    
    public void openFile(String entryName, byte[] data) {
        for (String key : entryList.keySet()) {
            if(entryName.equals(key)) {
                DumperTab dumper = entryList.get(key);
                tabbedDumper.setSelectedComponent(dumper);
                return;
            }
        }
        
        try {
            DumperTab newDumper = new DumperTab(entryName, data);
            tabbedDumper.addTab(entryName, newDumper);
            tabbedDumper.setSelectedIndex(tabbedDumper.getTabCount() - 1);
            entryList.put(entryName, newDumper);
        } catch (IOException ex) {
            Logger.getLogger(DumperDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void openFile(String fileUri) {
        for(String key : fileList.keySet()) {
            if(fileUri.equals(key)) {
                DumperTab dumper = fileList.get(key);
                tabbedDumper.setSelectedComponent(dumper);
                return;
            }
        }
        
        try {
            int fileNamePos = fileUri.lastIndexOf(File.separator) + 1;
            String fileName = fileUri.substring(fileNamePos);
            DumperTab newDumper = new DumperTab(fileName, fileUri);
            tabbedDumper.addTab(fileName, newDumper);
            tabbedDumper.setSelectedIndex(tabbedDumper.getTabCount() - 1);
            fileList.put(fileUri, newDumper);
        } catch (IOException ex) {
            Logger.getLogger(DumperDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void removeCurrentTab() {
        DumperTab tab = (DumperTab)tabbedDumper.getSelectedComponent();
        
        for(Entry<String, DumperTab> entry : entryList.entrySet()) {
            if(entry.getValue().equals(tab)) {
                entryList.remove(entry.getKey());
                tabbedDumper.remove(tab);
                return;
            }
        }
        
        for(Entry<String, DumperTab> entry : fileList.entrySet()) {
            if(entry.getValue().equals(tab)) {
                fileList.remove(entry.getKey());
                tabbedDumper.remove(tab);
                return;
            }
        }
    }
    
    private void removeAllTabs() {
        ChangeListener[] listeners = tabbedDumper.getChangeListeners();
        for(ChangeListener l : listeners) tabbedDumper.removeChangeListener(l);
        tabbedDumper.removeAll();
        entryList.clear();
        fileList.clear();
        setTitle(FORM_TITLE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popOpen = new javax.swing.JPopupMenu();
        mnuCloseCurrent = new javax.swing.JMenuItem();
        mnuCloseAll = new javax.swing.JMenuItem();
        tabbedDumper = new javax.swing.JTabbedPane();
        jLabel1 = new javax.swing.JLabel();
        btnOpenFile = new javax.swing.JButton();

        mnuCloseCurrent.setText("Close");
        mnuCloseCurrent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCloseCurrentActionPerformed(evt);
            }
        });
        popOpen.add(mnuCloseCurrent);

        mnuCloseAll.setText("Close All");
        mnuCloseAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCloseAllActionPerformed(evt);
            }
        });
        popOpen.add(mnuCloseAll);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("RenderWare Dumper");
        setMinimumSize(new java.awt.Dimension(320, 240));
        setSize(new java.awt.Dimension(0, 0));

        tabbedDumper.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedDumper.setComponentPopupMenu(popOpen);
        tabbedDumper.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                onSwitchTab(evt);
            }
        });

        jLabel1.setText("RenderWare Dumper v0.1 - by Thinh Pham");

        btnOpenFile.setText("Open...");
        btnOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedDumper, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnOpenFile)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedDumper, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOpenFile)
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onSwitchTab(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_onSwitchTab
        if(tabbedDumper.getTabCount() == 0) {
            this.setTitle(FORM_TITLE);
        } else {
            String subTitle = ((DumperTab)tabbedDumper.getSelectedComponent()).getFullTitle();
            this.setTitle(FORM_TITLE + " - " + subTitle);
        }
    }//GEN-LAST:event_onSwitchTab

    private void btnOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenFileActionPerformed
        JFileChooser fcOpen = new JFileChooser(currentFile);
        fcOpen.setAcceptAllFileFilterUsed(false);
        fcOpen.addChoosableFileFilter(new FileNameExtensionFilter("RenderWare Supported Formats (*.dff, *.txd, *.dat)","dff","txd","dat"));
        fcOpen.addChoosableFileFilter(new FileNameExtensionFilter("RenderWare Model Files (*.dff)","dff"));
        fcOpen.addChoosableFileFilter(new FileNameExtensionFilter("RenderWare Texture Archives (*.txd)","txd"));
        if (fcOpen.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        
        currentFile = fcOpen.getSelectedFile();
        openFile(currentFile.toString());
    }//GEN-LAST:event_btnOpenFileActionPerformed

    private void mnuCloseAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCloseAllActionPerformed
        removeAllTabs();
    }//GEN-LAST:event_mnuCloseAllActionPerformed

    private void mnuCloseCurrentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCloseCurrentActionPerformed
        removeCurrentTab();
    }//GEN-LAST:event_mnuCloseCurrentActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOpenFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuItem mnuCloseAll;
    private javax.swing.JMenuItem mnuCloseCurrent;
    private javax.swing.JPopupMenu popOpen;
    private javax.swing.JTabbedPane tabbedDumper;
    // End of variables declaration//GEN-END:variables
}
