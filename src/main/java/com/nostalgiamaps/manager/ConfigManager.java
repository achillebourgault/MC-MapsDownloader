/*********************************************************\
*   @Author: AchilleBourgault                             *
*   @Github: https://github.com/achillebourgault          *
*   @Project: NostalgiaMaps                               *
\*********************************************************/

package com.nostalgiamaps.manager;

import com.nostalgiamaps.NostalgiaMaps;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private FileConfiguration config = NostalgiaMaps.getInstance().getConfig();

    public ConfigManager() {
        loadConfig();
    }

    private void loadConfig() {
        config.options().copyDefaults(false);
        NostalgiaMaps.getInstance().saveConfig();
    }

    public String getOwnerName() {
        return "none".equals(config.getString("owner")) || "ops".equals(config.getString("owner")) ? null : config.getString("owner");
    }

    public String getMapDisplayName() {
        return "none".equals(config.getString("map.selected_map_display_name")) ? null : config.getString("map.selected_map_display_name");
    }

    public String getMapName() {
        return "none".equals(config.getString("map.selected_map")) ? null : config.getString("map.selected_map");
    }

    public String getMapUrl() {
        return "none".equals(config.getString("map.selected_map_url")) ? null : config.getString("map.selected_map_url");
    }
}
