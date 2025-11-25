package com.hanielcota.nexoapi.scheduler;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a scheduled task that can be cancelled.
 * Wraps a BukkitTask to provide a more convenient API.
 *
 * @since 1.0.0
 */
public final class ScheduledTask {
    private final BukkitTask bukkitTask;

    private ScheduledTask(@NotNull BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    /**
     * Creates a new ScheduledTask from a BukkitTask.
     *
     * @param bukkitTask the BukkitTask to wrap
     * @return a new ScheduledTask instance
     * @throws NullPointerException if bukkitTask is null
     */
    public static @NotNull ScheduledTask of(@NotNull BukkitTask bukkitTask) {
        return new ScheduledTask(bukkitTask);
    }

    /**
     * Cancels this task if it is scheduled.
     * If the task is already cancelled, this method does nothing.
     */
    public void cancel() {
        if (!bukkitTask.isCancelled()) {
            bukkitTask.cancel();
        }
    }

    /**
     * Returns whether this task has been cancelled.
     *
     * @return true if the task has been cancelled, false otherwise
     */
    public boolean isCancelled() {
        return bukkitTask.isCancelled();
    }

    /**
     * Gets the underlying BukkitTask.
     *
     * @return the BukkitTask
     */
    public @NotNull BukkitTask getBukkitTask() {
        return bukkitTask;
    }
}

