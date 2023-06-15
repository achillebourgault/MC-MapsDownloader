package com.nostalgiamaps.manager;

import com.nostalgiamaps.NostalgiaMaps;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

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
        return Objects.equals(config.getString("owner"), "none") ? null : config.getString("owner");
    }

    public String getMapDisplayName() {
        return Objects.equals(config.getString("map.selected_map_display_name"), "none") ? null : config.getString("map.selected_map_display_name");
    }

    public String getMapName() {
        return Objects.equals(config.getString("map.selected_map"), "none") ? null : config.getString("map.selected_map");
    }

    public String getMapUrl() {
        return config.getString("map.selected_map_url") == "none" ? null : config.getString("map.selected_map_url");
    }
}
