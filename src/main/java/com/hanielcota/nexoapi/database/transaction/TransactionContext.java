package com.hanielcota.nexoapi.database.transaction;

import com.hanielcota.nexoapi.database.connection.ConnectionPool;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides a context for executing transactions with automatic resource management.
 * This class simplifies transaction handling by automatically committing or rolling back.
 * <p>
 * This class is thread-safe and can be used concurrently from multiple threads.
 * </p>
 *
 * @since 1.0.0
 */
public final class TransactionContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionContext.class);

    private final ConnectionPool connectionPool;

    private TransactionContext(@NonNull ConnectionPool connectionPool) {
        this.connectionPool = Objects.requireNonNull(connectionPool, "Connection pool cannot be null");
    }

    /**
     * Creates a new transaction context with the given connection pool.
     *
     * @param connectionPool the connection pool to use
     * @return a new TransactionContext instance
     * @throws NullPointerException if connectionPool is null
     */
    public static TransactionContext with(@NonNull ConnectionPool connectionPool) {
        return new TransactionContext(connectionPool);
    }

    /**
     * Executes a transaction with automatic commit/rollback handling.
     * If the consumer completes successfully, the transaction is committed.
     * If an exception is thrown, the transaction is rolled back.
     *
     * @param consumer the consumer that receives the transaction
     * @throws SQLException if a database access error occurs
     */
    public void execute(@NonNull Consumer<Transaction> consumer) throws SQLException {
        Objects.requireNonNull(consumer, "Consumer cannot be null");

        try (Transaction transaction = Transaction.begin(connectionPool)) {
            consumer.accept(transaction);
            transaction.commit();
        } catch (SQLException e) {
            LOGGER.error("Transaction failed and was rolled back", e);
            throw e;
        }
    }

    /**
     * Executes a transaction asynchronously with automatic commit/rollback handling.
     *
     * @param consumer the consumer that receives the transaction
     * @return a CompletableFuture that completes when the transaction is done
     */
    public CompletableFuture<Void> executeAsync(@NonNull Consumer<Transaction> consumer) {
        Objects.requireNonNull(consumer, "Consumer cannot be null");

        return CompletableFuture.runAsync(() -> {
            try {
                execute(consumer);
            } catch (SQLException e) {
                throw new RuntimeException("Async transaction failed", e);
            }
        });
    }

    /**
     * Executes a transaction that returns a result, with automatic commit/rollback handling.
     * If the function completes successfully, the transaction is committed.
     * If an exception is thrown, the transaction is rolled back.
     *
     * @param function the function that receives the transaction and returns a result
     * @param <T>      the result type
     * @return the result returned by the function
     * @throws SQLException if a database access error occurs
     */
    public <T> T executeWithResult(@NonNull Function<Transaction, T> function) throws SQLException {
        Objects.requireNonNull(function, "Function cannot be null");

        try (Transaction transaction = Transaction.begin(connectionPool)) {
            T result = function.apply(transaction);
            transaction.commit();
            return result;
        } catch (SQLException e) {
            LOGGER.error("Transaction with result failed and was rolled back", e);
            throw e;
        }
    }

    /**
     * Executes a transaction asynchronously that returns a result.
     *
     * @param function the function that receives the transaction and returns a result
     * @param <T>      the result type
     * @return a CompletableFuture that completes with the result
     */
    public <T> CompletableFuture<T> executeWithResultAsync(@NonNull Function<Transaction, T> function) {
        Objects.requireNonNull(function, "Function cannot be null");

        return CompletableFuture.supplyAsync(() -> {
            try {
                return executeWithResult(function);
            } catch (SQLException e) {
                throw new RuntimeException("Async transaction with result failed", e);
            }
        });
    }

    /**
     * Gets the connection pool used by this context.
     *
     * @return the connection pool
     */
    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }
}

