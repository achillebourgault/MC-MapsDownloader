package com.nostalgiamaps.events;

import com.nostalgiamaps.NostalgiaMaps;
import com.nostalgiamaps.manager.MapsManager;
import com.nostalgiamaps.utils.Static;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryEvents implements Listener {

    @EventHandler // Maps Selector Inventory
    public void onPlayerInteractInventory(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();


        if (!e.getView().getOriginalTitle().equals(Static.InventoryMapsName))
            return;
        e.setCancelled(true);

        if (item.getType().equals(Material.FILLED_MAP)) {
            p.closeInventory();
            String mapName = item.getItemMeta().getDisplayName().split(" ", 2)[1];
            mapName = mapName.replaceAll("[^a-zA-Z0-9_]", "");

            p.sendMessage("§eYou have selected the map §6[" + mapName + "]§e.");

            if (NostalgiaMaps.getInstance().getMapsManager().isPlayerHasPrivilege(p)) {
                NostalgiaMaps.getInstance().getMapsManager().setCurrentMap(
                        NostalgiaMaps.getInstance().getMapsManager().getMapByName(mapName)
                );
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.teleport(NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getWorld().getSpawnLocation());
                    player.sendMessage("§7You are being teleported to the map §e" + NostalgiaMaps.getInstance().getMapsManager()
                            .getCurrentMap().getDisplayName() + "§7.");
                }
            } else {
                //TODO: Voting system
            }
        } else if (item.getType().equals(Material.BARRIER)) {
            p.closeInventory();
            p.performCommand("map add");
        }

    }
}
