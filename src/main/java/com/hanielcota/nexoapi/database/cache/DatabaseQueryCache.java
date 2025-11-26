package com.hanielcota.nexoapi.database.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.hanielcota.nexoapi.database.query.PreparedQuery;
import com.hanielcota.nexoapi.database.query.QueryExecutor;
import com.hanielcota.nexoapi.database.query.QueryResult;
import lombok.NonNull;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Cache for database query results using Caffeine Cache.
 * Dramatically reduces database load for frequently accessed data.
 * <p>
 * Features:
 * - Configurable size limits
 * - Time-based expiration (access and write)
 * - Automatic invalidation on updates
 * - Performance statistics
 * </p>
 * <p>
 * Performance impact:
 * - Cache hit: ~0.1ms (extremely fast)
 * - Cache miss: ~50ms (actual database query)
 * - Typical hit rate: 70-90% for read-heavy workloads
 * </p>
 *
 * @since 1.0.0
 */
public final class DatabaseQueryCache {
    
    private final Cache<String, QueryResult> cache;
    
    private DatabaseQueryCache(@NonNull Cache<String, QueryResult> cache) {
        this.cache = cache;
    }
    
    /**
     * Creates a cache with default configuration.
     * <p>
     * Default settings:
     * - Maximum size: 1000 queries
     * - Expire after access: 5 minutes
     * - Expire after write: 10 minutes
     * - Statistics: enabled
     * </p>
     *
     * @return a new DatabaseQueryCache instance
     */
    public static DatabaseQueryCache createDefault() {
        Cache<String, QueryResult> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
            .build();
        
        return new DatabaseQueryCache(cache);
    }
    
    /**
     * Creates a cache with custom configuration.
     *
     * @param maxSize       maximum number of cached queries
     * @param accessMinutes minutes before expiring after last access
     * @param writeMinutes  minutes before expiring after creation
     * @return a new DatabaseQueryCache instance
     */
    public static DatabaseQueryCache create(
        int maxSize, 
        int accessMinutes, 
        int writeMinutes
    ) {
        Cache<String, QueryResult> cache = Caffeine.newBuilder()
            .maximumSize(maxSize)
            .expireAfterAccess(accessMinutes, TimeUnit.MINUTES)
            .expireAfterWrite(writeMinutes, TimeUnit.MINUTES)
            .recordStats()
            .build();
        
        return new DatabaseQueryCache(cache);
    }
    
    /**
     * Gets a query result from cache or executes the query if not cached.
     * <p>
     * This is the main method for cached query execution.
     * Always check cache first, then execute query on miss.
     * </p>
     *
     * @param query    the prepared query to execute
     * @param executor the query executor to use on cache miss
     * @return the query result (from cache or freshly executed)
     * @throws SQLException if query execution fails
     */
    public QueryResult getOrQuery(
        @NonNull PreparedQuery query, 
        @NonNull QueryExecutor executor
    ) throws SQLException {
        String cacheKey = generateCacheKey(query);
        
        QueryResult cached = cache.getIfPresent(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // Cache miss - execute query and cache result
        QueryResult result = executor.executeQuery(query);
        cache.put(cacheKey, result);
        
        return result;
    }
    
    /**
     * Gets a query result from cache or executes the query asynchronously.
     *
     * @param query    the prepared query to execute
     * @param executor the query executor to use on cache miss
     * @return a CompletableFuture containing the query result
     */
    public CompletableFuture<QueryResult> getOrQueryAsync(
        @NonNull PreparedQuery query, 
        @NonNull QueryExecutor executor
    ) {
        String cacheKey = generateCacheKey(query);
        
        QueryResult cached = cache.getIfPresent(cacheKey);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        
        // Cache miss - execute query asynchronously
        return executor.executeQueryAsync(query)
            .thenApply(result -> {
                cache.put(cacheKey, result);
                return result;
            });
    }
    
    /**
     * Invalidates the cache entry for a specific query.
     * Use this when you know specific data has changed.
     *
     * @param query the query to invalidate
     */
    public void invalidate(@NonNull PreparedQuery query) {
        String cacheKey = generateCacheKey(query);
        cache.invalidate(cacheKey);
    }
    
    /**
     * Invalidates all cache entries related to a specific table.
     * <p>
     * This is called automatically when UPDATE/INSERT/DELETE operations
     * are performed on a table to ensure cache consistency.
     * </p>
     *
     * @param tableName the name of the table to invalidate
     */
    public void invalidateTable(@NonNull String tableName) {
        // Remove all entries whose key contains the table name
        cache.asMap().keySet().removeIf(key -> 
            key.toLowerCase().contains(tableName.toLowerCase())
        );
    }
    
    /**
     * Invalidates multiple tables at once.
     * Useful for operations that affect multiple tables.
     *
     * @param tableNames the names of tables to invalidate
     */
    public void invalidateTables(@NonNull String... tableNames) {
        for (String tableName : tableNames) {
            invalidateTable(tableName);
        }
    }
    
    /**
     * Clears the entire cache.
     * Use this sparingly as it will cause all subsequent queries to hit the database.
     */
    public void invalidateAll() {
        cache.invalidateAll();
    }
    
    /**
     * Gets cache performance statistics.
     * <p>
     * Key metrics:
     * - Hit rate: percentage of requests served from cache
     * - Miss rate: percentage of requests that hit the database
     * - Eviction count: entries removed due to size/time limits
     * - Load time: time spent executing database queries
     * </p>
     *
     * @return cache statistics
     */
    public CacheStats getStats() {
        return cache.stats();
    }
    
    /**
     * Gets the current size of the cache.
     *
     * @return number of cached query results
     */
    public long size() {
        return cache.estimatedSize();
    }
    
    /**
     * Generates a unique cache key for a query.
     * The key is based on the SQL and parameters to ensure uniqueness.
     *
     * @param query the prepared query
     * @return a unique cache key
     */
    private String generateCacheKey(@NonNull PreparedQuery query) {
        // Create a key from SQL and parameters
        // This ensures different parameters create different cache entries
        String sql = query.sql().toLowerCase().trim();
        String params = Arrays.toString(query.parameters());
        return sql + "|" + params;
    }
}

