package com.hanielcota.nexoapi.config.builder;

import com.hanielcota.nexoapi.config.ConfigSchema;
import com.hanielcota.nexoapi.config.domain.ConfigEntry;
import com.hanielcota.nexoapi.config.domain.ConfigurationKey;
import com.hanielcota.nexoapi.config.domain.ValueSpecification;
import com.hanielcota.nexoapi.config.exception.ConfigurationInitializationException;
import com.hanielcota.nexoapi.config.persistence.YamlPersistence;
import com.hanielcota.nexoapi.config.registry.ConfigurationRegistry;
import lombok.NonNull;

import java.io.File;
import java.util.function.Predicate;

public final class ConfigSchemaBuilder {

    private final ConfigurationRegistry registry;
    private File targetFile;

    private ConfigSchemaBuilder() {
        this.registry = new ConfigurationRegistry();
    }

    public static ConfigSchemaBuilder create() {
        return new ConfigSchemaBuilder();
    }

    public ConfigSchemaBuilder withFile(@NonNull File file) {
        this.targetFile = file;
        return this;
    }

    public <T> ConfigSchemaBuilder addEntry(@NonNull String key, @NonNull T defaultValue, Predicate<T> validator) {
        var configKey = new ConfigurationKey(key);
        var spec = new ValueSpecification<>(defaultValue, validator);
        var entry = new ConfigEntry<>(configKey, spec);

        registry.register(entry);
        return this;
    }

    public <T> ConfigSchemaBuilder addEntry(@NonNull String key, @NonNull T defaultValue) {
        return addEntry(key, defaultValue, null);
    }

    public ConfigSchema build() {
        if (targetFile == null) {
            throw new ConfigurationInitializationException("Arquivo não definido. Use .withFile()");
        }

        var persistence = new YamlPersistence(targetFile);
        return new ConfigSchema(registry, persistence);
    }
}