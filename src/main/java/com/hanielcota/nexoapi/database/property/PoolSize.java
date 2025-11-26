package com.hanielcota.nexoapi.database.property;

/**
 * Represents the size configuration for a database connection pool.
 * This is a value object that encapsulates minimum and maximum pool sizes.
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @param minimum the minimum number of connections to maintain in the pool
 * @param maximum the maximum number of connections allowed in the pool
 * @since 1.0.0
 */
public record PoolSize(int minimum, int maximum) {

    /**
     * Default pool size: 5 minimum, 20 maximum connections.
     */
    public static final PoolSize DEFAULT = new PoolSize(5, 20);

    /**
     * Small pool size: 2 minimum, 5 maximum connections.
     * Suitable for plugins with low database usage.
     */
    public static final PoolSize SMALL = new PoolSize(2, 5);

    /**
     * Medium pool size: 10 minimum, 30 maximum connections.
     * Suitable for plugins with moderate database usage.
     */
    public static final PoolSize MEDIUM = new PoolSize(10, 30);

    /**
     * Large pool size: 20 minimum, 50 maximum connections.
     * Suitable for plugins with high database usage.
     */
    public static final PoolSize LARGE = new PoolSize(20, 50);

    public PoolSize {
        if (minimum < 1) {
            throw new IllegalArgumentException("Minimum pool size must be at least 1, but was: " + minimum);
        }
        if (maximum < minimum) {
            throw new IllegalArgumentException("Maximum pool size must be greater than or equal to minimum, but was: " + maximum + " (minimum: " + minimum + ")");
        }
    }

    /**
     * Creates a custom pool size with the given minimum and maximum.
     *
     * @param minimum the minimum number of connections
     * @param maximum the maximum number of connections
     * @return a new PoolSize instance
     * @throws IllegalArgumentException if minimum is less than 1 or maximum is less than minimum
     */
    public static PoolSize of(int minimum, int maximum) {
        return new PoolSize(minimum, maximum);
    }

    /**
     * Creates a pool size with a fixed number of connections (minimum equals maximum).
     *
     * @param size the fixed number of connections
     * @return a new PoolSize instance
     * @throws IllegalArgumentException if size is less than 1
     */
    public static PoolSize fixed(int size) {
        return new PoolSize(size, size);
    }
}

