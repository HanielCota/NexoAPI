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

/**
 * Service for managing player cooldowns.
 * Provides methods to check, consume, and manage cooldowns for players.
 * <p>
 * Cooldowns are automatically cleaned up when they expire.
 * </p>
 *
 * @param registry the cooldown registry for storage
 * @param clock    the clock for time-based operations
 * @since 1.0.0
 */
public record CooldownService(@NotNull CooldownRegistry registry, @NotNull Clock clock) {

    public CooldownService {
        Objects.requireNonNull(registry, "Cooldown registry cannot be null.");
        Objects.requireNonNull(clock, "Clock cannot be null.");
    }

    /**
     * Creates a default CooldownService with a new registry and system UTC clock.
     *
     * @return a new CooldownService instance
     */
    public static CooldownService createDefault() {
        return new CooldownService(new CooldownRegistry(), Clock.systemUTC());
    }

    /**
     * Checks if a player is currently on cooldown for the specified cooldown ID.
     * <p>
     * Note: With Caffeine Cache, expired cooldowns are automatically removed,
     * so no manual cleanup is needed. This method is now simpler and faster.
     * </p>
     *
     * @param player     the player to check
     * @param cooldownId the cooldown ID to check
     * @return true if the player is on cooldown, false otherwise
     * @throws NullPointerException if player or cooldownId is null
     */
    public boolean isOnCooldown(@NotNull Player player, @NotNull CooldownId cooldownId) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(cooldownId);

        var key = CooldownKey.forPlayer(player, cooldownId);
        
        // Caffeine automatically removes expired entries
        // If the cooldown exists in cache, it's active
        return registry.find(key).isPresent();
    }

    /**
     * Gets the remaining duration of a cooldown for a player.
     * Returns Duration.ZERO if the player is not on cooldown.
     * <p>
     * Note: With Caffeine Cache, expired cooldowns are automatically removed,
     * so this will always return accurate remaining time or ZERO.
     * </p>
     *
     * @param player     the player to check
     * @param cooldownId the cooldown ID to check
     * @return the remaining duration, or Duration.ZERO if not on cooldown
     * @throws NullPointerException if player or cooldownId is null
     */
    public Duration remaining(@NotNull Player player, @NotNull CooldownId cooldownId) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(cooldownId);

        var now = Instant.now(clock);
        var key = CooldownKey.forPlayer(player, cooldownId);
        var cooldown = registry.find(key);

        // If cooldown exists, it's not expired (Caffeine removes expired entries)
        return cooldown.map(cd -> cd.remaining(now)).orElse(Duration.ZERO);
    }

    /**
     * Attempts to consume a cooldown for a player.
     * If the player is not on cooldown, a new cooldown is created.
     * <p>
     * Performance note: With Caffeine Cache, this is faster than before
     * as we don't need to manually check expiration.
     * </p>
     *
     * @param player     the player
     * @param cooldownId the cooldown ID
     * @param duration   the duration of the cooldown to set if consumed
     * @return true if the cooldown was consumed (player was not on cooldown), false otherwise
     * @throws NullPointerException if any parameter is null
     */
    public boolean tryConsume(@NotNull Player player, @NotNull CooldownId cooldownId, @NotNull CooldownDuration duration) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(cooldownId);
        Objects.requireNonNull(duration);

        var key = CooldownKey.forPlayer(player, cooldownId);
        var existing = registry.find(key);

        // If no cooldown exists (or it expired and was auto-removed), we can consume
        if (existing.isEmpty()) {
            var expiration = CooldownExpiration.fromNow(duration, clock);
            registry.save(new Cooldown(key, expiration));
            return true;
        }

        return false;
    }

    /**
     * Resets (removes) a cooldown for a player.
     *
     * @param player     the player
     * @param cooldownId the cooldown ID to reset
     * @throws NullPointerException if player or cooldownId is null
     */
    public void reset(@NotNull Player player, @NotNull CooldownId cooldownId) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(cooldownId);
        registry.remove(CooldownKey.forPlayer(player, cooldownId));
    }

    /**
     * Clears all cooldowns for a player.
     *
     * @param player the player
     * @throws NullPointerException if player is null
     */
    public void clearAllFor(@NotNull Player player) {
        Objects.requireNonNull(player);
        registry.clearForOwner(player.getUniqueId());
    }

    /**
     * Attempts to consume a cooldown and runs an action after a delay if successful.
     * The action is scheduled to run after the cooldown duration expires.
     *
     * @param plugin     the plugin instance
     * @param player     the player
     * @param cooldownId the cooldown ID
     * @param duration   the cooldown duration
     * @param action     the action to run after consumption (if successful)
     * @throws NullPointerException if any parameter is null
     */
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
    
    /**
     * Gets cache performance statistics for the cooldown system.
     * <p>
     * Useful metrics include:
     * - Hit rate: percentage of cooldown checks that found an active cooldown
     * - Miss rate: percentage of checks where no cooldown existed
     * - Eviction count: number of cooldowns automatically removed after expiration
     * </p>
     *
     * @return cache statistics
     */
    public com.github.benmanes.caffeine.cache.stats.CacheStats getStats() {
        return registry.getStats();
    }
    
    /**
     * Gets the current number of active cooldowns in the system.
     * <p>
     * Note: This is an estimate due to asynchronous expiration handling.
     * </p>
     *
     * @return estimated number of active cooldowns
     */
    public long getActiveCooldownCount() {
        return registry.size();
    }
}
