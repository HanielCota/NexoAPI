package com.hanielcota.nexoapi.database.property;

import java.time.Duration;
import java.util.Objects;

/**
 * Represents a connection timeout duration for database operations.
 * This is a value object that encapsulates timeout configuration.
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @param duration the timeout duration
 * @since 1.0.0
 */
public record ConnectionTimeout(Duration duration) {

    /**
     * Default timeout: 30 seconds.
     */
    public static final ConnectionTimeout DEFAULT = new ConnectionTimeout(Duration.ofSeconds(30));

    /**
     * Short timeout: 10 seconds.
     */
    public static final ConnectionTimeout SHORT = new ConnectionTimeout(Duration.ofSeconds(10));

    /**
     * Long timeout: 60 seconds.
     */
    public static final ConnectionTimeout LONG = new ConnectionTimeout(Duration.ofSeconds(60));

    public ConnectionTimeout {
        Objects.requireNonNull(duration, "Timeout duration cannot be null");
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Timeout duration must be positive, but was: " + duration);
        }
    }

    /**
     * Creates a connection timeout from a duration.
     *
     * @param duration the timeout duration
     * @return a new ConnectionTimeout instance
     * @throws NullPointerException     if duration is null
     * @throws IllegalArgumentException if duration is negative or zero
     */
    public static ConnectionTimeout of(Duration duration) {
        return new ConnectionTimeout(duration);
    }

    /**
     * Creates a connection timeout from seconds.
     *
     * @param seconds the timeout in seconds
     * @return a new ConnectionTimeout instance
     * @throws IllegalArgumentException if seconds is less than or equal to zero
     */
    public static ConnectionTimeout ofSeconds(long seconds) {
        return new ConnectionTimeout(Duration.ofSeconds(seconds));
    }

    /**
     * Creates a connection timeout from milliseconds.
     *
     * @param milliseconds the timeout in milliseconds
     * @return a new ConnectionTimeout instance
     * @throws IllegalArgumentException if milliseconds is less than or equal to zero
     */
    public static ConnectionTimeout ofMillis(long milliseconds) {
        return new ConnectionTimeout(Duration.ofMillis(milliseconds));
    }

    /**
     * Returns the timeout in milliseconds.
     *
     * @return the timeout in milliseconds
     */
    public long toMillis() {
        return duration.toMillis();
    }

    /**
     * Returns the timeout in seconds.
     *
     * @return the timeout in seconds
     */
    public long toSeconds() {
        return duration.toSeconds();
    }
}

