package com.hanielcota.nexoapi.scheduler;

import com.hanielcota.nexoapi.scheduler.property.TaskTiming;
import com.hanielcota.nexoapi.scheduler.type.TaskExecutionMode;
import com.hanielcota.nexoapi.title.time.TickDuration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record NexoTask(@NotNull TaskExecutionMode mode,
                       @NotNull TaskTiming timing) {


    public static NexoTask sync() {
        return new NexoTask(TaskExecutionMode.SYNC, TaskTiming.IMMEDIATE);
    }

    public static NexoTask async() {
        return new NexoTask(TaskExecutionMode.ASYNC, TaskTiming.IMMEDIATE);
    }


    public NexoTask delay(@NotNull TickDuration delay) {
        return new NexoTask(mode, timing.withInitialDelay(delay));
    }

    public NexoTask delay(long ticks) {
        return delay(TickDuration.of(ticks));
    }

    public NexoTask interval(@NotNull TickDuration interval) {
        if (interval.ticks() <= 0) {
            throw new IllegalArgumentException("Interval must be greater than zero for repeating tasks.");
        }

        return new NexoTask(mode, timing.withInterval(interval));
    }

    public NexoTask interval(long ticks) {
        if (ticks <= 0) {
            throw new IllegalArgumentException("Interval must be greater than zero for repeating tasks.");
        }

        return interval(TickDuration.of(ticks));
    }

    public void start(@NotNull Plugin plugin, @NotNull Runnable runnable) {
        if (timing.isOneTime()) {
            mode.scheduleOnce(plugin, runnable, timing.initialDelay());
            return;
        }

        mode.scheduleRepeating(plugin, runnable, timing.initialDelay(), timing.interval());
    }

    public void start(@NotNull Plugin plugin, @NotNull Consumer<BukkitTask> consumer) {
        if (timing.isOneTime()) {
            throw new IllegalStateException("Self-consuming tasks require a repeating interval.");
        }

        mode.scheduleConsuming(plugin, consumer, timing.initialDelay(), timing.interval());
    }
}
