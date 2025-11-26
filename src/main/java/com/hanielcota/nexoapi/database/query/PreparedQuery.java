package com.hanielcota.nexoapi.database.query;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a prepared SQL query with parameters.
 * This is a value object that encapsulates a SQL statement and its parameters.
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @param sql        the SQL query
 * @param parameters the query parameters
 * @since 1.0.0
 */
public record PreparedQuery(@NonNull String sql, @NonNull Object... parameters) {

    public PreparedQuery {
        Objects.requireNonNull(sql, "SQL cannot be null");
        Objects.requireNonNull(parameters, "Parameters cannot be null");

        if (sql.isBlank()) {
            throw new IllegalArgumentException("SQL cannot be empty");
        }
    }

    /**
     * Creates a prepared query with SQL and parameters.
     *
     * @param sql        the SQL query
     * @param parameters the query parameters
     * @return a new PreparedQuery instance
     * @throws NullPointerException     if sql or parameters is null
     * @throws IllegalArgumentException if sql is empty
     */
    public static PreparedQuery of(@NonNull String sql, Object... parameters) {
        return new PreparedQuery(sql, parameters);
    }

    /**
     * Creates a prepared query with SQL and no parameters.
     *
     * @param sql the SQL query
     * @return a new PreparedQuery instance
     * @throws NullPointerException     if sql is null
     * @throws IllegalArgumentException if sql is empty
     */
    public static PreparedQuery of(@NonNull String sql) {
        return new PreparedQuery(sql);
    }

    /**
     * Gets the number of parameters in the query.
     *
     * @return the parameter count
     */
    public int getParameterCount() {
        return parameters.length;
    }

    /**
     * Checks if the query has parameters.
     *
     * @return true if the query has at least one parameter, false otherwise
     */
    public boolean hasParameters() {
        return parameters.length > 0;
    }

    @Override
    public String toString() {
        return String.format("PreparedQuery{sql='%s', parameters=%s}", sql, Arrays.toString(parameters));
    }
}

