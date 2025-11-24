package com.hanielcota.nexoapi.command.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * First-class collection for command arguments.
 * Encapsulates all operations related to command arguments.
 *
 * @param values the list of argument strings (immutable)
 * @since 1.0.0
 */
public record CommandArguments(@NotNull List<String> values) {
    public CommandArguments {
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
}
