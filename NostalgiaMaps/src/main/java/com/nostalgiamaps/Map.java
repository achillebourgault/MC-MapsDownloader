package com.nostalgiamaps;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

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

public class Map {

    private boolean isLoaded;
    private String mapName;
    private String mapUrl;

    public Map(String mapName, String mapUrl, boolean loadImmediately) {
        this.isLoaded = false;
        this.mapName = mapName;
        this.mapUrl = mapUrl;

        if (loadImmediately) load();
    }

    public void load() {
        // Fetch map from URL and load it into server
    }

    private void downloadAndExtractMap() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    // Téléchargement de la carte
                    URL url = new URL(mapUrl);
                    try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
                        // Création d'un fichier temporaire pour stocker l'archive zip
                        File tempFile = File.createTempFile("map", ".zip");
                        tempFile.deleteOnExit();

                        // Copie du contenu téléchargé dans le fichier temporaire
                        Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                        // Extraction de l'archive zip
                        extractZip(tempFile);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(NostalgiaMaps.getInstance());
    }

    private void extractZip(File zipFile) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(Files.newInputStream(zipFile.toPath())))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    // Génération d'un nouveau nom UUID pour le dossier de la carte
                    String newDirectoryName = UUID.randomUUID().toString();

                    // Création du dossier principal du serveur Minecraft s'il n'existe pas
                    File serverDirectory = new File("./");
                    if (!serverDirectory.exists())
                        serverDirectory.mkdirs();

                    // Création du dossier de la carte dans le répertoire principal du serveur
                    File mapDirectory = new File(serverDirectory, newDirectoryName);
                    if (mapDirectory.mkdir() != true)

                    mapDirectory.mkdir();

                    // Copie des fichiers extraits dans le dossier de la carte
                    Path outputPath = mapDirectory.toPath().resolve(entry.getName());
                    Files.copy(zipInputStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
