package com.hanielcota.nexoapi.item.skull.value;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * Represents a skull texture encoded in base64.
 * Used to create custom player head textures.
 *
 * @param base64 the base64-encoded texture string
 * @since 1.0.0
 */
public record SkullTexture(@NonNull String base64) {

    public SkullTexture {
        if (base64.isBlank()) {
            throw new IllegalArgumentException("Texture Base64 cannot be blank.");
        }
    }

    /**
     * Creates a new SkullTexture from a base64 string.
     *
     * @param base64 the base64-encoded texture string
     * @return a new SkullTexture instance
     * @throws IllegalArgumentException if base64 is blank
     */
    public static SkullTexture of(@NonNull String base64) {
        return new SkullTexture(base64);
    }

    /**
     * Converts this texture to a PlayerProfile.
     *
     * @return a PlayerProfile with this texture
     */
    public PlayerProfile toPlayerProfile() {
        UUID uuid = generateUuid();
        PlayerProfile profile = createProfile(uuid);
        applyTextureProperty(profile);
        return profile;
    }

    private UUID generateUuid() {
        return UUID.nameUUIDFromBytes(base64.getBytes());
    }

    private PlayerProfile createProfile(UUID uuid) {
        return Bukkit.createProfile(uuid);
    }

    private void applyTextureProperty(PlayerProfile profile) {
        var property = new ProfileProperty("textures", base64);
        profile.setProperty(property);
    }
}
