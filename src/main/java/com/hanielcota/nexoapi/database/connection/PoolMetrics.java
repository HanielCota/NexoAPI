package com.hanielcota.nexoapi.database.connection;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import lombok.NonNull;

import java.util.Objects;

/**
 * Encapsulates pool metrics extraction following Object Calisthenics.
 * Removes multiple dot per line and encapsulates behavior.
 * <p>
 * This class has single responsibility: extract metrics from HikariCP.
 * </p>
 *
 * @since 1.0.0
 */
final class PoolMetrics {

    private final HikariPoolMXBean poolMXBean;
    private final int maxPoolSize;

    private PoolMetrics(@NonNull HikariPoolMXBean poolMXBean, int maxPoolSize) {
        this.poolMXBean = Objects.requireNonNull(poolMXBean, "Pool MXBean cannot be null");
        this.maxPoolSize = maxPoolSize;
    }

    static PoolMetrics from(@NonNull HikariDataSource dataSource, int maxPoolSize) {
        Objects.requireNonNull(dataSource, "Data source cannot be null");
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
        return new PoolMetrics(poolMXBean, maxPoolSize);
    }

    PoolStatus extractStatus() {
        int active = extractActiveConnections();
        int idle = extractIdleConnections();
        int total = extractTotalConnections();
        int awaiting = extractAwaitingThreads();

        return new PoolStatus(active, idle, total, awaiting, maxPoolSize);
    }

    private int extractActiveConnections() {
        return poolMXBean.getActiveConnections();
    }

    private int extractIdleConnections() {
        return poolMXBean.getIdleConnections();
    }

    private int extractTotalConnections() {
        return poolMXBean.getTotalConnections();
    }

    private int extractAwaitingThreads() {
        return poolMXBean.getThreadsAwaitingConnection();
    }
}

