package com.nostalgiamaps.manager;

import com.nostalgiamaps.NostalgiaMaps;
import com.nostalgiamaps.utils.Logs;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class VotingManager {

    private HashMap<Player, String> votes;
    private boolean isVotingActive = false;
    private BukkitTask votingTask = null;
    private int votingTime = -1;

    public VotingManager() {
        this.votes = new HashMap<>();
        try {
            this.votingTime = NostalgiaMaps.getInstance().getConfigManager().getVotingSystemConfig().getVotingDuration();
        } catch (NumberFormatException e) {
            Logs.send("Voting Time in config.yml is not a number.", Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
        }
        if (!NostalgiaMaps.getInstance().getConfigManager().getVotingSystemConfig().isActive())
            Logs.send("Voting System is disabled in config.yml", Logs.LogType.INFO, Logs.LogPrivilege.CONSOLE);
    }

    public void startNewVote(String mapName) {
        TextComponent message;
        TextComponent clickHere = new TextComponent("§e§n§lCLICK HERE");

        resetVotes();
        startVotingTask();
        if (mapName == null) {
            clickHere.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click here to vote for the next map").create()));
            clickHere.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/maps vote"));
            message = new TextComponent("§f§lVOTE  §r§7A new vote has started. §7Vote for the next map with §f/maps vote §7or ");
        } else {
            clickHere.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click here to vote for §f"+mapName+"§7").create()));
            clickHere.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/maps vote"));
            message = new TextComponent("§f§lVOTE  §r§7A new map has been downloaded. §7Vote using §f/maps vote§7 to play it next or ");
        }

        for (Player pls : Bukkit.getOnlinePlayers()) {
            pls.playSound(pls.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            pls.spigot().sendMessage(message, clickHere);
        }
    }

    public void startVotingTask() {
        if (this.votingTime == -1) // If voting config not valid
            return;
        this.votingTask = Bukkit.getScheduler().runTaskLater(NostalgiaMaps.getInstance(), () -> {
            NostalgiaMaps.getInstance().getVotingManager().stopVote();
        }, this.votingTime * 20L);
    }

    public void stopVote() {
        if (this.votes.size() == 0) {
            for (Player pls : Bukkit.getOnlinePlayers())
                pls.sendMessage("§f§lVOTE  §r§7No one voted. §7The next map will be chosen randomly.");
            NostalgiaMaps.getInstance().getMapsManager().addMapToQueue(NostalgiaMaps.getInstance().getMapsManager().chooseRandomMap());
        } else {
            String winningMap = getWinningMap();
            for (Player pls : Bukkit.getOnlinePlayers())
                pls.sendMessage("§f§lVOTE  §r§7The next map will be §f"+winningMap+"§7.");
            NostalgiaMaps.getInstance().getMapsManager().addMapToQueue(NostalgiaMaps.getInstance().getMapsManager()
                    .getMapByName(winningMap));
        }
    }

    public void forceStopVote() {
        if (this.votingTask == null)
            return;
        this.votingTask.cancel();
        stopVote();
    }

    public void resetVotes() {
        this.votes = new HashMap<>();
        this.isVotingActive = false;
        this.votingTime = NostalgiaMaps.getInstance().getVotingManager().getVotingTime();
    }

    public void addVote(Player p, String vote) {
        String message;

        if (this.votes.containsKey(p)) {
            this.votes.replace(p, vote);
            message = "§f§lVOTE  §r§e"+ p.getName() +" §7changed his vote for §f"+vote + "§7.";
        } else {
            this.votes.put(p, vote);
            message = "§f§lVOTE  §r§e"+ p.getName() +" §7voted for §f"+vote + "§7.";
        }
        for (Player pls : Bukkit.getOnlinePlayers())
            pls.sendMessage(message);
    }

    public String getWinningMap() {
        String winner = null;
        HashMap<String, Integer> mapVotes = new HashMap<>();

        for (String map : this.votes.values()) {
            if (mapVotes.containsKey(map))
                mapVotes.replace(map, mapVotes.get(map) + 1);
            else
                mapVotes.put(map, 1);
        }
        for (String map : mapVotes.keySet()) {
            if (winner == null)
                winner = map;
            else if (mapVotes.get(map) > mapVotes.get(winner))
                winner = map;
        }
        return winner;
    }

    public HashMap<Player, String> getVotes() {
        return votes;
    }

    public boolean isVotingActive() {
        return isVotingActive;
    }

    public BukkitTask getVotingTask() {
        return votingTask;
    }

    public void resetVotingTask() {
        this.votingTask = null;
    }

    public int getVotingTime() {
        return votingTime;
    }
}
