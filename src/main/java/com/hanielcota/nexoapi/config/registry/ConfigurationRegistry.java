package com.hanielcota.nexoapi.config.registry;

import com.hanielcota.nexoapi.config.domain.ConfigEntry;
import com.hanielcota.nexoapi.config.domain.ConfigurationKey;
import com.hanielcota.nexoapi.config.exception.UnregisteredKeyException;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public final class ConfigurationRegistry {
    private final Map<String, ConfigEntry<?>> entries;

    public ConfigurationRegistry() {
        this.entries = new HashMap<>();
    }

    public void register(@NonNull ConfigEntry<?> entry) {
        entries.put(entry.key().toString(), entry);
    }

    public ConfigEntry<?> find(@NonNull ConfigurationKey key) {
        String identity = key.toString();

        if (!entries.containsKey(identity)) {
            throw new UnregisteredKeyException(identity);
        }

        return entries.get(identity);
    }

    public Map<String, Object> mapDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        entries.values().forEach(entry ->
                defaults.put(entry.key().toString(), entry.defaultValue())
        );
        return defaults;
    }
}