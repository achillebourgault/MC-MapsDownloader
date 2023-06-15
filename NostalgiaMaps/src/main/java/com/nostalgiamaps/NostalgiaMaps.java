package com.nostalgiamaps;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class NostalgiaMaps extends JavaPlugin {

    private static NostalgiaMaps instance;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static NostalgiaMaps getInstance() {
        return instance;
    }
}
