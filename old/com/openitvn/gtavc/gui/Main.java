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

import com.openitvn.engine.renderware.tool.RwTexer;
import com.openitvn.engine.renderware.tool.RwDumper;
import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.gtavc.gui.g3d.ViewportApp;
import com.openitvn.gtavc.gui.g3d.ViewportMode;
import com.openitvn.gtavc.gui.pref.MainState;
import com.openitvn.unicore.archive.IArchiveEntry;
import com.openitvn.unicore.plugin.gta.ResourceModel;
import com.openitvn.unicore.plugin.gta.WorldScript;
import com.openitvn.unicore.plugin.gta.item.*;
import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Thinh Pham
 */
public class Main extends javax.swing.JFrame
{
    private static Main instance;
    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }
    
    private final WorldScriptModel worldScriptModel = new WorldScriptModel();
    private final WorldItemModel worldItemModel = new WorldItemModel();
    private final ResourceModel resourceModel = ResourceModel.getInstance();
    private final ViewportApp gdxApp = ViewportApp.getInstance();
    private String dffFileName, txdFileName;
    
    private Main() {
        initComponents();
        recallWindowState();
        initViewpot();
        initAssetTable();
        initWorldScriptTable();
        initWorldItemTable();
    }

    //<editor-fold defaultstate="collapsed" desc="Initialize and Dispose">
    
    private void recallWindowState() {
        MainState state = MainState.getInstance();
        setSize(state.windowWidth, state.windowHeight);
        setLocation(new Point(state.windowLeft, state.windowTop));
        setExtendedState(state.windowExtendedState);
        splMain.setDividerLocation(state.dividerLocation);
    }

    private void initViewpot() {
        Canvas canvas = gdxApp.getCanvas();
        viewpotArea.add(canvas);
        onSwitchControlPanel(null);
    }

    private void initAssetTable() {
        TableColumnModel tcm = resourceTable.getColumnModel();
        tcm.getColumn(ResourceModel.COL_INDEX).setMinWidth(40);
        tcm.getColumn(ResourceModel.COL_INDEX).setMaxWidth(40);
        tcm.getColumn(ResourceModel.COL_SIZE).setMinWidth(60);
        tcm.getColumn(ResourceModel.COL_SIZE).setMaxWidth(60);
    }

    private void initWorldScriptTable() {
        // Setup column
        TableColumnModel tcm = worldScriptTable.getColumnModel();
        tcm.getColumn(WorldScriptModel.COL_ACTIVE).setMinWidth(20);
        tcm.getColumn(WorldScriptModel.COL_ACTIVE).setMaxWidth(20);
        tcm.getColumn(WorldScriptModel.COL_TYPE).setMinWidth(40);
        tcm.getColumn(WorldScriptModel.COL_TYPE).setMaxWidth(40);
        // Bind data
        try {
            worldScriptModel.reload(resourceModel);
            refineWorldScriptTable();
        } catch (IOException ex) {}
        
        worldScriptTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                int row = worldScriptTable.getSelectedRow();
                if (row >= 0) {
                    row = worldScriptTable.convertRowIndexToModel(row);
                    WorldScript script = worldScriptModel.scripts.get(row);
                    worldItemModel.entries = worldScriptModel.getScriptItems(script);
                    worldItemModel.fireTableDataChanged();
                }
            }
        });
    }

    private void refineWorldScriptTable() {
        String regex;
        if (rdoIDE.isSelected()) {
            regex = "(^IDE$)";
        }
        else if (rdoIPL.isSelected()) {
            regex = "(^IPL$)";
        }
        else {
            regex = "(^NULL$)";
        }
        TableRowSorter<WorldScriptModel> sorter = (TableRowSorter)worldScriptTable.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(regex, WorldScriptModel.COL_TYPE));
    }

    private void initWorldItemTable() {
        TableColumnModel tcm = worldItemTable.getColumnModel();
        tcm.getColumn(WorldItemModel.COL_TYPE).setMinWidth(40);
        tcm.getColumn(WorldItemModel.COL_TYPE).setMaxWidth(40);
    }

    public int getDividerLocation() {
        return splMain.getDividerLocation();
    }

    @Override
    public void dispose() {
        instance = null;
        MainState.getInstance().saveWindowState(this);
        if (mnuTexture.isSelected()) {
            RwTexer.getInstance().dispose();
        }
        gdxApp.dispose();
        super.dispose();
        System.exit(0);
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="RenderWare Tools">
    
    private IArchiveEntry getEntryFromResourceTable() {
        int id = resourceTable.convertRowIndexToModel(resourceTable.getSelectedRow());
        if (id >= 0) {
            return resourceModel.getEntry(id);
        }
        return null;
    }
    
    private void openRwDump() {
        IArchiveEntry e = getEntryFromResourceTable();
        if (e == null) return;
        String type = e.getExt().toLowerCase();
        if (type.equals("txd") || type.equals("dff")) {
            RwDumper dlg = RwDumper.getInstance();
            dlg.setVisible(true);
            dlg.openEntry(e);
        }
    }
    
    private void openRwTexture() {
        IArchiveEntry e = getEntryFromResourceTable();
        if (e == null) return;
        if (e.getExt().equalsIgnoreCase("txd")) {
            RwTexer texer = RwTexer.getInstance();
            texer.setVisible(true);
            texer.openEntry(e);
        }
    }
    
    private void openRwModel() {
        IArchiveEntry e = getEntryFromResourceTable();
        if (e == null || !e.getExt().equalsIgnoreCase("dff")) return;
        try {
            dffFileName = e.getName();
            // Find match name DFF and TXD for texture dictionary
            String modName = dffFileName.substring(0, dffFileName.length() - 4);
            String txdName = findTexDic(modName);
            gdxApp.modelView.openModel(modName, txdName);
            RpTextureDictionary texDic = gdxApp.modelView.getTexDic(txdName);
            if (texDic != null) {
                txdFileName = txdName + ".txd";
                if (mnuTexture.isSelected()) {
                    RwTexer.getInstance().loadTexDic(txdFileName, texDic);
                }
            } else {
                txdFileName = null;
            }
            updateInfoLabel();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    private String findTexDic(String modName) {
        for (ItemNULL script : worldItemModel.entries) {
            switch (script.getType()){
                case "OBJS":
                case "TOBJ":
                    ItemOBJS objs = (ItemOBJS)script;
                    if (modName.equals(objs.modName)) {
                        return objs.txdName;
                    }
            }
        }
        return modName;
    }
    
    //</editor-fold>
    
    private void updateInfoLabel() {
        ViewportMode mode = gdxApp.getViewpotMode();
        lblInfo.setText(mode.toString());
        if (mode == ViewportMode.SingleModel) {
            lblInfo.setText(lblInfo.getText() + ": " + dffFileName + " < " + txdFileName);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mnuTxd = new javax.swing.JPopupMenu();
        mnuTxdViewer = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuTxdDumper = new javax.swing.JMenuItem();
        mnuDff = new javax.swing.JPopupMenu();
        mnuDffViewer = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuDffDumper = new javax.swing.JMenuItem();
        rdoMapDefinition = new javax.swing.ButtonGroup();
        splMain = new javax.swing.JSplitPane();
        tabbedControlPanel = new javax.swing.JTabbedPane();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        resourceTable = new javax.swing.JTable();
        txtFindResource = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        worldScriptTable = new javax.swing.JTable();
        javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
        worldItemTable = new javax.swing.JTable();
        rdoIDE = new javax.swing.JRadioButton();
        rdoIPL = new javax.swing.JRadioButton();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        cboDistance = new javax.swing.JComboBox<>();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        cboTime = new javax.swing.JComboBox<>();
        viewpotArea = new javax.swing.JPanel();
        statusBar = new javax.swing.JToolBar();
        lblInfo = new javax.swing.JLabel();
        javax.swing.JMenuBar jMenuBar1 = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu1 = new javax.swing.JMenu();
        mnuWorkspace = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuExit = new javax.swing.JMenuItem();
        javax.swing.JMenu jMenu2 = new javax.swing.JMenu();
        mnuTexture = new javax.swing.JCheckBoxMenuItem();

        mnuTxdViewer.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        mnuTxdViewer.setText("Texture Viewer...");
        mnuTxdViewer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTxdViewerActionPerformed(evt);
            }
        });
        mnuTxd.add(mnuTxdViewer);
        mnuTxd.add(jSeparator2);

        mnuTxdDumper.setText("Section Dumper...");
        mnuTxdDumper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTxdDumperActionPerformed(evt);
            }
        });
        mnuTxd.add(mnuTxdDumper);

        mnuDffViewer.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        mnuDffViewer.setText("RW Model Viewer...");
        mnuDffViewer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDffViewerActionPerformed(evt);
            }
        });
        mnuDff.add(mnuDffViewer);
        mnuDff.add(jSeparator3);

        mnuDffDumper.setText("RW Engine Dumper...");
        mnuDffDumper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDffDumperActionPerformed(evt);
            }
        });
        mnuDff.add(mnuDffDumper);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("GTA Viewer");
        setMinimumSize(new java.awt.Dimension(960, 600));

        splMain.setBorder(null);

        tabbedControlPanel.setMinimumSize(new java.awt.Dimension(320, 0));
        tabbedControlPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                onSwitchControlPanel(evt);
            }
        });

        resourceTable.setAutoCreateRowSorter(true);
        resourceTable.setModel(resourceModel);
        resourceTable.setRowHeight(20);
        resourceTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        resourceTable.setShowHorizontalLines(false);
        resourceTable.setShowVerticalLines(false);
        resourceTable.getTableHeader().setResizingAllowed(false);
        resourceTable.getTableHeader().setReorderingAllowed(false);
        resourceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                onResouceTablePopupTriggered(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onResouceTablePopupTriggered(evt);
                onResourceTableClicked(evt);
            }
        });
        resourceTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                onResourceTableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(resourceTable);

        txtFindResource.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFindResourceKeyTyped(evt);
            }
        });

        jLabel2.setLabelFor(txtFindResource);
        jLabel2.setText("Search:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(7, 7, 7)
                        .addComponent(txtFindResource)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFindResource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlPanel.addTab("Resource", new javax.swing.ImageIcon(getClass().getResource("/icon16/box_open.png")), jPanel1); // NOI18N

        worldScriptTable.setAutoCreateRowSorter(true);
        worldScriptTable.setModel(worldScriptModel);
        worldScriptTable.setRowHeight(20);
        worldScriptTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        worldScriptTable.setShowHorizontalLines(false);
        worldScriptTable.setShowVerticalLines(false);
        worldScriptTable.getTableHeader().setResizingAllowed(false);
        worldScriptTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(worldScriptTable);

        worldItemTable.setAutoCreateRowSorter(true);
        worldItemTable.setModel(worldItemModel);
        worldItemTable.setRowHeight(20);
        worldItemTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        worldItemTable.setShowHorizontalLines(false);
        worldItemTable.setShowVerticalLines(false);
        worldItemTable.getTableHeader().setResizingAllowed(false);
        worldItemTable.getTableHeader().setReorderingAllowed(false);
        worldItemTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                worldItemTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(worldItemTable);

        rdoMapDefinition.add(rdoIDE);
        rdoIDE.setText("Definition (IDE)");
        rdoIDE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onWorldScriptChanged(evt);
            }
        });

        rdoMapDefinition.add(rdoIPL);
        rdoIPL.setSelected(true);
        rdoIPL.setText("Placement (IPL)");
        rdoIPL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onWorldScriptChanged(evt);
            }
        });

        jLabel5.setLabelFor(cboDistance);
        jLabel5.setText("Distance Mode:");

        cboDistance.setModel(new DefaultComboBoxModel(new ViewportMode[] {
            ViewportMode.NormalWorld,
            ViewportMode.DistanceWorld
        })
    );
    cboDistance.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            cboDistanceItemStateChanged(evt);
        }
    });

    jLabel3.setLabelFor(cboTime);
    jLabel3.setText("Time:");

    cboTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));
    cboTime.setSelectedIndex(12);
    cboTime.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            cboTimeItemStateChanged(evt);
        }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(rdoIDE)
                    .addGap(18, 18, 18)
                    .addComponent(rdoIPL)
                    .addGap(0, 77, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jLabel5)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(cboDistance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(cboTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(rdoIDE)
                .addComponent(rdoIPL))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel3)
                .addComponent(cboTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel5)
                .addComponent(cboDistance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    tabbedControlPanel.addTab("Map", new javax.swing.ImageIcon(getClass().getResource("/icon16/map.png")), jPanel2); // NOI18N

    tabbedControlPanel.setSelectedIndex(1);

    splMain.setLeftComponent(tabbedControlPanel);

    viewpotArea.setLayout(new java.awt.BorderLayout());
    splMain.setRightComponent(viewpotArea);

    statusBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
    statusBar.setFloatable(false);

    lblInfo.setText("<none>");
    lblInfo.setMaximumSize(new java.awt.Dimension(360, 14));
    lblInfo.setMinimumSize(new java.awt.Dimension(360, 14));
    lblInfo.setPreferredSize(new java.awt.Dimension(360, 14));
    statusBar.add(lblInfo);

    jMenu1.setText("File");

    mnuWorkspace.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    mnuWorkspace.setText("Switch Workspace");
    mnuWorkspace.setEnabled(false);
    mnuWorkspace.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            mnuWorkspaceActionPerformed(evt);
        }
    });
    jMenu1.add(mnuWorkspace);
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

    jMenu2.setText("Tools");

    mnuTexture.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_DOWN_MASK));
    mnuTexture.setText("Texture Viewer...");
    mnuTexture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon16/application_view_gallery.png"))); // NOI18N
    mnuTexture.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            mnuTextureItemStateChanged(evt);
        }
    });
    jMenu2.add(mnuTexture);

    jMenuBar1.add(jMenu2);

    setJMenuBar(jMenuBar1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(splMain, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
            .addContainerGap())
        .addComponent(statusBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(splMain)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onResourceTableClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onResourceTableClicked
        if (evt.getButton() == MouseEvent.BUTTON3)
            return; // Cancel right mouse for popup trigger
        boolean isDoubleClick = (evt.getClickCount() == 2);
        if (mnuTexture.isSelected() || isDoubleClick) {
            openRwTexture();
        }
        if (gdxApp.getViewpotMode() == ViewportMode.SingleModel || isDoubleClick) {
            openRwModel();
        }
    }//GEN-LAST:event_onResourceTableClicked

    private void txtFindResourceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFindResourceKeyTyped
        String regex = txtFindResource.getText();
        regex = String.format("(?i)(%1$s)", regex);
        TableRowSorter<WorldItemModel> sorter = (TableRowSorter) resourceTable.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(regex, ResourceModel.COL_NAME));
    }//GEN-LAST:event_txtFindResourceKeyTyped

    private void onResouceTablePopupTriggered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onResouceTablePopupTriggered
        int x = evt.getX();
        int y = evt.getY();
        if (evt.getButton() == MouseEvent.BUTTON3) {
            int selRow = resourceTable.rowAtPoint(new Point(x, y));
            resourceTable.setRowSelectionInterval(selRow, selRow);
        }
        int id = resourceTable.convertRowIndexToModel(resourceTable.getSelectedRow());
        if (evt.isPopupTrigger() && id > -1) {
            IArchiveEntry e = resourceModel.getEntry(id);
            switch (e.getExt().toLowerCase()) {
                case "txd":
                    mnuTxd.show(evt.getComponent(), x, y);
                    break;
                    
                case "dff":
                    mnuDff.show(evt.getComponent(), x, y);
                    break;
            }
        }
    }//GEN-LAST:event_onResouceTablePopupTriggered

    private void mnuTxdViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTxdViewerActionPerformed
        openRwTexture();
    }//GEN-LAST:event_mnuTxdViewerActionPerformed

    private void mnuTxdDumperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTxdDumperActionPerformed
        openRwDump();
    }//GEN-LAST:event_mnuTxdDumperActionPerformed

    private void mnuDffDumperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDffDumperActionPerformed
        openRwDump();
    }//GEN-LAST:event_mnuDffDumperActionPerformed

    private void onResourceTableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_onResourceTableKeyReleased
        if (mnuTexture.isSelected()) {
            openRwTexture();
        }
        if (gdxApp.getViewpotMode() == ViewportMode.SingleModel) {
            openRwModel();
        }
    }//GEN-LAST:event_onResourceTableKeyReleased

    private void mnuDffViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDffViewerActionPerformed
        openRwModel();
    }//GEN-LAST:event_mnuDffViewerActionPerformed

    private void worldItemTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_worldItemTableMouseClicked
        int id = worldItemTable.convertRowIndexToModel(worldItemTable.getSelectedRow());
        ItemNULL e = worldItemModel.entries.get(id);
        switch (e.getType()) {
            case "INST":
                ItemINST inst = (ItemINST)e;
                gdxApp.worldView.moveCameraTo(inst.posX, inst.posZ, -inst.posY);
                break;
        }
    }//GEN-LAST:event_worldItemTableMouseClicked

    private void onWorldScriptChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onWorldScriptChanged
        refineWorldScriptTable();
    }//GEN-LAST:event_onWorldScriptChanged

    private void setViewportMode(ViewportMode mode) {
        gdxApp.setViewpotMode(mode);
        updateInfoLabel();
    }
    
    private void onSwitchControlPanel(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_onSwitchControlPanel
        switch (tabbedControlPanel.getSelectedIndex()) {
            case 0: // Resource
                setViewportMode(ViewportMode.SingleModel);
                break;
            case 1: // World
                setViewportMode((ViewportMode)cboDistance.getSelectedItem());
                break;
        }
    }//GEN-LAST:event_onSwitchControlPanel

    private void cboDistanceItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboDistanceItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED)
            setViewportMode((ViewportMode)evt.getItem());
    }//GEN-LAST:event_cboDistanceItemStateChanged

    private void mnuTextureItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mnuTextureItemStateChanged
        RwTexer dialog = RwTexer.getInstance();
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            dialog.setVisible(true);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent evt) {
                    mnuTexture.setSelected(false);
                }
            });
        } else {
            dialog.dispose();
        }
    }//GEN-LAST:event_mnuTextureItemStateChanged

    private void cboTimeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTimeItemStateChanged
        gdxApp.worldView.setTime(cboTime.getSelectedIndex());
    }//GEN-LAST:event_cboTimeItemStateChanged

    private void mnuWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuWorkspaceActionPerformed
        dispose();
        Startup dlg = new Startup();
        dlg.setVisible(true);
    }//GEN-LAST:event_mnuWorkspaceActionPerformed

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        dispose();
    }//GEN-LAST:event_mnuExitActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<ViewportMode> cboDistance;
    private javax.swing.JComboBox<String> cboTime;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JPopupMenu mnuDff;
    private javax.swing.JMenuItem mnuDffDumper;
    private javax.swing.JMenuItem mnuDffViewer;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JCheckBoxMenuItem mnuTexture;
    private javax.swing.JPopupMenu mnuTxd;
    private javax.swing.JMenuItem mnuTxdDumper;
    private javax.swing.JMenuItem mnuTxdViewer;
    private javax.swing.JMenuItem mnuWorkspace;
    private javax.swing.JRadioButton rdoIDE;
    private javax.swing.JRadioButton rdoIPL;
    private javax.swing.ButtonGroup rdoMapDefinition;
    private javax.swing.JTable resourceTable;
    private javax.swing.JSplitPane splMain;
    private javax.swing.JToolBar statusBar;
    private javax.swing.JTabbedPane tabbedControlPanel;
    private javax.swing.JTextField txtFindResource;
    private javax.swing.JPanel viewpotArea;
    private javax.swing.JTable worldItemTable;
    private javax.swing.JTable worldScriptTable;
    // End of variables declaration//GEN-END:variables
}
