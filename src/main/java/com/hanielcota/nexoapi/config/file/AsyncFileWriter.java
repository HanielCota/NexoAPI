package com.hanielcota.nexoapi.config.file;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronous file writer that performs file operations on a background thread.
 * This prevents blocking the main thread when writing configuration files.
 * <p>
 * Uses virtual threads (Java 21) for efficient, lightweight asynchronous operations.
 * The executor is properly managed and will be shut down on JVM shutdown.
 * </p>
 *
 * @param target the target file to write to
 * @since 1.0.0
 */
public record AsyncFileWriter(@NonNull File target) {

    private static final ExecutorService VIRTUAL_EXECUTOR = 
            Executors.newVirtualThreadPerTaskExecutor();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            VIRTUAL_EXECUTOR.shutdown();
            try {
                if (!VIRTUAL_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                    VIRTUAL_EXECUTOR.shutdownNow();
                }
            } catch (InterruptedException e) {
                VIRTUAL_EXECUTOR.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));
    }

    /**
     * Writes content to the target file asynchronously.
     * The write operation is performed on a virtual thread (Java 21 feature).
     * <p>
     * If an error occurs during writing, the CompletableFuture will complete
     * exceptionally with the underlying exception. Use {@link CompletableFuture#exceptionally}
     * or {@link CompletableFuture#handle} to handle errors.
     * </p>
     *
     * @param content the content to write to the file
     * @return a CompletableFuture that completes when the write operation finishes
     */
    public CompletableFuture<Void> write(@NonNull String content) {
        return CompletableFuture.runAsync(() -> writeSync(content), VIRTUAL_EXECUTOR);
    }

    @SneakyThrows
    private void writeSync(String content) {
        Files.writeString(target.toPath(), content, StandardCharsets.UTF_8);
    }
}