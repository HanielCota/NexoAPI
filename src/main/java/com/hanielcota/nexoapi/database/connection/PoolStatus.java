package com.hanielcota.nexoapi.database.connection;

/**
 * Represents the status of a database connection pool.
 * Provides information about active, idle, and total connections.
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @param active      the number of active (in-use) connections
 * @param idle        the number of idle (available) connections
 * @param total       the total number of connections in the pool
 * @param awaiting    the number of threads waiting for a connection
 * @param maxPoolSize the maximum pool size configured
 * @since 1.0.0
 */
public record PoolStatus(int active, int idle, int total, int awaiting, int maxPoolSize) {

    /**
     * Checks if the pool is healthy (has available connections).
     *
     * @return true if there are idle connections available
     */
    public boolean isHealthy() {
        return idle > 0;
    }

    /**
     * Checks if the pool is at maximum capacity.
     *
     * @return true if total connections equals max pool size
     */
    public boolean isAtMaxCapacity() {
        return total >= maxPoolSize;
    }

    /**
     * Checks if there are threads waiting for connections.
     *
     * @return true if threads are awaiting connections
     */
    public boolean hasWaitingThreads() {
        return awaiting > 0;
    }

    /**
     * Calculates the pool utilization as a percentage.
     *
     * @return the pool utilization (0.0 to 1.0)
     */
    public double utilization() {
        if (maxPoolSize == 0) {
            return 0.0;
        }
        return (double) active / maxPoolSize;
    }

    /**
     * Returns a formatted string representation of the pool status.
     *
     * @return a formatted status string
     */
    @Override
    public String toString() {
        return String.format("PoolStatus{active=%d, idle=%d, total=%d, awaiting=%d, max=%d, utilization=%.1f%%}",
                active, idle, total, awaiting, maxPoolSize, utilization() * 100);
    }
}

