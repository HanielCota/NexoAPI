package com.hanielcota.nexoapi.cooldown;

import com.hanielcota.nexoapi.cooldown.property.CooldownKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public record CooldownRegistry(
        @NotNull ConcurrentMap<CooldownKey, Cooldown> cooldowns
) {

    public CooldownRegistry {
        Objects.requireNonNull(cooldowns, "Cooldown storage map cannot be null.");
    }

    public CooldownRegistry() {
        this(new ConcurrentHashMap<>());
    }

    public Optional<Cooldown> find(@NotNull CooldownKey cooldownKey) {
        Objects.requireNonNull(cooldownKey, "Cooldown key cannot be null.");
        return Optional.ofNullable(cooldowns.get(cooldownKey));
    }

    public void save(@NotNull Cooldown cooldown) {
        Objects.requireNonNull(cooldown, "Cooldown cannot be null.");
        cooldowns.put(cooldown.key(), cooldown);
    }

    public void remove(@NotNull CooldownKey cooldownKey) {
        Objects.requireNonNull(cooldownKey, "Cooldown key cannot be null.");
        cooldowns.remove(cooldownKey);
    }

    public void clearForOwner(@NotNull UUID ownerId) {
        Objects.requireNonNull(ownerId, "Owner id cannot be null.");
        cooldowns.keySet().removeIf(key -> ownerId.equals(key.ownerId()));
    }

    public void clearAll() {
        cooldowns.clear();
    }
}
