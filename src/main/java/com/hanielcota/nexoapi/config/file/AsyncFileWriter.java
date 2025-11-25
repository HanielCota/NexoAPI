package com.hanielcota.nexoapi.config.file;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Asynchronous file writer that performs file operations on a background thread.
 * This prevents blocking the main thread when writing configuration files.
 * <p>
 * Uses virtual threads (Java 21) for efficient, lightweight asynchronous operations.
 * The executor is properly managed and will be shut down on JVM shutdown or when
 * {@link #shutdown()} is called.
 * </p>
 *
 * @param target the target file to write to
 * @since 1.0.0
 */
public record AsyncFileWriter(@NonNull File target) {

    private static final ExecutorService VIRTUAL_EXECUTOR =
            Executors.newVirtualThreadPerTaskExecutor();
    private static final AtomicBoolean SHUTDOWN_INITIATED = new AtomicBoolean(false);
    private static final Thread SHUTDOWN_HOOK = new Thread(() -> shutdownExecutor(false));

    static {
        Runtime.getRuntime().addShutdownHook(SHUTDOWN_HOOK);
    }

    /**
     * Writes content to the target file asynchronously.
     * The write operation is performed on a virtual thread (Java 21 feature).
     * <p>
     * If an error occurs during writing, the CompletableFuture will complete
     * exceptionally with the underlying exception. Use {@link CompletableFuture#exceptionally}
     * or {@link CompletableFuture#handle} to handle errors.
     * </p>
     * <p>
     * If the executor has been shut down, the CompletableFuture will complete
     * exceptionally with an {@link IllegalStateException}.
     * </p>
     *
     * @param content the content to write to the file
     * @return a CompletableFuture that completes when the write operation finishes
     * @throws IllegalStateException if the executor has been shut down
     */
    public CompletableFuture<Void> write(@NonNull String content) {
        if (SHUTDOWN_INITIATED.get()) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException("AsyncFileWriter executor has been shut down")
            );
        }
        return CompletableFuture.runAsync(() -> {
            try {
                writeSync(content);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to write config file: " + target.getPath(), e);
            }
        }, VIRTUAL_EXECUTOR);
    }

    /**
     * Shuts down the executor gracefully.
     * This should be called when the plugin is disabled to ensure all pending
     * write operations complete before the plugin unloads.
     * <p>
     * After calling this method, any new write operations will fail.
     * This method is idempotent and can be called multiple times safely.
     * </p>
     * <p>
     * Recommended usage in plugin's onDisable:
     * </p>
     * <pre>{@code
     * @Override
     * public void onDisable() {
     *     AsyncFileWriter.shutdown();
     * }
     * }</pre>
     */
    public static void shutdown() {
        if (SHUTDOWN_INITIATED.compareAndSet(false, true)) {
            try {
                Runtime.getRuntime().removeShutdownHook(SHUTDOWN_HOOK);
            } catch (IllegalStateException e) {
                // Shutdown hook is already running, ignore
            }
            shutdownExecutor(true);
        }
    }

    private static void shutdownExecutor(boolean removeShutdownHook) {
        VIRTUAL_EXECUTOR.shutdown();
        try {
            if (!VIRTUAL_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                VIRTUAL_EXECUTOR.shutdownNow();
                // Wait a bit more for cancellation to take effect
                if (!VIRTUAL_EXECUTOR.awaitTermination(2, TimeUnit.SECONDS)) {
                    // Log warning if still not terminated (but we can't use logger here)
                    System.err.println("[AsyncFileWriter] Executor did not terminate gracefully");
                }
            }
        } catch (InterruptedException e) {
            VIRTUAL_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void writeSync(String content) throws IOException {
        try {
            Files.writeString(target.toPath(), content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IOException("Failed to write config file: " + target.getPath(), e);
        }
    }
}