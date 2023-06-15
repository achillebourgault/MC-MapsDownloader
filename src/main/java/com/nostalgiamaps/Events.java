package com.nostalgiamaps;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        p.sendMessage("§7Welcome to the server of §e" + NostalgiaMaps.getInstance().getConfigManager().getOwnerName() + "§7!");
        p.sendMessage("");
        if (NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getWorld() == null) {
            p.sendMessage("§7Wait for the server owner to choose a map.");
            p.teleport(Bukkit.getWorld("world").getSpawnLocation());
        } else {
            if (!p.getWorld().getName().equals(NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getWorld().getName())) {
                p.sendMessage("§7You are being teleported to the map §e" + NostalgiaMaps.getInstance().getMapsManager()
                        .getCurrentMap().getDisplayName() + "§7.");
                p.teleport(NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getWorld().getSpawnLocation());
            }
        }
    }
}
