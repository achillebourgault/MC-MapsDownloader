package com.nostalgiamaps.events;

import com.nostalgiamaps.utils.Logs;
import com.nostalgiamaps.MapInstance;
import com.nostalgiamaps.NostalgiaMaps;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class onJoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String ownerName = NostalgiaMaps.getInstance().getConfigManager().getOwnerName();
        String defaultOwner = Bukkit.getOperators().iterator().next().getName();

        // Set default owner as first player if no owner found in config.yml and no operator found
        if (ownerName == null) {
            if (defaultOwner == null) {
                defaultOwner = p.getName();
                ownerName = defaultOwner;
            }
            NostalgiaMaps.getInstance().getConfig().set("owner", defaultOwner);
            NostalgiaMaps.getInstance().saveConfig();
            Logs.send("No owner found in config.yml. Setting default owner as " + defaultOwner + ".", Logs.LogType.INFO, Logs.LogPrivilege.OPS);
            Logs.send("You're now the server owner.", Logs.LogType.WARNING, Logs.LogPrivilege.EXACT_PLAYER, defaultOwner);
        }

        // If owner join the server
        if (p.getUniqueId() == Bukkit.getOfflinePlayer(ownerName).getUniqueId()) {
            p.sendMessage("§7Welcome to the server !");
            p.sendMessage("");
            if (NostalgiaMaps.getInstance().getMapsManager().getCurrentMap() == null) {
                TextComponent message1 = new TextComponent("§7You're the server owner. " +
                        "You can now choose a map by clicking here or with §e/map choose <map name>§7.");
                TextComponent message2 = new TextComponent("§f§nclicking here");
                TextComponent message3 = new TextComponent("§7 or with §e/maps§7.");

                message2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("\n§7Click here to choose a map.\n").create()));
                p.spigot().sendMessage(message1, message2, message3);
            } else {
                p.sendMessage("§7Selected map: §e" + NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getDisplayName() + "§7.");
            }
        } else {
            p.sendMessage("§7Welcome to the server of §e" + NostalgiaMaps.getInstance().getConfigManager().getOwnerName() + "§7!");
            p.sendMessage("");

            // If player join the server and no map is selected or loaded
            if (NostalgiaMaps.getInstance().getMapsManager().getCurrentMap() == null &&
                    NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getLoadStatus() != MapInstance.LoadStatus.LOADED) {
                if (p.getUniqueId() != Bukkit.getOfflinePlayer(ownerName).getUniqueId())
                    p.sendMessage("§7Wait for the server owner to choose a map.");
                p.teleport(Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation());
            } else {
                if (!p.getWorld().getName().equals(NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getWorld().getName())) {
                    p.sendMessage("§7You are being teleported to the map §e" + NostalgiaMaps.getInstance().getMapsManager()
                            .getCurrentMap().getDisplayName() + "§7.");
                    p.teleport(NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getWorld().getSpawnLocation());
                }
            }
        }
    }
}
