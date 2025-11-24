package com.hanielcota.nexoapi.cooldown.property;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record CooldownId(@NotNull String value) {

    public CooldownId {
        Objects.requireNonNull(value, "Cooldown id cannot be null.");

        if (value.isBlank()) {
            throw new IllegalArgumentException("Cooldown id cannot be blank.");
        }
    }

    public static CooldownId of(@NotNull String value) {
        return new CooldownId(value);
    }

    @Override
    public @NotNull String toString() {
        return value;
    }
}
