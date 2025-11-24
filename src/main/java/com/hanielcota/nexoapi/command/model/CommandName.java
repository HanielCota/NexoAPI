package com.hanielcota.nexoapi.command.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a validated command name.
 * Encapsulates the command name string and ensures it's not blank.
 *
 * @param value the command name (lowercase, trimmed)
 * @since 1.0.0
 */
public record CommandName(@NotNull String value) {

    public CommandName {
        Objects.requireNonNull(value, "Command name cannot be null.");
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException("Command name cannot be blank.");
        }
        value = trimmedValue.toLowerCase();
    }

    /**
     * Creates a new CommandName from a raw string.
     *
     * @param rawValue the raw command name string
     * @return a new CommandName instance
     * @throws IllegalArgumentException if the value is blank
     */
    public static CommandName from(String rawValue) {
        return new CommandName(rawValue);
    }
}

