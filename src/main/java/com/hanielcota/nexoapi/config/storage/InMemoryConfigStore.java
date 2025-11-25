package com.hanielcota.nexoapi.config.storage;

import com.hanielcota.nexoapi.config.path.ConfigPath;
import lombok.NonNull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * In-memory storage for YAML configuration data.
 * This record wraps a YamlConfiguration and provides methods to load, update,
 * retrieve, and serialize configuration values.
 * <p>
 * All operations are thread-safe through synchronization.
 * </p>
 *
 * @param configuration the underlying YamlConfiguration instance
 * @since 1.0.0
 */
public record InMemoryConfigStore(@NonNull YamlConfiguration configuration) {

    private static final Object LOCK = new Object();

    /**
     * Creates a new InMemoryConfigStore with an empty YamlConfiguration.
     */
    public InMemoryConfigStore() {
        this(new YamlConfiguration());
    }

    /**
     * Loads configuration data from a file.
     * If the file is empty or doesn't exist, an empty configuration is created.
     * <p>
     * This operation is thread-safe and prevents TOCTOU race conditions.
     * </p>
     *
     * @param source the source file to load from
     * @throws RuntimeException if an I/O error occurs or the configuration is invalid
     */
    public void load(@NonNull File source) {
        synchronized (LOCK) {
            // Check file existence inside synchronized block to prevent TOCTOU race condition
            if (!source.exists() || source.length() == 0) {
                return;
            }
            try {
                configuration.load(source);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config file: " + source.getPath(), e);
            } catch (InvalidConfigurationException e) {
                throw new RuntimeException("Invalid configuration file: " + source.getPath(), e);
            }
        }
    }

    /**
     * Updates a configuration value at the specified path.
     * <p>
     * This operation is thread-safe.
     * </p>
     *
     * @param path  the configuration path
     * @param value the value to set
     */
    public void update(@NonNull ConfigPath path, Object value) {
        synchronized (LOCK) {
            configuration.set(path.value(), value);
        }
    }

    /**
     * Retrieves a configuration value at the specified path.
     * Returns the default value if the path doesn't exist.
     * <p>
     * This operation is thread-safe and includes type validation.
     * If the actual value type doesn't match the expected type, a clear
     * ClassCastException will be thrown with detailed information.
     * </p>
     *
     * @param path         the configuration path
     * @param defaultValue the default value to return if the path doesn't exist
     * @param <T>          the type of the value
     * @return the configuration value or the default value
     * @throws ClassCastException if the actual value type doesn't match the expected type
     */
    @SuppressWarnings("unchecked")
    public <T> T retrieve(@NonNull ConfigPath path, T defaultValue) {
        synchronized (LOCK) {
            var result = configuration.get(path.value());

            if (result == null) {
                return defaultValue;
            }

            // Type validation for better error messages
            if (defaultValue != null && !defaultValue.getClass().isInstance(result)) {
                throw new ClassCastException(
                        String.format(
                                "Type mismatch for path '%s': expected %s but got %s",
                                path.value(),
                                defaultValue.getClass().getName(),
                                result.getClass().getName()
                        )
                );
            }

            return (T) result;
        }
    }

    /**
     * Serializes the configuration to a YAML string.
     * <p>
     * This operation is thread-safe.
     * </p>
     *
     * @return the YAML string representation of the configuration
     */
    public String serialize() {
        synchronized (LOCK) {
            return configuration.saveToString();
        }
    }
}