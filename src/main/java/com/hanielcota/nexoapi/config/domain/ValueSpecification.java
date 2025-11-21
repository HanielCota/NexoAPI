package com.hanielcota.nexoapi.config.domain;

import com.hanielcota.nexoapi.config.exception.InvalidConfigValueException;
import lombok.NonNull;

import java.util.function.Predicate;

public record ValueSpecification<T>(@NonNull T defaultValue, Predicate<T> validator) {

    public void ensureCompliance(T value, ConfigurationKey key) {
        if (validator == null) {
            return;
        }

        if (validator.test(value)) {
            return;
        }

        throw new InvalidConfigValueException(key.toString(), value, "Não atende aos critérios de validação");
    }
}