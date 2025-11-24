package com.hanielcota.nexoapi.config;

import com.hanielcota.nexoapi.config.file.AsyncFileWriter;
import com.hanielcota.nexoapi.config.file.ConfigFile;
import com.hanielcota.nexoapi.config.path.ConfigPath;
import com.hanielcota.nexoapi.config.persistence.ConfigPersistence;
import com.hanielcota.nexoapi.config.storage.InMemoryConfigStore;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Asynchronous configuration API for Minecraft plugins.
 * Manages YAML configuration files with in-memory storage and asynchronous file writing.
 * <p>
 * This class provides a thread-safe way to manage configuration files, allowing
 * plugins to read and write configuration values without blocking the main thread.
 * </p>
 *
 * @since 1.0.0
 */
public final class NexoConfig {

    private static final Map<String, ConfigPath> PATH_CACHE = new ConcurrentHashMap<>();

    private final InMemoryConfigStore store;
    private volatile ConfigPersistence persistence;

    /**
     * Creates a new NexoConfig instance from a file.
     * The file will be created if it doesn't exist.
     *
     * @param file the configuration file
     */
    public NexoConfig(@NonNull File file) {
        var disk = ConfigFile.prepare(file);

        this.store = new InMemoryConfigStore();
        var writer = new AsyncFileWriter(disk.file());
        this.persistence = ConfigPersistence.with(writer);

        store.load(disk.file());
    }

    /**
     * Creates a new NexoConfig instance from a directory and file name.
     * The file will be created if it doesn't exist.
     *
     * @param dataFolder the directory where the file will be created
     * @param fileName   the name of the configuration file
     */
    public NexoConfig(@NonNull File dataFolder, @NonNull String fileName) {
        var disk = ConfigFile.prepare(dataFolder, fileName);

        this.store = new InMemoryConfigStore();
        var writer = new AsyncFileWriter(disk.file());
        this.persistence = ConfigPersistence.with(writer);

        store.load(disk.file());
    }

    /**
     * Helper method for plugins using JavaPlugin.
     * Creates a new NexoConfig instance using the plugin's data folder.
     * If the file exists as a resource in the plugin, it will be copied.
     *
     * @param plugin   the JavaPlugin instance
     * @param fileName the name of the configuration file
     */
    public NexoConfig(@NonNull JavaPlugin plugin, @NonNull String fileName) {
        var disk = ConfigFile.prepare(plugin, fileName);

        this.store = new InMemoryConfigStore();
        var writer = new AsyncFileWriter(disk.file());
        this.persistence = ConfigPersistence.with(writer);

        store.load(disk.file());
    }

    /**
     * Retrieves a value from the configuration.
     * Returns the default value if the path doesn't exist.
     * <p>
     * ConfigPath instances are cached to reduce object allocations.
     * </p>
     *
     * @param path         the configuration path (e.g., "database.host")
     * @param defaultValue the default value to return if the path doesn't exist
     * @param <T>          the type of the value
     * @return the configuration value or the default value
     */
    public <T> T get(@NonNull String path, T defaultValue) {
        ConfigPath configPath = PATH_CACHE.computeIfAbsent(path, ConfigPath::new);
        return store.retrieve(configPath, defaultValue);
    }

    /**
     * Sets a value in the configuration.
     * The value will be stored in memory and persisted when {@link #save()} is called.
     * <p>
     * ConfigPath instances are cached to reduce object allocations.
     * This operation marks the configuration as dirty, requiring a save.
     * </p>
     *
     * @param path  the configuration path (e.g., "database.host")
     * @param value the value to set
     */
    public void set(@NonNull String path, Object value) {
        ConfigPath configPath = PATH_CACHE.computeIfAbsent(path, ConfigPath::new);
        store.update(configPath, value);
        markDirty();
    }

    private synchronized void markDirty() {
        persistence = persistence.markDirty();
    }

    /**
     * Saves the configuration to the file asynchronously.
     * This method returns immediately and performs the file write on a background thread.
     * <p>
     * If the configuration hasn't been modified since the last save, this method
     * returns a completed future without performing any I/O operations.
     * </p>
     *
     * @return a CompletableFuture that completes when the file is saved and dirty flag is reset
     */
    public CompletableFuture<Void> save() {
        if (!persistence.isDirty()) {
            return CompletableFuture.completedFuture(null);
        }

        return performSave();
    }

    private CompletableFuture<Void> performSave() {
        var data = store.serialize();
        var writeFuture = persistence.write(data);

        return writeFuture.thenRun(this::markClean)
                .exceptionally(throwable -> null);
    }

    private synchronized void markClean() {
        persistence = persistence.markClean();
    }

    /**
     * Forces a save operation even if the configuration hasn't been modified.
     * Useful for ensuring the file is written regardless of the dirty flag.
     *
     * @return a CompletableFuture that completes when the file is saved and dirty flag is reset
     */
    public CompletableFuture<Void> forceSave() {
        return performSave();
    }
}