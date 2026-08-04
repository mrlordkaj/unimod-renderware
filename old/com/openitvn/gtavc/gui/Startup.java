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

import com.openitvn.gtavc.gui.pref.Setting;
import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.Workspace;
import com.openitvn.unicore.plugin.gta.GameConfig;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

/**
 *
 * @author Thinh Pham
 */
public class Startup extends javax.swing.JFrame {
    
    static {
        System.loadLibrary("lwjgl64");
        System.loadLibrary("gdx64");
    }
    
    public Startup() {
        initComponents();
        txtGta3Location.setText(Setting.getInstance().getGta3Location());
        txtGtaVcLocation.setText(Setting.getInstance().getGtaVcLocation());
        txtGtaSaLocation.setText(Setting.getInstance().getGtaSaLocation());
    }
    
    private void browser(JTextField txtLoc, String title) {
        JFileChooser fc = new JFileChooser(txtLoc.getText());
        fc.setDialogTitle(title);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtLoc.setText(fc.getSelectedFile().getPath());
        }
    }
    
    private void launch(JTextField txtLoc, String alias, String title) {
        Workspace workspace = new Workspace();
        workspace.name = alias;
        workspace.location = txtLoc.getText();
        Setting.getInstance().saveDirectory(workspace);
        GameConfig.setWorkspace(workspace);
        Main.getInstance().setTitle(title);
        Main.getInstance().setVisible(true);
        dispose();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        txtGtaVcLocation = new javax.swing.JTextField();
        btnGtaVcBrowser = new javax.swing.JButton();
        btnGtaVcLaunch = new javax.swing.JButton();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        btnGta3Launch = new javax.swing.JButton();
        btnGta3Browser = new javax.swing.JButton();
        txtGta3Location = new javax.swing.JTextField();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        btnGtaSaLaunch = new javax.swing.JButton();
        btnGtaSaBrowser = new javax.swing.JButton();
        txtGtaSaLocation = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GTA Viewer Startup");
        setLocationByPlatform(true);
        setResizable(false);

        jLabel1.setLabelFor(txtGtaVcLocation);
        jLabel1.setText("Game Location:");

        txtGtaVcLocation.setEditable(false);

        btnGtaVcBrowser.setText("...");
        btnGtaVcBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGtaVcBrowserActionPerformed(evt);
            }
        });

        btnGtaVcLaunch.setText("Launch");
        btnGtaVcLaunch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGtaVcLaunchActionPerformed(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logo/gtavc.png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setText("Grand Theft Auto III");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logo/gta3.png"))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel5.setText("Grand Theft Auto: Vice City");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/logo/gtasa.png"))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel7.setText("Grand Theft Auto: San Andreas");

        jLabel8.setText("Game Location:");

        btnGta3Launch.setText("Launch");
        btnGta3Launch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGta3LaunchActionPerformed(evt);
            }
        });

        btnGta3Browser.setText("...");
        btnGta3Browser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGta3BrowserActionPerformed(evt);
            }
        });

        txtGta3Location.setEditable(false);

        jLabel9.setText("Game Location:");

        btnGtaSaLaunch.setText("Launch");
        btnGtaSaLaunch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGtaSaLaunchActionPerformed(evt);
            }
        });

        btnGtaSaBrowser.setText("...");
        btnGtaSaBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGtaSaBrowserActionPerformed(evt);
            }
        });

        txtGtaSaLocation.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtGtaVcLocation)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGtaVcBrowser)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGtaVcLaunch))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtGta3Location)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGta3Browser)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGta3Launch))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(0, 97, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtGtaSaLocation)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGtaSaBrowser)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnGtaSaLaunch)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(btnGta3Launch)
                            .addComponent(btnGta3Browser)
                            .addComponent(txtGta3Location, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtGtaVcLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnGtaVcLaunch)
                            .addComponent(btnGtaVcBrowser)))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(btnGtaSaLaunch)
                            .addComponent(btnGtaSaBrowser)
                            .addComponent(txtGtaSaLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGtaVcBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGtaVcBrowserActionPerformed
        browser(txtGtaVcLocation, "GTA Vice City Directory");
    }//GEN-LAST:event_btnGtaVcBrowserActionPerformed

    private void btnGtaVcLaunchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGtaVcLaunchActionPerformed
        launch(txtGtaVcLocation, GameConfig.ALIAS_VC, "Grand Theft Auto Vice City");
    }//GEN-LAST:event_btnGtaVcLaunchActionPerformed

    private void btnGta3BrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGta3BrowserActionPerformed
        browser(txtGta3Location, "GTA III Directory");
    }//GEN-LAST:event_btnGta3BrowserActionPerformed

    private void btnGta3LaunchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGta3LaunchActionPerformed
        launch(txtGta3Location, GameConfig.ALIAS_III, "Grand Theft Auto III");
    }//GEN-LAST:event_btnGta3LaunchActionPerformed

    private void btnGtaSaBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGtaSaBrowserActionPerformed
        browser(txtGtaSaLocation, "GTA San Andreas Directory");
    }//GEN-LAST:event_btnGtaSaBrowserActionPerformed

    private void btnGtaSaLaunchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGtaSaLaunchActionPerformed
        launch(txtGtaSaLocation, GameConfig.ALIAS_SA, "Grand Theft Auto San Andreas");
    }//GEN-LAST:event_btnGtaSaLaunchActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Unicore.loadDefaultStyle();
                Startup startup = new Startup();
                startup.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGta3Browser;
    private javax.swing.JButton btnGta3Launch;
    private javax.swing.JButton btnGtaSaBrowser;
    private javax.swing.JButton btnGtaSaLaunch;
    private javax.swing.JButton btnGtaVcBrowser;
    private javax.swing.JButton btnGtaVcLaunch;
    private javax.swing.JTextField txtGta3Location;
    private javax.swing.JTextField txtGtaSaLocation;
    private javax.swing.JTextField txtGtaVcLocation;
    // End of variables declaration//GEN-END:variables
}
