package com.hanielcota.nexoapi.config.file;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Represents a configuration file and provides utilities for file preparation.
 * This record handles the creation of configuration files and their parent directories.
 *
 * @param file the configuration file
 * @since 1.0.0
 */
public record ConfigFile(@NonNull File file) {

    /**
     * Prepares a configuration file from a direct File instance.
     * Creates the file and necessary directories if they don't exist.
     *
     * @param file the configuration file
     * @return a prepared ConfigFile instance
     */
    public static ConfigFile prepare(@NonNull File file) {
        ensureExists(file);
        return new ConfigFile(file);
    }

    /**
     * Prepares a configuration file from a directory and file name.
     * Creates the file and necessary directories if they don't exist.
     *
     * @param dataFolder the directory where the file will be created
     * @param fileName the name of the configuration file
     * @return a prepared ConfigFile instance
     */
    public static ConfigFile prepare(@NonNull File dataFolder, @NonNull String fileName) {
        var file = new File(dataFolder, fileName);
        ensureExists(file);
        return new ConfigFile(file);
    }

    /**
     * Helper method for plugins using JavaPlugin.
     * Prepares a configuration file using the plugin's data folder.
     * If the file exists as a resource in the plugin, it will be copied.
     *
     * @param plugin the JavaPlugin instance
     * @param fileName the name of the configuration file
     * @return a prepared ConfigFile instance
     */
    public static ConfigFile prepare(@NonNull JavaPlugin plugin, @NonNull String fileName) {
        var file = new File(plugin.getDataFolder(), fileName);
        ensureExists(plugin, fileName, file);
        return new ConfigFile(file);
    }

    private static void ensureExists(File file) {
        if (file.exists()) {
            return;
        }
        create(file);
    }

    private static void ensureExists(JavaPlugin plugin, String name, File file) {
        if (file.exists()) {
            return;
        }
        create(plugin, name, file);
    }

    @SneakyThrows
    private static void create(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Critical failure: Could not create directory " + parent.getAbsolutePath());
        }

        if (!file.createNewFile()) {
            throw new IOException("Critical failure: Operating system prevented creation of file " + file.getName());
        }
    }

    @SneakyThrows
    private static void create(JavaPlugin plugin, String name, File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Critical failure: Could not create directory " + parent.getAbsolutePath());
        }

        if (plugin.getResource(name) != null) {
            plugin.saveResource(name, false);
            return;
        }

        if (!file.createNewFile()) {
            throw new IOException("Critical failure: Operating system prevented creation of file " + file.getName());
        }
    }
}