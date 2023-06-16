/*********************************************************\
*   @Author: AchilleBourgault                             *
*   @Github: https://github.com/achillebourgault          *
*   @Project: NostalgiaMaps                               *
\*********************************************************/

package com.nostalgiamaps;

import com.nostalgiamaps.utils.Logs;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
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
                URL url = new URL(mapUrl);
                mapDisplayName = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
                try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
                    File tempFile = File.createTempFile("map", ".zip");
                    tempFile.deleteOnExit();

                    // Copie du contenu téléchargé dans le fichier temporaire
                    Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    extractZip(tempFile);
                    createWorld();
                } catch (IOException e) {
                    Logs.send("Error while downloading map " + mapDisplayName + ".", Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
                    Logs.send(e.getMessage(), Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void extractZip(File zipFile) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(Files.newInputStream(zipFile.toPath())))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String newDirectoryName = UUID.randomUUID().toString();
                    File serverDirectory = new File("./");

                    if (!serverDirectory.exists())
                        Logs.send("Error while retrieving server directory", Logs.LogType.ERROR, Logs.LogPrivilege.OPS);

                    File mapDirectory = new File(serverDirectory, newDirectoryName);
                    if (!mapDirectory.mkdir()) {
                        Logs.send("Error while creating map directory", Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
                        return;
                    }

                    // Copie des fichiers extraits dans le dossier de la carte
                    Path outputPath = mapDirectory.toPath().resolve(entry.getName());
                    Files.copy(zipInputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
                    this.mapName = entry.getName();
                    this.loadStatus = LoadStatus.LOADED;
                    Logs.send("Map " + mapDisplayName + " loaded successfully.", Logs.LogType.INFO, Logs.LogPrivilege.OPS);
                    teleportPlayerIfImmediatelyLoaded();
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            this.loadStatus = LoadStatus.ERROR;
            Logs.send("Error while extracting map " + mapDisplayName + ".", Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
            Logs.send(e.getMessage(), Logs.LogType.ERROR, Logs.LogPrivilege.OPS);
            e.printStackTrace();
        }
    }

    private void createWorld() {
        if (this.mapName == null) {
            System.out.println("[DEBUG](MapInstance:createWorld) mapName is null.");
            return;
        }
        Bukkit.createWorld(new WorldCreator(this.mapName));
    }

    private void teleportPlayerIfImmediatelyLoaded() {
        if (loadImmediately) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.teleport(player.getWorld().getSpawnLocation());
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
}
