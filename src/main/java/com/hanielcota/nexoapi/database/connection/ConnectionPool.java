package com.hanielcota.nexoapi.database.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Manages a pool of database connections using HikariCP.
 * Refactored following Object Calisthenics principles for better maintainability.
 * <p>
 * This class is thread-safe and can be used concurrently from multiple threads.
 * All connections obtained from this pool should be closed after use (try-with-resources).
 * </p>
 *
 * @since 1.0.0
 */
public final class ConnectionPool implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);

    private final ConnectionConfig config;
    private final HikariDataSource dataSource;
    private volatile boolean closed;

    private ConnectionPool(@NonNull ConnectionConfig config, @NonNull HikariDataSource dataSource) {
        this.config = Objects.requireNonNull(config, "Connection config cannot be null");
        this.dataSource = Objects.requireNonNull(dataSource, "Data source cannot be null");
        this.closed = false;
    }

    /**
     * Creates a new connection pool with the given configuration.
     * The pool is immediately initialized and ready to serve connections.
     *
     * @param config the connection configuration
     * @return a new ConnectionPool instance
     * @throws NullPointerException if config is null
     * @throws RuntimeException     if the pool cannot be initialized
     */
    public static ConnectionPool create(@NonNull ConnectionConfig config) {
        Objects.requireNonNull(config, "Connection config cannot be null");

        try {
            return createPoolFromConfig(config);
        } catch (Exception exception) {
            handleCreationFailure(exception);
            throw new RuntimeException("Failed to create database connection pool", exception);
        }
    }

    private static ConnectionPool createPoolFromConfig(ConnectionConfig config) {
        HikariConfig hikariConfig = buildHikariConfiguration(config);
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        
        logSuccessfulCreation(config);
        return new ConnectionPool(config, dataSource);
    }

    private static HikariConfig buildHikariConfiguration(ConnectionConfig config) {
        return HikariConfigBuilder.from(config).build();
    }

    private static void logSuccessfulCreation(ConnectionConfig config) {
        LOGGER.info("Database connection pool created successfully for: {}", config.url().url());
    }

    private static void handleCreationFailure(Exception exception) {
        LOGGER.error("Failed to create database connection pool", exception);
    }

    /**
     * Gets a connection from the pool.
     * The connection must be closed after use (preferably with try-with-resources).
     *
     * @return a database connection
     * @throws SQLException          if a database access error occurs
     * @throws IllegalStateException if the pool is closed
     */
    public Connection getConnection() throws SQLException {
        ensureNotClosed();
        
        try {
            return obtainConnectionFromDataSource();
        } catch (SQLException exception) {
            handleConnectionFailure(exception);
            throw exception;
        }
    }

    private Connection obtainConnectionFromDataSource() throws SQLException {
        return dataSource.getConnection();
    }

    private void handleConnectionFailure(SQLException exception) {
        LOGGER.error("Failed to get connection from pool", exception);
    }

    /**
     * Gets a connection from the pool asynchronously.
     * The returned CompletableFuture completes with a connection that must be closed after use.
     *
     * @return a CompletableFuture that completes with a database connection
     * @throws IllegalStateException if the pool is closed
     */
    public CompletableFuture<Connection> getConnectionAsync() {
        ensureNotClosed();
        return CompletableFuture.supplyAsync(this::getConnectionOrThrow);
    }

    private Connection getConnectionOrThrow() {
        try {
            return getConnection();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to get connection asynchronously", exception);
        }
    }

    /**
     * Tests the connection pool by attempting to get a connection.
     *
     * @return true if a connection can be obtained, false otherwise
     */
    public boolean testConnection() {
        return ConnectionValidator.forPool(this).isValid();
    }

    /**
     * Gets the current status of the connection pool.
     *
     * @return the pool status
     * @throws IllegalStateException if the pool is closed
     */
    public PoolStatus getStatus() {
        ensureNotClosed();
        return extractPoolMetrics();
    }

    private PoolStatus extractPoolMetrics() {
        int maxPoolSize = config.poolSize().maximum();
        return PoolMetrics.from(dataSource, maxPoolSize).extractStatus();
    }

    /**
     * Checks if the connection pool is closed.
     *
     * @return true if the pool is closed, false otherwise
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Gets the connection configuration used by this pool.
     *
     * @return the connection configuration
     */
    public ConnectionConfig getConfig() {
        return config;
    }

    /**
     * Closes the connection pool and releases all connections.
     * After closing, no new connections can be obtained.
     * <p>
     * This method is idempotent and can be called multiple times safely.
     * </p>
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }

        markAsClosed();
        closeDataSource();
    }

    private void markAsClosed() {
        closed = true;
    }

    private void closeDataSource() {
        if (isDataSourceOpen()) {
            performDataSourceClose();
            logSuccessfulClosure();
        }
    }

    private boolean isDataSourceOpen() {
        return dataSource != null && !dataSource.isClosed();
    }

    private void performDataSourceClose() {
        dataSource.close();
    }

    private void logSuccessfulClosure() {
        LOGGER.info("Database connection pool closed");
    }

    /**
     * Evicts idle connections from the pool.
     * This can be useful for reducing resource usage during periods of low activity.
     */
    public void evictIdleConnections() {
        ensureNotClosed();
        performEviction();
        logEviction();
    }

    private void performEviction() {
        dataSource.getHikariPoolMXBean().softEvictConnections();
    }

    private void logEviction() {
        LOGGER.debug("Evicted idle connections from pool");
    }

    private void ensureNotClosed() {
        if (closed) {
            throw new IllegalStateException("Connection pool is closed");
        }
    }
}

