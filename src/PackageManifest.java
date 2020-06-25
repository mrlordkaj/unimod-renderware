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


import com.openitvn.format.col.ColPack;
import com.openitvn.format.dff.RwModel;
import com.openitvn.format.img.RwArchive;
import com.openitvn.format.txd.RwTexturePack;
import com.openitvn.unicore.plugin.FileType;
import com.openitvn.unicore.plugin.PanelLocation;
import com.openitvn.unicore.plugin.PluginManifest;
import com.openitvn.unicore.plugin.gta.WorldPanel;
import com.openitvn.unicore.plugin.gta.ResourcePanel;
import com.openitvn.unicore.plugin.gta.VehiclePanel;

/**
 *
 * @author Thinh Pham
 */
public final class PackageManifest extends PluginManifest {
    
    public PackageManifest() {
        // TODO: define your supported file extensions here
        putFileView("RenderWare Archive", FileType.Archive, RwArchive.class, "img");
        putFileView("RenderWare Texture", FileType.Texture, RwTexturePack.class, "txd");
        putFileView("RenderWare Model", FileType.World, RwModel.class, "dff");
        putFileView("GTA Collision", FileType.World, ColPack.class, "col");
        
        // TODO: define your custom control panel here
        putControlPanel("GTA Resource", PanelLocation.Sidebar, ResourcePanel.class, true);
        putControlPanel("GTA World",    PanelLocation.Sidebar, WorldPanel.class,    true);
        putControlPanel("GTA Vehicle",  PanelLocation.Sidebar, VehiclePanel.class,  true);
    }

    @Override
    public String getId() {
        return "com.unimod.renderware";
    }

    @Override
    public String getName() {
        return "GTA Trilogy Pack";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}