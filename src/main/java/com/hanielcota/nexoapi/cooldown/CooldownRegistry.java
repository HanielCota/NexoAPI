package com.hanielcota.nexoapi.cooldown;

import com.hanielcota.nexoapi.cooldown.property.CooldownKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe registry for storing and managing cooldowns.
 * Uses a concurrent map to store cooldowns by their keys.
 *
 * @param cooldowns the concurrent map storing cooldowns
 * @since 1.0.0
 */
public record CooldownRegistry(
        @NotNull ConcurrentMap<CooldownKey, Cooldown> cooldowns
) {

    public CooldownRegistry {
        Objects.requireNonNull(cooldowns, "Cooldown storage map cannot be null.");
    }

    /**
     * Creates a new CooldownRegistry with an empty concurrent map.
     */
    public CooldownRegistry() {
        this(new ConcurrentHashMap<>());
    }

    /**
     * Finds a cooldown by its key.
     *
     * @param cooldownKey the cooldown key
     * @return an Optional containing the cooldown if found, empty otherwise
     * @throws NullPointerException if cooldownKey is null
     */
    public Optional<Cooldown> find(@NotNull CooldownKey cooldownKey) {
        Objects.requireNonNull(cooldownKey, "Cooldown key cannot be null.");
        return Optional.ofNullable(cooldowns.get(cooldownKey));
    }

    /**
     * Saves a cooldown to the registry.
     *
     * @param cooldown the cooldown to save
     * @throws NullPointerException if cooldown is null
     */
    public void save(@NotNull Cooldown cooldown) {
        Objects.requireNonNull(cooldown, "Cooldown cannot be null.");
        cooldowns.put(cooldown.key(), cooldown);
    }

    /**
     * Removes a cooldown from the registry.
     *
     * @param cooldownKey the cooldown key to remove
     * @throws NullPointerException if cooldownKey is null
     */
    public void remove(@NotNull CooldownKey cooldownKey) {
        Objects.requireNonNull(cooldownKey, "Cooldown key cannot be null.");
        cooldowns.remove(cooldownKey);
    }

    /**
     * Clears all cooldowns for a specific owner (player UUID).
     * <p>
     * This operation is thread-safe and uses entry iteration for safe removal.
     * </p>
     *
     * @param ownerId the owner UUID
     * @throws NullPointerException if ownerId is null
     */
    public void clearForOwner(@NotNull UUID ownerId) {
        Objects.requireNonNull(ownerId, "Owner id cannot be null.");
        // Use entrySet iteration for thread-safe removal from ConcurrentHashMap
        cooldowns.entrySet().removeIf(entry -> ownerId.equals(entry.getKey().ownerId()));
    }

    /**
     * Clears all cooldowns from the registry.
     */
    public void clearAll() {
        cooldowns.clear();
    }
}
