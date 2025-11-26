package com.hanielcota.nexoapi.database.transaction;

import com.hanielcota.nexoapi.database.connection.ConnectionPool;
import com.hanielcota.nexoapi.database.query.PreparedQuery;
import com.hanielcota.nexoapi.database.query.QueryResult;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a database transaction that ensures ACID properties.
 * Refactored following Object Calisthenics principles for better maintainability.
 * <p>
 * Transactions should be used when multiple queries need to be executed atomically.
 * Either all queries succeed, or all are rolled back.
 * </p>
 * <p>
 * This class is NOT thread-safe. Each transaction should be used from a single thread.
 * </p>
 *
 * @since 1.0.0
 */
public final class Transaction implements AutoCloseable {

    private final ConnectionManager connectionManager;
    private final TransactionState state;

    private Transaction(@NonNull ConnectionManager connectionManager) {
        this.connectionManager = Objects.requireNonNull(connectionManager, "Connection manager cannot be null");
        this.state = new TransactionState();
    }

    /**
     * Begins a new transaction using a connection from the pool.
     *
     * @param connectionPool the connection pool to get a connection from
     * @return a new Transaction instance
     * @throws SQLException if a database access error occurs
     */
    public static Transaction begin(@NonNull ConnectionPool connectionPool) throws SQLException {
        Objects.requireNonNull(connectionPool, "Connection pool cannot be null");
        
        Connection connection = connectionPool.getConnection();
        ConnectionManager manager = ConnectionManager.forConnection(connection);
        
        return new Transaction(manager);
    }

    /**
     * Begins a new transaction asynchronously.
     *
     * @param connectionPool the connection pool to get a connection from
     * @return a CompletableFuture that completes with a new Transaction
     */
    public static CompletableFuture<Transaction> beginAsync(@NonNull ConnectionPool connectionPool) {
        Objects.requireNonNull(connectionPool, "Connection pool cannot be null");
        return CompletableFuture.supplyAsync(() -> beginOrThrow(connectionPool));
    }

    private static Transaction beginOrThrow(ConnectionPool connectionPool) {
        try {
            return begin(connectionPool);
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to begin transaction", exception);
        }
    }

    /**
     * Executes a SELECT query within the transaction.
     *
     * @param query the prepared query to execute
     * @return a QueryResult containing the results
     * @throws SQLException          if a database access error occurs
     * @throws IllegalStateException if the transaction is already completed
     */
    public QueryResult executeQuery(@NonNull PreparedQuery query) throws SQLException {
        state.ensureNotCompleted();
        Objects.requireNonNull(query, "Query cannot be null");

        try {
            return performQuery(query);
        } catch (SQLException exception) {
            handleQueryFailure(query, exception);
            throw exception;
        }
    }

    private QueryResult performQuery(PreparedQuery query) throws SQLException {
        try (PreparedStatement statement = prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            return QueryResult.from(resultSet);
        }
    }

    private void handleQueryFailure(PreparedQuery query, SQLException exception) throws SQLException {
        TransactionLogger.logQueryFailure(query, exception);
        rollback();
    }

    /**
     * Executes an UPDATE, INSERT, or DELETE query within the transaction.
     *
     * @param query the prepared query to execute
     * @return the number of affected rows
     * @throws SQLException          if a database access error occurs
     * @throws IllegalStateException if the transaction is already completed
     */
    public int executeUpdate(@NonNull PreparedQuery query) throws SQLException {
        state.ensureNotCompleted();
        Objects.requireNonNull(query, "Query cannot be null");

        try {
            return performUpdate(query);
        } catch (SQLException exception) {
            handleUpdateFailure(query, exception);
            throw exception;
        }
    }

    private int performUpdate(PreparedQuery query) throws SQLException {
        try (PreparedStatement statement = prepareStatement(query)) {
            int affectedRows = statement.executeUpdate();
            TransactionLogger.logUpdateSuccess(affectedRows);
            return affectedRows;
        }
    }

    private void handleUpdateFailure(PreparedQuery query, SQLException exception) throws SQLException {
        TransactionLogger.logUpdateFailure(query, exception);
        rollback();
    }

    /**
     * Executes multiple queries within the transaction.
     * All queries are executed atomically - either all succeed or all are rolled back.
     *
     * @param queries the prepared queries to execute
     * @return an array containing the number of affected rows for each query
     * @throws SQLException          if a database access error occurs
     * @throws IllegalStateException if the transaction is already completed
     */
    public int[] executeAll(@NonNull PreparedQuery... queries) throws SQLException {
        state.ensureNotCompleted();
        Objects.requireNonNull(queries, "Queries cannot be null");

        try {
            return executeAllQueries(queries);
        } catch (SQLException exception) {
            rollback();
            throw exception;
        }
    }

    private int[] executeAllQueries(PreparedQuery[] queries) throws SQLException {
        int[] results = new int[queries.length];
        
        for (int index = 0; index < queries.length; index++) {
            results[index] = executeUpdate(queries[index]);
        }
        
        return results;
    }

    /**
     * Commits the transaction, making all changes permanent.
     * After committing, the transaction is completed and cannot be used again.
     *
     * @throws SQLException          if a database access error occurs
     * @throws IllegalStateException if the transaction is already completed
     */
    public void commit() throws SQLException {
        state.ensureNotCompleted();

        try {
            performCommit();
        } catch (SQLException exception) {
            handleCommitFailure(exception);
            throw exception;
        }
    }

    private void performCommit() throws SQLException {
        connectionManager.commit();
        state.markCompleted();
        TransactionLogger.logCommitSuccess();
    }

    private void handleCommitFailure(SQLException exception) throws SQLException {
        TransactionLogger.logCommitFailure(exception);
        rollback();
    }

    /**
     * Commits the transaction asynchronously.
     *
     * @return a CompletableFuture that completes when the commit is done
     */
    public CompletableFuture<Void> commitAsync() {
        return CompletableFuture.runAsync(this::commitOrThrow);
    }

    private void commitOrThrow() {
        try {
            commit();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to commit transaction asynchronously", exception);
        }
    }

    /**
     * Rolls back the transaction, undoing all changes.
     * After rolling back, the transaction is completed and cannot be used again.
     *
     * @throws SQLException if a database access error occurs
     */
    public void rollback() throws SQLException {
        if (state.isCompleted() || state.isRolledBack()) {
            return;
        }

        performRollback();
    }

    private void performRollback() throws SQLException {
        try {
            connectionManager.rollback();
            state.markRolledBack();
            TransactionLogger.logRollbackSuccess();
        } catch (SQLException exception) {
            TransactionLogger.logRollbackFailure(exception);
            throw exception;
        }
    }

    /**
     * Rolls back the transaction asynchronously.
     *
     * @return a CompletableFuture that completes when the rollback is done
     */
    public CompletableFuture<Void> rollbackAsync() {
        return CompletableFuture.runAsync(this::rollbackOrThrow);
    }

    private void rollbackOrThrow() {
        try {
            rollback();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to rollback transaction asynchronously", exception);
        }
    }

    /**
     * Checks if the transaction has been completed (committed or rolled back).
     *
     * @return true if the transaction is completed, false otherwise
     */
    public boolean isCompleted() {
        return state.isCompleted();
    }

    /**
     * Checks if the transaction was rolled back.
     *
     * @return true if the transaction was rolled back, false otherwise
     */
    public boolean isRolledBack() {
        return state.isRolledBack();
    }

    /**
     * Closes the transaction and releases the connection.
     * If the transaction has not been committed, it will be automatically rolled back.
     * <p>
     * This method is idempotent and can be called multiple times safely.
     * </p>
     */
    @Override
    public void close() {
        closeTransaction();
        closeConnection();
    }

    private void closeTransaction() {
        if (state.canExecute()) {
            rollbackOnClose();
        }
    }

    private void rollbackOnClose() {
        try {
            rollback();
        } catch (SQLException exception) {
            TransactionLogger.logCloseRollbackFailure(exception);
        }
    }

    private void closeConnection() {
        try {
            connectionManager.close();
        } catch (SQLException exception) {
            TransactionLogger.logCloseConnectionFailure(exception);
        }
    }

    private PreparedStatement prepareStatement(PreparedQuery query) throws SQLException {
        Connection connection = connectionManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(query.sql());
        
        bindParameters(statement, query.parameters());
        return statement;
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
}
