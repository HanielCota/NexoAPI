package com.hanielcota.nexoapi.item.skull.value;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
     * Creates a new SkullTexture from a Minecraft texture URL or hash.
     * Accepts either:
     * - Full URL: http://textures.minecraft.net/texture/&lt;hash&gt;
     * - Just the hash: &lt;hash&gt;
     *
     * @param textureUrlOrHash the Minecraft texture URL or hash
     * @return a new SkullTexture instance
     * @throws IllegalArgumentException if textureUrlOrHash is blank or invalid
     */
    public static SkullTexture fromUrl(@NonNull String textureUrlOrHash) {
        if (textureUrlOrHash.isBlank()) {
            throw new IllegalArgumentException("Texture URL or hash cannot be blank.");
        }

        String fullUrl = normalizeUrl(textureUrlOrHash);
        String json = createTextureJson(fullUrl);
        String base64 = encodeToBase64(json);
        return new SkullTexture(base64);
    }

    private static String normalizeUrl(String input) {
        // Se já é uma URL completa, retorna como está
        if (input.startsWith("http://") || input.startsWith("https://")) {
            return input;
        }
        
        // Se é apenas o hash, constrói a URL completa
        return "http://textures.minecraft.net/texture/" + input;
    }

    private static String createTextureJson(String url) {
        return String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", url);
    }

    private static String encodeToBase64(String json) {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
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
