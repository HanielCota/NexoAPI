package com.hanielcota.nexoapi.database.property;

import lombok.NonNull;

import java.util.Objects;

/**
 * Represents database credentials with username and password.
 * This is a value object that encapsulates database authentication information.
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @param username the database username
 * @param password the database password
 * @since 1.0.0
 */
public record DatabaseCredentials(@NonNull String username, @NonNull String password) {

    public DatabaseCredentials {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
    }

    /**
     * Creates database credentials with the given username and password.
     *
     * @param username the database username
     * @param password the database password
     * @return a new DatabaseCredentials instance
     * @throws NullPointerException if username or password is null
     */
    public static DatabaseCredentials of(@NonNull String username, @NonNull String password) {
        return new DatabaseCredentials(username, password);
    }

    /**
     * Creates database credentials with default root user and empty password.
     * This is typically used for local development.
     *
     * @return a new DatabaseCredentials instance with root/empty credentials
     */
    public static DatabaseCredentials defaultRoot() {
        return new DatabaseCredentials("root", "");
    }

    /**
     * Returns a string representation of the credentials with masked password.
     * The password is replaced with asterisks for security.
     *
     * @return a string representation with masked password
     */
    @Override
    public String toString() {
        return "DatabaseCredentials{username='" + username + "', password='***'}";
    }
}

