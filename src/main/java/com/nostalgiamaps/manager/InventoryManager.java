/*********************************************************\
*   @Author: AchilleBourgault                             *
*   @Github: https://github.com/achillebourgault          *
*   @Project: NostalgiaMaps                               *
\*********************************************************/

package com.nostalgiamaps.manager;

import com.nostalgiamaps.MapInstance;
import com.nostalgiamaps.NostalgiaMaps;
import com.nostalgiamaps.utils.Item;
import com.nostalgiamaps.utils.Static;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private Inventory mapsInventory;
    private BukkitTask inventoryTask = null;
    private int mapInvAnimationIdx = 0;

    public InventoryManager() {
        initMapInventory();
        registerAsyncInventoryTask();
    }

    private void initMapInventory() {
        mapsInventory = Bukkit.createInventory(null, 54, Static.InventoryMapsName);

        for (int i = 0; i < 9; i++) {
            mapsInventory.setItem(i, Item.createItem(" ", 1, Material.BLACK_STAINED_GLASS_PANE, null));
            mapsInventory.setItem(i + 45, Item.createItem(" ", 1, Material.BLACK_STAINED_GLASS_PANE, null));
        }
        for (int i = 9; i < 54; i += 9) {
            mapsInventory.setItem(i, Item.createItem(" ", 1, Material.BLACK_STAINED_GLASS_PANE, null));
            mapsInventory.setItem(i + 8, Item.createItem(" ", 1, Material.BLACK_STAINED_GLASS_PANE, null));
        }
    }

    private void registerAsyncInventoryTask() {
        inventoryTask = Bukkit.getScheduler().runTaskTimer(NostalgiaMaps.getInstance(), () -> {
            Inventory currentInventory = NostalgiaMaps.getInstance().getInventoryManager().getMapsInventory();
            ArrayList<MapInstance> mapsPool = NostalgiaMaps.getInstance().getMapsManager().getMapsPool();
            boolean isMapPoolEmpty = mapsPool.isEmpty() || mapsPool.stream().noneMatch(map -> map.getLoadStatus().equals(MapInstance.LoadStatus.LOADED));

            if (isMapPoolEmpty) {
                currentInventory.setItem(22, Item.createItem("§cNo maps available. Click below to add one.",
                        1, Material.BARRIER, null));
            } else {
                int startIndex = 10;
                int rowLength = 7;
                int id = 1;

                for (int i = 0; i < mapsPool.size(); i++) {
                    MapInstance map = mapsPool.get(i);
                    List<String> lore = generateMapDescription(map);

                    currentInventory.setItem(startIndex + i, Item.createItem("§f§l#" + id + " §e" +
                            map.getDisplayName(), id++, Material.FILLED_MAP, lore));
                    if (i % rowLength == 0 && i != 0) startIndex += 9;
                }

                currentInventory.setItem(41, Item.createItem("§7Current map: §f§l#" + id + " §e" +
                                NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getDisplayName(),
                        id, Material.FILLED_MAP, null));
                currentInventory.setItem(49, Item.createItem("§cExit", 1, Material.BARRIER, null));
            }

            if (NostalgiaMaps.getInstance().getInventoryManager().getMapInvAnimationIdx() == 2) {
                NostalgiaMaps.getInstance().getInventoryManager().setMapInvAnimationIdx(0);
            } else {
                NostalgiaMaps.getInstance().getInventoryManager().setMapInvAnimationIdx(
                        NostalgiaMaps.getInstance().getInventoryManager().getMapInvAnimationIdx() + 1);
            }
        }, 0, 10);
    }

    public List<String> generateMapDescription(MapInstance map) {
        ArrayList<String> desc = new ArrayList<>();
        String[] dots = {".", "..", "..."};
        String[] exclamation  = {"!!!", "!!", "!!!"};

        desc.add("");
        desc.add("§f§lSTATUS");
        switch (map.getLoadStatus()) {
            case READY_TO_LOAD:
                desc.add("§7Ready to load §f(Click to load)");
                break;
            case LOADING:
                desc.add("§7Loading" + dots[mapInvAnimationIdx]);
                break;
            case LOADED:
                desc.add("§aLoaded" + (!NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getName().equals(map.getName()) ?
                        " & Ready to be played" : "§e (Current map)"));
                break;
            case ERROR:
                desc.add("§c" + exclamation[mapInvAnimationIdx] + " Error " + exclamation[mapInvAnimationIdx]);
                break;
        }
        desc.add("");
        desc.add("§f§lURL");
        desc.add("§7" + map.getUrl());
        desc.add("");
        desc.add("§f§lWORLD CONTAINER");
        desc.add("§7" + map.getName());
        desc.add("");
        return desc;
    }

    public void openMapsInventory(Player p) {
        p.playSound(p.getLocation(), Sound.ENTITY_SHULKER_OPEN, 1, 1);
        p.openInventory(mapsInventory);
    }

    public Inventory getMapsInventory() {
        return mapsInventory;
    }

    public BukkitTask getInventoryTask() {
        return inventoryTask;
    }

    public int getMapInvAnimationIdx() {
        return mapInvAnimationIdx;
    }

    public void setMapInvAnimationIdx(int mapInvAnimationIdx) {
        this.mapInvAnimationIdx = mapInvAnimationIdx;
    }
}
