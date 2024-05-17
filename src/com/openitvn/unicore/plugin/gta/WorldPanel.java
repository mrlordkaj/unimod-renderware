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
import com.openitvn.control.UCFileChooser;
import com.openitvn.control.table.UCBooleanCellRenderer;
import com.openitvn.engine.renderware.RpClump;
import com.openitvn.engine.renderware.RpGeometry;
import com.openitvn.engine.renderware.RpMaterial;
import com.openitvn.engine.renderware.RpSection;
import com.openitvn.engine.renderware.RpTextureNative;
import com.openitvn.format.col.ColFile;
import com.openitvn.format.dff.RwMaterial;
import com.openitvn.format.dff.RwWorld;
import com.openitvn.format.txd.RwTexture;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.Workspace;
import com.openitvn.unicore.data.DataStream;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.plugin.PanelViewer;
import com.openitvn.unicore.plugin.gta.item.ItemINST;
import com.openitvn.unicore.plugin.gta.item.ItemOBJS;
import com.openitvn.unicore.plugin.gta.item.ItemPATHSegment;
import com.openitvn.unicore.world.IGeometry;
import com.openitvn.unicore.world.ILayer;
import com.openitvn.unicore.world.IMesh;
import com.openitvn.unicore.world.INode;
import com.openitvn.unicore.world.IWorldCoord;
import com.openitvn.unicore.world.IWorldUnit;
import com.openitvn.unicore.world.resource.IModel;
import com.openitvn.unicore.world.resource.ITexture;
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
import javax.swing.filechooser.FileNameExtensionFilter;
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
    private final HashMap<String, ItemPATHSegment> pathMap = new HashMap<>(); // inst find
    private final HashMap<Integer, Integer> modelLayerMap = new HashMap<>(); // layer find - objs.id, layer.id
    private final WorldScriptModel scriptModel = new WorldScriptModel();
    
    private final RwWorld world;
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
        tblMap.setDefaultRenderer(Boolean.class, new UCBooleanCellRenderer());
        // prepare world world
        world = new RwWorld("GTA World");
        world.setCoordinate(IWorldCoord.Zup, IWorldUnit.Meters);
        world.layers.add(new ILayer(LAYER_NORMAL, "Normal Map", true));
        world.layers.add(new ILayer(LAYER_DISTANCE, "Distance Map", false));
        world.layers.add(new ILayer(LAYER_COLLISION, "Collision Map", false));
        world.layers.add(new ILayer(LAYER_CAR_PATH, "Vehicle Path", false));
        Unicore.registerWorld(world);
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
        pendingNodes = null;
        Unicore.unregisterWorld(world);
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
                    com.openitvn.unicore.plugin.gta.WorldScriptEntry e = scriptModel.getScript(row);
                    return e.path;
                }
                return null;
            }
        };

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
        jScrollPane1.setViewportView(tblMap);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(rdoIPL)
                .addGap(18, 18, 18)
                .addComponent(rdoIDE)
                .addGap(18, 18, 18)
                .addComponent(rdoAll)
                .addGap(0, 67, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refineWorldTable(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refineWorldTable
        String regex = (evt == null) ? "^IPL$" : evt.getActionCommand();
        TableRowSorter sorter = (TableRowSorter) tblMap.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(regex, WorldScriptModel.COL_TYPE));
    }//GEN-LAST:event_refineWorldTable
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup groupType;
    private javax.swing.JTable tblMap;
    // End of variables declaration//GEN-END:variables
    
    private void addOBJS(ItemOBJS objs, GroupRegistry reg) {
        ResourceModel res = ResourceModel.getInstance();
        // load textures
        try (EntryStream ts = res.getEntryStream(objs.txdName, "txd")) {
            world.loadTexDic(ts);
        } catch (IOException ex) {
            Logger.printWarning("TXD not found: " + objs.txdName);
        }
        // prepare texture native cache from resource manager
        HashMap<String, RpTextureNative> texNavMap = new HashMap<>();
        for (ITexture tex : world.resource.getTextures()) {
            if (tex instanceof RwTexture) {
                RpTextureNative texData = ((RwTexture)tex).getTextureData();
                if (!texData.textureName.isEmpty()) {
                    texNavMap.put(texData.textureName.toLowerCase(), texData);
                }
                if (!texData.maskName.isEmpty()) {
                    texNavMap.put(texData.maskName.toLowerCase(), texData);
                }
            }
        }
        // load model
        try (EntryStream ms = res.getEntryStream(objs.modName, "dff")) {
            RpClump clump = RpSection.loadRoot(ms, RpClump.class);
            if (clump != null) {
                // only load root geometry as model
                RpGeometry geoData = clump.getRootGeometry();
                if (geoData != null) {
                    IModel mod = new IModel(objs.modName);
                    reg.modNames.add(objs.modName);
                    // meshes = materials
                    for (short i = 0; i < geoData.materials.size(); i++) {
                        RpMaterial matData = geoData.materials.get(i);
                        // search texture by name which defined in material
                        String texName = matData.getMaskName();
                        RpTextureNative texNav = texNavMap.get(texName.toLowerCase());
                        if (texNav == null) {
                            texName = matData.getTextureName();
                            texNav = texNavMap.get(texName.toLowerCase());
                        }
                        // create material
                        String matName = "M_";
                        if (texName.isEmpty()) {
                            matName += "Blank";
                        } else {
                            matName += texName;
                            if (texNav == null) {
                                Logger.printWarning("Texture not found: %s (%s.txd)", texName, objs.txdName);
                            }
                        }
                        // register new material when missing
                        if (!world.resource.containsMaterial(matName)) {
                            RwMaterial mat = new RwMaterial(matName, matData, texNav);
                            world.resource.register(mat);
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
                }
            }
        } catch (IOException ex) {
            Logger.printWarning("DFF not found: " + objs.modName);
        }
    }
    
    private void addINST(INode group, ItemINST inst) {
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
        ColFile col = collisionMap.get(modName);
        if (col != null) {
            IGeometry geo = new IGeometry(col.model.getName());
            geo.transform.localMatrix.set(transform);
            geo.setLayerIndex(LAYER_COLLISION);
            geo.attach(group);
        }
        
        // add paths (only GTA III for now)
        ItemPATHSegment path = pathMap.get(inst.modName);
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
            ResourceModel res = ResourceModel.getInstance();
            String colFile = groupName.substring(0, groupName.length() - 4);
            try (EntryStream cs = res.getEntryStream(colFile, "col")) {
                int fourCC;
                while (cs.remaining() > 4 && (fourCC = cs.getInt()) != 0) {
                    ColFile col = new ColFile(fourCC, cs);
                    if (col.model != null) {
                        world.resource.register(col.model);
                        reg.modNames.add(col.model.getName());
                        collisionMap.put(col.objsName, col);
                        reg.colNames.add(col.objsName);
                    }
                }
            } catch (IOException ex) { }
            
            // parse objs list
            String[] args;
            while ((args = ScriptHelper.parseLineByComma(br)) != null) {
                try {
                    ItemOBJS objs = new ItemOBJS(args);
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
            for (String colName : reg.colNames) {
                collisionMap.remove(colName);
            }
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
            world.deleteNode(groupName);
            Unicore.focusToWorld(world);
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
            world.deleteNode(groupName);
            Unicore.focusToWorld(world);
        }
    }
    
    private class GroupRegistry {
        // registry for world resource
        private final ArrayList<String> modNames = new ArrayList<>();
        private final ArrayList<String> matNames = new ArrayList<>();
        private final ArrayList<String> texNames = new ArrayList<>();
        // registry for collisionMap
        private final ArrayList<String> colNames = new ArrayList<>();
    }
    
    // world dispatcher for schedule push world data
    // when asynchronous loading thread done
    private ArrayList<INode> pendingNodes;
    
    // store all resource names used by groups,
    // for quickly delete when deactive a group
    private final HashMap<String, GroupRegistry> groupRegistryMap = new HashMap<>(); // groupName, registry
    
    void prepareDispatcher() {
        tblMap.setEnabled(false);
        pendingNodes = new ArrayList<>();
    }
    
    void executeDispatcher() {
        for (INode node : pendingNodes) {
            node.attach(world);
            node.construct(world.resource);
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
        System.gc();
        Unicore.focusToWorld(world);
    }
    
    private void optimizePathData3() {
        ArrayList<INode> groups = new ArrayList<>();
        world.getChildrenByClass(INode.class, groups, false);
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
            ArrayList<PathSegment> segments = new ArrayList<>();
            group.getChildrenByClass(PathSegment.class, segments);
            for (PathSegment seg : segments) {
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
    
    private void exportPathData3() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setApproveButtonText("Export");
        if (fc.showOpenDialog(Unicore.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
            String dir = fc.getSelectedFile().getAbsolutePath();
            ArrayList<INode> nodes = new ArrayList<>();
            world.getChildrenByClass(INode.class, nodes, false);
            for (INode group : nodes) {
                String name = group.getName();
                name = name.substring(0, name.length() - 4);
                File out = new File(dir + "/" + name + ".road");
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
