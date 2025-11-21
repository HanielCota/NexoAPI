package com.hanielcota.nexoapi.config.storage;

import com.hanielcota.nexoapi.config.path.ConfigPath;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * In-memory storage for YAML configuration data.
 * This record wraps a YamlConfiguration and provides methods to load, update,
 * retrieve, and serialize configuration values.
 *
 * @param configuration the underlying YamlConfiguration instance
 * @since 1.0.0
 */
public record InMemoryConfigStore(@NonNull YamlConfiguration configuration) {

    /**
     * Creates a new InMemoryConfigStore with an empty YamlConfiguration.
     */
    public InMemoryConfigStore() {
        this(new YamlConfiguration());
    }

    /**
     * Loads configuration data from a file.
     *
     * @param source the source file to load from
     */
    @SneakyThrows
    public void load(@NonNull File source) {
        configuration.load(source);
    }

    /**
     * Updates a configuration value at the specified path.
     *
     * @param path  the configuration path
     * @param value the value to set
     */
    public void update(@NonNull ConfigPath path, Object value) {
        configuration.set(path.value(), value);
    }

    /**
     * Retrieves a configuration value at the specified path.
     * Returns the default value if the path doesn't exist.
     *
     * @param path         the configuration path
     * @param defaultValue the default value to return if the path doesn't exist
     * @param <T>          the type of the value
     * @return the configuration value or the default value
     */
    @SuppressWarnings("unchecked")
    public <T> T retrieve(@NonNull ConfigPath path, T defaultValue) {
        var result = configuration.get(path.value());

        if (result == null) {
            return defaultValue;
        }

        return (T) result;
    }

    /**
     * Serializes the configuration to a YAML string.
     *
     * @return the YAML string representation of the configuration
     */
    public String serialize() {
        return configuration.saveToString();
    }
}