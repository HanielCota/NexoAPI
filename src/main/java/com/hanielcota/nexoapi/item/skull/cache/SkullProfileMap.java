package com.hanielcota.nexoapi.item.skull.cache;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.hanielcota.nexoapi.item.skull.value.SkullTexture;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * First-class collection for cached skull profiles using Caffeine Cache.
 * Provides automatic eviction, size limits, and performance statistics.
 * <p>
 * Features:
 * - Maximum 1000 cached profiles (prevents memory bloat)
 * - Auto-expires after 30 minutes of no access
 * - Performance statistics (hit rate, miss rate, etc.)
 * </p>
 *
 * @param cache the underlying Caffeine cache
 * @since 1.0.0
 */
public record SkullProfileMap(@NotNull Cache<String, PlayerProfile> cache) {

    /**
     * Creates a new empty SkullProfileMap with optimized Caffeine configuration.
     * <p>
     * Configuration:
     * - Maximum size: 1000 profiles
     * - Expire after access: 30 minutes
     * - Statistics: enabled
     * </p>
     *
     * @return a new empty SkullProfileMap instance
     */
    public static SkullProfileMap empty() {
        Cache<String, PlayerProfile> cache = Caffeine.newBuilder()
                .maximumSize(1000) // Limit to 1000 profiles to prevent memory bloat
                .expireAfterAccess(30, TimeUnit.MINUTES) // Remove unused profiles after 30min
                .recordStats() // Enable performance statistics
                .build();
        
        return new SkullProfileMap(cache);
    }

    /**
     * Gets or creates a player profile for the given texture.
     * <p>
     * This method benefits from Caffeine's intelligent caching:
     * - Cache hits are extremely fast (~0.1ms)
     * - Cache misses create the profile and cache it automatically
     * - Automatic eviction of old/unused profiles
     * </p>
     *
     * @param texture      the skull texture
     * @param debugEnabled whether to log debug messages for cache hits
     * @return the cached or newly created player profile
     */
    public PlayerProfile getOrCreate(@NotNull SkullTexture texture, boolean debugEnabled) {
        String key = texture.base64();
        
        // Get from cache or compute if absent
        PlayerProfile profile = cache.get(key, k -> createProfile(texture));
        
        // Log debug information if enabled
        if (debugEnabled) {
            CacheStats stats = cache.stats();
            String keyPreview = key.length() > 20 ? key.substring(0, 20) + "..." : key;
            System.out.println(String.format(
                "[SkullProfileCache] Texture: %s | Hit Rate: %.2f%% | Total Requests: %d",
                keyPreview,
                stats.hitRate() * 100,
                stats.requestCount()
            ));
        }
        
        return profile;
    }

    private PlayerProfile createProfile(SkullTexture texture) {
        return texture.toPlayerProfile();
    }

    /**
     * Gets cache statistics including hit rate, miss rate, and eviction count.
     * <p>
     * This is useful for monitoring cache performance and tuning parameters.
     * </p>
     *
     * @return cache statistics
     */
    public CacheStats getStats() {
        return cache.stats();
    }

    /**
     * Gets the current number of cached profiles.
     *
     * @return the number of entries in the cache
     */
    public long size() {
        return cache.estimatedSize();
    }

    /**
     * Clears all cached profiles.
     * This will cause all subsequent requests to create new profiles.
     */
    public void clear() {
        cache.invalidateAll();
    }
}

