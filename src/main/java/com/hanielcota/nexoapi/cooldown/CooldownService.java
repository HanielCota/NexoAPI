package com.hanielcota.nexoapi.cooldown;

import com.hanielcota.nexoapi.cooldown.property.CooldownDuration;
import com.hanielcota.nexoapi.cooldown.property.CooldownExpiration;
import com.hanielcota.nexoapi.cooldown.property.CooldownId;
import com.hanielcota.nexoapi.cooldown.property.CooldownKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record CooldownService(@NotNull CooldownRegistry registry, @NotNull Clock clock) {

    public CooldownService {
        Objects.requireNonNull(registry, "Cooldown registry cannot be null.");
        Objects.requireNonNull(clock, "Clock cannot be null.");
    }

    public static CooldownService createDefault() {
        return new CooldownService(new CooldownRegistry(), Clock.systemUTC());
    }

    public boolean isOnCooldown(@NotNull Player player, @NotNull CooldownId cooldownId) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(cooldownId);

        var now = Instant.now(clock);
        var key = CooldownKey.forPlayer(player, cooldownId);
        var cooldown = registry.find(key);

        if (cooldown.isEmpty()) return false;
        if (cooldown.get().isExpired(now)) {
            registry.remove(key);
            return false;
        }

        return true;
    }

    public Duration remaining(@NotNull Player player, @NotNull CooldownId cooldownId) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(cooldownId);

        var now = Instant.now(clock);
        var key = CooldownKey.forPlayer(player, cooldownId);
        var cooldown = registry.find(key);

        if (cooldown.isEmpty()) return Duration.ZERO;
        if (cooldown.get().isExpired(now)) {
            registry.remove(key);
            return Duration.ZERO;
        }

        return cooldown.get().remaining(now);
    }

    public boolean tryConsume(@NotNull Player player, @NotNull CooldownId cooldownId, @NotNull CooldownDuration duration) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(cooldownId);
        Objects.requireNonNull(duration);

        var now = Instant.now(clock);
        var key = CooldownKey.forPlayer(player, cooldownId);
        var existing = registry.find(key);

        boolean canConsume = existing.isEmpty() || existing.get().isExpired(now);

        if (canConsume) {
            if (existing.isPresent()) {
                registry.remove(key);
            }
            var expiration = CooldownExpiration.fromNow(duration, clock);
            registry.save(new Cooldown(key, expiration));
            return true;
        }

        return false;
    }

    public void reset(@NotNull Player player, @NotNull CooldownId cooldownId) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(cooldownId);
        registry.remove(CooldownKey.forPlayer(player, cooldownId));
    }

    public void clearAllFor(@NotNull Player player) {
        Objects.requireNonNull(player);
        registry.clearForOwner(player.getUniqueId());
    }

    public void runAfterConsumption(@NotNull Plugin plugin, @NotNull Player player, @NotNull CooldownId cooldownId, @NotNull CooldownDuration duration, @NotNull Runnable action) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(player);
        Objects.requireNonNull(cooldownId);
        Objects.requireNonNull(duration);
        Objects.requireNonNull(action);

        if (!tryConsume(player, cooldownId, duration)) return;

        long ticks = duration.toTicks();
        if (ticks <= 0) {
            ticks = 1;
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, action, ticks);
    }
}
