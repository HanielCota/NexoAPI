package com.hanielcota.nexoapi.item.skull.cache;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.hanielcota.nexoapi.item.skull.value.SkullTexture;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for caching skull profiles.
 * Provides a singleton cache instance for efficient profile reuse.
 *
 * @since 1.0.0
 */
@UtilityClass
public class SkullProfileCache {

    private final SkullProfileMap CACHE = SkullProfileMap.empty();
    private volatile boolean debugEnabled = false;

    /**
     * Gets or creates a player profile for the given texture.
     *
     * @param texture the skull texture
     * @return the cached or newly created player profile
     */
    public PlayerProfile getOrCreate(@NotNull SkullTexture texture) {
        return CACHE.getOrCreate(texture, debugEnabled);
    }

    /**
     * Clears all cached profiles.
     */
    public void clear() {
        CACHE.clear();
    }

    /**
     * Enables debug logging for cache hits.
     * When enabled, logs a message whenever a cached profile is retrieved.
     */
    public void enableDebug() {
        debugEnabled = true;
    }

    /**
     * Disables debug logging for cache hits.
     */
    public void disableDebug() {
        debugEnabled = false;
    }

    /**
     * Checks if debug logging is currently enabled.
     *
     * @return true if debug is enabled, false otherwise
     */
    public boolean isDebugEnabled() {
        return debugEnabled;
    }
}
