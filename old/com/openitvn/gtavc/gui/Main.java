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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
    static {
        String arch = System.getProperty("os.arch");
        if (arch.endsWith("64")) {
            System.loadLibrary("lwjgl64");
            System.loadLibrary("gdx64");
        } else if (arch.endsWith("86")) {
            System.loadLibrary("lwjgl");
            System.loadLibrary("gdx");
        }
    }
    
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
        initResourceTable();
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

    private void initResourceTable() {
        TableColumnModel tcm = resourceTable.getColumnModel();
        tcm.getColumn(ResourceModel.COL_INDEX).setMinWidth(40);
        tcm.getColumn(ResourceModel.COL_INDEX).setMaxWidth(40);
        tcm.getColumn(ResourceModel.COL_SIZE).setMinWidth(60);
        tcm.getColumn(ResourceModel.COL_SIZE).setMaxWidth(60);
        resourceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                if (texer != null) {
                    tryOpenTexture();
                }
                tryOpenModel();
            }
        });
    }

    private void initWorldScriptTable() {
        TableColumnModel tcm = worldScriptTable.getColumnModel();
        tcm.getColumn(WorldScriptModel.COL_ACTIVE).setMinWidth(20);
        tcm.getColumn(WorldScriptModel.COL_ACTIVE).setMaxWidth(20);
        tcm.getColumn(WorldScriptModel.COL_TYPE).setMinWidth(40);
        tcm.getColumn(WorldScriptModel.COL_TYPE).setMaxWidth(40);
        // Bind data
        try {
            worldScriptModel.reload();
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
                    WorldScript script = worldScriptModel.resource.scripts.get(row);
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
        // TODO: Prevent hanging on exit
        requestFocus();
        viewpotArea.removeAll();
        // Save window state
        MainState.getInstance().saveWindowState(this);
        // Dispose components
        if (texer != null) {
            texer.dispose();
        }
        super.dispose();
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Texture Viewer">
    
    RwTexer texer;
    
    private void openTexerDialog() {
        if (texer != null) return;
        texer = new RwTexer();
        texer.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                mnuTexer.setSelected(true);
            }
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                mnuTexer.setSelected(false);
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        texer.setVisible(true);
    }
    
    private void closeTexerDialog() {
        if (texer != null) {
            texer.dispose();
            texer = null;
        }
    }
    
    private void tryOpenTexture() {
        IArchiveEntry e = getEntryFromResourceTable();
        if (e != null && "txd".equalsIgnoreCase(e.getExt())) {
            openTexerDialog();
            texer.openEntry(e);
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Section Dumper">
    
    RwDumper dumper;
    
    private void openDumperDialog() {
        if (dumper != null) return;
        dumper = new RwDumper();
        dumper.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                mnuDumper.setSelected(true);
            }
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                mnuDumper.setSelected(false);
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        dumper.setVisible(true);
    }
    
    private void closeDumperDialog() {
        if (dumper != null) {
            dumper.dispose();
            dumper = null;
        }
    }
    
    private void tryOpenSection() {
        IArchiveEntry e = getEntryFromResourceTable();
        if (e == null) return;
        switch (e.getExt().toLowerCase()) {
            case "txd":
            case "dff":
                openDumperDialog();
                dumper.openEntry(e);
                break;
        }
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="RenderWare Tools">
    
    private IArchiveEntry getEntryFromResourceTable() {
        int row = resourceTable.getSelectedRow();
        if (row >= 0) {
            row = resourceTable.convertRowIndexToModel(row);
            return resourceModel.entries.get(row);
        }
        return null;
    }
    
    private void tryOpenModel() {
        IArchiveEntry e = getEntryFromResourceTable();
        if (e == null || !"dff".equalsIgnoreCase(e.getExt())) {
            return;
        }
        try {
            dffFileName = e.getName();
            // Find match name DFF and TXD for texture dictionary
            String modName = dffFileName.substring(0, dffFileName.length() - 4);
            String txdName = resourceModel.findTexDic(modName);
            gdxApp.modelView.openModel(modName, txdName);
            RpTextureDictionary texDic = gdxApp.modelView.getTexDic(txdName);
            if (texDic != null) {
                txdFileName = txdName + ".txd";
                if (texer != null) {
                    texer.loadTexDic(txdFileName, texDic);
                }
            } else {
                txdFileName = null;
            }
            updateInfoLabel();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
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
        mnuDff = new javax.swing.JPopupMenu();
        mnuDffViewer = new javax.swing.JMenuItem();
        rdoMapDefinition = new javax.swing.ButtonGroup();
        mnuDumperPopup = new javax.swing.JMenuItem();
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
        javax.swing.JToolBar statusBar = new javax.swing.JToolBar();
        lblInfo = new javax.swing.JLabel();
        javax.swing.JMenuBar jMenuBar1 = new javax.swing.JMenuBar();
        javax.swing.JMenu jMenu1 = new javax.swing.JMenu();
        mnuWorkspace = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuExit = new javax.swing.JMenuItem();
        javax.swing.JMenu jMenu2 = new javax.swing.JMenu();
        mnuTexer = new javax.swing.JCheckBoxMenuItem();
        mnuDumper = new javax.swing.JCheckBoxMenuItem();

        mnuTxdViewer.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        mnuTxdViewer.setText("Texture Viewer");
        mnuTxdViewer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTxdViewerActionPerformed(evt);
            }
        });
        mnuTxd.add(mnuTxdViewer);

        mnuDffViewer.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        mnuDffViewer.setText("Model Viewer");
        mnuDffViewer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDffViewerActionPerformed(evt);
            }
        });
        mnuDff.add(mnuDffViewer);

        mnuDumperPopup.setText("Section Dumper");
        mnuDumperPopup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDumperPopupActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                resourceTableMouseReleased(evt);
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

    tabbedControlPanel.addTab("World", new javax.swing.ImageIcon(getClass().getResource("/icon16/map.png")), jPanel2); // NOI18N

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

    mnuTexer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_DOWN_MASK));
    mnuTexer.setText("Texture Viewer");
    mnuTexer.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            mnuTexerItemStateChanged(evt);
        }
    });
    jMenu2.add(mnuTexer);

    mnuDumper.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_DOWN_MASK));
    mnuDumper.setText("Section Dumper");
    mnuDumper.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            mnuDumperItemStateChanged(evt);
        }
    });
    jMenu2.add(mnuDumper);

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

    private void txtFindResourceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFindResourceKeyTyped
        String regex = txtFindResource.getText();
        regex = String.format("(?i)(%1$s)", regex);
        TableRowSorter<WorldItemModel> sorter = (TableRowSorter) resourceTable.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(regex, ResourceModel.COL_NAME));
    }//GEN-LAST:event_txtFindResourceKeyTyped

    private void mnuTxdViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTxdViewerActionPerformed
        tryOpenTexture();
    }//GEN-LAST:event_mnuTxdViewerActionPerformed

    private void mnuDumperPopupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDumperPopupActionPerformed
        tryOpenSection();
    }//GEN-LAST:event_mnuDumperPopupActionPerformed

    private void mnuDffViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDffViewerActionPerformed
        tryOpenModel();
    }//GEN-LAST:event_mnuDffViewerActionPerformed

    private void worldItemTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_worldItemTableMouseClicked
        int row = worldItemTable.getSelectedRow();
        if (row >= 0) {
            row = worldItemTable.convertRowIndexToModel(row);
            ItemNULL e = worldItemModel.entries.get(row);
            switch (e.getType()) {
                case "INST":
                    ItemINST inst = (ItemINST)e;
                    gdxApp.worldView.moveCameraTo(inst.posX, inst.posZ, -inst.posY);
                    break;
            }
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

    private void mnuTexerItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mnuTexerItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            openTexerDialog();
        }
        else {
            closeTexerDialog();
        }
    }//GEN-LAST:event_mnuTexerItemStateChanged

    private void cboTimeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboTimeItemStateChanged
        gdxApp.worldView.setTime(cboTime.getSelectedIndex());
    }//GEN-LAST:event_cboTimeItemStateChanged

    private void mnuWorkspaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuWorkspaceActionPerformed
        Startup dlg = new Startup(this, true);
        dlg.setVisible(true);
    }//GEN-LAST:event_mnuWorkspaceActionPerformed

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        dispose();
    }//GEN-LAST:event_mnuExitActionPerformed

    private void mnuDumperItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mnuDumperItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            openDumperDialog();
        }
        else {
            closeDumperDialog();
        }
    }//GEN-LAST:event_mnuDumperItemStateChanged

    private void resourceTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resourceTableMouseReleased
        if (evt.isPopupTrigger()) {
            int x = evt.getX();
            int y = evt.getY();
            // Select row at mouse location
            int row = resourceTable.rowAtPoint(new Point(x, y));
            if (row >= 0) {
                resourceTable.setRowSelectionInterval(row, row);
                row = resourceTable.convertRowIndexToModel(row);
                // Show popup menu
                IArchiveEntry e = resourceModel.entries.get(row);
                switch (e.getExt().toLowerCase()) {
                    case "txd":
                        mnuTxd.add(mnuDumperPopup);
                        mnuTxd.show(evt.getComponent(), x, y);
                        break;
                    case "dff":
                        mnuDff.add(mnuDumperPopup);
                        mnuDff.show(evt.getComponent(), x, y);
                        break;
                }
            }
        }
        else if (texer == null && (evt.getClickCount() == 2)) {
            // Open Texer on double click
            tryOpenTexture();
        }
    }//GEN-LAST:event_resourceTableMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<ViewportMode> cboDistance;
    private javax.swing.JComboBox<String> cboTime;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JPopupMenu mnuDff;
    private javax.swing.JMenuItem mnuDffViewer;
    private javax.swing.JCheckBoxMenuItem mnuDumper;
    private javax.swing.JMenuItem mnuDumperPopup;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JCheckBoxMenuItem mnuTexer;
    private javax.swing.JPopupMenu mnuTxd;
    private javax.swing.JMenuItem mnuTxdViewer;
    private javax.swing.JMenuItem mnuWorkspace;
    private javax.swing.JRadioButton rdoIDE;
    private javax.swing.JRadioButton rdoIPL;
    private javax.swing.ButtonGroup rdoMapDefinition;
    private javax.swing.JTable resourceTable;
    private javax.swing.JSplitPane splMain;
    private javax.swing.JTabbedPane tabbedControlPanel;
    private javax.swing.JTextField txtFindResource;
    private javax.swing.JPanel viewpotArea;
    private javax.swing.JTable worldItemTable;
    private javax.swing.JTable worldScriptTable;
    // End of variables declaration//GEN-END:variables
}
