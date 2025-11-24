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

    /**
     * Gets or creates a player profile for the given texture.
     *
     * @param texture the skull texture
     * @return the cached or newly created player profile
     */
    public PlayerProfile getOrCreate(@NotNull SkullTexture texture) {
        return CACHE.getOrCreate(texture);
    }

    /**
     * Clears all cached profiles.
     */
    public void clear() {
        CACHE.clear();
    }
}
