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
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MapsManager {

    private final MapInstance currentMap;
    private final ArrayList<MapInstance> mapsPool = new ArrayList<MapInstance>();

    public MapsManager() {
        this.currentMap = null;
    }

    public void addMap(MapInstance map) {
        mapsPool.add(map);
        for (Player p : Bukkit.getOnlinePlayers())
            p.sendMessage("§eMap '"+map.getDisplayName()+"' has been added to the maps library.");
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

    public ArrayList<MapInstance> getMapsPool() {
        return mapsPool;
    }

    public MapInstance getCurrentMap() {
        return currentMap;
    }

    public MapInstance getMapByName(String mapName) {
        for (MapInstance map : mapsPool) {
            if (map.getName().equals(mapName))
                return map;
        }
        return null;
    }
}
