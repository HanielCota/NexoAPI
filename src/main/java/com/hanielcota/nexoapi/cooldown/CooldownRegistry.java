package com.hanielcota.nexoapi.cooldown;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.hanielcota.nexoapi.cooldown.property.CooldownKey;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Thread-safe registry for storing and managing cooldowns using Caffeine Cache.
 * Provides automatic expiration based on cooldown duration and performance statistics.
 * <p>
 * Key features:
 * - Automatic removal of expired cooldowns (no manual cleanup needed)
 * - Memory efficient with size limits
 * - Performance statistics (hit rate, eviction count, etc.)
 * - Custom expiration per cooldown based on its duration
 * </p>
 *
 * @param cooldowns the Caffeine cache storing cooldowns
 * @since 1.0.0
 */
public record CooldownRegistry(
        @NotNull Cache<CooldownKey, Cooldown> cooldowns
) {

    public CooldownRegistry {
        Objects.requireNonNull(cooldowns, "Cooldown storage cache cannot be null.");
    }

    /**
     * Creates a new CooldownRegistry with Caffeine Cache.
     * <p>
     * Configuration:
     * - Maximum size: 10,000 cooldowns
     * - Custom expiration: based on each cooldown's expiration time
     * - Statistics: enabled for monitoring
     * </p>
     */
    public CooldownRegistry() {
        this(Caffeine.newBuilder()
                .maximumSize(10000) // Limit to prevent memory bloat
                .expireAfter(new Expiry<CooldownKey, Cooldown>() {
                    @Override
                    public long expireAfterCreate(
                            @NotNull CooldownKey key,
                            @NotNull Cooldown value,
                            long currentTime
                    ) {
                        // Calculate expiration time based on cooldown's expiration
                        Instant now = Instant.now();
                        Duration remaining = value.remaining(now);
                        
                        // Convert to nanoseconds (Caffeine's time unit)
                        long nanos = remaining.toNanos();
                        
                        // Ensure at least 1 nanosecond (Caffeine requirement)
                        return Math.max(1, nanos);
                    }

                    @Override
                    public long expireAfterUpdate(
                            @NotNull CooldownKey key,
                            @NotNull Cooldown value,
                            long currentTime,
                            @NonNegative long currentDuration
                    ) {
                        // Recalculate on update
                        return expireAfterCreate(key, value, currentTime);
                    }

                    @Override
                    public long expireAfterRead(
                            @NotNull CooldownKey key,
                            @NotNull Cooldown value,
                            long currentTime,
                            @NonNegative long currentDuration
                    ) {
                        // Don't change expiration on read
                        return currentDuration;
                    }
                })
                .recordStats() // Enable performance statistics
                .build()
        );
    }

    /**
     * Finds a cooldown by its key.
     * <p>
     * Note: Expired cooldowns are automatically removed by Caffeine,
     * so this will only return active cooldowns.
     * </p>
     *
     * @param cooldownKey the cooldown key
     * @return an Optional containing the cooldown if found and not expired, empty otherwise
     * @throws NullPointerException if cooldownKey is null
     */
    public Optional<Cooldown> find(@NotNull CooldownKey cooldownKey) {
        Objects.requireNonNull(cooldownKey, "Cooldown key cannot be null.");
        return Optional.ofNullable(cooldowns.getIfPresent(cooldownKey));
    }

    /**
     * Saves a cooldown to the registry.
     * The cooldown will be automatically removed after it expires.
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
        cooldowns.invalidate(cooldownKey);
    }

    /**
     * Clears all cooldowns for a specific owner (player UUID).
     * <p>
     * This operation is thread-safe.
     * </p>
     *
     * @param ownerId the owner UUID
     * @throws NullPointerException if ownerId is null
     */
    public void clearForOwner(@NotNull UUID ownerId) {
        Objects.requireNonNull(ownerId, "Owner id cannot be null.");
        // Remove all cooldowns for this owner
        cooldowns.asMap().keySet().removeIf(key -> ownerId.equals(key.ownerId()));
    }

    /**
     * Clears all cooldowns from the registry.
     */
    public void clearAll() {
        cooldowns.invalidateAll();
    }

    /**
     * Gets cache performance statistics.
     * <p>
     * Useful metrics include:
     * - Hit rate: percentage of cooldown checks that found an active cooldown
     * - Miss rate: percentage of checks where no cooldown existed
     * - Eviction count: number of cooldowns automatically removed after expiration
     * </p>
     *
     * @return cache statistics
     */
    public CacheStats getStats() {
        return cooldowns.stats();
    }

    /**
     * Gets the current number of active cooldowns.
     * <p>
     * Note: This is an estimate and may not be perfectly accurate
     * due to asynchronous expiration handling.
     * </p>
     *
     * @return estimated number of active cooldowns
     */
    public long size() {
        return cooldowns.estimatedSize();
    }
}
