package com.nostalgiamaps.commands;

import com.nostalgiamaps.MapInstance;
import com.nostalgiamaps.NostalgiaMaps;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MapsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length == 0) {
                NostalgiaMaps.getInstance().getInventoryManager().openMapsInventory(p);
                return true;
            } else {
                if (args[0].equalsIgnoreCase("add")) {
                    if (p.getName().equals(NostalgiaMaps.getInstance().getConfigManager().getOwnerName()) || p.isOp()) {
                        if (args.length >= 2) {
                            NostalgiaMaps.getInstance().getMapsManager().addMap(new MapInstance(args[1],
                                    args[2] != null && (args[2].equalsIgnoreCase("true") ||
                                            args[2].equalsIgnoreCase("false")) && Boolean.parseBoolean(args[2])
                            ));
                        } else {
                            p.sendMessage("§f§lERROR  §r§cUsage: /maps add <mapUrl> [optional: true|false]");
                        }
                    } else {
                        p.sendMessage("§f§lERROR  §r§cYou don't have permission to use this command.");
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (p.getName().equals(NostalgiaMaps.getInstance().getConfigManager().getOwnerName()) || p.isOp()) {
                        if (args.length == 2) {
                            NostalgiaMaps.getInstance().getMapsManager().removeMap(NostalgiaMaps.getInstance().getMapsManager().getMapByName(args[1]));
                        } else {
                            p.sendMessage("§f§lERROR  §r§cUsage: /maps remove <mapName>");
                        }
                    } else {
                        p.sendMessage("§f§lERROR  §r§cYou don't have permission to use this command.");
                    }
                } else if (args[0].equalsIgnoreCase("info")) {
                    TextComponent message = new TextComponent("§e§nNostalgiaMaps §r§fv" +
                            NostalgiaMaps.getInstance().getDescription().getVersion());
                    TextComponent author = new TextComponent(" §7by §b§oAchille Bourgault");

                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("§7Rate this plugin on SpigotMC by clicking here").create()));
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                            "https://www.spigotmc.org/resources/nostalgiamaps.94887/"));
                    author.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("§7Need plugin development? Contact me on Discord: §b#Esporc").create()));
                    author.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                            "https://github.com/achillebourgault"));

                    p.spigot().sendMessage(message, author);
                    p.sendMessage("§f§lCOMMANDS\n");
                    p.sendMessage("§e/maps §7- §fOpens the maps inventory.");
                    p.sendMessage("§e/maps add <mapUrl> (true: optional) §7- §fAdds a map to the maps library.");
                    p.sendMessage("§e/maps remove <mapName> §7- §fRemoves a map from the maps library.");
                    p.sendMessage("§e/maps info §7- §fShows this message.");
                    p.sendMessage("");
                } else {
                    p.sendMessage("§f§lERROR  §r§cUnknown command. Type /maps info for help.");
                }
            }
        }
        return true;
    }
}
