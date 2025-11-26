package com.hanielcota.nexoapi.command.model;

import com.hanielcota.nexoapi.validation.OptionalString;

/**
 * Represents a command permission.
 * Empty string means no permission required.
 *
 * @param value the permission string (trimmed, never null)
 * @since 1.0.0
 */
public record CommandPermission(String value) {
    public CommandPermission {
        value = OptionalString.normalize(value);
    }

    /**
     * Creates a new CommandPermission from a raw string.
     *
     * @param rawValue the raw permission string (may be null)
     * @return a new CommandPermission instance
     */
    public static CommandPermission from(String rawValue) {
        return new CommandPermission(rawValue);
    }

    /**
     * Checks if a permission is required (not empty).
     *
     * @return true if permission is required, false otherwise
     */
    public boolean isRequired() {
        return OptionalString.isPresent(value);
    }
}

