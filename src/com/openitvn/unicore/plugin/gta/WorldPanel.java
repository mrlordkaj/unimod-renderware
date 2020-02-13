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
import com.openitvn.control.table.BooleanCellRenderer;
import com.openitvn.engine.renderware.RpClump;
import com.openitvn.engine.renderware.RpGeometry;
import com.openitvn.engine.renderware.RpMaterial;
import com.openitvn.engine.renderware.RpSection;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.format.col.ColFile;
import com.openitvn.format.dff.RwMaterial;
import com.openitvn.format.img.RwArchiveEntry;
import com.openitvn.format.txd.RwTexture;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.WorldFactory;
import com.openitvn.unicore.Workspace;
import com.openitvn.unicore.data.BufferStream;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.plugin.PanelViewer;
import com.openitvn.unicore.plugin.gta.item.INSTEntry;
import com.openitvn.unicore.plugin.gta.item.OBJSEntry;
import com.openitvn.unicore.plugin.gta.item.PATHSegment;
import com.openitvn.unicore.world.IGeometry;
import com.openitvn.unicore.world.ILayer;
import com.openitvn.unicore.world.IMesh;
import com.openitvn.unicore.world.INode;
import com.openitvn.unicore.world.IWorld;
import com.openitvn.unicore.world.IWorldCoord;
import com.openitvn.unicore.world.IWorldUnit;
import com.openitvn.unicore.world.resource.IModel;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.RowFilter;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Thinh Pham
 */
public final class WorldPanel extends PanelViewer {
    
    private static final int    LAYER_NORMAL = 0,
                                LAYER_DISTANCE = 1,
                                LAYER_COLLISION = 2,
                                LAYER_CAR_PATH = 3;
    
    private final HashMap<Integer, String> modelNameMap = new HashMap<>(); // inst find
    private final HashMap<String, ColFile> collisionMap = new HashMap<>(); // inst find
    private final HashMap<String, PATHSegment> pathMap = new HashMap<>(); // inst find
    private final HashMap<Integer, Integer> modelLayerMap = new HashMap<>(); // layer find - objs.id, layer.id
    private final WorldScriptModel scriptModel = new WorldScriptModel();
    
    private final IWorld world;
//    private final Camera camera;
//    private final Vector3 prevPos = new Vector3();
//    private final Vector3 prevDir = new Vector3();
//    private final Timer updateTimer;
    
    public WorldPanel() {
        initComponents();
        // setup the table
        TableColumnModel cm = tblMap.getColumnModel();
        cm.getColumn(WorldScriptModel.COL_ACTIVE).setMinWidth(20);
        cm.getColumn(WorldScriptModel.COL_ACTIVE).setMaxWidth(20);
        cm.getColumn(WorldScriptModel.COL_TYPE).setMinWidth(40);
        cm.getColumn(WorldScriptModel.COL_TYPE).setMaxWidth(40);
        refineWorldTable(null);
        tblMap.setDefaultRenderer(Boolean.class, new BooleanCellRenderer());
        // prepare world world
        world = WorldFactory.create("GTA World");
        world.setCoordinate(IWorldCoord.Zup, IWorldUnit.Meters);
//        world.setInfo(new String[] {
//            "GTA III World Viewer"
//        });
        world.layers.add(new ILayer(LAYER_NORMAL, "Normal Map", true));
        world.layers.add(new ILayer(LAYER_DISTANCE, "Distance Map", false));
        world.layers.add(new ILayer(LAYER_COLLISION, "Collision Map", false));
        world.layers.add(new ILayer(LAYER_CAR_PATH, "Vehicle Path", true));
        workspaceChanged(Workspace.getActive());
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
        modelNameMap.clear();
        collisionMap.clear();
        modelLayerMap.clear();
        groupRegistryMap.clear();
        pendingNode = null;
        txdCache = null;
        WorldFactory.unregister(world);
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
        rdoIDE = new javax.swing.JRadioButton();
        rdoIPL = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMap = new javax.swing.JTable() {
            public String getToolTipText(MouseEvent evt) {
                int row = tblMap.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    row = tblMap.convertRowIndexToModel(row);
                    com.openitvn.unicore.plugin.gta.WorldScriptEntry e = scriptModel.getScript(row);
                    return e.path;
                }
                return null;
            }
        };
        rdoAll = new javax.swing.JRadioButton();
        btnExportPath = new javax.swing.JButton();
        btnOptimizePath = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setName("World"); // NOI18N

        groupType.add(rdoIDE);
        rdoIDE.setText("IDE");
        rdoIDE.setToolTipText("Item Definition");
        rdoIDE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refineWorldTable(evt);
            }
        });

        groupType.add(rdoIPL);
        rdoIPL.setSelected(true);
        rdoIPL.setText("IPL");
        rdoIPL.setToolTipText("Item Placement");
        rdoIPL.addActionListener(new java.awt.event.ActionListener() {
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
        jScrollPane1.setViewportView(tblMap);

        groupType.add(rdoAll);
        rdoAll.setText("ALL");
        rdoAll.setToolTipText("All Definitions");
        rdoAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refineWorldTable(evt);
            }
        });

        btnExportPath.setText("Export Path");
        btnExportPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportPathActionPerformed(evt);
            }
        });

        btnOptimizePath.setText("Optimize Path");
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
                .addGap(0, 38, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExportPath)
                    .addComponent(btnOptimizePath)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refineWorldTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refineWorldTable
        String regex;
        if (rdoIDE.isSelected())
            regex = "^IDE$";
        else if (rdoIPL.isSelected())
            regex = "^IPL$";
        else
            regex = "";
        ((TableRowSorter) tblMap.getRowSorter())
            .setRowFilter(RowFilter.regexFilter(regex, WorldScriptModel.COL_TYPE));
    }//GEN-LAST:event_refineWorldTable

    private void btnExportPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportPathActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setApproveButtonText("Export");
        if (fc.showOpenDialog(Unicore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            String dir = fc.getSelectedFile().getAbsolutePath();
            for (INode group : world.getChildrenByClass(INode.class, false)) {
                String name = group.getName();
                name = name.substring(0, name.length() - 4);
                File out = new File(dir + "/" + name + ".road");
                try (FileOutputStream os = new FileOutputStream(out, false);
                        PrintStream ps = new PrintStream(os)) {
                    for (PathSegment seg : group.getChildrenByClass(PathSegment.class)) {
                        seg.exportData(ps);
                    }
                    Logger.printNormal("Data exported: %s", out);
                } catch (IOException ex) {
                    Logger.showErrorDialog(Unicore.getMainFrame(), ex);
                }
            }
        }
    }//GEN-LAST:event_btnExportPathActionPerformed

    private void btnOptimizePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOptimizePathActionPerformed
        optimizePathData3();
    }//GEN-LAST:event_btnOptimizePathActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportPath;
    private javax.swing.JButton btnOptimizePath;
    private javax.swing.ButtonGroup groupType;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rdoAll;
    private javax.swing.JRadioButton rdoIDE;
    private javax.swing.JRadioButton rdoIPL;
    private javax.swing.JTable tblMap;
    // End of variables declaration//GEN-END:variables
    
    private void addOBJS(OBJSEntry objs, GroupRegistry reg) {
        ResourceModel res = ResourceModel.getInstance();
        RpSection grand;
        // load textures
        grand = txdCache.get(objs.txdName);
        HashMap<String, RpTextureNative> texNavMap = new HashMap<>();
        if (grand == null) {
            // load txd from resource
            try (RwArchiveEntry e = res.findEntry(objs.txdName + ".txd");
                    BufferStream bs = e.toDataStream()) {
                grand = RpSection.fromData(bs, null);
                for (RpTextureNative texData : grand.getChildren(RpTextureNative.class)) {
                    texNavMap.put(texData.textureName.toLowerCase(), texData);
                    String texName = texData.getMapperName();
                    // register new texture when missing
                    if (world.resource.findTexture(texName) == null) {
                        RwTexture rTex = new RwTexture(texName, texData);
                        world.resource.register(rTex);
                        reg.texNames.add(texName);
                    }
                }
                txdCache.put(objs.txdName, grand);
            } catch (NullPointerException ex) {
                Logger.printWarning("TXD not found: " + objs.txdName);
            }
        } else {
            // load txd from cache
            for (RpTextureNative texData : grand.getChildren(RpTextureNative.class))
                texNavMap.put(texData.textureName.toLowerCase(), texData);
        }
        // load model
        try (RwArchiveEntry e = res.findEntry(objs.modName + ".dff");
                BufferStream ds = e.toDataStream()) {
            while ((grand = RpSection.fromData(ds, null)) != null) {
                if (grand instanceof RpClump) {
                    // only load root geometry as model
                    RpGeometry geoData = ((RpClump)grand).getRootGeometry();
                    if (geoData != null) {
                        IModel mod = new IModel(objs.modName);
                        reg.modNames.add(objs.modName);
                        // meshes = materials
                        for (short i = 0; i < geoData.materials.size(); i++) {
                            // material
                            RpMaterial matData = geoData.materials.get(i);
                            RpTextureNative texData = texNavMap.get(matData.getTextureName().toLowerCase());
                            String matName = (texData == null) ?
                                    objs.modName + "_untex" + i :
                                    texData.getMapperName() + "m";
                            // register new material when missing
                            if (world.resource.findMaterial(matName) == null) {
                                world.resource.register(new RwMaterial(matName, matData, texData));
                                reg.matNames.add(matName);
                            }
                            // mesh
                            IMesh mesh = new IMesh();
                            mesh.setVertices(geoData.numVerts, geoData.vertData, geoData.vertFmt);
                            mesh.setIndices(geoData.indexMap.get(i));
                            mesh.materialName = matName;
                            mod.meshes.add(mesh);
                        }
                        world.resource.register(mod);
                        modelNameMap.put(objs.modId, objs.modName);
                        break;
                    }
                }/* else {
                    Logger.printWarning("Nothing found in DFF: %1$s", dff);
                    break;
                }*/
            }
        } catch (NullPointerException ex) {
            Logger.printWarning("DFF not found: " + objs.modName);
        }
    }
    
    private void addINST(INode group, INSTEntry inst) {
        Matrix4 transform = new Matrix4().translate(inst.posX, inst.posY, inst.posZ)
                    .rotateRad(inst.rotX, inst.rotY, inst.rotZ, -2*(float)Math.acos(inst.rotW))
                    /*.scale(inst.sclX, inst.sclY, inst.sclZ)*/;
        
        // add model
        String modName = modelNameMap.get(inst.modId);
        if (modName != null) {
            IGeometry geo = new IGeometry(modName);
            geo.transform.localMatrix.set(transform);
            geo.setLayerIndex(modelLayerMap.get(inst.modId));
            geo.attach(group);
            
        }/* else {
            Logger.printWarning("Missing OBJS: %1$d, %2$s", inst.objsId, inst.objsName);
        }*/
        
        // add collision
        ColFile col = collisionMap.get(inst.modName);
        if (col != null) {
//            System.out.printf("%s, %f, %f, %f, %f, %f, %f, %f\n",
//                    inst.modName,
//                    inst.posX, -inst.posY, inst.posZ,
//                    inst.rotX, -inst.rotY, inst.rotZ, inst.rotW);
            // collision mesh
            if (col.model != null) {
                IGeometry geo = new IGeometry(col.model.getName());
                geo.transform.localMatrix.set(transform);
                geo.setLayerIndex(LAYER_COLLISION);
                geo.attach(group);
//                System.out.println("1, CLM_" + inst.modName);
            }
//            // collision box
//            for (ColBox box : col.boxes) {
//                Vector3 center = box.getCenter();
//                Vector3 size = box.getSize();
//                System.out.printf("2, %f, %f, %f, %f, %f, %f\n",
//                        center.x, -center.y, center.z,
//                        size.x * 0.5f, size.y * 0.5f, size.z * 0.5f);
//            }
//            // collision sphere
//            for (ColSphere sphere : col.spheres) {
//                System.out.printf("3, %f, %f, %f, %f\n",
//                        sphere.center.x,
//                        -sphere.center.y,
//                        sphere.center.z,
//                        sphere.radius);
//            }
//            // done
//            System.out.println("break");
        }
        
        // add paths (only GTA III for now)
        PATHSegment path = pathMap.get(inst.modName);
        if (path != null) {
            PathSegment seg = new PathSegment(path);
            seg.transform.localMatrix.set(transform);
            seg.setLayerIndex(LAYER_CAR_PATH);
            seg.attach(group);
        }
    }
      
    void executeOBJSGroup(String groupName, BufferedReader br, boolean active) throws IOException {
        GroupRegistry reg = groupRegistryMap.get(groupName);
        if (active) {
            // register new reg for this group
            if (reg == null) {
                reg = new GroupRegistry();
                groupRegistryMap.put(groupName, reg);
            }
            
            // parse collision list
            // TODO: current support collsion mesh only,
            // need implement primitive collision in future
            String colFile = groupName.substring(0, groupName.length() - 3)+"col";
            RwArchiveEntry colEntry = ResourceModel.getInstance().findEntry(colFile);
            if (colEntry != null) {
                BufferStream ds = colEntry.toDataStream();
                int fourCC;
                while (ds.remaining() > 4 && (fourCC = ds.getInt()) != 0) {
                    ColFile col = new ColFile(fourCC, ds);
                    if (col.model != null) {
                        world.resource.register(col.model);
                        reg.modNames.add(col.model.getName());
                        collisionMap.put(col.objsName, col);
                        reg.colNames.add(col.objsName);
                    }
                }
            }
            
            // parse objs list
            String[] args;
            while ((args = ScriptHelper.parseLineByComma(br)) != null) {
                try {
                    OBJSEntry objs = new OBJSEntry(args);
                    // cancel special objects
                    switch (GameConfig.getAlias()) {
                        case GameConfig.ALIAS_III:
                            if ((160 <= objs.modId && objs.modId <= 169) || // wheels
                                (170 <= objs.modId && objs.modId <= 184) || // weapons
                                (196 <= objs.modId && objs.modId <= 199)) { // misc
                                continue;
                            }
                            break;
                            
                        case GameConfig.ALIAS_VC:
                            if ((130 <= objs.modId && objs.modId <= 239) || // vehicles
                                (250 <= objs.modId && objs.modId <= 257)) { // wheels
                                continue;
                            }
                            break;
                    }
                    addOBJS(objs, reg);
                    String norName = objs.modName.toLowerCase();
                    int layer = objs.dd1 <= 300 && !norName.contains("lod") ?
                            LAYER_NORMAL : LAYER_DISTANCE;
                    modelLayerMap.put(objs.modId, layer);
                } catch (IllegalArgumentException ex) { }
            }
        } else if (reg != null) {
            // null registry means the group already unregistered
            world.resource.deleteModels(reg.modNames, true);
            world.resource.deleteMaterials(reg.matNames, true);
            world.resource.deleteTextures(reg.texNames, true);
            for (String colName : reg.colNames)
                collisionMap.remove(colName);
            groupRegistryMap.remove(groupName);
        }
    }
    
    /**
     * Parse text PATH, for III and VC.
     */
    void executePATHGroup(String groupName, BufferedReader br, boolean active) throws IOException {
        if (active) {
            String[] args;
            while ((args = ScriptHelper.parseLineByComma(br)) != null) {
                int segmentType = args[0].equals("car") ? PATHSegment.TYPE_CAR : PATHSegment.TYPE_PED;
                int modId = Integer.parseInt(args[1]);
                String modName = args[2];
                if (segmentType == PATHSegment.TYPE_CAR) {
                    PATHSegment entry = new PATHSegment(segmentType, modId, modName, br);
                    pathMap.put(modName, entry);
                } else {
                    // skip ped
                    for (int i = 0; i < 12; i++)
                        br.readLine();
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
            while ((args = ScriptHelper.parseLineByComma(br)) != null)
                addINST(group, new INSTEntry(args));
            pendingNode = group;
        } else {
            world.deleteNode(groupName);
            WorldFactory.focusTo(world);
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
            for (int j = 0; j < instCount; j++)
                addINST(group, new INSTEntry(ds));
            pendingNode = group;
        } else {
            world.deleteNode(groupName);
            WorldFactory.focusTo(world);
        }
    }
    
    private class GroupRegistry {
        // quick delete resource from world
        private final ArrayList<String> modNames = new ArrayList<>();
        private final ArrayList<String> matNames = new ArrayList<>();
        private final ArrayList<String> texNames = new ArrayList<>();
        // quick delete cache from collisionMap
        private final ArrayList<String> colNames = new ArrayList<>();
    }
    
    // world dispatcher for schedule push world data
    // when asynchronous loading thread done
    private HashMap<String, RpSection> txdCache;
    private INode pendingNode;
    
    // store all resource names used by groups,
    // for quickly delete when deactive a group
    private final HashMap<String, GroupRegistry> groupRegistryMap = new HashMap<>(); // groupName, registry
    
    void prepareDispatcher() {
        tblMap.setEnabled(false);
        txdCache = new HashMap();
        pendingNode = null;
    }
    
    void executeDispatcher() {
        if (pendingNode != null) {
            pendingNode.attach(world);
            pendingNode.construct(world.resource);
            pendingNode.update(true);
            for (PathSegment seg : pendingNode.getChildrenByClass(PathSegment.class)) {
                seg.compileData();
                seg.rebuildModel();
            }
            pendingNode = null;
        }
        txdCache = null;
        tblMap.setEnabled(true);
        System.gc();
        WorldFactory.focusTo(world);
    }
    
    private void optimizePathData3() {
        ArrayList<INode> groups = world.getChildrenByClass(INode.class, false);
        ArrayList<PathNode> allPorts = new ArrayList<>();
        // STEP: merge all possible segments
        for (INode group : groups) {
            ArrayList<PathNode> straightPorts = new ArrayList<>();
            for (PathSegment seg : group.getChildrenByClass(PathSegment.class)) {
                ArrayList<PathNode> segPorts = seg.getPorts();
                if (!seg.isCross())
                    straightPorts.addAll(segPorts);
                // collects all ports for fix step
                allPorts.addAll(segPorts);
            }
            mainWhile:
            while (!straightPorts.isEmpty()) {
                PathNode a = straightPorts.remove(0);
                for (PathNode b : straightPorts) {
                    if (a.tryMerge(b)) {
                        straightPorts.remove(b);
                        continue mainWhile;
                    }
                }
            }
        }
        // STEP: cross with opposite fix
        for (PathNode a : allPorts) {
            String segName = a.segment.getName().substring(4);
            if (segName.equals("rd_CrossRoads11") ||
                    segName.equals("rd_CrossRoads12") ||
                    segName.equals("rd_TJunction12") ||
                    segName.equals("com_cust_roads45") ||
                    segName.equals("com_cust_roads44") ||
                    segName.equals("rd_TJunction11way") ||
                    segName.equals("rd_TJunction11") ||
                    segName.equals("CrossRoadn1") ||
                    segName.equals("ind_customroad088") ||
                    segName.equals("ind_customroad0bb") ||
                    segName.equals("indmaincj1way") ||
                    segName.equals("cussRoads5")) {
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
                    if (b != null)
                        b.computeLanes(a.rightLanes.size(), a.leftLanes.size());
                }
            }
        }
        // STEP: cross without opposite fix
        for (PathNode a : allPorts) {
            String segName = a.segment.getName().substring(4);
            if (segName.equals("com_cust_roads57")) {
                for (PathNode b : allPorts) {
                    if (b.segment.getName().endsWith("rd_CrossRoads11") && a.position.dst(b.position) < 0.4f) {
                        tryFixLane(a, b);
                        break;
                    }
                }
            }
            if (segName.equals("com_roadkb17") ||
                    segName.equals("com_roadkb13")) {
                for (PathNode b : allPorts) {
                    if (b.segment.isCross() && a.position.dst(b.position) < 0.4f) {
                        tryFixLane(a, b);
                        break;
                    }
                }
                // fix opposite port
                PathNode b = a.segment.getOffensivePort(a);
                if (b != null)
                    b.computeLanes(a.rightLanes.size(), a.leftLanes.size());
            }
        }
        // STEP: 
        // FINAL: optimize data and rebuild models
        for (INode group : groups) {
            for (PathSegment seg : group.getChildrenByClass(PathSegment.class)) {
                seg.optimizeData();
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
}
