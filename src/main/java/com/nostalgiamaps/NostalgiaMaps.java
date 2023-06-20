/*********************************************************\
*   @Author: AchilleBourgault                             *
*   @Github: https://github.com/achillebourgault          *
*   @Project: NostalgiaMaps                               *
\*********************************************************/

package com.nostalgiamaps;

import com.nostalgiamaps.commands.MapsCommand;
import com.nostalgiamaps.events.LobbyEvents;
import com.nostalgiamaps.events.InventoryEvents;
import com.nostalgiamaps.events.ConnectionsEvents;
import com.nostalgiamaps.manager.*;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class NostalgiaMaps extends JavaPlugin {

    private static NostalgiaMaps instance;
    private ConfigManager config;
    private MapsManager mapsManager;
    private InventoryManager inventoryManager;
    private VotingManager votingManager;
    private BoardManager boardManager;

    @Override
    public void onEnable() {
        instance = this;
        config = new ConfigManager();
        mapsManager = new MapsManager();
        inventoryManager = new InventoryManager();
        votingManager = new VotingManager();
        boardManager = new BoardManager();

        registerEvents();
    }

    private void registerEvents() {
        Objects.requireNonNull(getCommand("maps")).setExecutor(new MapsCommand());
        Objects.requireNonNull(getCommand("maps")).setTabCompleter(MapsCommand.getTabCompleter());

        getServer().getPluginManager().registerEvents(new ConnectionsEvents(), this);
        getServer().getPluginManager().registerEvents(new InventoryEvents(), this);
        getServer().getPluginManager().registerEvents(new LobbyEvents(), this);
    }

    @Override
    public void onDisable() {
        if (getInventoryManager().getInventoryTask() != null)
            getInventoryManager().getInventoryTask().cancel();
        getBoardManager().cancelBoardTask();
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

    public BoardManager getBoardManager() { return boardManager; }
}
