package com.hanielcota.nexoapi.config.file;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous file writer that performs file operations on a background thread.
 * This prevents blocking the main thread when writing configuration files.
 *
 * @param target the target file to write to
 * @since 1.0.0
 */
public record AsyncFileWriter(@NonNull File target) {

    /**
     * Writes content to the target file asynchronously.
     * The write operation is performed on a background thread.
     *
     * @param content the content to write to the file
     * @return a CompletableFuture that completes when the write operation finishes
     */
    public CompletableFuture<Void> write(@NonNull String content) {
        return CompletableFuture.runAsync(() -> writeSync(content));
    }

    @SneakyThrows
    private void writeSync(String content) {
        Files.writeString(target.toPath(), content, StandardCharsets.UTF_8);
    }
}