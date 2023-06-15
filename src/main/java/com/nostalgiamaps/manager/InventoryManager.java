package com.nostalgiamaps.manager;

import com.nostalgiamaps.MapInstance;
import com.nostalgiamaps.NostalgiaMaps;
import com.nostalgiamaps.utils.Item;
import com.nostalgiamaps.utils.Static;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class InventoryManager {

    private Inventory mapsInventory;
    private BukkitTask inventoryTask = null;

    public InventoryManager() {
        initMapInventory();
        registerAsyncMapEvents();
    }

    private void initMapInventory() {
        mapsInventory = Bukkit.createInventory(null, 54, Static.InventoryMapsName);

        for (int i = 0; i < 9; i++) {
            mapsInventory.setItem(i, Item.createItem(" ", 1, Material.BLACK_STAINED_GLASS_PANE));
            mapsInventory.setItem(i + 45, Item.createItem(" ", 1, Material.BLACK_STAINED_GLASS_PANE));
        }
        for (int i = 9; i < 54; i += 9) {
            mapsInventory.setItem(i, Item.createItem(" ", 1, Material.BLACK_STAINED_GLASS_PANE));
            mapsInventory.setItem(i + 8, Item.createItem(" ", 1, Material.BLACK_STAINED_GLASS_PANE));
        }
    }

    private void registerAsyncMapEvents() {
        inventoryTask = Bukkit.getScheduler().runTaskTimer(NostalgiaMaps.getInstance(), () -> {
            Inventory currentInventory = NostalgiaMaps.getInstance().getInventoryManager().getMapsInventory();
            ArrayList<MapInstance> mapPool = NostalgiaMaps.getInstance().getMapsManager().getMapsPool();
            boolean isMapPoolEmpty = mapPool.isEmpty();

            if (isMapPoolEmpty) {
                currentInventory.setItem(22, Item.createItem("§cNo maps available. Click below to add one.",
                        1, Material.BARRIER));
            } else {
                int startIndex = 10;
                int rowLength = 7;
                int id = 1;

                for (int i = 0; i < mapPool.size(); i++) {
                    MapInstance map = mapPool.get(i);
                    currentInventory.setItem(startIndex + i, Item.createItem("§f§l#"+id+" §e"+map.getDisplayName(),
                            id++, Material.FILLED_MAP));
                    if (i % rowLength == 0 && i != 0) startIndex += 9;
                }

                currentInventory.setItem(49, Item.createItem("§7Current map: §f§l#"+id+" §e"+NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getDisplayName(),
                        id, Material.FILLED_MAP));
            }
        }, 0, 10);
    }

    public void openMapsInventory(Player p) {
        p.openInventory(mapsInventory);
    }

    public Inventory getMapsInventory() {
        return mapsInventory;
    }

    public BukkitTask getInventoryTask() {
        return inventoryTask;
    }
}
