# NostalgiaMaps Plugin

Minecraft plugin that allows players to download and play on Minecraft maps from the web.
</br>Development is not finished yet, so expect bugs and issues.

## Project's Goal

Goal of this project is to allow players to download and play on Minecraft maps 
from the web directly from Minecraft. This plugin is mainly focused on Minecraft maps
from the [MinecraftMaps](https://www.minecraftmaps.com/) website, but it should 
work with any other website that provides Minecraft maps.

> **Note:** This plugin is not affiliated with MinecraftMaps in any way.

## BETA Preview [WIP]

1. Download the demo plugin [here](./beta/NostalgiaMaps-BETA.jar).
2. Place the plugin in your server's `plugins` folder.
3. Start your server.

## Usage

### Downloading a map

> **Note:** The map url must be the same as the one used on the download button of the map's page.

1. Run the command `/map add <url>` to query map pool for download the map.
2. Wait for the download to finish.

### Playing a map

> Once a first map is downloaded, the plugin will automatically send a message to all players to ask them if they want to play the map. If so, just click on the **[CLICK HERE]** text button

Or you can manually start a map by using one of the following methods:
- Run the command `/map start <mapname>`.
- Run the command `/maps` and click on the map you want to play. 

### Current issues (to be resolved)

- [ ] No macOS' Maps support
- [ ] Maps textures pack not being applied to players
- [ ] If server restarts, previously downloaded maps are not loaded into MapPool
- [ ] If server restarts, players are not teleported back to the lobby (They are stuck in an unloaded map)
- [ ] If selecting a map from the `/maps` menu, the map will not be loaded because of the way the menu gets the map name

### Features

- [x] Download maps from the web
- [x] Load maps into the server
- [x] Teleport players to the map
- [x] Display a scoreboard with the map's information
- [x] Display a menu with all the maps
- [ ] (Partially functional) Maps Voting System
- [ ] Use maps textures pack if available
- [ ] Teleport players back to the lobby
- [ ] Automatically load maps into the server on startup
- [ ] Automatically teleport players to the lobby on startup
- [ ] Loading Bar for downloading maps (Using Bossbar, ActionBar or ExperienceBar)

## Configuration

### config.yml

```yaml
# Owner options: none | first_player | ops | <playername>
#   The owner of the server is the only one who can manage maps and use plugin commands.
#     Note: Server Operators (ops) can always use plugin commands.
#
#   Possible values:
#   - none: Everyone are the server owners
#   - first_player: The first player that joins the server will be the server owner
#   - ops: Only servers operators are the server owners
#   - <playername>: Only the player with the specified name is the server owner. Don't use "<" and ">".
owner: none

# If you want to specify a map by default, you need to selected_map & selected_map_display_name. Otherwise, leave it as it is.
# Map name should be the name of the folder in the map's folder.
map:
  selected_map: none
  selected_map_display_name: none
  selected_map_url: none

# When a new map is added, a vote will start for the duration you define (default: 60 seconds) to decide whether to play on this map.
# You can also vote on all available maps using the '/maps vote start' command. (Could be triggered from console)
voting_system:
  start_vote_on_new_map: true
  active: true
  voting_duration: 60

# !!! WARNING !!! If you use a custom scoreboard, this may lead to incompatibilities with maps that already display scoreboards.
# Use this option only if you know what you are doing.
scoreboard:
  active: true
  title: '&e&lNostalgiaMaps'
  show_map_name: true
  show_number_of_players: true
  show_number_of_maps: true
  shift: 0
  refresh_rate: 20
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.