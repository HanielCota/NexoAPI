package com.hanielcota.nexoapi.command.model;

import com.hanielcota.nexoapi.validation.NonEmptyString;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a validated command label.
 * Encapsulates the command label string and ensures it's not blank.
 *
 * @param value the command label (lowercase, trimmed)
 * @since 1.0.0
 */
public record CommandLabel(@NotNull String value) {

    public CommandLabel {
        value = NonEmptyString.validateAndNormalize(value, "Command label");
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

