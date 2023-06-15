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
        return config.getString("owner");
    }

    public String getMapDisplayName() {
        return config.getString("map.selected_map_display_name");
    }

    public String getMapName() {
        return config.getString("map.selected_map");
    }

    public String getMapUrl() {
        return config.getString("map.selected_map_url");
    }
}
