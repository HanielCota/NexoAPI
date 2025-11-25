package com.hanielcota.nexoapi.item.skull.cache;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.hanielcota.nexoapi.item.skull.value.SkullTexture;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * First-class collection for cached skull profiles.
 * Encapsulates all operations related to the profile cache.
 *
 * @param cache the underlying cache map
 * @since 1.0.0
 */
public record SkullProfileMap(@NotNull Map<String, PlayerProfile> cache) {

    /**
     * Creates a new empty SkullProfileMap.
     *
     * @return a new empty SkullProfileMap instance
     */
    public static SkullProfileMap empty() {
        return new SkullProfileMap(new ConcurrentHashMap<>());
    }

    /**
     * Gets or creates a player profile for the given texture.
     *
     * @param texture      the skull texture
     * @param debugEnabled whether to log debug messages for cache hits
     * @return the cached or newly created player profile
     */
    public PlayerProfile getOrCreate(@NotNull SkullTexture texture, boolean debugEnabled) {
        String key = texture.base64();
        
        // Check if the key already exists in cache (cache hit)
        // We check before computeIfAbsent to detect cache hits
        boolean isCacheHit = cache.containsKey(key);
        
        PlayerProfile profile = cache.computeIfAbsent(key, k -> {
            // This lambda is only called if the key doesn't exist (cache miss)
            return createProfile(texture);
        });
        
        // Log debug message if cache was hit and debug is enabled
        if (debugEnabled && isCacheHit) {
            String keyPreview = key.length() > 20 ? key.substring(0, 20) + "..." : key;
            System.out.println("[SkullProfileCache] Cache hit para texture: " + keyPreview);
        }
        
        return profile;
    }

    private PlayerProfile createProfile(SkullTexture texture) {
        return texture.toPlayerProfile();
    }

    /**
     * Clears all cached profiles.
     */
    public void clear() {
        cache.clear();
    }
}

