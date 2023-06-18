package com.nostalgiamaps.manager;

import com.nostalgiamaps.NostalgiaMaps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

public class BoardManager {

    private HashMap<String, Scoreboard> availableScoreboards;
    private String selectedScoreboard;
    private BukkitTask boardTask;

    public BoardManager() {
        init();
        initBoards();
    }

    private void initBoards() {
        if (!NostalgiaMaps.getInstance().getConfigManager().getScoreboardConfig().isActive())
            initBoardTask();
    }

    private void initBoardTask() {
        this.boardTask = Bukkit.getScheduler().runTaskTimer(NostalgiaMaps.getInstance(), () -> {
            for (Player p : Bukkit.getOnlinePlayers())
                displayBoard();
        }, 0, 20);
    }

    public void displayBoard() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (this.selectedScoreboard == null) return;
            p.setScoreboard(this.availableScoreboards.get(this.selectedScoreboard));
        }
    }

    public void cancelBoardTask() {
        if (this.boardTask != null) {
            this.boardTask.cancel();
            this.boardTask = null;
        }
    }

    private void init() {
        this.availableScoreboards = new HashMap<>();
        this.selectedScoreboard = null;
    }

    public String getSelectedScoreboard() {
        return selectedScoreboard;
    }

    public void setSelectedScoreboard(String selectedScoreboard) {
        this.selectedScoreboard = selectedScoreboard;
    }
}
