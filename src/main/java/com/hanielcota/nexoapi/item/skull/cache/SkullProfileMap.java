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
     * @param texture the skull texture
     * @return the cached or newly created player profile
     */
    public PlayerProfile getOrCreate(@NotNull SkullTexture texture) {
        String key = texture.base64();
        return cache.computeIfAbsent(key, k -> createProfile(texture));
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

