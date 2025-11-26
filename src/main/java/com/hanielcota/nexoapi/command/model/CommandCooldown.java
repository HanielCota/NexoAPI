package com.hanielcota.nexoapi.command.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the cooldown configuration for a command.
 * Cooldown is measured in seconds.
 *
 * @param seconds the cooldown duration in seconds
 * @since 1.0.0
 */
public record CommandCooldown(long seconds) {
    public CommandCooldown {
        if (seconds < 0) {
            throw new IllegalArgumentException("Cooldown cannot be negative: " + seconds);
        }
    }

    /**
     * Creates a CommandCooldown from a number of seconds.
     *
     * @param seconds the cooldown duration in seconds
     * @return a new CommandCooldown instance
     */
    @NotNull
    public static CommandCooldown from(long seconds) {
        return new CommandCooldown(seconds);
    }

    /**
     * Creates a CommandCooldown with no cooldown (0 seconds).
     *
     * @return a CommandCooldown with 0 seconds
     */
    @NotNull
    public static CommandCooldown none() {
        return new CommandCooldown(0);
    }

    /**
     * Checks if this cooldown is active (greater than 0).
     *
     * @return true if cooldown is active, false otherwise
     */
    public boolean isActive() {
        return seconds > 0;
    }
}

