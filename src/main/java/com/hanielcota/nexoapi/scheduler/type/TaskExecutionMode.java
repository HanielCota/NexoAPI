package com.hanielcota.nexoapi.scheduler.type;

import com.hanielcota.nexoapi.title.time.TickDuration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public enum TaskExecutionMode {

    SYNC {
        @Override
        public void scheduleOnce(@NotNull Plugin plugin,
                                 @NotNull Runnable task,
                                 @NotNull TickDuration delay) {
            scheduler().runTaskLater(plugin, task, delay.ticks());
        }

        @Override
        public void scheduleRepeating(@NotNull Plugin plugin,
                                      @NotNull Runnable task,
                                      @NotNull TickDuration delay,
                                      @NotNull TickDuration interval) {
            scheduler().runTaskTimer(plugin, task, delay.ticks(), interval.ticks());
        }

        @Override
        public void scheduleConsuming(@NotNull Plugin plugin,
                                      @NotNull Consumer<BukkitTask> consumer,
                                      @NotNull TickDuration delay,
                                      @NotNull TickDuration interval) {
            scheduler().runTaskTimer(plugin, consumer, delay.ticks(), interval.ticks());
        }
    },

    ASYNC {
        @Override
        public void scheduleOnce(@NotNull Plugin plugin,
                                 @NotNull Runnable task,
                                 @NotNull TickDuration delay) {
            scheduler().runTaskLaterAsynchronously(plugin, task, delay.ticks());
        }

        @Override
        public void scheduleRepeating(@NotNull Plugin plugin,
                                      @NotNull Runnable task,
                                      @NotNull TickDuration delay,
                                      @NotNull TickDuration interval) {
            scheduler().runTaskTimerAsynchronously(plugin, task, delay.ticks(), interval.ticks());
        }

        @Override
        public void scheduleConsuming(@NotNull Plugin plugin,
                                      @NotNull Consumer<BukkitTask> consumer,
                                      @NotNull TickDuration delay,
                                      @NotNull TickDuration interval) {
            scheduler().runTaskTimerAsynchronously(plugin, consumer, delay.ticks(), interval.ticks());
        }
    };

    protected BukkitScheduler scheduler() {
        return Bukkit.getScheduler();
    }

    public abstract void scheduleOnce(Plugin plugin, Runnable task, TickDuration delay);

    public abstract void scheduleRepeating(Plugin plugin, Runnable task, TickDuration delay, TickDuration interval);

    public abstract void scheduleConsuming(Plugin plugin, Consumer<BukkitTask> consumer, TickDuration delay, TickDuration interval);
}
