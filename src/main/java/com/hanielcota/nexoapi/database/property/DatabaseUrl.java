package com.hanielcota.nexoapi.database.property;

import lombok.NonNull;

import java.util.Objects;

/**
 * Represents a database connection URL.
 * This is a value object that encapsulates the JDBC connection string.
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @param url the JDBC connection URL
 * @since 1.0.0
 */
public record DatabaseUrl(@NonNull String url) {

    public DatabaseUrl {
        Objects.requireNonNull(url, "Database URL cannot be null");
        if (url.isBlank()) {
            throw new IllegalArgumentException("Database URL cannot be empty");
        }
    }

    /**
     * Creates a database URL from a string.
     *
     * @param url the JDBC connection URL
     * @return a new DatabaseUrl instance
     * @throws NullPointerException     if url is null
     * @throws IllegalArgumentException if url is empty
     */
    public static DatabaseUrl of(@NonNull String url) {
        return new DatabaseUrl(url);
    }

    /**
     * Creates a MySQL database URL from host, port, and database name.
     *
     * @param host     the database host
     * @param port     the database port
     * @param database the database name
     * @return a new DatabaseUrl instance for MySQL
     * @throws NullPointerException if any parameter is null
     */
    public static DatabaseUrl mysql(@NonNull String host, int port, @NonNull String database) {
        Objects.requireNonNull(host, "Host cannot be null");
        Objects.requireNonNull(database, "Database name cannot be null");
        validatePort(port);

        String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&autoReconnect=true&characterEncoding=utf8", host, port, database);
        return new DatabaseUrl(url);
    }

    /**
     * Creates a MySQL database URL with localhost and default port 3306.
     *
     * @param database the database name
     * @return a new DatabaseUrl instance for MySQL
     * @throws NullPointerException if database is null
     */
    public static DatabaseUrl mysqlLocalhost(@NonNull String database) {
        return mysql("localhost", 3306, database);
    }

    /**
     * Creates a PostgreSQL database URL from host, port, and database name.
     *
     * @param host     the database host
     * @param port     the database port
     * @param database the database name
     * @return a new DatabaseUrl instance for PostgreSQL
     * @throws NullPointerException if any parameter is null
     */
    public static DatabaseUrl postgresql(@NonNull String host, int port, @NonNull String database) {
        Objects.requireNonNull(host, "Host cannot be null");
        Objects.requireNonNull(database, "Database name cannot be null");
        validatePort(port);

        String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
        return new DatabaseUrl(url);
    }

    /**
     * Creates a PostgreSQL database URL with localhost and default port 5432.
     *
     * @param database the database name
     * @return a new DatabaseUrl instance for PostgreSQL
     * @throws NullPointerException if database is null
     */
    public static DatabaseUrl postgresqlLocalhost(@NonNull String database) {
        return postgresql("localhost", 5432, database);
    }

    /**
     * Creates a SQLite database URL from file path.
     *
     * @param filePath the path to the SQLite database file
     * @return a new DatabaseUrl instance for SQLite
     * @throws NullPointerException if filePath is null
     */
    public static DatabaseUrl sqlite(@NonNull String filePath) {
        Objects.requireNonNull(filePath, "File path cannot be null");
        String url = "jdbc:sqlite:" + filePath;
        return new DatabaseUrl(url);
    }

    /**
     * Creates an H2 database URL from file path (embedded mode).
     *
     * @param filePath the path to the H2 database file
     * @return a new DatabaseUrl instance for H2
     * @throws NullPointerException if filePath is null
     */
    public static DatabaseUrl h2(@NonNull String filePath) {
        Objects.requireNonNull(filePath, "File path cannot be null");
        String url = "jdbc:h2:" + filePath;
        return new DatabaseUrl(url);
    }

    /**
     * Creates an H2 in-memory database URL.
     *
     * @param databaseName the name of the in-memory database
     * @return a new DatabaseUrl instance for H2 in-memory
     * @throws NullPointerException if databaseName is null
     */
    public static DatabaseUrl h2Memory(@NonNull String databaseName) {
        Objects.requireNonNull(databaseName, "Database name cannot be null");
        String url = "jdbc:h2:mem:" + databaseName;
        return new DatabaseUrl(url);
    }

    private static void validatePort(int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535, but was: " + port);
        }
    }
}

