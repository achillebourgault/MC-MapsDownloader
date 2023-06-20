package com.nostalgiamaps.manager;

import com.nostalgiamaps.NostalgiaMaps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoardManager {

    private HashMap<String, Scoreboard> availableScoreboards;
    private String selectedScoreboard;
    private BukkitTask boardTask;

    public BoardManager() {
        this.availableScoreboards = new HashMap<>();
        this.selectedScoreboard = null;
        init();
    }

    private void init() {
        createMainMenuBoard();

        if (NostalgiaMaps.getInstance().getConfigManager().getScoreboardConfig().isActive()) {
            setSelectedScoreboard("main");
            this.boardTask = Bukkit.getScheduler().runTaskTimer(NostalgiaMaps.getInstance(), this::displayBoard, 0, NostalgiaMaps.getInstance().getConfigManager().getScoreboardConfig().getRefreshRate());
        }
    }

    private void createMainMenuBoard() {
        ConfigManager config = NostalgiaMaps.getInstance().getConfigManager();
        String configTitle = config.getScoreboardConfig().getTitle();
        String title = configTitle != null && configTitle.length() >= 2 ?
                configTitle.replace("&", "§") : "§6§lNostalgiaMaps";
        Scoreboard mainBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective mainObjective = mainBoard.registerNewObjective("main", "dummy", title);
        List<String> lines = new ArrayList<>();
        StringBuilder shift = new StringBuilder();

        for (int i = 0; i < config.getScoreboardConfig().getShift(); i++) shift.append(" ");
        mainObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

        lines.add("          ");

        if (config.getScoreboardConfig().isShowMapName()) {
            lines.add(shift+"§f§lCurrent map");
            lines.add(shift+"§e" + (NostalgiaMaps.getInstance().getMapsManager().getCurrentMap() != null ?
                    NostalgiaMaps.getInstance().getMapsManager().getCurrentMap().getName() : "§7No map selected"));
            lines.add(" ");
        }

        if (config.getScoreboardConfig().isShowNumberOfMaps()) {
            lines.add(shift+"§f§lAvailable maps");
            lines.add(shift+"§e" + NostalgiaMaps.getInstance().getMapsManager().getMapsPool().size());
            lines.add("  ");
        }

        if (config.getScoreboardConfig().isShowNumberOfPlayers()) {
            lines.add(shift+"§f§lPlayers");
            lines.add(shift+"§e" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
            lines.add("   ");
        }

        for (int i = 0; i < lines.size(); i++)
            mainObjective.getScore(lines.get(i)).setScore(lines.size() - i);

        this.availableScoreboards.put("main", mainBoard);
    }


    public void displayBoard() {
        Scoreboard clearBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective clearObjective = clearBoard.registerNewObjective("clear", "dummy", " ");

        createMainMenuBoard();
        clearObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (this.selectedScoreboard == null && p.getScoreboard() != null) {
                p.setScoreboard(clearBoard);
            } else {
                p.setScoreboard(this.availableScoreboards.get(this.selectedScoreboard));
            }
        }
    }

    public void cancelBoardTask() {
        if (this.boardTask != null) {
            this.boardTask.cancel();
            this.boardTask = null;
        }
    }

    public String getSelectedScoreboard() {
        return selectedScoreboard;
    }

    public void setSelectedScoreboard(String selectedScoreboard) {
        this.selectedScoreboard = selectedScoreboard;
    }
}
