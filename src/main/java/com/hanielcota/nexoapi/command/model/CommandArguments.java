package com.hanielcota.nexoapi.command.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * First-class collection for command arguments.
 * Encapsulates all operations related to command arguments.
 *
 * @param values the list of argument strings (immutable)
 * @since 1.0.0
 */
public record CommandArguments(@NotNull List<String> values) {
    public CommandArguments {
        Objects.requireNonNull(values, "Values cannot be null.");
        values = List.copyOf(values);
    }

    /**
     * Creates an empty CommandArguments instance.
     *
     * @return an empty CommandArguments
     */
    public static CommandArguments empty() {
        return new CommandArguments(List.of());
    }

    /**
     * Checks if there are no arguments.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * Returns the number of arguments.
     *
     * @return the size
     */
    public int size() {
        return values.size();
    }

    /**
     * Gets the first argument, or null if empty.
     *
     * @return the first argument string, or null if empty
     */
    @Nullable
    public String firstOrNull() {
        if (isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    /**
     * Gets the argument at the specified index.
     *
     * @param index the index (0-based)
     * @return the argument string, or null if index is out of bounds
     */
    @Nullable
    public String argumentAt(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative.");
        }
        if (index >= values.size()) {
            return null;
        }
        return values.get(index);
    }

    /**
     * Returns a new CommandArguments without the first argument.
     *
     * @return a new CommandArguments instance
     */
    public CommandArguments withoutFirst() {
        if (values.isEmpty()) {
            return this;
        }
        List<String> tailValues = values.subList(1, values.size());
        return new CommandArguments(List.copyOf(tailValues));
    }

    /**
     * Performs an action if an argument exists at the specified index.
     *
     * @param index  the index to check
     * @param action the action to perform with the argument
     */
    public void ifPresent(int index, @NotNull Consumer<String> action) {
        Objects.requireNonNull(action, "Action cannot be null.");
        if (index >= 0 && index < values.size()) {
            action.accept(values.get(index));
        }
    }

    /**
     * Maps the first argument using the provided mapper function.
     *
     * @param mapper the mapper function
     * @param <T>    the result type
     * @return the mapped value, or null if no first argument exists
     */
    @Nullable
    public <T> T mapFirst(@NotNull Function<String, T> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null.");
        var first = firstOrNull();
        if (first == null) {
            return null;
        }
        return mapper.apply(first);
    }

    /**
     * Returns all arguments as an immutable list.
     *
     * @return an immutable list of arguments
     */
    @NotNull
    public List<String> all() {
        return values;
    }
}
