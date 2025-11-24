package com.hanielcota.nexoapi.cooldown.property;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Objects;

public record CooldownDuration(@NotNull Duration value) {

    public CooldownDuration {
        Objects.requireNonNull(value, "Cooldown duration cannot be null.");

        if (value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException("Cooldown duration must be positive.");
        }
    }

    public static CooldownDuration ofSeconds(long seconds) {
        var duration = Duration.ofSeconds(seconds);
        return new CooldownDuration(duration);
    }

    public static CooldownDuration ofMillis(long millis) {
        var duration = Duration.ofMillis(millis);
        return new CooldownDuration(duration);
    }

    public static CooldownDuration ofTicks(long ticks) {
        var millis = ticks * 50L;
        var duration = Duration.ofMillis(millis);
        return new CooldownDuration(duration);
    }

    public long toTicks() {
        var millis = value.toMillis();
        long ticks = millis / 50L;
        if (millis % 50L != 0 && ticks == 0) {
            return 1;
        }
        return ticks;
    }
}
