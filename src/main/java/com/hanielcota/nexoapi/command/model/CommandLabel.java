package com.hanielcota.nexoapi.command.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a validated command label.
 * Encapsulates the command label string and ensures it's not blank.
 *
 * @param value the command label (lowercase, trimmed)
 * @since 1.0.0
 */
public record CommandLabel(@NotNull String value) {

    public CommandLabel {
        Objects.requireNonNull(value, "Command label cannot be null.");
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException("Command label cannot be blank.");
        }
        value = trimmedValue.toLowerCase();
    }

    /**
     * Creates a new CommandLabel from a raw string.
     *
     * @param rawValue the raw command label string
     * @return a new CommandLabel instance
     * @throws IllegalArgumentException if the value is blank
     */
    public static CommandLabel from(String rawValue) {
        return new CommandLabel(rawValue);
    }
}

