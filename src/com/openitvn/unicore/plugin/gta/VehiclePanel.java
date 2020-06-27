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

import com.badlogic.gdx.math.Vector3;
import com.openitvn.format.dff.RwModel;
import com.openitvn.unicore.world.WorldFactory;
import com.openitvn.unicore.Workspace;
import com.openitvn.unicore.plugin.PanelViewer;
import com.openitvn.unicore.plugin.gta.item.ItemCARS;
import com.openitvn.unicore.world.IGeometry;
import com.openitvn.unicore.world.INode;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Thinh Pham
 */
public final class VehiclePanel extends PanelViewer {

    private RwModel vehicleWorld;
    private final VehicleModel vehicleModel = new VehicleModel();
    
    private final ArrayList<INode> nodes = new ArrayList<>(); // for easy control
    private HashMap<Integer, String> wheelMap;
    
    public VehiclePanel() {
        initComponents();
        initVehicleTable();
        workspaceChanged(Workspace.getActive());
    }

    @Override
    public boolean requestClose() {
        WorldFactory.unregister(vehicleWorld);
        vehicleWorld = null;
        return true;
    }
    
    @Override
    public void workspaceChanged(Workspace workspace) {
        GameConfig.setWorkspace(workspace);
        vehicleModel.bind(ResourceModel.getInstance());
        wheelMap = GameConfig.getWheelNameMap();
    }
    
    private void initVehicleTable() {
        // setup the table
        TableColumnModel cm = tblCar.getColumnModel();
        cm.getColumn(ResourceModel.COL_INDEX).setMinWidth(32);
        cm.getColumn(ResourceModel.COL_INDEX).setMaxWidth(32);
        cm.getColumn(ResourceModel.COL_SIZE).setMinWidth(40);
        cm.getColumn(ResourceModel.COL_SIZE).setMaxWidth(40);
        // item change listener
        tblCar.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                int row = tblCar.getSelectedRow();
                if (row >= 0 && !evt.getValueIsAdjusting()) {
                    // unregister current world
                    WorldFactory.unregister(vehicleWorld);
                    // create new world and pre-register libraries
                    int id = tblCar.convertRowIndexToModel(row);
                    ItemCARS e = vehicleModel.entries.get(id);
                    vehicleWorld = new RwModel(e.modName);
                    vehicleWorld.resource.register(vehicleModel.comTexLib);
                    vehicleWorld.resource.register(vehicleModel.comMatLib);
                    vehicleWorld.resource.register(vehicleModel.comModLib);
                    // load world from dff stream
                    ResourceModel rm = ResourceModel.getInstance();
                    rm.extractModel(vehicleWorld, nodes);
                    // create wheels
                    switch (e.type) {
                        case "car":
                        case "mtruck":
                        case "trailer":
                            createWheels(e);
                            break;
                    }
                    // update world
                    vehicleWorld.construct(vehicleWorld.resource);
                    WorldFactory.focusTo(vehicleWorld);
                    updateVisibility();
                }
            }
        });
    }
    
    private void createWheels(ItemCARS car) {
        ArrayList<INode> wheelNodes = new ArrayList<>();
        switch (GameConfig.getAlias()) {
            case GameConfig.ALIAS_III:
            case GameConfig.ALIAS_VC:
                // find wheel by id
                String wheelName = wheelMap.get(car.wheelModelId)+"_l0";
                float scl = car.wheelScale;
                for (INode node : nodes) {
                    String name = node.getName();
                    if (name.startsWith("wheel_")) {
                        IGeometry wheel = new IGeometry(wheelName);
                        wheel.attach(node);
                        if (name.startsWith("wheel_l")) {
                            wheel.transform.localMatrix.rotate(Vector3.Z, 180);
                        }
                        node.transform.localMatrix.scale(scl, scl, scl);
                        wheelNodes.add(wheel);
                    }
                }
                break;

            case GameConfig.ALIAS_SA:
                for (INode node : nodes) {
                    String name = node.getName();
                    if (name.startsWith("wheel_") && !name.startsWith("wheel_rf")) {
                        IGeometry wheel = new IGeometry("wheel");
                        wheel.attach(node);
                        wheelNodes.add(wheel);
                    }
                    if (name.startsWith("wheel_l")) {
                        node.transform.localMatrix.rotate(Vector3.Z, 180);
                    }
                }
                break;
        }
        nodes.addAll(wheelNodes);
    }
    
    private enum ViewMode { Normal, Damaged, Distance }
    
    private ViewMode viewMode = ViewMode.Normal;
    
    private void viewModeChanged() {
        boolean changed = false;
        if (rdoNormal.isSelected()) {
            changed = viewMode != ViewMode.Normal;
            viewMode = ViewMode.Normal;
        } else if (rdoDamage.isSelected()) {
            changed = viewMode != ViewMode.Damaged;
            viewMode = ViewMode.Damaged;
        } else if (rdoDistance.isSelected()) {
            changed = viewMode != ViewMode.Distance;
            viewMode = ViewMode.Distance;
        }
        if (changed) {
            updateVisibility();
        }
    }
    
    private void updateVisibility() {
        for (INode node : nodes) {
            String name = node.getName();
            if (name == null) {
                node.setVisible(false);
                continue;
            }
            boolean visible = false;
            if (!name.startsWith("extra")) {
                switch (viewMode) {
                    case Normal:
                        visible = !name.contains("_vlo") && !name.contains("_lo") && !name.contains("_dam");
                        break;
                    case Damaged:
                        visible = !name.contains("_vlo") && !name.contains("_lo") && !name.contains("_ok");
                        break;
                    case Distance:
                        visible = name.contains("_vlo") || name.contains("_lo");
                        break;
                }
            }
            if (!visible) {
                visible = name.endsWith("_dummy");
            }
            node.setVisible(visible);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        viewGroup = new javax.swing.ButtonGroup() {
            public void setSelected(javax.swing.ButtonModel m, boolean b) {
                super.setSelected(m, b);
                viewModeChanged();
            }
        };
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCar = new javax.swing.JTable();
        rdoNormal = new javax.swing.JRadioButton();
        rdoDamage = new javax.swing.JRadioButton();
        rdoDistance = new javax.swing.JRadioButton();
        txtSearch = new com.openitvn.control.UCTextField();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setName("Vehicle"); // NOI18N

        tblCar.setAutoCreateRowSorter(true);
        tblCar.setModel(vehicleModel);
        tblCar.setRowHeight(20);
        tblCar.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCar.setShowHorizontalLines(false);
        tblCar.setShowVerticalLines(false);
        tblCar.getTableHeader().setResizingAllowed(false);
        tblCar.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblCar);

        viewGroup.add(rdoNormal);
        rdoNormal.setSelected(true);
        rdoNormal.setText("Normal");

        viewGroup.add(rdoDamage);
        rdoDamage.setText("Damaged");

        viewGroup.add(rdoDistance);
        rdoDistance.setText("Distance");

        txtSearch.setPrompt("search here");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(rdoNormal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdoDamage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdoDistance))
            .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdoNormal)
                    .addComponent(rdoDamage)
                    .addComponent(rdoDistance)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        String regex = txtSearch.getSearchRegex();
        TableRowSorter sorter = (TableRowSorter)tblCar.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(regex, ResourceModel.COL_NAME));
    }//GEN-LAST:event_txtSearchKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rdoDamage;
    private javax.swing.JRadioButton rdoDistance;
    private javax.swing.JRadioButton rdoNormal;
    private javax.swing.JTable tblCar;
    private com.openitvn.control.UCTextField txtSearch;
    private javax.swing.ButtonGroup viewGroup;
    // End of variables declaration//GEN-END:variables
}
