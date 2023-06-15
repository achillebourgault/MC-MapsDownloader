package com.nostalgiamaps.events;

import com.nostalgiamaps.utils.Static;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InteractEvent implements Listener {

    @EventHandler // Maps Selector Inventory
    public void onPlayerInteractInventory(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (!e.getView().getTitle().equals(Static.InventoryMapsName))
            return;
        e.setCancelled(true);

    }
}
