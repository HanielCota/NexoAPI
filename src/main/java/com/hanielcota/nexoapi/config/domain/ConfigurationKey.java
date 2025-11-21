package com.hanielcota.nexoapi.config.domain;

import com.hanielcota.nexoapi.config.exception.InvalidKeyFormatException;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public record ConfigurationKey(@NonNull String identity) {

    public ConfigurationKey {
        if (identity.isBlank()) {
            throw new InvalidKeyFormatException("[Vazia]");
        }
    }

    @Override
    public @NotNull String toString() {
        return identity;
    }
}