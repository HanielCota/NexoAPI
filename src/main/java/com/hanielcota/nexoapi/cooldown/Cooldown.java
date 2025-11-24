package com.hanielcota.nexoapi.cooldown;

import com.hanielcota.nexoapi.cooldown.property.CooldownExpiration;
import com.hanielcota.nexoapi.cooldown.property.CooldownKey;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents a cooldown with a key and expiration time.
 * Provides methods to check expiration and calculate remaining time.
 *
 * @param key        the cooldown key identifying the cooldown
 * @param expiration the expiration time of the cooldown
 * @since 1.0.0
 */
public record Cooldown(
        @NotNull CooldownKey key,
        @NotNull CooldownExpiration expiration
) {

    public Cooldown {
        Objects.requireNonNull(key, "Cooldown key cannot be null.");
        Objects.requireNonNull(expiration, "Cooldown expiration cannot be null.");
    }

    /**
     * Checks if the cooldown has expired.
     *
     * @param now the current instant to compare against
     * @return true if expired, false otherwise
     * @throws NullPointerException if now is null
     */
    public boolean isExpired(@NotNull Instant now) {
        return expiration.isExpired(now);
    }

    /**
     * Calculates the remaining duration until the cooldown expires.
     *
     * @param now the current instant
     * @return the remaining duration, or Duration.ZERO if already expired
     * @throws NullPointerException if now is null
     */
    public Duration remaining(@NotNull Instant now) {
        return expiration.remainingUntil(now);
    }
}
