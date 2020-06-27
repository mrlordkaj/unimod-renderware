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

import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.archive.IArchiveEntry;
import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Thinh Pham
 */
public class RwDumper extends javax.swing.JDialog {
    
    private final HashMap<Object, RwDumperTab> entryMap = new HashMap<>(); // key is String or RwArchiveEntry
    private File currentFile; // for file chooser default location
    
    private static RwDumper instance;
    public static RwDumper getInstance() {
        if (instance == null) {
            instance = new RwDumper();
        }
        return instance;
    }
    
    private RwDumper() {
        super(Unicore.getMainFrame(), false);
        initComponents();
        onSwitchTab(null);
    }
    
    @Override
    public void dispose() {
        removeAllTabs(null);
        super.dispose();
        instance = null;
    }
    
    public void openEntry(IArchiveEntry entry) {
        if (!trySwitchTab(entry)) {
            RwDumperTab tab = new RwDumperTab(entry);
            tabbedDumper.addTab(entry.getName(), tab);
            tabbedDumper.setSelectedIndex(tabbedDumper.getTabCount() - 1);
            entryMap.put(entry, tab);
        }
    }
    
    private boolean trySwitchTab(Object entryKey) {
        for (Object key : entryMap.keySet()) {
            if (entryKey.equals(key)) {
                RwDumperTab tab = entryMap.get(key);
                tabbedDumper.setSelectedComponent(tab);
                return true;
            }
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popOpen = new javax.swing.JPopupMenu();
        mnuCloseCurrent = new javax.swing.JMenuItem();
        mnuCloseAll = new javax.swing.JMenuItem();
        tabbedDumper = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuOpen = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

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
                removeAllTabs(evt);
            }
        });
        popOpen.add(mnuCloseAll);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(320, 240));
        setSize(new java.awt.Dimension(0, 0));

        tabbedDumper.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedDumper.setComponentPopupMenu(popOpen);
        tabbedDumper.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                onSwitchTab(evt);
            }
        });

        jMenu1.setText("File");

        mnuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mnuOpen.setText("Open...");
        mnuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenActionPerformed(evt);
            }
        });
        jMenu1.add(mnuOpen);
        jMenu1.add(jSeparator1);

        mnuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        mnuExit.setText("Exit");
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        jMenu1.add(mnuExit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Help");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedDumper, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedDumper, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onSwitchTab(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_onSwitchTab
        StringBuilder sb = new StringBuilder("Renderware Dumper");
        if (tabbedDumper.getTabCount() > 0) {
            RwDumperTab tab = (RwDumperTab) tabbedDumper.getSelectedComponent();
            sb.append(" - ").append(tab.getName());
        }
        setTitle(sb.toString());
    }//GEN-LAST:event_onSwitchTab

    private void mnuCloseCurrentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCloseCurrentActionPerformed
        RwDumperTab tab = (RwDumperTab) tabbedDumper.getSelectedComponent();
        for (Entry<Object, RwDumperTab> entry : entryMap.entrySet()) {
            if (entry.getValue().equals(tab)) {
                entryMap.remove(entry.getKey());
                tabbedDumper.remove(tab);
                return;
            }
        }
    }//GEN-LAST:event_mnuCloseCurrentActionPerformed

    private void mnuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOpenActionPerformed
        JFileChooser fc = new JFileChooser(currentFile);
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Renderware Formats (dff, txd)","dff","txd"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Renderware Models (dff)","dff"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Renderware Textures (txd)","txd"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = fc.getSelectedFile();
            String fileName = currentFile.toString();
            if (!trySwitchTab(fileName)) {
                RwDumperTab tab = new RwDumperTab(fileName);
                tabbedDumper.addTab(new File(fileName).getName(), tab);
                tabbedDumper.setSelectedIndex(tabbedDumper.getTabCount() - 1);
                entryMap.put(fileName, tab);
            }
        }
    }//GEN-LAST:event_mnuOpenActionPerformed

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        dispose();
    }//GEN-LAST:event_mnuExitActionPerformed

    private void removeAllTabs(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllTabs
        for (ChangeListener l : tabbedDumper.getChangeListeners()) {
            tabbedDumper.removeChangeListener(l);
        }
        tabbedDumper.removeAll();
        entryMap.clear();
        onSwitchTab(null);
    }//GEN-LAST:event_removeAllTabs

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Unicore.loadDefaultStyle();
                RwDumper dumper = new RwDumper();
                dumper.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem mnuCloseAll;
    private javax.swing.JMenuItem mnuCloseCurrent;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenuItem mnuOpen;
    private javax.swing.JPopupMenu popOpen;
    private javax.swing.JTabbedPane tabbedDumper;
    // End of variables declaration//GEN-END:variables
}
