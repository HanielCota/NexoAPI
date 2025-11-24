package com.hanielcota.nexoapi.queue;

import com.hanielcota.nexoapi.queue.capacity.QueueCapacity;
import com.hanielcota.nexoapi.queue.elements.QueueElements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Thread-safe queue implementation with capacity management.
 * Supports both bounded and unbounded queues.
 * <p>
 * This queue follows Object Calisthenics principles:
 * - First-class collections for elements
 * - Encapsulated primitives
 * - Single responsibility per method
 * </p>
 *
 * @param <T> the type of elements in the queue
 * @since 1.0.0
 */
public record NexoQueue<T>(QueueCapacity capacity, QueueElements<T> elements) {

    /**
     * Creates a bounded queue with the specified maximum capacity.
     *
     * @param maxCapacity the maximum number of elements (must be greater than zero)
     * @param <T>         the type of elements
     * @return a new bounded NexoQueue instance
     * @throws IllegalArgumentException if maxCapacity is less than or equal to zero
     */
    public static <T> NexoQueue<T> bounded(int maxCapacity) {
        return new NexoQueue<>(
                QueueCapacity.of(maxCapacity),
                QueueElements.empty()
        );
    }

    /**
     * Creates an unbounded queue with unlimited capacity.
     *
     * @param <T> the type of elements
     * @return a new unbounded NexoQueue instance
     */
    public static <T> NexoQueue<T> unbounded() {
        return new NexoQueue<>(
                QueueCapacity.UNLIMITED,
                QueueElements.empty()
        );
    }

    /**
     * Adds an element to the queue.
     * Returns false if the element is null or if the capacity limit is reached.
     *
     * @param element the element to add
     * @return true if the element was added, false otherwise
     */
    public boolean add(@Nullable T element) {
        if (element == null) {
            return false;
        }

        if (isCapacityReached()) {
            return false;
        }

        return elements.offer(element);
    }

    /**
     * Processes the next element in the queue.
     * Does nothing if the queue is empty.
     *
     * @param action the action to perform on the element
     */
    public void processNext(@NotNull Consumer<T> action) {
        T item = elements.poll();
        if (item == null) {
            return;
        }

        action.accept(item);
    }

    /**
     * Processes all elements in the queue.
     * The queue will be empty after this operation.
     *
     * @param action the action to perform on each element
     */
    public void processAll(@NotNull Consumer<T> action) {
        elements.processAll(action);
    }

    /**
     * Removes the specified element from the queue.
     *
     * @param element the element to remove
     */
    public void remove(@Nullable T element) {
        elements.remove(element);
    }

    /**
     * Gets the position of an element in the queue.
     * Positions are 1-based (first element is at position 1).
     *
     * @param element the element to find
     * @return the position (1-based), or -1 if not found
     */
    public int getPosition(@Nullable T element) {
        return elements.findPosition(element);
    }

    /**
     * Returns the current size of the queue.
     *
     * @return the number of elements in the queue
     */
    public int size() {
        return elements.size();
    }

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    private boolean isCapacityReached() {
        int currentSize = elements.size();
        return capacity.isReachedBy(currentSize);
    }
}
