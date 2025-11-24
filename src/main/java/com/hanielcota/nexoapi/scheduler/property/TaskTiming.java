package com.hanielcota.nexoapi.scheduler.property;

import com.hanielcota.nexoapi.title.time.TickDuration;
import org.jetbrains.annotations.NotNull;

public record TaskTiming(@NotNull TickDuration initialDelay,
                         @NotNull TickDuration interval) {

    private static final TickDuration ZERO = TickDuration.of(0);

    public static final TaskTiming IMMEDIATE = new TaskTiming(ZERO, ZERO);

    public boolean isOneTime() {
        return interval.ticks() == 0;
    }

    public TaskTiming withInitialDelay(TickDuration newDelay) {
        return new TaskTiming(newDelay, interval);
    }

    public TaskTiming withInterval(TickDuration newInterval) {
        return new TaskTiming(initialDelay, newInterval);
    }
}
