/*********************************************************\
*   @Author: AchilleBourgault                             *
*   @Github: https://github.com/achillebourgault          *
*   @Project: NostalgiaMaps                               *
\*********************************************************/

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
}
