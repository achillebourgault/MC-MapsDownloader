package com.nostalgiamaps.manager;

import com.nostalgiamaps.NostalgiaMaps;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private FileConfiguration config = NostalgiaMaps.getInstance().getConfig();

    public ConfigManager() {
        loadConfig();
    }

    private void loadConfig() {
        NostalgiaMaps.getInstance().saveDefaultConfig();
    }

    public String getOwnerName() {
        return config.getString("owner");
    }

    public String getMapDisplayName() {
        return "none".equals(config.getString("map.selected_map_display_name")) ? null : config.getString("map.selected_map_display_name");
    }

    public String getMapName() {
        return "none".equals(config.getString("map.selected_map")) ? null : config.getString("map.selected_map");
    }

    public String getMapUrl() {
        return "none".equals(config.getString("map.selected_map_url")) ? null : config.getString("map.selected_map_url");
    }

    public static class VotingSystemConfig {
        private final boolean startVoteOnNewMap;
        private final boolean active;
        private final int votingDuration;

        public VotingSystemConfig(boolean startVoteOnNewMap, boolean active, int votingDuration) {
            this.startVoteOnNewMap = startVoteOnNewMap;
            this.active = active;
            this.votingDuration = votingDuration;
        }
        public boolean isStartVoteOnNewMap() { return startVoteOnNewMap; }
        public boolean isActive() { return active; }
        public int getVotingDuration() { return votingDuration; }
    }

    public VotingSystemConfig getVotingSystemConfig() {
        return new VotingSystemConfig(
                config.getBoolean("voting_system.start_vote_on_new_map"),
                config.getBoolean("voting_system.active"),
                config.getInt("voting_system.voting_duration")
        );
    }

    public static class ScoreboardConfig {
        private final boolean active;
        private final String title;
        private final boolean showMapName;
        private final boolean showNumberOfPlayers;
        private final boolean showNumberOfMaps;
        private final int shift;
        private final int refreshRate;

        public ScoreboardConfig(boolean active, String title, boolean showMapName, boolean showNumberOfPlayers, boolean showNumberOfMaps, int shift, int refreshRate) {
            this.active = active;
            this.title = title;
            this.showMapName = showMapName;
            this.showNumberOfPlayers = showNumberOfPlayers;
            this.showNumberOfMaps = showNumberOfMaps;
            this.shift = shift;
            this.refreshRate = refreshRate;
        }

        public boolean isActive() {
            return active;
        }

        public String getTitle() {
            return title;
        }

        public boolean isShowMapName() {
            return showMapName;
        }

        public boolean isShowNumberOfPlayers() {
            return showNumberOfPlayers;
        }

        public boolean isShowNumberOfMaps() {
            return showNumberOfMaps;
        }

        public int getShift() {
            return shift;
        }

        public int getRefreshRate() {
            return refreshRate;
        }
    }

    public ScoreboardConfig getScoreboardConfig() {
        return new ScoreboardConfig(
                config.getBoolean("scoreboard.active"),
                config.getString("scoreboard.title"),
                config.getBoolean("scoreboard.show_map_name"),
                config.getBoolean("scoreboard.show_number_of_players"),
                config.getBoolean("scoreboard.show_number_of_maps"),
                config.getInt("scoreboard.shift"),
                config.getInt("scoreboard.refresh_rate")
        );
    }
}
