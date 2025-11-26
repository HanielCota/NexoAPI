package com.hanielcota.nexoapi.database.connection;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Validates database connections following Object Calisthenics.
 * Single responsibility: connection validation logic.
 * <p>
 * This class encapsulates all connection validation behavior.
 * </p>
 *
 * @since 1.0.0
 */
final class ConnectionValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionValidator.class);
    private static final int VALIDATION_TIMEOUT_SECONDS = 5;

    private final ConnectionPool pool;

    private ConnectionValidator(@NonNull ConnectionPool pool) {
        this.pool = Objects.requireNonNull(pool, "Connection pool cannot be null");
    }

    static ConnectionValidator forPool(@NonNull ConnectionPool pool) {
        return new ConnectionValidator(pool);
    }

    boolean isValid() {
        if (pool.isClosed()) {
            return false;
        }

        return testConnection();
    }

    private boolean testConnection() {
        try (Connection connection = pool.getConnection()) {
            return validateConnection(connection);
        } catch (SQLException exception) {
            logValidationFailure(exception);
            return false;
        }
    }

    private boolean validateConnection(Connection connection) throws SQLException {
        return connection.isValid(VALIDATION_TIMEOUT_SECONDS);
    }

    private void logValidationFailure(SQLException exception) {
        LOGGER.error("Connection test failed", exception);
    }
}

