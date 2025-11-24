package com.hanielcota.nexoapi.item.skull.property;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record SkullTexture(@NotNull String value) {

    public static SkullTexture of(@NotNull String base64) {
        if (base64.isBlank()) {
            throw new IllegalArgumentException("Texture value cannot be empty");
        }
        return new SkullTexture(base64);
    }


    public PlayerProfile toProfile() {
        // Gera um UUID consistente baseado na textura para que itens iguais empilhem no inventário
        final var uuid = UUID.nameUUIDFromBytes(value.getBytes());
        final var profile = Bukkit.createProfile(uuid);

        profile.setProperty(new ProfileProperty("textures", value));
        return profile;
    }
}