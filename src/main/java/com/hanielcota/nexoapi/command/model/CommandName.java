package com.hanielcota.nexoapi.command.model;

import com.hanielcota.nexoapi.validation.NonEmptyString;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a validated command name.
 * Encapsulates the command name string and ensures it's not blank.
 *
 * @param value the command name (lowercase, trimmed)
 * @since 1.0.0
 */
public record CommandName(@NotNull String value) {

    public CommandName {
        value = NonEmptyString.validateAndNormalize(value, "Command name");
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

