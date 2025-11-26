package com.hanielcota.nexoapi.database.transaction;

import com.hanielcota.nexoapi.database.query.PreparedQuery;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Handles transaction logging following Object Calisthenics principles.
 * Single responsibility: logging transaction operations.
 * <p>
 * This class encapsulates all logging behavior for transactions.
 * </p>
 *
 * @since 1.0.0
 */
final class TransactionLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionLogger.class);

    private TransactionLogger() {
        throw new UnsupportedOperationException("Utility class");
    }

    static void logQueryFailure(@NonNull PreparedQuery query, @NonNull SQLException exception) {
        Objects.requireNonNull(query, "Query cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");

        LOGGER.error("Failed to execute query in transaction: {}", query.sql(), exception);
    }

    static void logUpdateFailure(@NonNull PreparedQuery query, @NonNull SQLException exception) {
        Objects.requireNonNull(query, "Query cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");

        LOGGER.error("Failed to execute update in transaction: {}", query.sql(), exception);
    }

    static void logCommitFailure(@NonNull SQLException exception) {
        Objects.requireNonNull(exception, "Exception cannot be null");
        LOGGER.error("Failed to commit transaction", exception);
    }

    static void logRollbackFailure(@NonNull SQLException exception) {
        Objects.requireNonNull(exception, "Exception cannot be null");
        LOGGER.error("Failed to rollback transaction", exception);
    }

    static void logCloseRollbackFailure(@NonNull SQLException exception) {
        Objects.requireNonNull(exception, "Exception cannot be null");
        LOGGER.error("Failed to rollback transaction on close", exception);
    }

    static void logCloseConnectionFailure(@NonNull SQLException exception) {
        Objects.requireNonNull(exception, "Exception cannot be null");
        LOGGER.error("Failed to close connection", exception);
    }

    static void logUpdateSuccess(int affectedRows) {
        LOGGER.debug("Query executed in transaction, affected rows: {}", affectedRows);
    }

    static void logCommitSuccess() {
        LOGGER.debug("Transaction committed successfully");
    }

    static void logRollbackSuccess() {
        LOGGER.debug("Transaction rolled back");
    }
}

