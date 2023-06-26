package com.nostalgiamaps.commands;

import com.nostalgiamaps.MapInstance;
import com.nostalgiamaps.NostalgiaMaps;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

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
                    if (NostalgiaMaps.getInstance().getMapsManager().isPlayerHasPrivilege(p)) {
                        if (args.length >= 2) {
                            if (NostalgiaMaps.getInstance().getMapsManager().getMapByName(args[1]) != null) {
                                p.sendMessage("§f§lERROR  §r§cMap '"+args[1]+"' already exist.");
                                return true;
                            }
                            p.sendMessage("§ePlease wait while the map is being downloaded...");
                            new MapInstance(args[1],
                                    args.length == 3 && (args[2].equalsIgnoreCase("true") ||
                                            args[2].equalsIgnoreCase("false")) && Boolean.parseBoolean(args[2])
                            );
                        } else {
                            p.sendMessage("§f§lERROR  §r§cUsage: /maps add <mapUrl> [optional: true|false]");
                        }
                    } else {
                        p.sendMessage("§f§lERROR  §r§cYou don't have permission to use this command.");
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (NostalgiaMaps.getInstance().getMapsManager().isPlayerHasPrivilege(p)) {
                        if (args.length == 2) {
                            if (NostalgiaMaps.getInstance().getMapsManager().getMapByName(args[1]) == null) {
                                p.sendMessage("§f§lERROR  §r§cMap '"+args[1]+"' doesn't exist.");
                                return true;
                            }
                            NostalgiaMaps.getInstance().getMapsManager().removeMap(NostalgiaMaps.getInstance().getMapsManager().getMapByName(args[1]));
                        } else {
                            p.sendMessage("§f§lERROR  §r§cUsage: /maps remove <mapName>");
                        }
                    } else {
                        p.sendMessage("§f§lERROR  §r§cYou don't have permission to use this command.");
                    }
                } else if (args[0].equalsIgnoreCase("load")) {
                    if (NostalgiaMaps.getInstance().getMapsManager().isPlayerHasPrivilege(p)) {
                        if (args.length == 2) {
                            if (NostalgiaMaps.getInstance().getMapsManager().getMapByName(args[1]) == null) {
                                p.sendMessage("§f§lERROR  §r§cMap '"+args[1]+"' doesn't exist.");
                                return true;
                            }
                            // TODO: !!!
                            // NostalgiaMaps.getInstance().getMapsManager().getMapByName(args[1]).load(false);
                        } else {
                            p.sendMessage("§f§lERROR  §r§cUsage: /maps remove <mapName>");
                        }
                    } else {
                        p.sendMessage("§f§lERROR  §r§cYou don't have permission to use this command.");
                    }
                } else if (args[0].equalsIgnoreCase("vote")) {
                    if (args.length == 1) {
                        p.sendMessage("§f§lERROR  §r§cUsage: /maps vote <mapName>");
                    } else {

                        if (NostalgiaMaps.getInstance().getMapsManager().getMapByName(args[1]) == null) {
                            if (args[1].equalsIgnoreCase("start") || args[1].equalsIgnoreCase("stop")) {
                                boolean isStart = args[1].equalsIgnoreCase("start");

                                if (NostalgiaMaps.getInstance().getMapsManager().isPlayerHasPrivilege(p)) {
                                    p.sendMessage("§f§lERROR  §r§cYou don't have permission to use this command.");
                                    return true;
                                }

                                if (isStart) {
                                    if (NostalgiaMaps.getInstance().getVotingManager().getVotingTask() != null) {
                                        p.sendMessage("§f§lERROR  §r§cVoting is already started.");
                                    } else
                                        NostalgiaMaps.getInstance().getVotingManager().startNewVote(args.length == 3 ?
                                                args[2] : null);
                                } else {
                                    if (NostalgiaMaps.getInstance().getVotingManager().getVotingTask() == null) {
                                        p.sendMessage("§f§lERROR  §r§cThere is no voting in progress.");
                                    } else
                                        NostalgiaMaps.getInstance().getVotingManager().forceStopVote();
                                }
                            } else {
                                p.sendMessage("§f§lERROR  §r§cMap '"+args[1]+"' doesn't exist.");
                            }
                        } else {
                            if (NostalgiaMaps.getInstance().getVotingManager().getVotingTask() == null) {
                                p.sendMessage("§f§lVOTE  §eThere is no voting in progress.");
                                p.sendMessage("§fNew voting started for the map §e"+NostalgiaMaps.getInstance()
                                        .getMapsManager().getMapByName(args[1]).getDisplayName()+" §f.");
                                NostalgiaMaps.getInstance().getVotingManager().startNewVote(args[1]);
                            } else {
                                if (NostalgiaMaps.getInstance().getMapsManager().getMapByName(args[1]) == null) {
                                    p.sendMessage("§f§lERROR  §r§cMap '"+args[1]+"' doesn't exist.");
                                    return true;
                                }
                                NostalgiaMaps.getInstance().getVotingManager().addVote(p, args[1]);
                            }
                        }

                    }
                } else if (args[0].equalsIgnoreCase("info")) {
                    sendPluginInfo(p);
                } else if (args[0].equalsIgnoreCase("start")) {
                    if (args.length == 1) {
                        p.sendMessage("§f§lERROR  §r§cUsage: /maps start <mapName>");
                    } else {
                        if (NostalgiaMaps.getInstance().getMapsManager().getMapByName(args[1]) == null) {
                            p.sendMessage("§f§lERROR  §r§cMap '"+args[1]+"' doesn't exist.");
                            return true;
                        }
                        NostalgiaMaps.getInstance().getMapsManager().setCurrentMap(
                                NostalgiaMaps.getInstance().getMapsManager().getMapByName(args[1])
                        );
                        if (NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getWorld() == null) {
                            p.sendMessage("§f§lERROR  §r§cMap '"+args[1]+"' is not loaded.");
                            p.sendMessage("§e§nINFO  §r§eLoading map...");
                            NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().createWorld();
                            return true;
                        }
                        for (Player player : Bukkit.getOnlinePlayers())
                            player.teleport(NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getWorld().getSpawnLocation());
                    }
                } else {
                    p.sendMessage("§f§lERROR  §r§cUnknown command. Type /maps info for help.");
                }
            }
        }
        return true;
    }

    // TODO: Replace links
    private void sendPluginInfo(Player p) {
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
        p.sendMessage("§f§lCOMMANDS §7§m-----§r\n");
        p.sendMessage("§e/maps §7- §fOpens the maps inventory.");
        p.sendMessage("§e/maps add <mapUrl> §7[true|false: §fOptional Load Immediately option§7] - §fAdds a map to the maps library.");
        p.sendMessage("§e/maps load <mapName> §7- §fLoads a map from the maps library.");
        p.sendMessage("§e/maps remove <mapName> §7- §fRemoves a map from the maps library.");
        p.sendMessage("§e/maps info §7- §fShows this message.");
        p.sendMessage("§e/maps vote <start|stop> §7- §fStarts or stops a vote for the next map.");
        p.sendMessage("");
    }

    public static TabCompleter getTabCompleter() {
        return (sender, cmd, alias, args) -> {
            if (args.length == 1) {
                return Arrays.asList("add", "remove", "info", "load", "vote", "start");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("vote"))
                    return Arrays.asList("start", "stop");
                else if (args[0].equalsIgnoreCase("start")) {
                    ArrayList<String> mapNames = new ArrayList<>();

                    for (MapInstance map : NostalgiaMaps.getInstance().getMapsManager().getMapsPool())
                        mapNames.add(map.getName());
                    return mapNames;
                }
            } else if (args.length == 3) {
                // Handle the optional argument for the add command
                if (args[0].equalsIgnoreCase("add"))
                    return Arrays.asList("true", "false");
            }
            return null;
        };
    }
}
