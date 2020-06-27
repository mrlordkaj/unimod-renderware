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
import com.openitvn.unicore.plugin.gta.item.ItemNULL;
import com.openitvn.gtavc.gui.g3d.GtaTextureManager;
import com.openitvn.gtavc.gui.g3d.ViewportApp;
import com.openitvn.gtavc.gui.g3d.ViewportMode;
import com.openitvn.gtavc.gui.pref.MainState;
import com.openitvn.unicore.archive.IArchiveEntry;
import com.openitvn.unicore.plugin.gta.GameConfig;
import com.openitvn.unicore.plugin.gta.ResourceModel;
import com.openitvn.unicore.plugin.gta.item.ItemCARS;
import com.openitvn.unicore.plugin.gta.item.ItemOBJS;
import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.RowFilter;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Thinh Pham
 */
public class Main extends javax.swing.JFrame {
    
    private final ScriptFileModel scriptGroupModel = new ScriptFileModel();
    private final ResourceModel resource = ResourceModel.getInstance();
    private final ViewportApp gdxApp = ViewportApp.getInstance();
    private String currentModelFile, currentTexDicFile;
    
    private static Main instance;
    public static Main getInstance() {
        if (instance == null)
            instance = new Main();
        return instance;
    }

    private Main() {
        initComponents();
        recallWindowState();
        initViewpot();
        initAssetTable();
        initItemGroupTable();
        initItemTable();
        
        //active default item groups
        for (String ide : GameConfig.getDependencies()) {
            scriptGroupModel.activeIPL(ide);
        }
        
        initVehicleTable();
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
        //setup the table
        TableColumnModel cm = tblResource.getColumnModel();
        cm.getColumn(ResourceModel.COL_INDEX).setMinWidth(40);
        cm.getColumn(ResourceModel.COL_INDEX).setMaxWidth(40);
        cm.getColumn(ResourceModel.COL_SIZE).setMinWidth(60);
        cm.getColumn(ResourceModel.COL_SIZE).setMaxWidth(60);
    }

    private void initItemGroupTable() {
        //setup the table
        TableColumnModel cm = tblDefinitionGroup.getColumnModel();
        cm.getColumn(ScriptFileModel.COL_ACTIVE).setMinWidth(20);
        cm.getColumn(ScriptFileModel.COL_ACTIVE).setMaxWidth(20);
        cm.getColumn(ScriptFileModel.COL_INDEX).setMinWidth(30);
        cm.getColumn(ScriptFileModel.COL_INDEX).setMaxWidth(30);
        cm.getColumn(ScriptFileModel.COL_TYPE).setMinWidth(40);
        cm.getColumn(ScriptFileModel.COL_TYPE).setMaxWidth(40);
        //bind data
        try {
            scriptGroupModel.reload();
            refineItemGroupTable();
        } catch (IOException ex) { }
    }

    private void refineItemGroupTable() {
        String regex;
        if (rdoIDE.isSelected()) {
            regex = "(^IDE$)";
        } else if (rdoIPL.isSelected()) {
            regex = "(^IPL$)";
        } else {
            regex = "(^NULL$)";
        }
        TableRowSorter<ScriptFileModel> sorter = (TableRowSorter) tblDefinitionGroup.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(regex, ScriptFileModel.COL_TYPE));
    }

    private void initItemTable() {
        TableColumnModel cm = tblDefinitionItem.getColumnModel();
        cm.getColumn(ScriptItemModel.COL_TYPE).setMinWidth(40);
        cm.getColumn(ScriptItemModel.COL_TYPE).setMaxWidth(40);
        cm.getColumn(ScriptItemModel.COL_FILE).setMinWidth(30);
        cm.getColumn(ScriptItemModel.COL_FILE).setMaxWidth(30);
        refineItemTable();
    }

    private void refineItemTable() {
        ArrayList<RowFilter<Object, Object>> filters = new ArrayList<>(2);

        //rebuild regex of selected definition file
        String filterByFile = "(^-1$)";
        int row = tblDefinitionGroup.getSelectedRow();
        if (row >= 0) {
            row = tblDefinitionGroup.convertRowIndexToModel(row);
            int fileId = scriptGroupModel.getEntries().get(row).getIndex();
            filterByFile = "(^" + fileId + "$)";
        }
        filters.add(RowFilter.regexFilter(filterByFile, ScriptItemModel.COL_FILE));

        //rebuild regex of selected types
        filters.add(RowFilter.regexFilter("", ScriptItemModel.COL_TYPE));

        TableRowSorter<ScriptItemModel> sorter = (TableRowSorter)tblDefinitionItem.getRowSorter();
        sorter.setRowFilter(RowFilter.andFilter(filters));
    }
    
    private void initVehicleTable() {
        tblVehicle.setModel(ViewportApp.getInstance().vehicleModel);
        TableColumnModel cm = tblVehicle.getColumnModel();
        cm.getColumn(VehicleTableModel.COL_INDEX).setMinWidth(40);
        cm.getColumn(VehicleTableModel.COL_INDEX).setMaxWidth(40);
        cm.getColumn(VehicleTableModel.COL_TYPE).setMinWidth(50);
        cm.getColumn(VehicleTableModel.COL_TYPE).setMaxWidth(50);
    }

    public int getDividerLocation() {
        return splMain.getDividerLocation();
    }

    @Override
    public void dispose() {
        MainState.getInstance().saveWindowState(this);
        gdxApp.dispose();
        super.dispose();
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="RenderWare Tools">
    
    private void openRwDump() {
        int id = tblResource.convertRowIndexToModel(tblResource.getSelectedRow());
        if (id >= 0) {
            IArchiveEntry e = resource.getEntry(id);
            String type = e.getExt().toLowerCase();
            if (type.equals("txd") || type.equals("dff")) {
                RwDumper dlg = RwDumper.getInstance();
                dlg.setVisible(true);
                dlg.openEntry(e);
            }
        }
    }
    
    private void openRwTexture() {
        int id = tblResource.convertRowIndexToModel(tblResource.getSelectedRow());
        if (id >= 0) {
            IArchiveEntry e = resource.getEntry(id);
            if (e.getExt().equalsIgnoreCase("txd")) {
                RwTexer dlg = RwTexer.getInstance();
                dlg.setVisible(true);
                dlg.openEntry(e);
            }
        }
    }
    
    private void openRwModel() {
        try {
            int id = tblResource.convertRowIndexToModel(tblResource.getSelectedRow());
            if (id >= 0) {
                IArchiveEntry e = resource.getEntry(id);
                if (e.getExt().equalsIgnoreCase("dff")) {
                    String fileName = e.getName();
                    currentModelFile = fileName;
                    // find match name dff and txd for texture dictionary
                    String modName = fileName.substring(0, fileName.length() - 4);
                    String txdName = findTexDic(modName);
                    RpTextureDictionary texDic = GtaTextureManager.getTexDic(txdName);
                    if (texDic != null) {
                        currentTexDicFile = txdName + ".txd";
                        if (mnuTexture.isSelected()) {
                            RwTexer.getInstance().loadTexDic(currentTexDicFile, texDic);
                        }
                    } else {
                        currentTexDicFile = null;
                    }
                    gdxApp.setSingleModel(modName, txdName);
                    lblInfo.setText(ViewportMode.SingleModel + ": " + currentModelFile + " < " + currentTexDicFile);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    private String findTexDic(String modName) {
        for (ItemNULL def : scriptGroupModel.getDefinitionItemModel().getEntries()) {
            switch (def.getType()){
                case OBJS:
                case TOBJ:
                    ItemOBJS objs = (ItemOBJS)def;
                    if (modName.equals(objs.modName)) {
                        return objs.txdName;
                    }
            }
        }
        return modName;
    }
    
    private void showVehicle() {
        try {
            ViewportMode viewMode;
            if (rdoVehicleDamaged.isSelected()) {
                viewMode = ViewportMode.VehicleDamaged;
            } else if (rdoVehicleDistance.isSelected()) {
                viewMode = ViewportMode.VehicleDistance;
            } else {
                viewMode = ViewportMode.VehicleNormal;
            }
            int row = tblVehicle.getSelectedRow();
            if (row >= 0) {
                int id = tblVehicle.convertRowIndexToModel(row);
                VehicleTableModel data = (VehicleTableModel) tblVehicle.getModel();
                ItemCARS entry = data.entries.get(id);
                if (entry != null) {
                    gdxApp.setVehicle(entry, viewMode);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    //</editor-fold>
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mnuTxd = new javax.swing.JPopupMenu();
        mnuTxdViewer = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuTxdDumper = new javax.swing.JMenuItem();
        mnuDff = new javax.swing.JPopupMenu();
        mnuDffViewer = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuDffDumper = new javax.swing.JMenuItem();
        rdoMapDefinition = new javax.swing.ButtonGroup();
        rdoVehicleMode = new javax.swing.ButtonGroup();
        splMain = new javax.swing.JSplitPane();
        tabbedControlPanel = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblResource = new javax.swing.JTable();
        txtFindResource = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDefinitionGroup = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDefinitionItem = new javax.swing.JTable();
        rdoIDE = new javax.swing.JRadioButton();
        rdoIPL = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        cboViewMode = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cboMapTime = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblVehicle = new javax.swing.JTable();
        rdoVehicleNormal = new javax.swing.JRadioButton();
        rdoVehicleDamaged = new javax.swing.JRadioButton();
        rdoVehicleDistance = new javax.swing.JRadioButton();
        viewpotArea = new javax.swing.JPanel();
        statusBar = new javax.swing.JToolBar();
        lblInfo = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        mnuTexture = new javax.swing.JCheckBoxMenuItem();

        mnuTxdViewer.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        mnuTxdViewer.setText("RW Texture Viewer...");
        mnuTxdViewer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTxdViewerActionPerformed(evt);
            }
        });
        mnuTxd.add(mnuTxdViewer);
        mnuTxd.add(jSeparator2);

        mnuTxdDumper.setText("RW Engine Dumper...");
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

        tblResource.setAutoCreateRowSorter(true);
        tblResource.setModel(resource);
        tblResource.setRowHeight(20);
        tblResource.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblResource.setShowHorizontalLines(false);
        tblResource.setShowVerticalLines(false);
        tblResource.getTableHeader().setResizingAllowed(false);
        tblResource.getTableHeader().setReorderingAllowed(false);
        tblResource.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                onTableResourcePopupTrigger(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onTableResourcePopupTrigger(evt);
                onResourceEntryClicked(evt);
            }
        });
        tblResource.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblResourceKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblResource);

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlPanel.addTab("Resource", new javax.swing.ImageIcon(getClass().getResource("/icon16/box_open.png")), jPanel1); // NOI18N

        tblDefinitionGroup.setAutoCreateRowSorter(true);
        tblDefinitionGroup.setModel(scriptGroupModel);
        tblDefinitionGroup.setRowHeight(20);
        tblDefinitionGroup.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDefinitionGroup.setShowHorizontalLines(false);
        tblDefinitionGroup.setShowVerticalLines(false);
        tblDefinitionGroup.getTableHeader().setResizingAllowed(false);
        tblDefinitionGroup.getTableHeader().setReorderingAllowed(false);
        tblDefinitionGroup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                refineDefinitionItemMouse(evt);
            }
        });
        tblDefinitionGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                refineDefinitionItemKey(evt);
            }
        });
        jScrollPane2.setViewportView(tblDefinitionGroup);

        tblDefinitionItem.setAutoCreateRowSorter(true);
        tblDefinitionItem.setModel(scriptGroupModel.getDefinitionItemModel());
        tblDefinitionItem.setRowHeight(20);
        tblDefinitionItem.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDefinitionItem.setShowHorizontalLines(false);
        tblDefinitionItem.setShowVerticalLines(false);
        tblDefinitionItem.getTableHeader().setResizingAllowed(false);
        tblDefinitionItem.getTableHeader().setReorderingAllowed(false);
        tblDefinitionItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDefinitionItemMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblDefinitionItem);

        rdoMapDefinition.add(rdoIDE);
        rdoIDE.setText("Item Definition (IDE)");
        rdoIDE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRefineDefinitionGroup(evt);
            }
        });

        rdoMapDefinition.add(rdoIPL);
        rdoIPL.setSelected(true);
        rdoIPL.setText("Item Placement (IPL)");
        rdoIPL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRefineDefinitionGroup(evt);
            }
        });

        jLabel5.setLabelFor(cboViewMode);
        jLabel5.setText("Map Mode:");

        cboViewMode.setModel(new DefaultComboBoxModel(new ViewportMode[] {
            ViewportMode.MapNormal,
            ViewportMode.MapDistance
        })
    );
    cboViewMode.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            cboViewModeItemStateChanged(evt);
        }
    });

    jLabel3.setLabelFor(cboMapTime);
    jLabel3.setText("Time:");

    cboMapTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));
    cboMapTime.setSelectedIndex(12);
    cboMapTime.setEnabled(false);

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
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(rdoIDE)
                            .addGap(18, 18, 18)
                            .addComponent(rdoIPL))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cboViewMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cboMapTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 29, Short.MAX_VALUE)))
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
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel3)
                .addComponent(cboMapTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel5)
                .addComponent(cboViewMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    tabbedControlPanel.addTab("Map", new javax.swing.ImageIcon(getClass().getResource("/icon16/map.png")), jPanel2); // NOI18N

    tblVehicle.setAutoCreateRowSorter(true);
    tblVehicle.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {

        },
        new String [] {

        }
    ));
    tblVehicle.setRowHeight(20);
    tblVehicle.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    tblVehicle.setShowHorizontalLines(false);
    tblVehicle.setShowVerticalLines(false);
    tblVehicle.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseReleased(java.awt.event.MouseEvent evt) {
            tblVehicleMouseReleased(evt);
        }
    });
    tblVehicle.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            tblVehicleKeyReleased(evt);
        }
    });
    jScrollPane4.setViewportView(tblVehicle);

    rdoVehicleMode.add(rdoVehicleNormal);
    rdoVehicleNormal.setSelected(true);
    rdoVehicleNormal.setText("Normal");
    rdoVehicleNormal.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            onVehicleModeChanged(evt);
        }
    });

    rdoVehicleMode.add(rdoVehicleDamaged);
    rdoVehicleDamaged.setText("Damaged");
    rdoVehicleDamaged.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            onVehicleModeChanged(evt);
        }
    });

    rdoVehicleMode.add(rdoVehicleDistance);
    rdoVehicleDistance.setText("Distance");
    rdoVehicleDistance.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            onVehicleModeChanged(evt);
        }
    });

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(rdoVehicleNormal)
                    .addGap(18, 18, 18)
                    .addComponent(rdoVehicleDamaged)
                    .addGap(18, 18, 18)
                    .addComponent(rdoVehicleDistance)
                    .addGap(0, 62, Short.MAX_VALUE))
                .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(rdoVehicleNormal)
                .addComponent(rdoVehicleDamaged)
                .addComponent(rdoVehicleDistance))
            .addContainerGap())
    );

    tabbedControlPanel.addTab("Vehicle", new javax.swing.ImageIcon(getClass().getResource("/icon16/lorry.png")), jPanel3); // NOI18N

    tabbedControlPanel.setSelectedIndex(1);

    splMain.setLeftComponent(tabbedControlPanel);

    viewpotArea.setLayout(new java.awt.BorderLayout());
    splMain.setRightComponent(viewpotArea);

    statusBar.setFloatable(false);
    statusBar.setRollover(true);

    lblInfo.setText("<none>");
    lblInfo.setMaximumSize(new java.awt.Dimension(360, 14));
    lblInfo.setMinimumSize(new java.awt.Dimension(360, 14));
    lblInfo.setPreferredSize(new java.awt.Dimension(360, 14));
    statusBar.add(lblInfo);

    jMenu1.setText("File");
    jMenuBar1.add(jMenu1);

    jMenu2.setText("Tools");

    mnuTexture.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
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
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(splMain)
                .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE))
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(splMain)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onResourceEntryClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onResourceEntryClicked
        if(evt.getButton() == MouseEvent.BUTTON3) return; //cancel right mouse for popup trigger
        boolean isDoubleClick = (evt.getClickCount() == 2);
        if(mnuTexture.isSelected() || isDoubleClick) openRwTexture();
        if(gdxApp.getViewpotMode() == ViewportMode.SingleModel || isDoubleClick) openRwModel();
    }//GEN-LAST:event_onResourceEntryClicked

    private void refineDefinitionItemKey(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_refineDefinitionItemKey
        refineItemTable();
    }//GEN-LAST:event_refineDefinitionItemKey

    private void refineDefinitionItemMouse(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refineDefinitionItemMouse
        refineItemTable();
    }//GEN-LAST:event_refineDefinitionItemMouse

    private void txtFindResourceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFindResourceKeyTyped
        String regex = txtFindResource.getText();
        regex = String.format("(?i)(%1$s)", regex);
        TableRowSorter<ScriptItemModel> sorter = (TableRowSorter) tblResource.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(regex, ResourceModel.COL_NAME));
    }//GEN-LAST:event_txtFindResourceKeyTyped

    private void onTableResourcePopupTrigger(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onTableResourcePopupTrigger
        int x = evt.getX();
        int y = evt.getY();
        if (evt.getButton() == MouseEvent.BUTTON3) {
            int selRow = tblResource.rowAtPoint(new Point(x, y));
            tblResource.setRowSelectionInterval(selRow, selRow);
        }
        int id = tblResource.convertRowIndexToModel(tblResource.getSelectedRow());
        if (evt.isPopupTrigger() && id > -1) {
            IArchiveEntry e = resource.getEntry(id);
            switch (e.getExt().toLowerCase()) {
                case "txd":
                    mnuTxd.show(evt.getComponent(), x, y);
                    break;
                    
                case "dff":
                    mnuDff.show(evt.getComponent(), x, y);
                    break;
            }
        }
    }//GEN-LAST:event_onTableResourcePopupTrigger

    private void mnuTxdViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTxdViewerActionPerformed
        openRwTexture();
    }//GEN-LAST:event_mnuTxdViewerActionPerformed

    private void mnuTxdDumperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTxdDumperActionPerformed
        openRwDump();
    }//GEN-LAST:event_mnuTxdDumperActionPerformed

    private void mnuDffDumperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDffDumperActionPerformed
        openRwDump();
    }//GEN-LAST:event_mnuDffDumperActionPerformed

    private void tblResourceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblResourceKeyReleased
        if (mnuTexture.isSelected()) openRwTexture();
        if (gdxApp.getViewpotMode() == ViewportMode.SingleModel) openRwModel();
    }//GEN-LAST:event_tblResourceKeyReleased

    private void mnuDffViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDffViewerActionPerformed
        openRwModel();
    }//GEN-LAST:event_mnuDffViewerActionPerformed

    private void tblDefinitionItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDefinitionItemMouseClicked
        int id = tblDefinitionItem.convertRowIndexToModel(tblDefinitionItem.getSelectedRow());
        ItemNULL e = scriptGroupModel.getDefinitionItemModel().getEntry(id);
        switch (e.getType()) {
            case INST:
                gdxApp.select(e);
                break;
        }
    }//GEN-LAST:event_tblDefinitionItemMouseClicked

    private void onRefineDefinitionGroup(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRefineDefinitionGroup
        refineItemGroupTable();
    }//GEN-LAST:event_onRefineDefinitionGroup

    private void setViewportMode(ViewportMode mode) {
        gdxApp.setViewpotMode(mode);
        lblInfo.setText(mode.toString());
        if (mode == ViewportMode.SingleModel)
            lblInfo.setText(lblInfo.getText() + ": " + currentModelFile + " < " + currentTexDicFile);
    }
    
    private void onSwitchControlPanel(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_onSwitchControlPanel
        switch (tabbedControlPanel.getSelectedIndex()) {
            case 0: // resource
                setViewportMode(ViewportMode.SingleModel);
                break;
                
            case 1: // map
                setViewportMode((ViewportMode)cboViewMode.getSelectedItem());
                break;
                
            case 2: //vehicle
                if (rdoVehicleNormal.isSelected())
                    setViewportMode(ViewportMode.VehicleNormal);
                else if (rdoVehicleDamaged.isSelected())
                    setViewportMode(ViewportMode.VehicleDamaged);
                else if (rdoVehicleDistance.isSelected())
                    setViewportMode(ViewportMode.VehicleDistance);
                break;
        }
    }//GEN-LAST:event_onSwitchControlPanel

    private void onVehicleModeChanged(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onVehicleModeChanged
        showVehicle();
    }//GEN-LAST:event_onVehicleModeChanged

    private void tblVehicleMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVehicleMouseReleased
        showVehicle();
    }//GEN-LAST:event_tblVehicleMouseReleased

    private void tblVehicleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVehicleKeyReleased
        showVehicle();
    }//GEN-LAST:event_tblVehicleKeyReleased

    private void cboViewModeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboViewModeItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED)
            setViewportMode((ViewportMode)evt.getItem());
    }//GEN-LAST:event_cboViewModeItemStateChanged

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboMapTime;
    private javax.swing.JComboBox<ViewportMode> cboViewMode;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JPopupMenu mnuDff;
    private javax.swing.JMenuItem mnuDffDumper;
    private javax.swing.JMenuItem mnuDffViewer;
    private javax.swing.JCheckBoxMenuItem mnuTexture;
    private javax.swing.JPopupMenu mnuTxd;
    private javax.swing.JMenuItem mnuTxdDumper;
    private javax.swing.JMenuItem mnuTxdViewer;
    private javax.swing.JRadioButton rdoIDE;
    private javax.swing.JRadioButton rdoIPL;
    private javax.swing.ButtonGroup rdoMapDefinition;
    private javax.swing.JRadioButton rdoVehicleDamaged;
    private javax.swing.JRadioButton rdoVehicleDistance;
    private javax.swing.ButtonGroup rdoVehicleMode;
    private javax.swing.JRadioButton rdoVehicleNormal;
    private javax.swing.JSplitPane splMain;
    private javax.swing.JToolBar statusBar;
    private javax.swing.JTabbedPane tabbedControlPanel;
    private javax.swing.JTable tblDefinitionGroup;
    private javax.swing.JTable tblDefinitionItem;
    private javax.swing.JTable tblResource;
    private javax.swing.JTable tblVehicle;
    private javax.swing.JTextField txtFindResource;
    private javax.swing.JPanel viewpotArea;
    // End of variables declaration//GEN-END:variables
}
