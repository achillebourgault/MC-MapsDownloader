/*********************************************************\
*   @Author: AchilleBourgault                             *
*   @Github: https://github.com/achillebourgault          *
*   @Project: NostalgiaMaps                               *
\*********************************************************/

package com.nostalgiamaps;

import com.nostalgiamaps.utils.Logs;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
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

        if (loadImmediately) load();
    }

    public void load() {
        if (this.loadStatus != LoadStatus.READY_TO_LOAD) return;

        this.loadStatus = LoadStatus.LOADING;
        Logs.send("Loading map from url: " + mapUrl, Logs.LogType.INFO, Logs.LogPrivilege.LAMBDA_PLAYER);
        Logs.send("Loading map from url: " + mapUrl, Logs.LogType.INFO, Logs.LogPrivilege.CONSOLE);

        Bukkit.getScheduler().runTask(NostalgiaMaps.getInstance(), () -> {
            try {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet request = new HttpGet(mapUrl);
                HttpResponse response = httpClient.execute(request);

                InputStream inputStream = response.getEntity().getContent();
                File tempFile = File.createTempFile("map", ".zip");
                tempFile.deleteOnExit();

                // Copie du contenu téléchargé dans le fichier temporaire
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                extractZip(tempFile);

                inputStream.close();
                httpClient.close();
            } catch (IOException e) {
                Logs.send("Error while downloading map " + mapDisplayName + ".", Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
                Logs.send(e.getMessage(), Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
            }
        });
    }

    private void extractZip(File zipFile) {
        String tmpMapName = null;
        boolean allFilesExtracted = true;

        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String entryName = entry.getName();
                    File destinationFile = new File("./" + entryName);

                    // Crée les répertoires parents si nécessaire
                    if (!destinationFile.getParentFile().exists()) {
                        destinationFile.getParentFile().mkdirs();
                    }

                    // Copie du fichier extrait dans le répertoire de destination
                    Files.copy(zipInputStream, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    // Si c'est le premier fichier extrait, assigne son nom à la variable tmpMapName
                    if (tmpMapName == null) {
                        tmpMapName = entryName;
                    }
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            allFilesExtracted = false;
            this.loadStatus = LoadStatus.ERROR;
            Logs.send("Error while extracting map " + mapDisplayName + ".", Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
            Logs.send(e.getMessage(), Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
            e.printStackTrace();
        }

        if (allFilesExtracted) {
            if (tmpMapName == null) {
                Logs.send("Unsupported map format.", Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
                Bukkit.getScheduler().runTaskLaterAsynchronously(NostalgiaMaps.getInstance(), () -> {
                    Logs.send("Retrying to load map with another configuration..", Logs.LogType.INFO, Logs.LogPrivilege.OPS);
                    this.loadStatus = LoadStatus.READY_TO_LOAD;
                    load();
                }, 15);
            } else {
                this.loadStatus = LoadStatus.LOADED;
                Logs.send("Got tmpMapName: [" + tmpMapName + "]", Logs.LogType.INFO, Logs.LogPrivilege.OPS);
                this.mapName = tmpMapName.substring(0, tmpMapName.indexOf("/"));

                Logs.send("APPLY Format to mapName: [" + this.mapName + "]", Logs.LogType.INFO, Logs.LogPrivilege.OPS);
                //format world name to remove special characters and be compatible with WorldCreator
                this.mapName = this.mapName.replaceAll("[^a-zA-Z0-9_]", "");
                Logs.send("APPLIED Format to mapName: [" + this.mapName + "]", Logs.LogType.INFO, Logs.LogPrivilege.OPS);
                this.mapDisplayName = this.mapName;
                NostalgiaMaps.getInstance().getMapsManager().addMap(this);
                createWorld();
                Bukkit.getScheduler().runTaskAsynchronously(NostalgiaMaps.getInstance(), () -> {
                    while (Bukkit.getWorld(mapName) == null) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Logs.send("Map " + mapDisplayName + " loaded successfully.", Logs.LogType.INFO, Logs.LogPrivilege.OPS);
                    teleportPlayerIfImmediatelyLoaded();
                });
            }

        }
    }

    private void createWorld() {
        if (this.mapName == null) {
            System.out.println("[DEBUG](MapInstance:createWorld) mapName is null.");
            return;
        }
        // load world into server
        Logs.send("Loading world with name [" + this.mapName + "]", Logs.LogType.INFO, Logs.LogPrivilege.OPS);
        WorldCreator worldCreator = new WorldCreator(this.mapName);
    }

    private void teleportPlayerIfImmediatelyLoaded() {
        if (loadImmediately) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.teleport(getWorld().getSpawnLocation());
            });
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
