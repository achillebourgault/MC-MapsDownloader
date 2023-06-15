package com.nostalgiamaps;

import com.nostalgiamaps.events.onJoinEvent;
import com.nostalgiamaps.manager.ConfigManager;
import com.nostalgiamaps.manager.MapsManager;
import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public final class NostalgiaMaps extends JavaPlugin {

    private static NostalgiaMaps instance;
    private ConfigManager config;
    private MapsManager mapsManager;

    @Override
    public void onEnable() {
        instance = this;
        System.out.println("Start Loading NostalgiaMaps../n");
        config = new ConfigManager();
        mapsManager = new MapsManager();
        Bukkit.getServer().getPluginManager().registerEvents(new onJoinEvent(), this);
        System.out.println("NostalgiaMaps has loaded!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static NostalgiaMaps getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return config;
    }

    public MapsManager getMapsManager() {
        return mapsManager;
    }
}
