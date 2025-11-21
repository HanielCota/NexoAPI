package com.hanielcota.nexoapi.title.time;

import java.time.Duration;

/**
 * Represents a duration in Minecraft ticks.
 * One tick equals 50 milliseconds (20 ticks per second).
 *
 * @param ticks the number of ticks
 * @since 1.0.0
 */
public record TickDuration(long ticks) {

    private static final long MILLIS_PER_TICK = 50L;

    /**
     * Converts this tick duration to a Java Duration.
     *
     * @return the Duration equivalent of this tick duration
     */
    public Duration toDuration() {
        return Duration.ofMillis(ticks * MILLIS_PER_TICK);
    }

    /**
     * Creates a new TickDuration from a number of ticks.
     *
     * @param ticks the number of ticks (must be &gt;= 0)
     * @return a new TickDuration instance
     * @throws IllegalArgumentException if ticks is negative
     */
    public static TickDuration of(long ticks) {
        if (ticks < 0) {
            throw new IllegalArgumentException("Tick duration cannot be negative");
        }

        return new TickDuration(ticks);
    }
}
