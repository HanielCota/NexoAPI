package com.hanielcota.nexoapi.config;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.hanielcota.nexoapi.config.file.AsyncFileWriter;
import com.hanielcota.nexoapi.config.file.ConfigFile;
import com.hanielcota.nexoapi.config.path.ConfigPath;
import com.hanielcota.nexoapi.config.persistence.ConfigPersistence;
import com.hanielcota.nexoapi.config.storage.InMemoryConfigStore;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Asynchronous configuration API for Minecraft plugins with intelligent caching.
 * Manages YAML configuration files with in-memory storage and asynchronous file writing.
 * <p>
 * This class provides a thread-safe way to manage configuration files, allowing
 * plugins to read and write configuration values without blocking the main thread.
 * </p>
 * <p>
 * Uses Caffeine Cache for path caching to optimize memory usage and provide
 * automatic cleanup of unused paths.
 * </p>
 *
 * @since 1.0.0
 */
public final class NexoConfig {

    /**
     * Caffeine cache for config paths.
     * Configuration:
     * - Maximum size: 500 paths
     * - Expire after access: 10 minutes
     * - Statistics: enabled
     */
    private static final LoadingCache<String, ConfigPath> PATH_CACHE = 
        Caffeine.newBuilder()
            .maximumSize(500) // Reasonable limit for most configs
            .expireAfterAccess(10, TimeUnit.MINUTES) // Auto-cleanup unused paths
            .recordStats() // Enable statistics
            .build(ConfigPath::new);

    private final InMemoryConfigStore store;
    private final AtomicReference<ConfigPersistence> persistenceRef;

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
        this.persistenceRef = new AtomicReference<>(ConfigPersistence.with(writer));

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
        this.persistenceRef = new AtomicReference<>(ConfigPersistence.with(writer));

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
        this.persistenceRef = new AtomicReference<>(ConfigPersistence.with(writer));

        store.load(disk.file());
    }

    /**
     * Retrieves a value from the configuration.
     * Returns the default value if the path doesn't exist.
     * <p>
     * ConfigPath instances are intelligently cached using Caffeine Cache:
     * - Frequently used paths remain in cache
     * - Unused paths are automatically cleaned up after 10 minutes
     * - Maximum 500 paths cached (prevents memory bloat)
     * </p>
     *
     * @param path         the configuration path (e.g., "database.host")
     * @param defaultValue the default value to return if the path doesn't exist
     * @param <T>          the type of the value
     * @return the configuration value or the default value
     */
    public <T> T get(@NonNull String path, T defaultValue) {
        ConfigPath configPath = PATH_CACHE.get(path);
        return store.retrieve(configPath, defaultValue);
    }

    /**
     * Sets a value in the configuration.
     * The value will be stored in memory and persisted when {@link #save()} is called.
     * <p>
     * ConfigPath instances are intelligently cached using Caffeine Cache.
     * This operation marks the configuration as dirty, requiring a save.
     * </p>
     *
     * @param path  the configuration path (e.g., "database.host")
     * @param value the value to set
     */
    public void set(@NonNull String path, Object value) {
        ConfigPath configPath = PATH_CACHE.get(path);
        store.update(configPath, value);
        markDirty();
    }
    
    /**
     * Gets cache performance statistics for config paths.
     * <p>
     * Useful metrics:
     * - Hit rate: should be very high (95%+) for typical usage
     * - Miss rate: only when accessing new paths for the first time
     * - Eviction count: number of unused paths automatically removed
     * </p>
     *
     * @return cache statistics for path cache
     */
    public static CacheStats getPathCacheStats() {
        return PATH_CACHE.stats();
    }
    
    /**
     * Gets the current number of cached config paths.
     *
     * @return number of paths currently in cache
     */
    public static long getPathCacheSize() {
        return PATH_CACHE.estimatedSize();
    }
    
    /**
     * Clears the path cache.
     * This is rarely needed, but can be useful for testing or
     * if you want to force recreation of path objects.
     */
    public static void clearPathCache() {
        PATH_CACHE.invalidateAll();
    }

    private void markDirty() {
        persistenceRef.updateAndGet(ConfigPersistence::markDirty);
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
        ConfigPersistence currentPersistence = persistenceRef.get();
        if (!currentPersistence.isDirty()) {
            return CompletableFuture.completedFuture(null);
        }

        return performSave();
    }

    private CompletableFuture<Void> performSave() {
        var data = store.serialize();
        var currentPersistence = persistenceRef.get();
        var writeFuture = currentPersistence.write(data);

        return writeFuture.thenRun(this::markClean)
                .exceptionally(throwable -> null);
    }

    private void markClean() {
        persistenceRef.updateAndGet(ConfigPersistence::markClean);
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