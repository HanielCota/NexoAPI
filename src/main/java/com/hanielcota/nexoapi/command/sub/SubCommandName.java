package com.hanielcota.nexoapi.command.sub;

import com.hanielcota.nexoapi.validation.NonEmptyString;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a validated subcommand name.
 * Encapsulates the subcommand name string and ensures it's not blank.
 *
 * @param value the subcommand name (lowercase, trimmed)
 * @since 1.0.0
 */
public record SubCommandName(@NotNull String value) {
    public SubCommandName {
        value = NonEmptyString.validateAndNormalize(value, "Subcommand name");
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

