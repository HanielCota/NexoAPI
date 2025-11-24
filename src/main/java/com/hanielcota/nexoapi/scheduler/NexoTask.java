package com.hanielcota.nexoapi.scheduler;

import com.hanielcota.nexoapi.scheduler.property.TaskTiming;
import com.hanielcota.nexoapi.scheduler.type.TaskExecutionMode;
import com.hanielcota.nexoapi.title.time.TickDuration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Represents a scheduled task with execution mode and timing configuration.
 * Provides a fluent API for creating and scheduling tasks.
 * <p>
 * Tasks can be executed synchronously or asynchronously, with delays and intervals.
 * </p>
 *
 * @param mode   the execution mode (sync or async)
 * @param timing the timing configuration (delays, intervals)
 * @since 1.0.0
 */
public record NexoTask(@NotNull TaskExecutionMode mode,
                       @NotNull TaskTiming timing) {

    /**
     * Creates a synchronous task that executes immediately.
     *
     * @return a new NexoTask instance
     */
    public static NexoTask sync() {
        return new NexoTask(TaskExecutionMode.SYNC, TaskTiming.IMMEDIATE);
    }

    /**
     * Creates an asynchronous task that executes immediately.
     *
     * @return a new NexoTask instance
     */
    public static NexoTask async() {
        return new NexoTask(TaskExecutionMode.ASYNC, TaskTiming.IMMEDIATE);
    }

    /**
     * Sets an initial delay for the task.
     *
     * @param delay the delay duration
     * @return a new NexoTask instance with the delay
     * @throws NullPointerException if delay is null
     */
    public NexoTask delay(@NotNull TickDuration delay) {
        return new NexoTask(mode, timing.withInitialDelay(delay));
    }

    /**
     * Sets an initial delay for the task in ticks.
     *
     * @param ticks the delay in ticks
     * @return a new NexoTask instance with the delay
     */
    public NexoTask delay(long ticks) {
        return delay(TickDuration.of(ticks));
    }

    /**
     * Sets a repeating interval for the task.
     *
     * @param interval the interval duration
     * @return a new NexoTask instance with the interval
     * @throws IllegalArgumentException if interval is less than or equal to zero
     * @throws NullPointerException     if interval is null
     */
    public NexoTask interval(@NotNull TickDuration interval) {
        if (interval.ticks() <= 0) {
            throw new IllegalArgumentException("Interval must be greater than zero for repeating tasks.");
        }

        return new NexoTask(mode, timing.withInterval(interval));
    }

    /**
     * Sets a repeating interval for the task in ticks.
     *
     * @param ticks the interval in ticks
     * @return a new NexoTask instance with the interval
     * @throws IllegalArgumentException if ticks is less than or equal to zero
     */
    public NexoTask interval(long ticks) {
        if (ticks <= 0) {
            throw new IllegalArgumentException("Interval must be greater than zero for repeating tasks.");
        }

        return interval(TickDuration.of(ticks));
    }

    /**
     * Starts the task with a runnable.
     *
     * @param plugin   the plugin instance
     * @param runnable the runnable to execute
     * @throws NullPointerException if plugin or runnable is null
     */
    public void start(@NotNull Plugin plugin, @NotNull Runnable runnable) {
        if (timing.isOneTime()) {
            mode.scheduleOnce(plugin, runnable, timing.initialDelay());
            return;
        }

        mode.scheduleRepeating(plugin, runnable, timing.initialDelay(), timing.interval());
    }

    /**
     * Starts the task with a consumer that receives the BukkitTask.
     * Only works for repeating tasks.
     *
     * @param plugin   the plugin instance
     * @param consumer the consumer that receives the BukkitTask
     * @throws IllegalStateException if the task is not a repeating task
     * @throws NullPointerException  if plugin or consumer is null
     */
    public void start(@NotNull Plugin plugin, @NotNull Consumer<BukkitTask> consumer) {
        if (timing.isOneTime()) {
            throw new IllegalStateException("Self-consuming tasks require a repeating interval.");
        }

        mode.scheduleConsuming(plugin, consumer, timing.initialDelay(), timing.interval());
    }
}
