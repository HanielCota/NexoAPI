package com.hanielcota.nexoapi.config.domain;

import lombok.NonNull;

public record ConfigEntry<T>(@NonNull ConfigurationKey key, @NonNull ValueSpecification<T> specification) {

    public T defaultValue() {
        return specification.defaultValue();
    }

    public void validate(@NonNull T value) {
        specification.ensureCompliance(value, key);
    }
}