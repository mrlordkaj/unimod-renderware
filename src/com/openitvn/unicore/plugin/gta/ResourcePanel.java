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

import com.openitvn.engine.renderware.tool.RwDumper;
import com.openitvn.format.dff.RwModel;
import com.openitvn.unicore.Unicore;
import com.openitvn.unicore.world.WorldFactory;
import com.openitvn.unicore.Workspace;
import com.openitvn.unicore.archive.IArchiveEntry;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.plugin.PanelViewer;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Thinh Pham
 */
public final class ResourcePanel extends PanelViewer {
    
    private final ResourceModel resModel = ResourceModel.getInstance();
    private IArchiveEntry selected;
    private RwModel viewer;
    
    public ResourcePanel() {
        initComponents();
        initResourceTable();
        workspaceChanged(Workspace.getActive());
    }
    
    @Override
    public boolean requestClose() {
        WorldFactory.unregister(viewer);
        viewer = null;
        return true;
    }
    
    @Override
    public void workspaceChanged(Workspace space) {
        GameConfig.setWorkspace(space);
        lblInfo.setText(String.format("Total %1$d entries", resModel.entries.size()));
    }
    
    private void initResourceTable() {
        // setup the table
        TableColumnModel cm = resTable.getColumnModel();
        cm.getColumn(ResourceModel.COL_INDEX).setMinWidth(40);
        cm.getColumn(ResourceModel.COL_INDEX).setMaxWidth(40);
        cm.getColumn(ResourceModel.COL_SIZE).setMinWidth(48);
        cm.getColumn(ResourceModel.COL_SIZE).setMaxWidth(48);
        // item change listener
        resTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                int row = resTable.getSelectedRow();
                if (row >= 0 && !evt.getValueIsAdjusting()) {
                    int id = resTable.convertRowIndexToModel(row);
                    ResourceModel res = ResourceModel.getInstance();
                    selected = res.entries.get(id);
                    String type = selected.getExt().toLowerCase();
                    String name = selected.getNameWithoutExt();
                    if (type.equals("dff")) {
                        String txdName = res.findTexDicByModel(name);
                        lblInfo.setText(String.format("%1$s | %2$s", name, txdName));
                    } else {
                        lblInfo.setText(name);
                    }
                }
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mnuEntry = new javax.swing.JPopupMenu();
        mnuOpen = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuDump = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        resTable = new javax.swing.JTable() {
            public String getToolTipText(MouseEvent evt) {
                int row = resTable.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    row = resTable.convertRowIndexToModel(row);
                    IArchiveEntry e = ResourceModel.getInstance().getEntry(row);
                    return e.getArchive().getFile().toString();
                }
                return null;
            }
        };
        txtSearch = new com.openitvn.control.UCTextField();
        lblInfo = new javax.swing.JLabel();

        mnuOpen.setText("Open...");
        mnuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenActionPerformed(evt);
            }
        });
        mnuEntry.add(mnuOpen);
        mnuEntry.add(jSeparator1);

        mnuDump.setText("Dump...");
        mnuDump.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDumpActionPerformed(evt);
            }
        });
        mnuEntry.add(mnuDump);

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setMinimumSize(new java.awt.Dimension(200, 200));
        setName("Resource"); // NOI18N

        resTable.setAutoCreateRowSorter(true);
        resTable.setModel(resModel);
        resTable.setRowHeight(20);
        resTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        resTable.setShowHorizontalLines(false);
        resTable.setShowVerticalLines(false);
        resTable.getTableHeader().setResizingAllowed(false);
        resTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resTableMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                resTableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(resTable);

        txtSearch.setPrompt("search here");
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        lblInfo.setText("<info>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
            .addComponent(txtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblInfo)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfo))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        String regex = txtSearch.getSearchRegex();
        TableRowSorter sorter = (TableRowSorter) resTable.getRowSorter();
        sorter.setRowFilter(RowFilter.regexFilter(regex, ResourceModel.COL_NAME));
    }//GEN-LAST:event_txtSearchKeyReleased

    private void resTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resTableMouseReleased
        if (evt.getButton() == MouseEvent.BUTTON3) {
            int row = resTable.rowAtPoint(evt.getPoint());
            resTable.setRowSelectionInterval(row, row);
        }
        int id = resTable.convertRowIndexToModel(resTable.getSelectedRow());
        if (evt.isPopupTrigger() && id > -1) {
            int x = evt.getX();
            int y = evt.getY();
            mnuEntry.show(evt.getComponent(), x, y);
        }
    }//GEN-LAST:event_resTableMouseReleased

    private void mnuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOpenActionPerformed
        if (selected != null) {
            String type = selected.getExt().toLowerCase();
            String name = selected.getNameWithoutExt();
            ResourceModel res = ResourceModel.getInstance();
            switch (type) {
                case "dff":
                    WorldFactory.unregister(viewer);
                    viewer = new RwModel(name);
                    res.extractModel(viewer);
                    viewer.construct(viewer.resource);
                    WorldFactory.register(viewer);
                    WorldFactory.focusTo(viewer);
                    break;
                    
                case "txd":
                    try (EntryStream es = new EntryStream(selected)) {
                        Unicore app = Unicore.getMainFrame();
                        app.openByDefault(es);
                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }
                    break;

            }
        }
    }//GEN-LAST:event_mnuOpenActionPerformed

    private void resTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resTableMouseClicked
        if (evt.getClickCount() >= 2) {
            mnuOpenActionPerformed(null);
            evt.consume();
        }
    }//GEN-LAST:event_resTableMouseClicked

    private void mnuDumpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDumpActionPerformed
        if (selected != null) {
            String type = selected.getExt().toLowerCase();
            if (type.equals("txd") || type.equals("dff")) {
                RwDumper dlg = RwDumper.getInstance();
                dlg.openEntry(selected);
                dlg.setVisible(true);
            }
        }
    }//GEN-LAST:event_mnuDumpActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JMenuItem mnuDump;
    private javax.swing.JPopupMenu mnuEntry;
    private javax.swing.JMenuItem mnuOpen;
    private javax.swing.JTable resTable;
    private com.openitvn.control.UCTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
