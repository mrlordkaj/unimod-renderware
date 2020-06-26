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
import com.openitvn.gtavc.core.GtaAssetModel;
import com.openitvn.engine.renderware.RpTextureDictionary;
import com.openitvn.format.img.RwArchiveEntry;
import com.openitvn.gtavc.core.item.CARSEntry;
import com.openitvn.gtavc.core.item.OBJSEntry;
import com.openitvn.gtavc.core.item.NULLEntry;
import com.openitvn.gtavc.gui.g3d.GtaTextureManager;
import com.openitvn.gtavc.gui.g3d.ViewportApp;
import com.openitvn.gtavc.gui.g3d.ViewportMode;
import com.openitvn.gtavc.gui.pref.MainState;
import com.openitvn.maintain.Logger;
import com.openitvn.maintain.StateViewer;
import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.plugin.gta.GameConfig;
import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.RowFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Thinh Pham
 */
public class Main extends javax.swing.JFrame {
    
    private final ScriptFileModel scriptGroupModel = new ScriptFileModel();
    private final GtaAssetModel assetModel = GtaAssetModel.getInstance();
    private final ViewportApp gdxApp = ViewportApp.getInstance();
    private String currentModelFile, currentTexDicFile;
    private File currentFile; //for FileChooser
    
    private StateViewer memView;

    private static Main instance;
    public static Main getInstance() {
        if (instance == null)
            instance = new Main();
        return instance;
    }

    private Main() {
        initComponents();
        
        progressBar.setVisible(false);
        recallWindowState();
        initViewpot();
        initAssetTable();
        initItemGroupTable();
        initItemTable();
        
        //active default item groups
        for (String ide : GameConfig.getDependencies())
            scriptGroupModel.activeIPL(ide);
        
        initVehicleTable();
        
        initMemoryViewer();
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
        TableColumnModel tcm = tblResource.getColumnModel();
        tcm.getColumn(GtaAssetModel.COL_INDEX).setMinWidth(40);
        tcm.getColumn(GtaAssetModel.COL_INDEX).setMaxWidth(40);
        tcm.getColumn(GtaAssetModel.COL_TYPE).setMinWidth(40);
        tcm.getColumn(GtaAssetModel.COL_TYPE).setMaxWidth(40);
        tcm.getColumn(GtaAssetModel.COL_SIZE).setMinWidth(60);
        tcm.getColumn(GtaAssetModel.COL_SIZE).setMaxWidth(60);
        // bind data
        for (String img : GameConfig.getMainArchives()) {
            Logger.printNormal("Loading archive: " + img);
            assetModel.addArchive(img);
        }
    }

    private void initItemGroupTable() {
        //setup the table
        TableColumnModel tcm = tblDefinitionGroup.getColumnModel();
        tcm.getColumn(ScriptFileModel.COL_ACTIVE).setMinWidth(20);
        tcm.getColumn(ScriptFileModel.COL_ACTIVE).setMaxWidth(20);
        tcm.getColumn(ScriptFileModel.COL_INDEX).setMinWidth(30);
        tcm.getColumn(ScriptFileModel.COL_INDEX).setMaxWidth(30);
        tcm.getColumn(ScriptFileModel.COL_TYPE).setMinWidth(40);
        tcm.getColumn(ScriptFileModel.COL_TYPE).setMaxWidth(40);
        //bind data
        try {
            scriptGroupModel.reload();
            refineItemGroupTable();
        } catch (IOException ex) { }
    }

    private void refineItemGroupTable() {
        String filterRegex = "(^NULL$)";
        if (rdoIDE.isSelected()) {
            filterRegex = "(^IDE$)";
        } else if (rdoIPL.isSelected()) {
            filterRegex = "(^IPL$)";
        }
        
        TableRowSorter<ScriptFileModel> sorter = (TableRowSorter)tblDefinitionGroup.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(filterRegex, ScriptFileModel.COL_TYPE));
    }

    private void initItemTable() {
        TableColumnModel tcm = tblDefinitionItem.getColumnModel();
        tcm.getColumn(ScriptItemModel.COL_TYPE).setMinWidth(40);
        tcm.getColumn(ScriptItemModel.COL_TYPE).setMaxWidth(40);
        tcm.getColumn(ScriptItemModel.COL_FILE).setMinWidth(30);
        tcm.getColumn(ScriptItemModel.COL_FILE).setMaxWidth(30);
        refineItemTable();
    }

    private void refineItemTable() {
        ArrayList<RowFilter<Object, Object>> filters = new ArrayList<>(2);

        //rebuild regex of selected definition file
        String filterByFile = "(^-1$)";
        int viewRow = tblDefinitionGroup.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = tblDefinitionGroup.convertRowIndexToModel(viewRow);
            int fileId = scriptGroupModel.getEntries().get(modelRow).index;
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
        TableColumnModel tcm = tblVehicle.getColumnModel();
        tcm.getColumn(VehicleTableModel.COL_INDEX).setMinWidth(40);
        tcm.getColumn(VehicleTableModel.COL_INDEX).setMaxWidth(40);
        tcm.getColumn(VehicleTableModel.COL_TYPE).setMinWidth(50);
        tcm.getColumn(VehicleTableModel.COL_TYPE).setMaxWidth(50);
    }

    private void initMemoryViewer() {
        memView = new StateViewer();
        memView.setMemoryViewer(lblMem, null, null);
        memView.start();
    }
    
    public int getDividerLocation() {
        return splMain.getDividerLocation();
    }

    @Override
    public void dispose() {
        memView.stop();
        MainState.getInstance().saveWindowState(this);
        gdxApp.dispose();
        super.dispose();
//        System.exit(0);
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="RenderWare Tools">
    
    private void openRwDump() {
        int selId = tblResource.convertRowIndexToModel(tblResource.getSelectedRow());
        if (selId >= 0) {
            RwArchiveEntry e = assetModel.getEntry(selId);
            String type = e.getType();
            if (type.equals("TXD") || type.equals("DFF")) {
                DumperDialog dlg = DumperDialog.getInstance();
                dlg.setVisible(true);
                dlg.openFile(e.getName(), e.getData());
            }
        }
    }
    
    public void setRwTextureVisible(boolean isVisible) {
        btnRwTexture.setSelected(isVisible);
    }
    
    private void openRwTexture() {
        try {
            int selId = tblResource.convertRowIndexToModel(tblResource.getSelectedRow());
            if (selId >= 0) {
                RwArchiveEntry e = assetModel.getEntry(selId);
                if (e.getType().equals("TXD")) {
                    TextureDialog dlg = TextureDialog.getInstance();
                    dlg.setVisible(true);
                    dlg.openFile(e);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    private void openRwModel() {
        try {
            int selId = tblResource.convertRowIndexToModel(tblResource.getSelectedRow());
            if (selId >= 0) {
                RwArchiveEntry e = assetModel.getEntry(selId);
                if (e.getType().equals("DFF")) {
                    String fileName = e.getName();
                    currentModelFile = fileName;
                    // find match name dff and txd for texture dictionary
                    String modName = fileName.substring(0, fileName.length() - 4);
                    String txdName = findTexDic(modName);
                    RpTextureDictionary texDic = GtaTextureManager.getTexDic(txdName);
                    if (texDic != null) {
                        currentTexDicFile = txdName + ".txd";
                        if (btnRwTexture.isSelected())
                            TextureDialog.getInstance().openFile(currentTexDicFile, texDic);
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
        for (NULLEntry def : scriptGroupModel.getDefinitionItemModel().getEntries()) {
            switch (def.getType()){
                case OBJS:
                case TOBJ:
                    OBJSEntry objs = (OBJSEntry)def;
                    if (modName.equals(objs.modName))
                        return objs.txdName;
            }
        }
        return modName;
    }
    
    private void showVehicle() {
        try {
            ViewportMode viewMode = ViewportMode.VehicleNormal;
            if(rdoVehicleDamaged.isSelected()) viewMode = ViewportMode.VehicleDamaged;
            else if(rdoVehicleDistance.isSelected()) viewMode = ViewportMode.VehicleDistance;
            
            int selectedRow = tblVehicle.getSelectedRow();
            if(selectedRow >= 0) {
                int vehicleEntryId = tblVehicle.convertRowIndexToModel(selectedRow);
                VehicleTableModel data = (VehicleTableModel)tblVehicle.getModel();
                CARSEntry entry = data.get(vehicleEntryId);
                if(entry != null) gdxApp.setVehicle(entry, viewMode);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    //</editor-fold>
    
    public JProgressBar getProgressBar() {
        return progressBar;
    }
    
    public boolean checkBusy() {
        if (progressBar.isVisible()) {
            JOptionPane.showMessageDialog(this, "I'm busy now, please be patient...", "In process...", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mnuTxd = new javax.swing.JPopupMenu();
        mnuTxdViewer = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuTxdDumper = new javax.swing.JMenuItem();
        mnuTxdExtractor = new javax.swing.JMenuItem();
        mnuDff = new javax.swing.JPopupMenu();
        mnuDffViewer = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuDffDumper = new javax.swing.JMenuItem();
        mnuDffExtractor = new javax.swing.JMenuItem();
        mnuFile = new javax.swing.JPopupMenu();
        mnuFileExtractor = new javax.swing.JMenuItem();
        rdoMapDefinition = new javax.swing.ButtonGroup();
        rdoVehicleMode = new javax.swing.ButtonGroup();
        toolBar = new javax.swing.JToolBar();
        btnRwTexture = new javax.swing.JToggleButton();
        btnVehicleDetail = new javax.swing.JToggleButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnExportScene = new javax.swing.JButton();
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
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cboMapTime = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cboViewMode = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        rdoVehicleNormal = new javax.swing.JRadioButton();
        rdoVehicleDamaged = new javax.swing.JRadioButton();
        rdoVehicleDistance = new javax.swing.JRadioButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblVehicle = new javax.swing.JTable();
        viewpotArea = new javax.swing.JPanel();
        statusBar = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        lblMem = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        lblInfo = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        progressBar = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

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

        mnuTxdExtractor.setText("Resource Extractor...");
        mnuTxdExtractor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onExtractResource(evt);
            }
        });
        mnuTxd.add(mnuTxdExtractor);

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

        mnuDffExtractor.setText("Resource Extractor...");
        mnuDffExtractor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onExtractResource(evt);
            }
        });
        mnuDff.add(mnuDffExtractor);

        mnuFileExtractor.setText("Resource Extractor...");
        mnuFileExtractor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onExtractResource(evt);
            }
        });
        mnuFile.add(mnuFileExtractor);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("GTA Viewer");
        setMinimumSize(new java.awt.Dimension(960, 600));

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        btnRwTexture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon32/application_view_gallery.png"))); // NOI18N
        btnRwTexture.setToolTipText("RenderWare Texture Viewer");
        btnRwTexture.setFocusable(false);
        btnRwTexture.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRwTexture.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRwTexture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRwTextureActionPerformed(evt);
            }
        });
        toolBar.add(btnRwTexture);

        btnVehicleDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon32/lorry.png"))); // NOI18N
        btnVehicleDetail.setToolTipText("Vehicle Details Viewer");
        btnVehicleDetail.setFocusable(false);
        btnVehicleDetail.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVehicleDetail.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnVehicleDetail);
        toolBar.add(jSeparator5);

        btnExportScene.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon32/camera_go.png"))); // NOI18N
        btnExportScene.setMnemonic('E');
        btnExportScene.setToolTipText("Export Scene");
        btnExportScene.setFocusable(false);
        btnExportScene.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportScene.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportScene.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportSceneActionPerformed(evt);
            }
        });
        toolBar.add(btnExportScene);

        splMain.setBorder(null);

        tabbedControlPanel.setMinimumSize(new java.awt.Dimension(320, 0));
        tabbedControlPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                onSwitchControlPanel(evt);
            }
        });

        tblResource.setAutoCreateRowSorter(true);
        tblResource.setModel(assetModel);
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
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

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("View Mode"));

        jLabel3.setLabelFor(cboMapTime);
        jLabel3.setText("Time:");

        cboMapTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00" }));
        cboMapTime.setSelectedIndex(12);
        cboMapTime.setEnabled(false);

        jLabel5.setLabelFor(cboViewMode);
        jLabel5.setText("Map Mode:");

        cboViewMode.setModel(new DefaultComboBoxModel(new ViewportMode[] {
            ViewportMode.MapNormal,
            ViewportMode.MapDistance,
            ViewportMode.MapCollision
        })
    );
    cboViewMode.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            cboViewModeItemStateChanged(evt);
        }
    });

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel5)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(cboViewMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(jLabel3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(cboMapTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel3)
                .addComponent(cboMapTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel5)
                .addComponent(cboViewMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

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
                    .addGap(0, 0, Short.MAX_VALUE))
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );

    tabbedControlPanel.addTab("Map", new javax.swing.ImageIcon(getClass().getResource("/icon16/map.png")), jPanel2); // NOI18N

    jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("View Mode"));

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

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(rdoVehicleNormal)
            .addGap(18, 18, 18)
            .addComponent(rdoVehicleDamaged)
            .addGap(18, 18, 18)
            .addComponent(rdoVehicleDistance)
            .addContainerGap(44, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(rdoVehicleNormal)
                .addComponent(rdoVehicleDamaged)
                .addComponent(rdoVehicleDistance))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

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

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
    );
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );

    tabbedControlPanel.addTab("Vehicle", new javax.swing.ImageIcon(getClass().getResource("/icon16/lorry.png")), jPanel3); // NOI18N

    tabbedControlPanel.setSelectedIndex(1);

    splMain.setLeftComponent(tabbedControlPanel);

    viewpotArea.setLayout(new java.awt.BorderLayout());
    splMain.setRightComponent(viewpotArea);

    statusBar.setFloatable(false);
    statusBar.setRollover(true);

    jLabel1.setText("Memory: ");
    statusBar.add(jLabel1);

    lblMem.setText("<memory>");
    lblMem.setMaximumSize(new java.awt.Dimension(100, 14));
    lblMem.setMinimumSize(new java.awt.Dimension(100, 14));
    lblMem.setPreferredSize(new java.awt.Dimension(100, 14));
    statusBar.add(lblMem);
    statusBar.add(jSeparator1);

    lblInfo.setText("<none>");
    lblInfo.setMaximumSize(new java.awt.Dimension(360, 14));
    lblInfo.setMinimumSize(new java.awt.Dimension(360, 14));
    lblInfo.setPreferredSize(new java.awt.Dimension(360, 14));
    statusBar.add(lblInfo);
    statusBar.add(jSeparator4);

    progressBar.setString("<current process>");
    progressBar.setStringPainted(true);
    statusBar.add(progressBar);

    jMenu1.setText("File");
    jMenuBar1.add(jMenu1);

    jMenu2.setText("Edit");
    jMenuBar1.add(jMenu2);

    setJMenuBar(jMenuBar1);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(splMain)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(splMain)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onResourceEntryClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onResourceEntryClicked
        if(evt.getButton() == MouseEvent.BUTTON3) return; //cancel right mouse for popup trigger
        boolean isDoubleClick = (evt.getClickCount() == 2);
        if(btnRwTexture.isSelected() || isDoubleClick) openRwTexture();
        if(gdxApp.getViewpotMode() == ViewportMode.SingleModel || isDoubleClick) openRwModel();
    }//GEN-LAST:event_onResourceEntryClicked

    private void refineDefinitionItemKey(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_refineDefinitionItemKey
        refineItemTable();
    }//GEN-LAST:event_refineDefinitionItemKey

    private void refineDefinitionItemMouse(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refineDefinitionItemMouse
        refineItemTable();
    }//GEN-LAST:event_refineDefinitionItemMouse

    private void txtFindResourceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFindResourceKeyTyped
        String textSearch = txtFindResource.getText();
        textSearch = String.format("(?i)(%1$s)", textSearch);
        TableRowSorter<ScriptItemModel> sorter = (TableRowSorter) tblResource.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(textSearch, GtaAssetModel.COL_NAME));
    }//GEN-LAST:event_txtFindResourceKeyTyped

    private void onTableResourcePopupTrigger(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onTableResourcePopupTrigger
        int x = evt.getX();
        int y = evt.getY();
        if (evt.getButton() == MouseEvent.BUTTON3) {
            int selRow = tblResource.rowAtPoint(new Point(x, y));
            tblResource.setRowSelectionInterval(selRow, selRow);
        }
        int selId = tblResource.convertRowIndexToModel(tblResource.getSelectedRow());
        if (evt.isPopupTrigger() && selId > -1) {
            RwArchiveEntry entry = assetModel.getEntry(selId);
            switch (entry.getType()) {
                case "TXD":
                    mnuTxd.show(evt.getComponent(), x, y);
                    break;
                    
                case "DFF":
                    mnuDff.show(evt.getComponent(), x, y);
                    break;
                    
                default:
                    mnuFile.show(evt.getComponent(), x, y);
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
        if (btnRwTexture.isSelected()) openRwTexture();
        if (gdxApp.getViewpotMode() == ViewportMode.SingleModel) openRwModel();
    }//GEN-LAST:event_tblResourceKeyReleased

    private void mnuDffViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDffViewerActionPerformed
        openRwModel();
    }//GEN-LAST:event_mnuDffViewerActionPerformed

    private void btnRwTextureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRwTextureActionPerformed
        if(btnRwTexture.isSelected()) {
            TextureDialog.getInstance().setVisible(true);
        } else {
            TextureDialog.getInstance().dispose();
        }
    }//GEN-LAST:event_btnRwTextureActionPerformed

    private void tblDefinitionItemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDefinitionItemMouseClicked
        int id = tblDefinitionItem.convertRowIndexToModel(tblDefinitionItem.getSelectedRow());
        NULLEntry e = scriptGroupModel.getDefinitionItemModel().getEntry(id);
        switch (e.getType()) {
            case INST:
                gdxApp.select(e);
                break;
        }
    }//GEN-LAST:event_tblDefinitionItemMouseClicked

    private void btnExportSceneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportSceneActionPerformed
        if (!checkBusy()) {
            UCFileChooser fcSave = new UCFileChooser(currentFile);
            fcSave.setAcceptAllFileFilterUsed(false);
            fcSave.addChoosableFileFilter(new FileNameExtensionFilter("Autodesk Filmbox (fbx)", "fbx"));
            if (fcSave.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                currentFile = fcSave.getSelectedFile();
                String outFileUri = currentFile.getPath();
                if (!outFileUri.toLowerCase().endsWith(".fbx"))
                    currentFile = new File(outFileUri.concat(".fbx"));
                new ExportDialog(currentFile).setVisible(true);
            }
        }
    }//GEN-LAST:event_btnExportSceneActionPerformed

    private void onRefineDefinitionGroup(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRefineDefinitionGroup
        refineItemGroupTable();
    }//GEN-LAST:event_onRefineDefinitionGroup

    private void onExtractResource(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onExtractResource
        int selId = tblResource.convertRowIndexToModel(tblResource.getSelectedRow());
        if (selId >= 0) {
            RwArchiveEntry entry = assetModel.getEntry(selId);
            UCFileChooser fcSave = new UCFileChooser(currentFile);
            fcSave.setSelectedFile(new File(entry.getName()));
            if (fcSave.showSaveDialog(this) == UCFileChooser.APPROVE_OPTION) {
                File out = fcSave.getSelectedFile();
                try (FileOutputStream fos = new FileOutputStream(out)) {
                    byte[] bb = assetModel.getEntry(selId).getData();
                    fos.write(bb);
                    currentFile = out;
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_onExtractResource

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportScene;
    private javax.swing.JToggleButton btnRwTexture;
    private javax.swing.JToggleButton btnVehicleDetail;
    private javax.swing.JComboBox<String> cboMapTime;
    private javax.swing.JComboBox<ViewportMode> cboViewMode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblMem;
    private javax.swing.JPopupMenu mnuDff;
    private javax.swing.JMenuItem mnuDffDumper;
    private javax.swing.JMenuItem mnuDffExtractor;
    private javax.swing.JMenuItem mnuDffViewer;
    private javax.swing.JPopupMenu mnuFile;
    private javax.swing.JMenuItem mnuFileExtractor;
    private javax.swing.JPopupMenu mnuTxd;
    private javax.swing.JMenuItem mnuTxdDumper;
    private javax.swing.JMenuItem mnuTxdExtractor;
    private javax.swing.JMenuItem mnuTxdViewer;
    private javax.swing.JProgressBar progressBar;
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
    private javax.swing.JToolBar toolBar;
    private javax.swing.JTextField txtFindResource;
    private javax.swing.JPanel viewpotArea;
    // End of variables declaration//GEN-END:variables
}
