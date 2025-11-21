package com.hanielcota.nexoapi.title.domain;

import java.time.Duration;

public record TickDuration(long value) {

    private static final long MILLIS_PER_TICK = 50L;

    public Duration toJavaDuration() {
        return Duration.ofMillis(value * MILLIS_PER_TICK);
    }

    public static TickDuration of(long ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("Tick duration cannot be negative.");
        }

        return new TickDuration(ticks);
    }
}