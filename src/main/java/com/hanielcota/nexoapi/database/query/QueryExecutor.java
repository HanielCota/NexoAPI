package com.hanielcota.nexoapi.database.query;

import com.hanielcota.nexoapi.database.connection.ConnectionPool;
import lombok.NonNull;

import java.sql.*;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Executes database queries using a connection pool.
 * Refactored following Object Calisthenics principles for better maintainability.
 * <p>
 * This class is thread-safe and can be used concurrently from multiple threads.
 * All database operations are automatically managed (connections, statements, result sets).
 * </p>
 *
 * @since 1.0.0
 */
public final class QueryExecutor {

    private final ConnectionPool connectionPool;

    private QueryExecutor(@NonNull ConnectionPool connectionPool) {
        this.connectionPool = Objects.requireNonNull(connectionPool, "Connection pool cannot be null");
    }

    /**
     * Creates a new query executor with the given connection pool.
     *
     * @param connectionPool the connection pool to use
     * @return a new QueryExecutor instance
     * @throws NullPointerException if connectionPool is null
     */
    public static QueryExecutor with(@NonNull ConnectionPool connectionPool) {
        return new QueryExecutor(connectionPool);
    }

    /**
     * Executes a SELECT query and returns the results.
     *
     * @param query the prepared query to execute
     * @return a QueryResult containing the results
     * @throws SQLException if a database access error occurs
     */
    public QueryResult executeQuery(@NonNull PreparedQuery query) throws SQLException {
        Objects.requireNonNull(query, "Query cannot be null");

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = StatementPreparer.prepare(connection, query);
             ResultSet resultSet = statement.executeQuery()) {

            return QueryResult.from(resultSet);
        } catch (SQLException exception) {
            QueryLogger.logQueryFailure(query, exception);
            throw exception;
        }
    }

    /**
     * Executes a SELECT query asynchronously and returns a CompletableFuture with the results.
     *
     * @param query the prepared query to execute
     * @return a CompletableFuture that completes with the query results
     */
    public CompletableFuture<QueryResult> executeQueryAsync(@NonNull PreparedQuery query) {
        Objects.requireNonNull(query, "Query cannot be null");
        return CompletableFuture.supplyAsync(() -> executeQueryOrThrow(query));
    }

    private QueryResult executeQueryOrThrow(PreparedQuery query) {
        try {
            return executeQuery(query);
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to execute query asynchronously", exception);
        }
    }

    /**
     * Executes an UPDATE, INSERT, or DELETE query and returns the number of affected rows.
     *
     * @param query the prepared query to execute
     * @return the number of affected rows
     * @throws SQLException if a database access error occurs
     */
    public int executeUpdate(@NonNull PreparedQuery query) throws SQLException {
        Objects.requireNonNull(query, "Query cannot be null");

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = StatementPreparer.prepare(connection, query)) {

            return executeAndLogUpdate(statement);
        } catch (SQLException exception) {
            QueryLogger.logUpdateFailure(query, exception);
            throw exception;
        }
    }

    private int executeAndLogUpdate(PreparedStatement statement) throws SQLException {
        int affectedRows = statement.executeUpdate();
        QueryLogger.logUpdateSuccess(affectedRows);
        return affectedRows;
    }

    /**
     * Executes an UPDATE, INSERT, or DELETE query asynchronously.
     *
     * @param query the prepared query to execute
     * @return a CompletableFuture that completes with the number of affected rows
     */
    public CompletableFuture<Integer> executeUpdateAsync(@NonNull PreparedQuery query) {
        Objects.requireNonNull(query, "Query cannot be null");
        return CompletableFuture.supplyAsync(() -> executeUpdateOrThrow(query));
    }

    private int executeUpdateOrThrow(PreparedQuery query) {
        try {
            return executeUpdate(query);
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to execute update asynchronously", exception);
        }
    }

    /**
     * Executes an INSERT query and returns the generated key (auto-increment ID).
     *
     * @param query the prepared query to execute
     * @return the generated key, or -1 if no key was generated
     * @throws SQLException if a database access error occurs
     */
    public long executeInsert(@NonNull PreparedQuery query) throws SQLException {
        Objects.requireNonNull(query, "Query cannot be null");

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = StatementPreparer.prepareWithGeneratedKeys(connection, query)) {

            return executeInsertAndExtractKey(statement);
        } catch (SQLException exception) {
            QueryLogger.logInsertFailure(query, exception);
            throw exception;
        }
    }

    private long executeInsertAndExtractKey(PreparedStatement statement) throws SQLException {
        statement.executeUpdate();
        return GeneratedKeyExtractor.extractFrom(statement);
    }

    /**
     * Executes an INSERT query asynchronously and returns the generated key.
     *
     * @param query the prepared query to execute
     * @return a CompletableFuture that completes with the generated key
     */
    public CompletableFuture<Long> executeInsertAsync(@NonNull PreparedQuery query) {
        Objects.requireNonNull(query, "Query cannot be null");
        return CompletableFuture.supplyAsync(() -> executeInsertOrThrow(query));
    }

    private long executeInsertOrThrow(PreparedQuery query) {
        try {
            return executeInsert(query);
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to execute insert asynchronously", exception);
        }
    }

    /**
     * Executes a batch of UPDATE, INSERT, or DELETE queries.
     *
     * @param queries the prepared queries to execute
     * @return an array containing the number of affected rows for each query
     * @throws SQLException if a database access error occurs
     */
    public int[] executeBatch(@NonNull PreparedQuery... queries) throws SQLException {
        Objects.requireNonNull(queries, "Queries cannot be null");

        if (hasNoQueries(queries)) {
            return new int[0];
        }

        return executeBatchQueries(queries);
    }

    private boolean hasNoQueries(PreparedQuery[] queries) {
        return queries.length == 0;
    }

    private int[] executeBatchQueries(PreparedQuery[] queries) throws SQLException {
        validateBatchQueries(queries);
        String sql = queries[0].sql();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            return executeAndLogBatch(statement, queries);
        } catch (SQLException exception) {
            QueryLogger.logBatchFailure(exception);
            throw exception;
        }
    }

    private void validateBatchQueries(PreparedQuery[] queries) {
        String firstSql = queries[0].sql();
        
        for (PreparedQuery query : queries) {
            validateQuerySql(query, firstSql);
        }
    }

    private void validateQuerySql(PreparedQuery query, String expectedSql) {
        if (!query.sql().equals(expectedSql)) {
            throw new IllegalArgumentException("All queries in a batch must have the same SQL");
        }
    }

    private int[] executeAndLogBatch(PreparedStatement statement, PreparedQuery[] queries) throws SQLException {
        addBatchStatements(statement, queries);
        int[] results = statement.executeBatch();
        QueryLogger.logBatchSuccess(queries.length);
        return results;
    }

    private void addBatchStatements(PreparedStatement statement, PreparedQuery[] queries) throws SQLException {
        for (PreparedQuery query : queries) {
            addBatchStatement(statement, query);
        }
    }

    private void addBatchStatement(PreparedStatement statement, PreparedQuery query) throws SQLException {
        bindParameters(statement, query.parameters());
        statement.addBatch();
    }

    private void bindParameters(PreparedStatement statement, Object[] parameters) throws SQLException {
        for (int index = 0; index < parameters.length; index++) {
            bindParameter(statement, index, parameters[index]);
        }
    }

    private void bindParameter(PreparedStatement statement, int index, Object value) throws SQLException {
        int parameterIndex = index + 1;
        statement.setObject(parameterIndex, value);
    }

    /**
     * Executes a batch of queries asynchronously.
     *
     * @param queries the prepared queries to execute
     * @return a CompletableFuture that completes with the batch results
     */
    public CompletableFuture<int[]> executeBatchAsync(@NonNull PreparedQuery... queries) {
        Objects.requireNonNull(queries, "Queries cannot be null");
        return CompletableFuture.supplyAsync(() -> executeBatchOrThrow(queries));
    }

    private int[] executeBatchOrThrow(PreparedQuery[] queries) {
        try {
            return executeBatch(queries);
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to execute batch asynchronously", exception);
        }
    }

    /**
     * Executes a raw SQL statement without parameters.
     * This should only be used for DDL statements (CREATE, ALTER, DROP).
     *
     * @param sql the SQL statement to execute
     * @return true if the statement executed successfully
     * @throws SQLException if a database access error occurs
     */
    public boolean execute(@NonNull String sql) throws SQLException {
        Objects.requireNonNull(sql, "SQL cannot be null");

        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement()) {

            return executeAndLogStatement(statement, sql);
        } catch (SQLException exception) {
            QueryLogger.logExecuteFailure(sql, exception);
            throw exception;
        }
    }

    private boolean executeAndLogStatement(Statement statement, String sql) throws SQLException {
        statement.execute(sql);
        QueryLogger.logExecuteSuccess(sql);
        return true;
    }

    /**
     * Executes a raw SQL statement asynchronously.
     *
     * @param sql the SQL statement to execute
     * @return a CompletableFuture that completes with true if successful
     */
    public CompletableFuture<Boolean> executeAsync(@NonNull String sql) {
        Objects.requireNonNull(sql, "SQL cannot be null");
        return CompletableFuture.supplyAsync(() -> executeOrThrow(sql));
    }

    private boolean executeOrThrow(String sql) {
        try {
            return execute(sql);
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to execute SQL asynchronously", exception);
        }
    }

    /**
     * Gets the connection pool used by this executor.
     *
     * @return the connection pool
     */
    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }
}
