/*********************************************************\
*   @Author: AchilleBourgault                             *
*   @Github: https://github.com/achillebourgault          *
*   @Project: NostalgiaMaps                               *
\*********************************************************/

package com.nostalgiamaps.manager;

import com.nostalgiamaps.MapInstance;
import com.nostalgiamaps.NostalgiaMaps;
import com.nostalgiamaps.utils.Logs;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MapsManager {

    private final MapInstance currentMap;
    private final ArrayList<MapInstance> mapsPool = new ArrayList<>();
    private final ArrayList<MapInstance> mapsQueue = new ArrayList<>();
    private String tempOwnerPlayername = null;

    public MapsManager() {
        this.currentMap = null;
    }

    public void addMap(MapInstance map) {
        Bukkit.getScheduler().runTaskAsynchronously(NostalgiaMaps.getInstance(), () -> {
            while (map.getLoadStatus() != MapInstance.LoadStatus.LOADED && map.getLoadStatus() != MapInstance.LoadStatus.ERROR) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mapsPool.add(map);
            for (Player p : Bukkit.getOnlinePlayers())
                p.sendMessage("§eMap '"+map.getDisplayName()+"' has been added to the maps library.");
        });
    }

    public void removeMap(MapInstance map) {
        try {
            NostalgiaMaps.getInstance().getServer().getWorld(map.getName()).getWorldFolder().delete();
            mapsPool.remove(map);
            Logs.send("Map '"+map.getName()+"' folder has been deleted.", Logs.LogType.INFO, Logs.LogPrivilege.OPS);
        } catch (Exception e) {
            Logs.send("Error while deleting map '"+map.getName()+"' folder: "
                    + e.getMessage(), Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
        }
    }

    public MapInstance getMapByName(String mapName) {
        for (MapInstance map : mapsPool) {
            if (map.getName().equals(mapName))
                return map;
        }
        return null;
    }

    public MapInstance chooseRandomMap() {
        return mapsPool.get((int) (Math.random() * mapsPool.size()));
    }

    public ArrayList<MapInstance> getMapsPool() {
        return mapsPool;
    }

    public MapInstance getCurrentMap() {
        return currentMap;
    }

    public ArrayList<MapInstance> getMapsQueue() {
        return mapsQueue;
    }

    public void addMapToQueue(MapInstance map) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("§f§lMAPS QUEUE  §eMap '" + map.getDisplayName() + "' has been added to the maps queue.");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
        mapsQueue.add(map);
    }

    public void removeMapFromQueue(MapInstance map) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("§f§lMAPS QUEUE  §eMap '" + map.getDisplayName() + "' has been removed from the maps queue.");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 1, 1);
        }
        mapsQueue.remove(map);
    }

    public String getTempOwnerPlayername() {
        return tempOwnerPlayername;
    }

    public void setTempOwnerPlayername(String tempOwnerPlayername) {
        this.tempOwnerPlayername = tempOwnerPlayername;
    }

    public boolean isPlayerHasPrivilege(Player p) {
        return
                p.getName().equals(NostalgiaMaps.getInstance().getConfigManager().getOwnerName()) ||
                p.getName().equals(NostalgiaMaps.getInstance().getMapsManager().getTempOwnerPlayername()) ||
                "none".equals(NostalgiaMaps.getInstance().getConfigManager().getOwnerName()) || p.isOp();
    }

}
