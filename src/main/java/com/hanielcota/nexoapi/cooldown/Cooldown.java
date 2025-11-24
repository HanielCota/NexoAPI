package com.hanielcota.nexoapi.cooldown;

import com.hanielcota.nexoapi.cooldown.property.CooldownExpiration;
import com.hanielcota.nexoapi.cooldown.property.CooldownKey;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record Cooldown(
        @NotNull CooldownKey key,
        @NotNull CooldownExpiration expiration
) {

    public Cooldown {
        Objects.requireNonNull(key, "Cooldown key cannot be null.");
        Objects.requireNonNull(expiration, "Cooldown expiration cannot be null.");
    }

    public boolean isExpired(@NotNull Instant now) {
        return expiration.isExpired(now);
    }

    public Duration remaining(@NotNull Instant now) {
        return expiration.remainingUntil(now);
    }
}
