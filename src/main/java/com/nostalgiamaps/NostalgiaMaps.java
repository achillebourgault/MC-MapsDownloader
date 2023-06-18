/*********************************************************\
*   @Author: AchilleBourgault                             *
*   @Github: https://github.com/achillebourgault          *
*   @Project: NostalgiaMaps                               *
\*********************************************************/

package com.nostalgiamaps;

import com.nostalgiamaps.commands.MapsCommand;
import com.nostalgiamaps.events.PlayerDamageEvent;
import com.nostalgiamaps.events.onInteractInventoryEvent;
import com.nostalgiamaps.events.onJoinEvent;
import com.nostalgiamaps.manager.*;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class NostalgiaMaps extends JavaPlugin {

    private static NostalgiaMaps instance;
    private ConfigManager config;
    private MapsManager mapsManager;
    private InventoryManager inventoryManager;
    private VotingManager votingManager;
    private BoardManager scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;
        config = new ConfigManager();
        mapsManager = new MapsManager();
        inventoryManager = new InventoryManager();
        votingManager = new VotingManager();

        registerEvents();
    }

    private void registerEvents() {
        Objects.requireNonNull(getCommand("maps")).setExecutor(new MapsCommand());
        Objects.requireNonNull(getCommand("maps")).setTabCompleter(MapsCommand.getTabCompleter());

        getServer().getPluginManager().registerEvents(new onJoinEvent(), this);
        getServer().getPluginManager().registerEvents(new onInteractInventoryEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageEvent(), this);
    }

    @Override
    public void onDisable() {
        if (getInventoryManager().getInventoryTask() != null)
            getInventoryManager().getInventoryTask().cancel();
        getScoreboardManager().cancelBoardTask();
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

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public VotingManager getVotingManager() {
        return votingManager;
    }

    public BoardManager getScoreboardManager() { return scoreboardManager; }
}
