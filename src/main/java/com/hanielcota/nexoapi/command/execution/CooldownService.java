package com.hanielcota.nexoapi.command.execution;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for managing command cooldowns.
 * Tracks the last execution time for each player-command combination.
 * <p>
 * This service uses a shared instance to maintain cooldown state across all commands.
 *
 * @since 1.0.0
 */
public final class CooldownService {
    private static final CooldownService INSTANCE = new CooldownService();
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    private CooldownService() {
    }

    /**
     * Gets the shared CooldownService instance.
     *
     * @return the shared CooldownService instance
     */
    @NotNull
    public static CooldownService getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new CooldownService instance.
     * For most use cases, prefer using {@link #getInstance()} to get the shared instance.
     *
     * @return a new CooldownService instance
     */
    @NotNull
    public static CooldownService create() {
        return new CooldownService();
    }

    /**
     * Gets the cooldown key for a player and command name.
     *
     * @param player      the player
     * @param commandName the command name
     * @return a unique key for this player-command combination
     */
    @NotNull
    private String getKey(@NotNull Player player, @NotNull String commandName) {
        Objects.requireNonNull(player, "Player cannot be null.");
        Objects.requireNonNull(commandName, "Command name cannot be null.");
        UUID playerId = player.getUniqueId();
        return playerId + ":" + commandName;
    }

    /**
     * Records that a command was executed by a player at the current time.
     *
     * @param player      the player who executed the command
     * @param commandName the command name
     * @throws NullPointerException if any parameter is null
     */
    public void recordExecution(@NotNull Player player, @NotNull String commandName) {
        Objects.requireNonNull(player, "Player cannot be null.");
        Objects.requireNonNull(commandName, "Command name cannot be null.");
        String key = getKey(player, commandName);
        long currentTime = System.currentTimeMillis();
        cooldowns.put(key, currentTime);
    }

    /**
     * Gets the remaining cooldown time in milliseconds for a player-command combination.
     *
     * @param player      the player
     * @param commandName the command name
     * @param cooldownSeconds the cooldown duration in seconds
     * @return the remaining cooldown time in milliseconds, or 0 if no cooldown is active
     * @throws NullPointerException if any parameter is null
     */
    public long getRemainingCooldown(
            @NotNull Player player,
            @NotNull String commandName,
            long cooldownSeconds
    ) {
        Objects.requireNonNull(player, "Player cannot be null.");
        Objects.requireNonNull(commandName, "Command name cannot be null.");

        if (cooldownSeconds <= 0) {
            return 0;
        }

        String key = getKey(player, commandName);
        Long lastExecution = cooldowns.get(key);
        if (lastExecution == null) {
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastExecution;
        long cooldownMillis = cooldownSeconds * 1000L;

        if (elapsed >= cooldownMillis) {
            return 0;
        }

        return cooldownMillis - elapsed;
    }

    /**
     * Checks if a player is currently in cooldown for a command.
     *
     * @param player      the player
     * @param commandName the command name
     * @param cooldownSeconds the cooldown duration in seconds
     * @return true if the player is in cooldown, false otherwise
     * @throws NullPointerException if any parameter is null
     */
    public boolean isInCooldown(@NotNull Player player, @NotNull String commandName, long cooldownSeconds) {
        return getRemainingCooldown(player, commandName, cooldownSeconds) > 0;
    }
}

