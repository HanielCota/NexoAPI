package com.hanielcota.nexoapi.scheduler.type;

import com.hanielcota.nexoapi.title.time.TickDuration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public enum TaskExecutionMode {

    SYNC {
        @Override
        public @NotNull BukkitTask scheduleOnce(@NotNull Plugin plugin,
                                                @NotNull Runnable task,
                                                @NotNull TickDuration delay) {
            return scheduler().runTaskLater(plugin, task, delay.ticks());
        }

        @Override
        public @NotNull BukkitTask scheduleRepeating(@NotNull Plugin plugin,
                                                     @NotNull Runnable task,
                                                     @NotNull TickDuration delay,
                                                     @NotNull TickDuration interval) {
            return scheduler().runTaskTimer(plugin, task, delay.ticks(), interval.ticks());
        }

        @Override
        public @NotNull BukkitTask scheduleConsuming(@NotNull Plugin plugin,
                                                     @NotNull Consumer<BukkitTask> consumer,
                                                     @NotNull TickDuration delay,
                                                     @NotNull TickDuration interval) {
            AtomicReference<BukkitTask> taskRef = new AtomicReference<>();
            Runnable wrapper = () -> {
                BukkitTask task = taskRef.get();
                if (task != null) {
                    consumer.accept(task);
                }
            };
            BukkitTask task = scheduler().runTaskTimer(plugin, wrapper, delay.ticks(), interval.ticks());
            taskRef.set(task);
            return task;
        }
    },

    ASYNC {
        @Override
        public @NotNull BukkitTask scheduleOnce(@NotNull Plugin plugin,
                                                @NotNull Runnable task,
                                                @NotNull TickDuration delay) {
            return scheduler().runTaskLaterAsynchronously(plugin, task, delay.ticks());
        }

        @Override
        public @NotNull BukkitTask scheduleRepeating(@NotNull Plugin plugin,
                                                     @NotNull Runnable task,
                                                     @NotNull TickDuration delay,
                                                     @NotNull TickDuration interval) {
            return scheduler().runTaskTimerAsynchronously(plugin, task, delay.ticks(), interval.ticks());
        }

        @Override
        public @NotNull BukkitTask scheduleConsuming(@NotNull Plugin plugin,
                                                     @NotNull Consumer<BukkitTask> consumer,
                                                     @NotNull TickDuration delay,
                                                     @NotNull TickDuration interval) {
            AtomicReference<BukkitTask> taskRef = new AtomicReference<>();
            Runnable wrapper = () -> {
                BukkitTask task = taskRef.get();
                if (task != null) {
                    consumer.accept(task);
                }
            };
            BukkitTask task = scheduler().runTaskTimerAsynchronously(plugin, wrapper, delay.ticks(), interval.ticks());
            taskRef.set(task);
            return task;
        }
    };

    protected BukkitScheduler scheduler() {
        return Bukkit.getScheduler();
    }

    public abstract @NotNull BukkitTask scheduleOnce(@NotNull Plugin plugin, @NotNull Runnable task, @NotNull TickDuration delay);

    public abstract @NotNull BukkitTask scheduleRepeating(@NotNull Plugin plugin, @NotNull Runnable task, @NotNull TickDuration delay, @NotNull TickDuration interval);

    public abstract @NotNull BukkitTask scheduleConsuming(@NotNull Plugin plugin, @NotNull Consumer<BukkitTask> consumer, @NotNull TickDuration delay, @NotNull TickDuration interval);
}
