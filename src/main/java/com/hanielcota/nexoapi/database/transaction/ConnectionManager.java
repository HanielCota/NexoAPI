package com.hanielcota.nexoapi.database.transaction;

import lombok.NonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Manages connection lifecycle within a transaction following Object Calisthenics.
 * Single responsibility: connection management.
 * <p>
 * This class encapsulates connection setup and cleanup behavior.
 * </p>
 *
 * @since 1.0.0
 */
final class ConnectionManager {

    private final Connection connection;

    private ConnectionManager(@NonNull Connection connection) throws SQLException {
        this.connection = Objects.requireNonNull(connection, "Connection cannot be null");
        disableAutoCommit();
    }

    static ConnectionManager forConnection(@NonNull Connection connection) throws SQLException {
        return new ConnectionManager(connection);
    }

    Connection getConnection() {
        return connection;
    }

    void commit() throws SQLException {
        connection.commit();
    }

    void rollback() throws SQLException {
        connection.rollback();
    }

    void close() throws SQLException {
        restoreAutoCommit();
        closeConnection();
    }

    private void disableAutoCommit() throws SQLException {
        connection.setAutoCommit(false);
    }

    private void restoreAutoCommit() throws SQLException {
        if (isConnectionValid()) {
            connection.setAutoCommit(true);
        }
    }

    private void closeConnection() throws SQLException {
        if (isConnectionValid()) {
            connection.close();
        }
    }

    private boolean isConnectionValid() {
        return connection != null;
    }
}

