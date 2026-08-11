/*
 * Copyright (C) 2017 Thinh Pham
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
package com.openitvn.unicore.plugin.gta;

import com.badlogic.gdx.math.Matrix4;
import com.openitvn.control.table.UCBooleanCellRenderer;
import com.openitvn.format.col.ColFile;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.Workspace;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.plugin.PanelViewer;
import com.openitvn.unicore.plugin.gta.item.ItemINST;
import com.openitvn.unicore.plugin.gta.item.ItemPATHSegment;
import com.openitvn.unicore.world.IGeometry;
import com.openitvn.unicore.world.INode;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.JFileChooser;
import javax.swing.RowFilter;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Thinh Pham
 */
public final class WorldPanel extends PanelViewer {
    
    private final WorldScriptModel scriptModel = new WorldScriptModel();
    
    private final HashMap<String, ItemPATHSegment> pathMap = new HashMap<>(); // inst find
    
    private GWorld ideWorld;
    private final GWorld bigWorld = new GWorld("GTA World");
//    private final Camera camera;
//    private final Vector3 prevPos = new Vector3();
//    private final Vector3 prevDir = new Vector3();
//    private final Timer updateTimer;
    
    public WorldPanel() {
        initComponents();
        // setup the table
        TableColumnModel tcm = tblMap.getColumnModel();
        tcm.getColumn(WorldScriptModel.COL_ACTIVE).setMinWidth(20);
        tcm.getColumn(WorldScriptModel.COL_ACTIVE).setMaxWidth(20);
        tcm.getColumn(WorldScriptModel.COL_TYPE).setMinWidth(40);
        tcm.getColumn(WorldScriptModel.COL_TYPE).setMaxWidth(40);
        refineWorldTable(null);
        tblMap.setDefaultRenderer(Boolean.class, new UCBooleanCellRenderer());
        // register worlds
        Unicore.registerWorld(bigWorld);
//        camera = Launcher.getWorldProcessor().getActiveCamera();
//        updateTimer = new Timer("GTA World Updater");
//        updateTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if (camera.pos.dst2(prevPos) > 0.01f || camera.direction.dst2(prevDir) > 0.01f) {
//                    System.out.println("need update");
//                    prevPos.set(camera.pos);
//                    prevDir.set(camera.direction);
//                }
//            }
//        }, 0, 400);
    }
    
    @Override
    public boolean requestClose() {
//        updateTimer.cancel();
        pendingNodes = null;
        Unicore.unregisterWorld(ideWorld);
        Unicore.unregisterWorld(bigWorld);
        return true;
    }
    
    @Override
    public void workspaceChanged(Workspace space) {
        GameConfig.setWorkspace(space);
        scriptModel.bind(this, ResourceModel.getInstance());
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupType = new javax.swing.ButtonGroup();
        javax.swing.JRadioButton rdoIDE = new javax.swing.JRadioButton();
        javax.swing.JRadioButton rdoIPL = new javax.swing.JRadioButton();
        javax.swing.JRadioButton rdoAll = new javax.swing.JRadioButton();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        tblMap = new javax.swing.JTable() {
            public String getToolTipText(MouseEvent evt) {
                int row = tblMap.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    row = tblMap.convertRowIndexToModel(row);
                    com.openitvn.unicore.plugin.gta.WorldScript e = scriptModel.getScript(row);
                    return e.path;
                }
                return null;
            }
        };
        btnExportPath = new javax.swing.JButton();
        btnOptimizePath = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setName("World"); // NOI18N

        groupType.add(rdoIDE);
        rdoIDE.setText("IDE");
        rdoIDE.setToolTipText("Item Definition");
        rdoIDE.setActionCommand("^IDE$");
        rdoIDE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refineWorldTable(evt);
            }
        });

        groupType.add(rdoIPL);
        rdoIPL.setSelected(true);
        rdoIPL.setText("IPL");
        rdoIPL.setToolTipText("Item Placement");
        rdoIPL.setActionCommand("^IPL$");
        rdoIPL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refineWorldTable(evt);
            }
        });

        groupType.add(rdoAll);
        rdoAll.setText("ALL");
        rdoAll.setToolTipText("All Definitions");
        rdoAll.setActionCommand("");
        rdoAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refineWorldTable(evt);
            }
        });

        tblMap.setAutoCreateRowSorter(true);
        tblMap.setModel(scriptModel);
        tblMap.setRowHeight(20);
        tblMap.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblMap.setShowHorizontalLines(false);
        tblMap.setShowVerticalLines(false);
        tblMap.getTableHeader().setReorderingAllowed(false);
        tblMap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMapMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblMap);

        btnExportPath.setText("Export Paths");
        btnExportPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportPathActionPerformed(evt);
            }
        });

        btnOptimizePath.setText("Optimize Paths");
        btnOptimizePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOptimizePathActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rdoIPL)
                        .addGap(18, 18, 18)
                        .addComponent(rdoIDE)
                        .addGap(18, 18, 18)
                        .addComponent(rdoAll))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnOptimizePath)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExportPath)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {rdoIDE, rdoIPL});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoIPL)
                    .addComponent(rdoIDE)
                    .addComponent(rdoAll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExportPath)
                    .addComponent(btnOptimizePath)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refineWorldTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refineWorldTable
        String regex = (evt == null) ? "^IPL$" : evt.getActionCommand();
        TableRowSorter sorter = (TableRowSorter) tblMap.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(regex, WorldScriptModel.COL_TYPE));
    }//GEN-LAST:event_refineWorldTable

    private void tblMapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMapMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() >= 2) {
            evt.consume();
            int i = tblMap.convertRowIndexToModel(tblMap.getSelectedRow());
            WorldScript script = scriptModel.getScript(i);
            if (script.type == WorldScript.Type.IDE) {
                // destroy previously world
                Unicore.unregisterWorld(ideWorld);
                // build new world
                ideWorld = new GWorld(script.name);
                ideWorld.setLayerVisible(GWorld.LAYER_COLLISION, true);
                Unicore.registerWorld(ideWorld);
                // read objects from script
                try (InputStream is = new FileInputStream(script.file);
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr)) {
                    String line;
                    while ((line = ScriptHelper.readLine(br)) != null) {
                        switch (line) {
                            case "objs":
                            case "tobj":
                                ideWorld.executeOBJSGroup(script.name, br, true);
                                break;
                        }
                    }
                } catch (IOException ex) {
//                    Logger.printError("%1$s failed: %2$s [%3$s]", script.type, name, state);
                }
                // add geometries to the world
                for (Entry<Integer, Integer> layerEntry : ideWorld.modelLayerMap.entrySet()) {
                    int modId = layerEntry.getKey();
                    int layer = layerEntry.getValue();
                    String modName = ideWorld.getModNameById(modId);
                    if (modName != null) {
                        // add model
                        IGeometry geo = new IGeometry("SM_"+modName);
                        geo.setLayerIndex(layer);
                        geo.attach(ideWorld);
                        geo.construct();
                        geo.update(true);
                        // add collision
                        ColFile col = ideWorld.collisionMap.get(modName);
                        if (col != null) {
                            geo = new IGeometry(col.model.getName());
                            geo.setLayerIndex(GWorld.LAYER_COLLISION);
                            geo.attach(ideWorld);
                            geo.construct();
                            geo.update(true);
                        }
                    } else {
                        Logger.printWarning("Model not found: %d", modId);
                    }
                }
                Unicore.focusToWorld(ideWorld);
            }
        }
    }//GEN-LAST:event_tblMapMouseClicked

    private void btnExportPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportPathActionPerformed
        exportPathData3();
    }//GEN-LAST:event_btnExportPathActionPerformed

    private void btnOptimizePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOptimizePathActionPerformed
        optimizePathData3();
    }//GEN-LAST:event_btnOptimizePathActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportPath;
    private javax.swing.JButton btnOptimizePath;
    private javax.swing.ButtonGroup groupType;
    private javax.swing.JTable tblMap;
    // End of variables declaration//GEN-END:variables
    
    private void addINST(INode group, ItemINST inst) {
        Matrix4 transform = new Matrix4().translate(inst.posX, inst.posY, inst.posZ)
                    .rotateRad(inst.rotX, inst.rotY, inst.rotZ, -2*(float)Math.acos(inst.rotW))
                    .scale(inst.sclX, inst.sclY, inst.sclZ);
        // add model
        String modName = bigWorld.getModNameById(inst.modId);
        if (modName != null) {
            IGeometry geo = new IGeometry("SM_"+modName);
            geo.transform.localMatrix.set(transform);
            geo.setLayerIndex(bigWorld.modelLayerMap.get(inst.modId));
            geo.attach(group);
        } else {
            Logger.printWarning("Model not found: %s (%d)", inst.modName, inst.modId);
        }
        // add collision
        ColFile col = bigWorld.collisionMap.get(modName);
        if (col != null) {
            IGeometry geo = new IGeometry(col.model.getName());
            geo.transform.localMatrix.set(transform);
            geo.setLayerIndex(GWorld.LAYER_COLLISION);
            geo.attach(group);
        }
        // add paths (only GTA III for now)
        ItemPATHSegment path = pathMap.get(inst.modName);
        if (path != null) {
            PathSegment seg = new PathSegment(path);
            seg.transform.localMatrix.set(transform);
            seg.setLayerIndex(GWorld.LAYER_CAR_PATH);
            seg.attach(group);
        }
    }
      
    void executeOBJSGroup(String groupName, BufferedReader br, boolean bActive) {
        bigWorld.executeOBJSGroup(groupName, br, bActive);
    }
    
    /**
     * Parse text PATH, for III and VC.
     */
    void executePATHGroup(String groupName, BufferedReader br, boolean bActive) throws IOException {
        if (bActive) {
            String[] args;
            while ((args = ScriptHelper.parseLineByComma(br)) != null) {
                int segmentType = args[0].equals("car") ? ItemPATHSegment.TYPE_CAR : ItemPATHSegment.TYPE_PED;
                int modId = Integer.parseInt(args[1]);
                String modName = args[2];
                if (segmentType == ItemPATHSegment.TYPE_CAR) {
                    ItemPATHSegment entry = new ItemPATHSegment(segmentType, modId, modName, br);
                    pathMap.put(modName, entry);
                } else {
                    // skip ped
                    for (int i = 0; i < 12; i++) {
                        br.readLine();
                    }
                }
            }
        } else {
            
        }
    }
    
    /**
     * Parse text INST, for III and VC.
     */
    void executeINSTGroup(String groupName, BufferedReader br, boolean active) throws IOException {
        if (active) {
            INode group = new INode(groupName);
            String[] args;
            while ((args = ScriptHelper.parseLineByComma(br)) != null) {
                ItemINST inst = new ItemINST(args);
                addINST(group, inst);
            }
            pendingNodes.add(group);
        } else {
            bigWorld.deleteNode(groupName);
            Unicore.focusToWorld(bigWorld);
        }
    }
    
    /**
     * Parse binary INST, only for SA.
     */
    void executeINSTGroup(String groupName, DataStream ds, boolean active) {
        if (active) {
            INode group = new INode(groupName);
            ds.position(4); // skip "bnry"
            int instCount = ds.getInt();
            ds.position(0x4c); // offset of INST, 0x4C by default
            for (int j = 0; j < instCount; j++) {
                addINST(group, new ItemINST(ds));
            }
            pendingNodes.add(group);
        } else {
            bigWorld.deleteNode(groupName);
            Unicore.focusToWorld(bigWorld);
        }
    }
    
    // world dispatcher for schedule push world data
    // when asynchronous loading thread done
    private ArrayList<INode> pendingNodes;
    
    void prepareDispatcher() {
        tblMap.setEnabled(false);
        pendingNodes = new ArrayList<>();
    }
    
    void executeDispatcher() {
        for (INode node : pendingNodes) {
            node.attach(bigWorld);
            node.construct();
            node.update(true);
            ArrayList<PathSegment> segments = new ArrayList<>();
            node.getChildrenByClass(PathSegment.class, segments);
            for (PathSegment seg : segments) {
                seg.compileData();
                seg.rebuildModel();
            }
        }
        pendingNodes = null;
        tblMap.setEnabled(true);
        Unicore.focusToWorld(bigWorld);
    }
    
    private void optimizePathData3() {
        ArrayList<INode> groups = new ArrayList<>();
        bigWorld.getChildrenByClass(INode.class, groups, false);
        ArrayList<PathNode> allPorts = new ArrayList<>();
        // STEP: merge all possible segments
        for (INode group : groups) {
            ArrayList<PathNode> straightPorts = new ArrayList<>();
            ArrayList<PathSegment> segments = new ArrayList<>();
            group.getChildrenByClass(PathSegment.class, segments);
            for (PathSegment seg : segments) {
                ArrayList<PathNode> segPorts = seg.getPorts();
                if (!seg.isCross()) {
                    straightPorts.addAll(segPorts);
                }
                // collects all ports for fix step
                allPorts.addAll(segPorts);
            }
            mainLoop:
            while (!straightPorts.isEmpty()) {
                PathNode a = straightPorts.remove(0);
                for (PathNode b : straightPorts) {
                    if (a.tryMerge(b)) {
                        straightPorts.remove(b);
                        continue mainLoop;
                    }
                }
            }
        }
        
        // STEP: cross with opposite fix
        for (PathNode a : allPorts) {
            String segName = a.segment.getName().substring(4);
            switch (segName) {
                case "rd_CrossRoads11":
                case "rd_CrossRoads12":
                case "rd_TJunction12":
                case "com_cust_roads45":
                case "com_cust_roads44":
                case "rd_TJunction11way":
                case "rd_TJunction11":
                case "CrossRoadn1":
                case "ind_customroad088":
                case "ind_customroad0bb":
                case "indmaincj1way":
                case "cussRoads5":
                    boolean oppositeFix = false;
                    for (PathNode b : allPorts) {
                        if (!b.segment.isCross() && a.position.dst(b.position) < 0.4f) {
                            oppositeFix = tryFixLane(a, b);
                            break;
                        }
                        // special com_cust_roads45 next to roadcustc1w010 (COMNbtm)
                        else if (b.segment.getName().endsWith("roadcustc1w010") && a.position.dst(b.position) < 0.4f) {
                            a.computeLanes(b.rightLanes.size(), b.leftLanes.size());
                            break;
                        }
                    }
                    if (oppositeFix) {
                        // fix opposite port
                        PathNode b = a.segment.getOffensivePort(a);
                        if (b != null) {
                            b.computeLanes(a.rightLanes.size(), a.leftLanes.size());
                        }
                    }
                    break;
            }
        }
        // STEP: cross without opposite fix
        for (PathNode a : allPorts) {
            String segName = a.segment.getName().substring(4);
            switch (segName) {
                case "com_cust_roads57":
                    for (PathNode b : allPorts) {
                        if (b.segment.getName().endsWith("rd_CrossRoads11") && a.position.dst(b.position) < 0.4f) {
                            tryFixLane(a, b);
                            break;
                        }
                    }
                    break;
                    
                case "com_roadkb17":
                case "com_roadkb13":
                    for (PathNode b : allPorts) {
                        if (b.segment.isCross() && a.position.dst(b.position) < 0.4f) {
                            tryFixLane(a, b);
                            break;
                        }
                    }
                    // fix opposite port
                    PathNode b = a.segment.getOffensivePort(a);
                    if (b != null) {
                        b.computeLanes(a.rightLanes.size(), a.leftLanes.size());
                    }
                    break;
            }
        }
        // STEP: optimize data
        for (INode group : groups) {
            ArrayList<PathSegment> segments = new ArrayList<>();
            group.getChildrenByClass(PathSegment.class, segments);
            for (PathSegment seg : segments) {
                seg.optimizeData();
            }
        }
//        // STEP: fix node offset for one-way segments
//        for (INode group : groups) {
//            ArrayList<PathSegment> segments = new ArrayList<>();
//            group.getChildrenByClass(PathSegment.class, segments);
//            for (PathSegment seg : segments) {
//                seg.deleteStraightNodes(2);
//            }
//        }
        // FINAL: rebuild models
        for (INode group : groups) {
            ArrayList<PathSegment> segments = new ArrayList<>();
            group.getChildrenByClass(PathSegment.class, segments);
            for (PathSegment seg : segments) {
                seg.rebuildModel();
            }
        }
    }
    
    private boolean tryFixLane(PathNode a, PathNode b) {
        if (a.leftLanes.size() != b.rightLanes.size() ||
                a.rightLanes.size() != b.leftLanes.size()) {
            a.computeLanes(b.rightLanes.size(), b.leftLanes.size());
            return true;
        }
        return false;
    }
    
    private void exportPathData3() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setApproveButtonText("Export");
        if (fc.showOpenDialog(Unicore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            String dir = fc.getSelectedFile().getAbsolutePath();
            ArrayList<INode> nodes = new ArrayList<>();
            bigWorld.getChildrenByClass(INode.class, nodes, false);
            for (INode group : nodes) {
                String name = group.getName();
                name = name.substring(0, name.length() - 4);
                File out = new File(dir + "/" + name + ".path");
                try (FileOutputStream os = new FileOutputStream(out, false);
                        PrintStream ps = new PrintStream(os)) {
                    ArrayList<PathSegment> segments = new ArrayList<>();
                    group.getChildrenByClass(PathSegment.class, segments);
                    for (PathSegment seg : segments) {
                        seg.exportData(ps);
                    }
                    Logger.printNormal("Data exported: %s", out);
                } catch (IOException ex) {
                    Logger.showErrorDialog(ex);
                }
            }
        }
    }
}
