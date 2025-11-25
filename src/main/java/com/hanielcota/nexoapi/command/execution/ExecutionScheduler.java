package com.hanielcota.nexoapi.command.execution;

import com.hanielcota.nexoapi.command.model.CommandExecutionType;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Service responsible for scheduling command execution (sync/async).
 *
 * @since 1.0.0
 */
public final class ExecutionScheduler {
    private final Plugin ownerPlugin;

    private ExecutionScheduler(@NotNull Plugin ownerPlugin) {
        this.ownerPlugin = Objects.requireNonNull(ownerPlugin, "Plugin cannot be null.");
    }

    /**
     * Creates a new ExecutionScheduler for the given plugin.
     *
     * @param ownerPlugin the plugin instance
     * @return a new ExecutionScheduler instance
     * @throws NullPointerException if ownerPlugin is null
     */
    public static ExecutionScheduler create(@NotNull Plugin ownerPlugin) {
        Objects.requireNonNull(ownerPlugin, "Plugin cannot be null.");
        return new ExecutionScheduler(ownerPlugin);
    }

    /**
     * Schedules a task according to the execution type.
     *
     * @param task          the task to execute
     * @param executionType the execution type (sync/async)
     * @throws NullPointerException if any parameter is null
     */
    public void schedule(@NotNull Runnable task, @NotNull CommandExecutionType executionType) {
        Objects.requireNonNull(task, "Task cannot be null.");
        Objects.requireNonNull(executionType, "Execution type cannot be null.");

        if (executionType.isAsync()) {
            executeAsync(task);
            return;
        }

        executeSync(task);
    }

    private void executeSync(@NotNull Runnable task) {
        if (!ownerPlugin.isEnabled()) {
            return;
        }

        Server server = ownerPlugin.getServer();
        BukkitScheduler scheduler = server.getScheduler();
        scheduler.runTask(ownerPlugin, task);
    }

    private void executeAsync(@NotNull Runnable task) {
        if (!ownerPlugin.isEnabled()) {
            return;
        }

        Server server = ownerPlugin.getServer();
        BukkitScheduler scheduler = server.getScheduler();
        scheduler.runTaskAsynchronously(ownerPlugin, task);
    }
}

