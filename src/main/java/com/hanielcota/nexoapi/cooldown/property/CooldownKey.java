package com.hanielcota.nexoapi.cooldown.property;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public record CooldownKey(@NotNull UUID ownerId, @NotNull CooldownId cooldownId) {

    public CooldownKey {
        Objects.requireNonNull(ownerId, "Owner id cannot be null.");
        Objects.requireNonNull(cooldownId, "Cooldown id cannot be null.");
    }

    public static CooldownKey forPlayer(
            @NotNull Player player,
            @NotNull CooldownId cooldownId
    ) {
        Objects.requireNonNull(player, "Player cannot be null.");
        Objects.requireNonNull(cooldownId, "Cooldown id cannot be null.");

        var ownerId = player.getUniqueId();
        return new CooldownKey(ownerId, cooldownId);
    }
}
