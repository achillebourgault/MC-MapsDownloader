/*********************************************************\
*   @Author: AchilleBourgault                             *
*   @Github: https://github.com/achillebourgault          *
*   @Project: NostalgiaMaps                               *
\*********************************************************/

package com.nostalgiamaps.events;

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
            String mapName = item.getItemMeta().getDisplayName().split(" ")[1];
            p.sendMessage("§eYou have selected the map §6[" + mapName + "]§e.");

            p.teleport(Bukkit.getWorld(mapName).getSpawnLocation());
        } else if (item.getType().equals(Material.BARRIER)) {
            p.closeInventory();
            p.performCommand("map add");
        }

    }
}
