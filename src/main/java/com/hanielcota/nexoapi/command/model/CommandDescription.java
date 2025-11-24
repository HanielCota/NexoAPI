package com.hanielcota.nexoapi.command.model;

import java.util.Objects;

/**
 * Represents a command description.
 * Empty string is allowed for commands without description.
 *
 * @param value the description text (trimmed, never null)
 * @since 1.0.0
 */
public record CommandDescription(String value) {

    public CommandDescription {
        value = value == null ? "" : value.trim();
    }

    /**
     * Creates a new CommandDescription from a raw string.
     *
     * @param rawValue the raw description string (may be null)
     * @return a new CommandDescription instance
     */
    public static CommandDescription from(String rawValue) {
        return new CommandDescription(Objects.requireNonNullElse(rawValue, ""));
    }

    /**
     * Checks if the description is empty.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }
}

