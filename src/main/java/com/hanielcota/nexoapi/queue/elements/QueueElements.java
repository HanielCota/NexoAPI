package com.hanielcota.nexoapi.queue.elements;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

/**
 * First-class collection for queue elements.
 * Encapsulates all operations related to the queue's element collection.
 *
 * @param <T> the type of elements in the queue
 * @since 1.0.0
 */
public record QueueElements<T>(@NotNull ConcurrentLinkedDeque<T> deque) {

    /**
     * Creates a new empty QueueElements instance.
     *
     * @param <T> the type of elements
     * @return a new empty QueueElements instance
     */
    public static <T> QueueElements<T> empty() {
        return new QueueElements<>(new ConcurrentLinkedDeque<>());
    }

    /**
     * Adds an element to the queue.
     *
     * @param element the element to add
     * @return true if the element was added, false otherwise
     */
    public boolean offer(@Nullable T element) {
        if (element == null) {
            return false;
        }
        return deque.offer(element);
    }

    /**
     * Removes and returns the next element from the queue.
     *
     * @return the next element, or null if the queue is empty
     */
    @Nullable
    public T poll() {
        return deque.poll();
    }

    /**
     * Removes the specified element from the queue.
     *
     * @param element the element to remove
     */
    public void remove(@Nullable T element) {
        if (element == null) {
            return;
        }
        deque.remove(element);
    }

    /**
     * Returns the current size of the queue.
     *
     * @return the number of elements in the queue
     */
    public int size() {
        return deque.size();
    }

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return deque.isEmpty();
    }

    /**
     * Finds the position of an element in the queue.
     *
     * @param element the element to find
     * @return the position (1-based), or -1 if not found
     */
    public int findPosition(@Nullable T element) {
        if (element == null) {
            return -1;
        }
        return calculatePosition(element);
    }

    private int calculatePosition(T target) {
        int position = 1;
        for (T item : deque) {
            if (item.equals(target)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    /**
     * Processes all elements in the queue.
     * The queue will be empty after this operation.
     *
     * @param action the action to perform on each element
     */
    public void processAll(@NotNull Consumer<T> action) {
        T item;
        while ((item = poll()) != null) {
            action.accept(item);
        }
    }
}

