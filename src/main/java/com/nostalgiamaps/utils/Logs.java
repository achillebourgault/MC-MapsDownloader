package com.nostalgiamaps.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Logs {

    public static enum LogType {
        INFO,
        WARNING,
        ERROR
    }

    public static enum LogPrivilege {
        LAMBDA_PLAYER,
        EXACT_PLAYER,
        OPS,
        CONSOLE
    }

    static String prefix = "§8[§6NostalgiaMaps§8] §7";

    public static void send(String message, LogType type, LogPrivilege privilege, String... playerNameIfExact) {
        switch (privilege) {
            case LAMBDA_PLAYER:
                for (Player player : Bukkit.getOnlinePlayers())
                    sendMessage(player, message, type);
                break;
                case EXACT_PLAYER:
                    sendMessage(Bukkit.getPlayer(playerNameIfExact[0]), message, type);
                    break;
                case OPS:
                    System.out.println(ChatColor.stripColor(message));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp())
                            sendMessage(player, message, type);
                    }
                    break;
                case CONSOLE:
                    System.out.println(ChatColor.stripColor(message));
                    break;
        }
    }

    public static void sendMessage(Player player, String message, LogType type) {
        switch (type) {
            default:
            case INFO:
                player.sendMessage(prefix + ChatColor.GRAY + message);
                break;
            case WARNING:
                player.sendMessage(prefix + ChatColor.YELLOW + message);
                break;
            case ERROR:
                player.sendMessage(prefix + ChatColor.RED + message);
                break;
        }
    }
}
