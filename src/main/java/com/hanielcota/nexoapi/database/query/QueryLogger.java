package com.hanielcota.nexoapi.database.query;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Handles query logging following Object Calisthenics principles.
 * Single responsibility: logging database operations.
 * <p>
 * This class encapsulates all logging behavior for queries.
 * </p>
 *
 * @since 1.0.0
 */
final class QueryLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryLogger.class);

    private QueryLogger() {
        throw new UnsupportedOperationException("Utility class");
    }

    static void logQueryFailure(@NonNull PreparedQuery query, @NonNull SQLException exception) {
        Objects.requireNonNull(query, "Query cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");

        LOGGER.error("Failed to execute query: {}", query.sql(), exception);
    }

    static void logUpdateFailure(@NonNull PreparedQuery query, @NonNull SQLException exception) {
        Objects.requireNonNull(query, "Query cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");

        LOGGER.error("Failed to execute update: {}", query.sql(), exception);
    }

    static void logInsertFailure(@NonNull PreparedQuery query, @NonNull SQLException exception) {
        Objects.requireNonNull(query, "Query cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");

        LOGGER.error("Failed to execute insert: {}", query.sql(), exception);
    }

    static void logBatchFailure(@NonNull SQLException exception) {
        Objects.requireNonNull(exception, "Exception cannot be null");
        LOGGER.error("Failed to execute batch", exception);
    }

    static void logExecuteFailure(@NonNull String sql, @NonNull SQLException exception) {
        Objects.requireNonNull(sql, "SQL cannot be null");
        Objects.requireNonNull(exception, "Exception cannot be null");

        LOGGER.error("Failed to execute SQL: {}", sql, exception);
    }

    static void logUpdateSuccess(int affectedRows) {
        LOGGER.debug("Query executed successfully, affected rows: {}", affectedRows);
    }

    static void logBatchSuccess(int queryCount) {
        LOGGER.debug("Batch executed successfully, {} queries", queryCount);
    }

    static void logExecuteSuccess(@NonNull String sql) {
        Objects.requireNonNull(sql, "SQL cannot be null");
        LOGGER.debug("SQL executed successfully: {}", sql);
    }
}

