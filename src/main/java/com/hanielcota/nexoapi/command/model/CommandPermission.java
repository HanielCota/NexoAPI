package com.hanielcota.nexoapi.command.model;

import java.util.Objects;

/**
 * Represents a command permission.
 * Empty string means no permission required.
 *
 * @param value the permission string (trimmed, never null)
 * @since 1.0.0
 */
public record CommandPermission(String value) {
    public CommandPermission {
        value = value == null ? "" : value.trim();
    }

    /**
     * Creates a new CommandPermission from a raw string.
     *
     * @param rawValue the raw permission string (may be null)
     * @return a new CommandPermission instance
     */
    public static CommandPermission from(String rawValue) {
        return new CommandPermission(Objects.requireNonNullElse(rawValue, ""));
    }

    /**
     * Checks if a permission is required (not empty).
     *
     * @return true if permission is required, false otherwise
     */
    public boolean isRequired() {
        return !value.isEmpty();
    }
}

