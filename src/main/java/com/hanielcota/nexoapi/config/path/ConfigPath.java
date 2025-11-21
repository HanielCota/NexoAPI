package com.hanielcota.nexoapi.config.path;

import lombok.NonNull;

/**
 * Represents a configuration path used to access configuration values.
 * The path is validated to ensure it's not blank.
 *
 * @param value the configuration path (e.g., "database.host")
 * @since 1.0.0
 */
public record ConfigPath(@NonNull String value) {
    /**
     * Compact constructor that validates the path is not blank.
     *
     * @param value the configuration path
     * @throws IllegalArgumentException if the path is blank
     */
    public ConfigPath {
        if (value.isBlank()) {
            throw new IllegalArgumentException("Configuration path cannot be blank");
        }
    }
}