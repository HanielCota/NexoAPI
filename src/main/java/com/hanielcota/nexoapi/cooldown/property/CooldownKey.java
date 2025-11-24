package com.hanielcota.nexoapi.cooldown.property;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a unique key for identifying a cooldown.
 * Combines a player UUID (owner) with a cooldown ID.
 *
 * @param ownerId    the UUID of the player who owns the cooldown
 * @param cooldownId the unique identifier for the cooldown type
 * @since 1.0.0
 */
public record CooldownKey(@NotNull UUID ownerId, @NotNull CooldownId cooldownId) {

    public CooldownKey {
        Objects.requireNonNull(ownerId, "Owner id cannot be null.");
        Objects.requireNonNull(cooldownId, "Cooldown id cannot be null.");
    }

    /**
     * Creates a CooldownKey for a player and cooldown ID.
     *
     * @param player     the player
     * @param cooldownId the cooldown ID
     * @return a new CooldownKey instance
     * @throws NullPointerException if player or cooldownId is null
     */
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
