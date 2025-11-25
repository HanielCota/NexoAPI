package com.hanielcota.nexoapi.command.model;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * First-class collection for command aliases.
 * Encapsulates all operations related to command aliases.
 *
 * @param values the immutable list of alias strings
 * @since 1.0.0
 */
public record CommandAliases(@NotNull List<String> values) implements Iterable<String> {

    public CommandAliases {
        Objects.requireNonNull(values, "Aliases cannot be null.");
        values = List.copyOf(values);
    }

    /**
     * Creates an empty CommandAliases instance.
     *
     * @return an empty CommandAliases
     */
    public static CommandAliases empty() {
        return new CommandAliases(List.of());
    }

    /**
     * Creates CommandAliases from an array of strings.
     *
     * @param rawAliases the raw alias strings
     * @return a new CommandAliases instance
     */
    public static CommandAliases from(@NotNull String[] rawAliases) {
        Objects.requireNonNull(rawAliases, "Raw aliases cannot be null.");
        if (rawAliases.length == 0) {
            return empty();
        }
        return new CommandAliases(List.of(rawAliases));
    }

    /**
     * Creates CommandAliases from a list of strings.
     *
     * @param rawAliases the raw alias strings
     * @return a new CommandAliases instance
     */
    public static CommandAliases from(@NotNull List<String> rawAliases) {
        Objects.requireNonNull(rawAliases, "Raw aliases cannot be null.");
        if (rawAliases.isEmpty()) {
            return empty();
        }
        return new CommandAliases(rawAliases);
    }

    /**
     * Checks if there are no aliases.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * Returns the number of aliases.
     *
     * @return the size
     */
    public int size() {
        return values.size();
    }

    /**
     * Performs an action for each non-blank alias.
     *
     * @param action the action to perform
     */
    public void forEachNonBlank(@NotNull Consumer<String> action) {
        Objects.requireNonNull(action, "Action cannot be null.");
        values.stream()
                .filter(alias -> alias != null && !alias.isBlank())
                .forEach(action);
    }

    @Override
    public Iterator<String> iterator() {
        return values.iterator();
    }

    /**
     * Returns all aliases as an immutable list.
     *
     * @return an immutable list of aliases
     */
    @NotNull
    public List<String> all() {
        return values;
    }
}

