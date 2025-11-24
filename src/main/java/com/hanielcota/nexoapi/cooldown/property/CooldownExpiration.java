package com.hanielcota.nexoapi.cooldown.property;

import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record CooldownExpiration(@NotNull Instant value) {

    public CooldownExpiration {
        Objects.requireNonNull(value, "Cooldown expiration instant cannot be null.");
    }

    public static CooldownExpiration at(@NotNull Instant instant) {
        return new CooldownExpiration(instant);
    }

    public static CooldownExpiration fromNow(@NotNull CooldownDuration duration, @NotNull Clock clock) {
        Objects.requireNonNull(duration, "Cooldown duration cannot be null.");
        Objects.requireNonNull(clock, "Clock cannot be null.");

        var now = Instant.now(clock);
        var expirationInstant = now.plus(duration.value());
        return new CooldownExpiration(expirationInstant);
    }

    public boolean isExpired(@NotNull Instant now) {
        Objects.requireNonNull(now, "Now instant cannot be null.");

        return !now.isBefore(value);
    }

    public Duration remainingUntil(@NotNull Instant now) {
        Objects.requireNonNull(now, "Now instant cannot be null.");

        if (isExpired(now)) {
            return Duration.ZERO;
        }

        return Duration.between(now, value);
    }
}
