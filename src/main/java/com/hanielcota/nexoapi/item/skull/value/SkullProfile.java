package com.hanielcota.nexoapi.item.skull.value;

import com.destroystokyo.paper.profile.PlayerProfile;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a skull profile that can be resolved to a PlayerProfile.
 * Supports both texture-based and owner-based profiles.
 *
 * @since 1.0.0
 */
public sealed interface SkullProfile permits SkullProfile.Texture, SkullProfile.Owner {

    /**
     * Resolves this profile to a PlayerProfile.
     *
     * @return a CompletableFuture that completes with the resolved PlayerProfile
     */
    CompletableFuture<PlayerProfile> resolve();

    record Texture(@NonNull SkullTexture texture) implements SkullProfile {

        @Override
        public CompletableFuture<PlayerProfile> resolve() {
            return CompletableFuture.completedFuture(texture.toPlayerProfile());
        }
    }
    record Owner(@NonNull SkullOwner owner) implements SkullProfile {

        @Override
        public CompletableFuture<PlayerProfile> resolve() {
            var profile = createProfile();
            return updateProfile(profile);
        }

        private PlayerProfile createProfile() {
            return Bukkit.createProfile(owner.uuid(), owner.name());
        }

        private CompletableFuture<PlayerProfile> updateProfile(PlayerProfile profile) {
            return profile.update().thenApply(unused -> profile);
        }
    }
}
