package com.hanielcota.nexoapi.database.connection;

import com.hanielcota.nexoapi.database.DatabaseType;
import com.hanielcota.nexoapi.database.property.ConnectionTimeout;
import com.hanielcota.nexoapi.database.property.DatabaseCredentials;
import com.hanielcota.nexoapi.database.property.DatabaseUrl;
import com.hanielcota.nexoapi.database.property.PoolSize;
import lombok.NonNull;

import java.util.Objects;

/**
 * Configuration for database connections and connection pooling.
 * This is a value object that encapsulates all database connection settings.
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @param url         the database URL
 * @param credentials the database credentials
 * @param poolSize    the connection pool size
 * @param timeout     the connection timeout
 * @param type        the database type (can be null for auto-detection)
 * @since 1.0.0
 */
public record ConnectionConfig(@NonNull DatabaseUrl url,
                                @NonNull DatabaseCredentials credentials,
                                @NonNull PoolSize poolSize,
                                @NonNull ConnectionTimeout timeout,
                                DatabaseType type) {

    public ConnectionConfig {
        Objects.requireNonNull(url, "Database URL cannot be null");
        Objects.requireNonNull(credentials, "Database credentials cannot be null");
        Objects.requireNonNull(poolSize, "Pool size cannot be null");
        Objects.requireNonNull(timeout, "Connection timeout cannot be null");
    }

    /**
     * Creates a connection config with default pool size and timeout.
     *
     * @param url         the database URL
     * @param credentials the database credentials
     * @return a new ConnectionConfig instance with defaults
     */
    public static ConnectionConfig of(@NonNull DatabaseUrl url, @NonNull DatabaseCredentials credentials) {
        return new ConnectionConfig(url, credentials, PoolSize.DEFAULT, ConnectionTimeout.DEFAULT, null);
    }

    /**
     * Creates a connection config with custom pool size and default timeout.
     *
     * @param url         the database URL
     * @param credentials the database credentials
     * @param poolSize    the connection pool size
     * @return a new ConnectionConfig instance
     */
    public static ConnectionConfig of(@NonNull DatabaseUrl url, @NonNull DatabaseCredentials credentials, @NonNull PoolSize poolSize) {
        return new ConnectionConfig(url, credentials, poolSize, ConnectionTimeout.DEFAULT, null);
    }

    /**
     * Creates a connection config with custom pool size and timeout.
     *
     * @param url         the database URL
     * @param credentials the database credentials
     * @param poolSize    the connection pool size
     * @param timeout     the connection timeout
     * @return a new ConnectionConfig instance
     */
    public static ConnectionConfig of(@NonNull DatabaseUrl url, @NonNull DatabaseCredentials credentials, @NonNull PoolSize poolSize, @NonNull ConnectionTimeout timeout) {
        return new ConnectionConfig(url, credentials, poolSize, timeout, null);
    }

    /**
     * Creates a connection config with explicit database type.
     *
     * @param url         the database URL
     * @param credentials the database credentials
     * @param poolSize    the connection pool size
     * @param timeout     the connection timeout
     * @param type        the database type
     * @return a new ConnectionConfig instance
     */
    public static ConnectionConfig of(@NonNull DatabaseUrl url, @NonNull DatabaseCredentials credentials, @NonNull PoolSize poolSize, @NonNull ConnectionTimeout timeout, DatabaseType type) {
        return new ConnectionConfig(url, credentials, poolSize, timeout, type);
    }

    /**
     * Gets the database type, auto-detecting it from the URL if not explicitly set.
     *
     * @return the database type, or null if it cannot be determined
     */
    public DatabaseType getType() {
        if (type != null) {
            return type;
        }
        return DatabaseType.fromUrl(url.url());
    }

    /**
     * Creates a new config with a different pool size.
     *
     * @param poolSize the new pool size
     * @return a new ConnectionConfig instance
     */
    public ConnectionConfig withPoolSize(@NonNull PoolSize poolSize) {
        return new ConnectionConfig(url, credentials, poolSize, timeout, type);
    }

    /**
     * Creates a new config with a different timeout.
     *
     * @param timeout the new timeout
     * @return a new ConnectionConfig instance
     */
    public ConnectionConfig withTimeout(@NonNull ConnectionTimeout timeout) {
        return new ConnectionConfig(url, credentials, poolSize, timeout, type);
    }

    /**
     * Creates a new config with a different database type.
     *
     * @param type the new database type
     * @return a new ConnectionConfig instance
     */
    public ConnectionConfig withType(DatabaseType type) {
        return new ConnectionConfig(url, credentials, poolSize, timeout, type);
    }
}

