package com.hanielcota.nexoapi.command.sub;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a validated subcommand name.
 * Encapsulates the subcommand name string and ensures it's not blank.
 *
 * @param value the subcommand name (lowercase, trimmed)
 * @since 1.0.0
 */
public record SubCommandName(@NotNull String value) {
    public SubCommandName {
        Objects.requireNonNull(value, "Subcommand name cannot be null.");
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException("Subcommand name cannot be blank.");
        }
        value = trimmedValue.toLowerCase();
    }

    /**
     * Creates a new SubCommandName from a raw string.
     *
     * @param rawValue the raw subcommand name string
     * @return a new SubCommandName instance
     * @throws IllegalArgumentException if the value is blank
     */
    public static SubCommandName from(String rawValue) {
        return new SubCommandName(rawValue);
    }
}

