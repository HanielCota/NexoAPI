package com.hanielcota.nexoapi.item.skull.value;

import lombok.NonNull;

import java.util.UUID;

/**
 * Represents a skull owner (player) identified by UUID and optionally by name.
 *
 * @param uuid the player UUID
 * @param name the player name (may be null)
 * @since 1.0.0
 */
public record SkullOwner(@NonNull UUID uuid, String name) {

    /**
     * Creates a new SkullOwner with UUID and name.
     *
     * @param uuid the player UUID
     * @param name the player name
     * @return a new SkullOwner instance
     */
    public static SkullOwner of(@NonNull UUID uuid, String name) {
        return new SkullOwner(uuid, name);
    }

    /**
     * Creates a new SkullOwner with only UUID.
     *
     * @param uuid the player UUID
     * @return a new SkullOwner instance with null name
     */
    public static SkullOwner of(@NonNull UUID uuid) {
        return new SkullOwner(uuid, null);
    }
}
