package com.hanielcota.nexoapi.database.connection;

import com.hanielcota.nexoapi.database.DatabaseType;
import com.zaxxer.hikari.HikariConfig;
import lombok.NonNull;

import java.util.Objects;

/**
 * Builder for HikariCP configuration following Object Calisthenics principles.
 * Encapsulates all configuration logic for the connection pool.
 * <p>
 * This class ensures single responsibility and high cohesion.
 * </p>
 *
 * @since 1.0.0
 */
final class HikariConfigBuilder {

    private static final String POOL_NAME = "NexoAPI-Pool";
    private static final long IDLE_TIMEOUT_MILLIS = 600000L; // 10 minutes
    private static final long MAX_LIFETIME_MILLIS = 1800000L; // 30 minutes

    private final ConnectionConfig config;

    private HikariConfigBuilder(@NonNull ConnectionConfig config) {
        this.config = Objects.requireNonNull(config, "Connection config cannot be null");
    }

    static HikariConfigBuilder from(@NonNull ConnectionConfig config) {
        return new HikariConfigBuilder(config);
    }

    HikariConfig build() {
        HikariConfig hikariConfig = new HikariConfig();

        configureBasicSettings(hikariConfig);
        configurePoolSettings(hikariConfig);
        configureTimeoutSettings(hikariConfig);
        configurePerformanceSettings(hikariConfig);
        configureDatabaseDriver(hikariConfig);
        configureOptimizations(hikariConfig);

        return hikariConfig;
    }

    private void configureBasicSettings(HikariConfig hikariConfig) {
        hikariConfig.setJdbcUrl(config.url().url());
        hikariConfig.setUsername(config.credentials().username());
        hikariConfig.setPassword(config.credentials().password());
        hikariConfig.setPoolName(POOL_NAME);
    }

    private void configurePoolSettings(HikariConfig hikariConfig) {
        hikariConfig.setMinimumIdle(config.poolSize().minimum());
        hikariConfig.setMaximumPoolSize(config.poolSize().maximum());
    }

    private void configureTimeoutSettings(HikariConfig hikariConfig) {
        hikariConfig.setConnectionTimeout(config.timeout().toMillis());
        hikariConfig.setIdleTimeout(IDLE_TIMEOUT_MILLIS);
        hikariConfig.setMaxLifetime(MAX_LIFETIME_MILLIS);
    }

    private void configurePerformanceSettings(HikariConfig hikariConfig) {
        hikariConfig.setAutoCommit(true);
        hikariConfig.setConnectionTestQuery("SELECT 1");
    }

    private void configureDatabaseDriver(HikariConfig hikariConfig) {
        DatabaseType type = config.getType();
        
        if (type == null) {
            return;
        }
        
        hikariConfig.setDriverClassName(type.getDriverClassName());
    }

    private void configureOptimizations(HikariConfig hikariConfig) {
        configurePreparedStatementCache(hikariConfig);
        configureSessionOptimizations(hikariConfig);
        configureMetadataOptimizations(hikariConfig);
    }

    private void configurePreparedStatementCache(HikariConfig hikariConfig) {
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
    }

    private void configureSessionOptimizations(HikariConfig hikariConfig) {
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
    }

    private void configureMetadataOptimizations(HikariConfig hikariConfig) {
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
    }
}

