package com.hanielcota.nexoapi.config;

import com.hanielcota.nexoapi.config.adapter.NumberNormalizer;
import com.hanielcota.nexoapi.config.domain.ConfigEntry;
import com.hanielcota.nexoapi.config.domain.ConfigurationKey;
import com.hanielcota.nexoapi.config.exception.InvalidConfigValueException;
import com.hanielcota.nexoapi.config.persistence.YamlPersistence;
import com.hanielcota.nexoapi.config.registry.ConfigurationRegistry;
import lombok.NonNull;

public final class ConfigSchema {
    private final ConfigurationRegistry registry;
    private final YamlPersistence persistence;
    private final NumberNormalizer normalizer;

    public ConfigSchema(@NonNull ConfigurationRegistry registry, @NonNull YamlPersistence persistence) {
        this.registry = registry;
        this.persistence = persistence;
        this.normalizer = new NumberNormalizer();
        initialize();
    }

    private void initialize() {
        var defaults = registry.mapDefaults();
        persistence.applyDefaults(defaults);
    }

    public <T> T retrieve(@NonNull ConfigurationKey key, @NonNull Class<T> type) {
        var entry = registry.find(key);
        var rawValue = persistence.read(key.toString());
        var normalized = normalizer.normalize(rawValue, type);

        return safeCastAndValidate(normalized, type, entry);
    }

    public void update(@NonNull ConfigurationKey key, @NonNull Object value) {
        var entry = registry.find(key);

        @SuppressWarnings("unchecked")
        var typedEntry = (ConfigEntry<Object>) entry;

        typedEntry.validate(value);
        persistence.write(key.toString(), value);
        persistence.save();
    }

    private <T> T safeCastAndValidate(Object raw, Class<T> type, ConfigEntry<?> entry) {
        try {
            T value = type.cast(raw);
            @SuppressWarnings("unchecked")
            ConfigEntry<T> typedEntry = (ConfigEntry<T>) entry;
            typedEntry.validate(value);
            return value;
        } catch (ClassCastException e) {
            throw new InvalidConfigValueException(entry.key().toString(), raw, "Tipo incompatível no arquivo");
        }
    }
}