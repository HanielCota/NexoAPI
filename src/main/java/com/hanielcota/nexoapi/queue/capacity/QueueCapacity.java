package com.hanielcota.nexoapi.queue.capacity;

/**
 * Represents the capacity limit of a queue.
 * Encapsulates the capacity value and provides validation.
 *
 * @param limit the capacity limit (-1 for unlimited)
 * @since 1.0.0
 */
public record QueueCapacity(int limit) {

    private static final int UNLIMITED_VALUE = -1;

    /**
     * Represents an unlimited queue capacity.
     */
    public static final QueueCapacity UNLIMITED = new QueueCapacity(UNLIMITED_VALUE);

    /**
     * Compact constructor that validates the capacity limit.
     *
     * @param limit the capacity limit
     * @throws IllegalArgumentException if limit is zero
     */
    public QueueCapacity {
        if (limit == 0) {
            throw new IllegalArgumentException("Capacity cannot be zero.");
        }
    }

    /**
     * Creates a new QueueCapacity with the specified limit.
     *
     * @param limit the capacity limit (must be greater than zero)
     * @return a new QueueCapacity instance
     * @throws IllegalArgumentException if limit is less than or equal to zero
     */
    public static QueueCapacity of(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        return new QueueCapacity(limit);
    }

    /**
     * Checks if this capacity is unlimited.
     *
     * @return true if unlimited, false otherwise
     */
    public boolean isUnlimited() {
        return limit == UNLIMITED_VALUE;
    }

    /**
     * Checks if the capacity has been reached by the current size.
     *
     * @param currentSize the current size of the queue
     * @return true if capacity is reached, false otherwise
     */
    public boolean isReachedBy(int currentSize) {
        if (isUnlimited()) {
            return false;
        }
        return currentSize >= limit;
    }
}
