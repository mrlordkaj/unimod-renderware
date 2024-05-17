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
package com.openitvn.unicore.plugin.gta;

import com.openitvn.format.dff.RwWorld;
import com.openitvn.unicore.Workspace;
import com.openitvn.format.img.RwArchive;
import com.openitvn.maintain.Logger;
import com.openitvn.unicore.archive.IArchiveEntry;
import com.openitvn.unicore.data.EntryStream;
import com.openitvn.unicore.world.INode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Thinh Pham
 */
public class ResourceModel extends AbstractTableModel {
    
    public static final String[] COLUMNS = { "", "Name", "Size" };
    public static final int COL_INDEX = 0;
    public static final int COL_NAME = 1;
    public static final int COL_SIZE = 2;
    
    private static ResourceModel instance;
    
    public static ResourceModel getInstance() {
        if (instance == null) {
            instance = new ResourceModel();
        }
        return instance;
    }
    
    final ArrayList<IArchiveEntry> entries;
    final ArrayList<WorldScriptEntry> scripts;
    final HashMap<String, String> dffTxdMap;
    
    private ResourceModel() {
        entries = new ArrayList<>();
        scripts = new ArrayList<>();
        dffTxdMap = new HashMap<>();
    }
    
    private boolean isScriptAbsent(String name) {
        for (WorldScriptEntry e : scripts) {
            if (e.path.equals(name)) {
                return false;
            }
        }
        return true;
    }
    
    void load(Workspace space) {
        entries.clear();
        scripts.clear();
        dffTxdMap.clear();
        if (!GameConfig.ALIAS_III.equals(space.name) &&
                !GameConfig.ALIAS_VC.equals(space.name) &&
                !GameConfig.ALIAS_SA.equals(space.name)) {
            return;
        }
        LinkedHashMap<String, IArchiveEntry> entryMap = new LinkedHashMap<>();
        // load resources from main archive
        for (String arc : GameConfig.getMainArchives()) {
            loadImg(arc, entryMap);
        }
        // override or load addition resources from loaders
        for (String loader : GameConfig.getLoaders()) {
            Logger.printNotice("Executing loader: %1$s", loader);
            try (InputStream is = new FileInputStream(space.location + loader);
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] args = line.replaceAll("#.*$", "").trim().split("\\s+");
                    switch (args[0]) {
                        case "IDE":
                            if (isScriptAbsent(args[1])) {
                                WorldScriptEntry script = new WorldScriptEntry(args[1], WorldScriptType.IDE);
                                parseDffTxdMap(script.file);
                                scripts.add(script);
                            }
                            break;
                            
                        case "IPL":
                            if (isScriptAbsent(args[1])) {
                                scripts.add(new WorldScriptEntry(args[1], WorldScriptType.IPL));
                            }
                            break;
                            
                        case "IMG":
                        case "TEXDICTION":
                        case "MODELFILE":
                        case "MAPZONE":
                            loadImg(args[1], entryMap);
                            break;
                            
                        case "COLFILE":
                            loadImg(args[2], entryMap);
                            break;
                            
                        case "SPLASH":
                            continue;
                            
                        default:
                            if (args.length > 1)
                                Logger.printWarning(line);
                            continue;
                    }
                    Logger.printNormal(line);
                }
            } catch (IOException ex) {
                Logger.printError("Failed executing: " + loader);
            }
        }
        entries.addAll(entryMap.values());
        switch (space.name) {
            case GameConfig.ALIAS_III:
                dffTxdMap.put("wheels", "misc");
                break;
                
            case GameConfig.ALIAS_VC:
            case GameConfig.ALIAS_SA:
                dffTxdMap.put("wheels", "wheels");
                break;
        }
        dffTxdMap.put("weapons", "misc");
        fireTableDataChanged();
        System.gc();
    }
    
    private void parseDffTxdMap(File ide) {
        try (InputStream is = new FileInputStream(ide);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr)) {
            String line;
            String[] args;
            while ((line = ScriptHelper.readLine(br)) != null) {
                switch (line) {
                    case "objs":
                    case "cars":
                        while ((args = ScriptHelper.parseLineByComma(br)) != null) {
                            if (args.length > 2) {
                                String dff = args[1].toLowerCase();
                                String txd = args[2].toLowerCase();
                                dffTxdMap.put(dff, txd);
                            }
                        }
                        break;
                }
            }
        } catch (IOException ex) { }
    }
    
    private boolean loadImg(String revPath, LinkedHashMap<String, IArchiveEntry> entryMap) {
        try {
            RwArchive arc = new RwArchive();
            arc.open(GameConfig.getDirectory() + "/" + revPath);
            for (IArchiveEntry e : arc.entries) {
                entryMap.put(e.getName().toLowerCase(), e);
            }
            return true;
        } catch (IOException ex) {
            Logger.printError("IMG not found: " + revPath);
            return false;
        }
    }
    
    Collection<INode> extractModel(String modName, RwWorld target) {
        // try load dependency texDic
        String txdName = dffTxdMap.get(modName.toLowerCase());
        if (txdName == null) {
            txdName = modName;
        }
        try (EntryStream ts = getEntryStream(txdName, "txd")) {
            target.loadTexDic(ts);
        } catch (IOException ex) { }
        // try load model content
        try (EntryStream ms = getEntryStream(modName, "dff")) {
            return target.loadData(ms, false);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            return new ArrayList<>();
        }
    }
    
    public IArchiveEntry findEntry(String fullName) {
        for (IArchiveEntry e : entries) {
            if (e.getName().equalsIgnoreCase(fullName)) {
                return e;
            }
        }
        return null;
    }
    
    public EntryStream getEntryStream(String name, String ext) throws IOException {
        IArchiveEntry e = findEntry(name+"."+ext);
        return new EntryStream(e);
    }
    
    @Override
    public int getRowCount() {
        return entries.size();
    }
    
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }
    
    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }
    
    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COL_INDEX:
            case COL_SIZE:
                return Integer.class;
                
            case COL_NAME:
                return String.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        IArchiveEntry e = entries.get(row);
        switch (col) {
            case COL_INDEX:
                return row;

            case COL_NAME:
                return e.getName();
                
            case COL_SIZE:
                long size = e.getSize();
                return size / 1024 + " kB";
        }
        return null;
    }
    
    @Deprecated
    public IArchiveEntry getEntry(int index) {
        return entries.get(index);
    }
}
