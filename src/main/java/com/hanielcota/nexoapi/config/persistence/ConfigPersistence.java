package com.hanielcota.nexoapi.config.persistence;

import com.hanielcota.nexoapi.config.file.AsyncFileWriter;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * Encapsulates the persistence state and writer for configuration files.
 * Manages the dirty flag and asynchronous file writing operations.
 *
 * @param writer the asynchronous file writer
 * @param dirty  the dirty flag indicating if the configuration has been modified
 * @since 1.0.0
 */
public record ConfigPersistence(@NonNull AsyncFileWriter writer, boolean dirty) {

    /**
     * Creates a new ConfigPersistence with the specified writer.
     *
     * @param writer the asynchronous file writer
     * @return a new ConfigPersistence instance with dirty flag set to false
     */
    public static ConfigPersistence with(@NonNull AsyncFileWriter writer) {
        return new ConfigPersistence(writer, false);
    }

    /**
     * Marks this persistence as dirty (modified).
     *
     * @return a new ConfigPersistence instance with dirty flag set to true
     */
    public ConfigPersistence markDirty() {
        return new ConfigPersistence(writer, true);
    }

    /**
     * Marks this persistence as clean (saved).
     *
     * @return a new ConfigPersistence instance with dirty flag set to false
     */
    public ConfigPersistence markClean() {
        return new ConfigPersistence(writer, false);
    }

    /**
     * Checks if the configuration is dirty (has been modified).
     *
     * @return true if dirty, false otherwise
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Writes the content to the file asynchronously.
     *
     * @param content the content to write
     * @return a CompletableFuture that completes when the write operation finishes
     */
    public CompletableFuture<Void> write(@NonNull String content) {
        return writer.write(content);
    }
}

