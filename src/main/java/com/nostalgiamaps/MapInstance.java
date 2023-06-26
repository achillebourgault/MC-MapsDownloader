package com.nostalgiamaps;

import com.nostalgiamaps.utils.Logs;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MapInstance {

    public enum LoadStatus {
        READY_TO_LOAD,
        LOADING,
        LOADED,
        ERROR
    }

    private LoadStatus loadStatus;
    private String mapName = null;
    private String mapDisplayName = null;
    private String mapUrl;
    private boolean loadImmediately;

    public MapInstance(String mapUrl, boolean loadImmediately) {
        this.loadStatus = LoadStatus.READY_TO_LOAD;
        this.mapUrl = mapUrl;
        this.loadImmediately = loadImmediately;

        if (loadImmediately) downloadAndExtractMap();
    }

    private void downloadAndExtractMap() {
        if (this.loadStatus != LoadStatus.READY_TO_LOAD) return;

        this.loadStatus = LoadStatus.LOADING;
        Logs.send("Downloading map from url §f" + mapUrl + "§e...", Logs.LogType.INFO, Logs.LogPrivilege.OPS);

        Bukkit.getScheduler().runTaskAsynchronously(NostalgiaMaps.getInstance(), () -> {
            File tempDir = null;

            try {
                tempDir = createTempDirectory();

                // ZIP Download
                File zipFile = new File(tempDir, "map.zip");
                downloadFile(mapUrl, zipFile);
                List<File> extractedFiles = extractZipFile(zipFile, tempDir);

                // Map validation
                if (isMinecraftMap(extractedFiles)) {
                    File mapDir = findMapDirectory(extractedFiles);

                    if (mapDir != null) {
                        this.mapDisplayName = mapDir.getName();
                        this.mapName = formatMapName(this.mapDisplayName);

                        // Move map to server directory
                        moveMapToServerDir(mapDir);

                        validateMapInitialization();
                        return;
                    }
                }

                // Map validation failed
                Logs.send("Unable to find a valid map in the downloaded archive", Logs.LogType.ERROR, Logs.LogPrivilege.CONSOLE);
            } catch (IOException e) {
                this.loadStatus = LoadStatus.ERROR;
                e.printStackTrace();
                Logs.send("Error while downloading map", Logs.LogType.ERROR, Logs.LogPrivilege.CONSOLE);
            } finally {
                // Delete temp directory
                if (tempDir != null) {
                    try {
                        FileUtils.deleteDirectory(tempDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Logs.send("Error while deleting temp directory", Logs.LogType.ERROR, Logs.LogPrivilege.CONSOLE);
                    }
                }
            }
        });
    }

    private void validateMapInitialization() {
        this.loadStatus = LoadStatus.LOADED;
        Bukkit.getLogger().info("Map loaded");

        NostalgiaMaps.getInstance().getMapsManager().addMap(this);
        createWorld();
        Logs.send("Start generating map " + mapDisplayName + "..", Logs.LogType.INFO, Logs.LogPrivilege.CONSOLE);

        while (Bukkit.getWorld(mapName) == null);
        Logs.send("Map " + mapDisplayName + " successfully generated.", Logs.LogType.INFO, Logs.LogPrivilege.CONSOLE);
        announceNewMap();
        // If the map is loaded and has a spawn location, teleport all players to the map spawn
        try {
            for (Player player : Bukkit.getOnlinePlayers())
                player.teleport(Bukkit.getWorld(mapName).getSpawnLocation());
        } catch (Exception ignored) {} // No spawn location
    }

    private String formatMapName(String mapName) {
        return mapName.toLowerCase().replaceAll("[^a-z0-9_]+", "");
    }

    private File createTempDirectory() throws IOException {
        File serverDir = Bukkit.getWorldContainer();
        return Files.createTempDirectory(serverDir.toPath(), "map-download").toFile();
    }

    private void downloadFile(String url, File outputFile) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);

        try (InputStream inputStream = response.getEntity().getContent();
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private List<File> extractZipFile(File zipFile, File outputDir) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            List<File> extractedFiles = extractZipEntries(zipInputStream, outputDir);
            zipInputStream.closeEntry();
            return extractedFiles;
        }
    }

    private List<File> extractZipEntries(ZipInputStream zipInputStream, File outputDir) throws IOException {
        List<File> extractedFiles = new ArrayList<>();

        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            // TODO: Find a better way to do this, maybe use a config field to specify if server is running on macOS
            //  if so, get the map inside the __MACOSX directory
            if (entry.getName().startsWith("__MACOSX")) { // Ignore __MACOSX directory
                zipInputStream.closeEntry();
                continue;
            }

            File outputFile = new File(outputDir, entry.getName());
            if (entry.isDirectory()) {
                FileUtils.forceMkdir(outputFile);
            } else {
                try (OutputStream outputStream = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
            extractedFiles.add(outputFile);
            zipInputStream.closeEntry();
        }

        return extractedFiles;
    }


    private boolean isMinecraftMap(List<File> extractedFiles) {
        List<String> requiredDirectories = Arrays.asList("advancements", "data", "datapacks", "poi", "region");
        int foundDirectories = 0;

        for (File file : extractedFiles) {
            if (file.isDirectory() && requiredDirectories.contains(file.getName())) {
                foundDirectories++;
                if (foundDirectories >= 2) {
                    return true;
                }
            }
        }

        return false;
    }

    private File findMapDirectory(List<File> extractedFiles) {
        for (File file : extractedFiles) {
            if (file.isDirectory() && isPotentialMapDirectory(file)) {
                return file;
            }
        }
        return null;
    }

    private boolean isPotentialMapDirectory(File directory) {
        List<String> requiredDirectories = Arrays.asList("advancements", "data", "datapacks", "poi", "region");
        for (String requiredDir : requiredDirectories) {
            if (new File(directory, requiredDir).exists()) {
                return true;
            }
        }
        return false;
    }

    private void moveMapToServerDir(File mapDir) throws IOException {
        File serverDir = Bukkit.getWorldContainer();
        Path sourcePath = mapDir.toPath();
        String newMapName = formatMapName(this.mapName);
        Path targetPath = new File(serverDir, newMapName).toPath();

        if (!mapDir.getName().equals(newMapName)) {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void createWorld() {
        if (this.mapName == null) {
            System.out.println("[DEBUG](MapInstance:createWorld) mapName is null.");
            return;
        }
        // load world into server
        Logs.send("Loading world with name [" + this.mapName + "]", Logs.LogType.INFO, Logs.LogPrivilege.OPS);
        try {
            new WorldCreator(this.mapName).createWorld();
        } catch (Exception ignored) {}
    }

    private void announceNewMap() {
        String owner = NostalgiaMaps.getInstance().getConfigManager().getOwnerName();
        TextComponent click = new TextComponent("§e§l[CLICK HERE]");
        TextComponent message = new TextComponent(" §r§f to start the map.");

        click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/maps start " + this.mapName));

        ArrayList<Player> receivers = new ArrayList<>();

        if (owner.equalsIgnoreCase("none"))
            for (Player player : Bukkit.getOnlinePlayers()) receivers.add(player);
        else if (owner.equalsIgnoreCase("ops")) {
            for (OfflinePlayer player : Bukkit.getOperators())
                if (player.isOnline()) receivers.add(player.getPlayer());

        } else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
            if (player.isOnline()) receivers.add(player.getPlayer());
        }

        for (Player player : receivers) {
            player.sendMessage("§e§l[MAPS] §r§fThe map §e" + this.mapDisplayName + " §r§fis ready to be played.");
            player.spigot().sendMessage(click, message);
        }
        if (loadImmediately) {
            //TODO: Trigger MapLoadedEvent
        }
    }

    public LoadStatus getLoadStatus() {
        return loadStatus;
    }

    public String getName() {
        return mapName;
    }

    public String getDisplayName() {
        return mapDisplayName;
    }

    public String getUrl() {
        return mapUrl;
    }

    public World getWorld() {
        return Bukkit.getWorld(mapName);
    }

    public boolean isLoadImmediately() {
        return loadImmediately;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
