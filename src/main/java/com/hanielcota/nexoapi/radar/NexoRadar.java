package com.hanielcota.nexoapi.radar;

import com.hanielcota.nexoapi.radar.range.DetectionRadius;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a radar system that can detect players within a certain radius.
 * Provides methods to scan for nearby players and check for their presence.
 *
 * @param center the center location of the radar
 * @param radius the detection radius
 * @since 1.0.0
 */
public record NexoRadar(@NotNull Location center,
                        @NotNull DetectionRadius radius) {

    /**
     * Creates a NexoRadar around an entity.
     *
     * @param entity       the entity to center the radar around
     * @param radiusBlocks the detection radius in blocks
     * @return a new NexoRadar instance
     */
    public static NexoRadar around(@NotNull Entity entity, double radiusBlocks) {
        Location location = entity.getLocation();
        DetectionRadius radius = DetectionRadius.ofBlocks(radiusBlocks);
        return new NexoRadar(location, radius);
    }

    /**
     * Creates a NexoRadar at a specific location.
     *
     * @param location     the center location
     * @param radiusBlocks the detection radius in blocks
     * @return a new NexoRadar instance
     */
    public static NexoRadar at(@NotNull Location location, double radiusBlocks) {
        DetectionRadius radius = DetectionRadius.ofBlocks(radiusBlocks);
        return new NexoRadar(location, radius);
    }

    /**
     * Scans for all players within the radar radius.
     *
     * @return a collection of players found within the radius
     */
    public Collection<Player> scanPlayers() {
        return scanPlayersNearby(player -> true, null);
    }

    /**
     * Scans for players within the radar radius, excluding a specific player.
     *
     * @param ignoredPlayer the player to exclude from the scan
     * @return a collection of players found within the radius
     */
    public Collection<Player> scanPlayersExcluding(@Nullable Player ignoredPlayer) {
        return scanPlayersNearby(player -> true, ignoredPlayer);
    }

    /**
     * Checks if there are any nearby players within the radar radius.
     *
     * @param ignoredPlayer the player to ignore (may be null)
     * @return true if at least one player is found, false otherwise
     */
    public boolean hasNearbyPlayers(@Nullable Player ignoredPlayer) {
        var world = center.getWorld();
        if (world == null) {
            return false;
        }

        double radiusSquared = calculateRadiusSquared();
        Collection<Entity> nearbyEntities = getNearbyEntities(world);

        return hasPlayerInRange(nearbyEntities, ignoredPlayer, radiusSquared);
    }

    private double calculateRadiusSquared() {
        double radiusBlocks = radius.blocks();
        return radiusBlocks * radiusBlocks;
    }

    private Collection<Entity> getNearbyEntities(org.bukkit.World world) {
        double radiusBlocks = radius.blocks();
        return world.getNearbyEntities(center, radiusBlocks, radiusBlocks, radiusBlocks);
    }

    private boolean hasPlayerInRange(Collection<Entity> entities, Player ignoredPlayer, double radiusSquared) {
        for (Entity entity : entities) {
            if (!(entity instanceof Player player)) {
                continue;
            }

            if (isPlayerWithinRadius(player, ignoredPlayer, radiusSquared)) {
                return true;
            }
        }

        return false;
    }

    private boolean isPlayerWithinRadius(Player player, Player ignoredPlayer, double radiusSquared) {
        if (shouldIgnorePlayer(player, ignoredPlayer)) {
            return false;
        }

        double distanceSquared = center.distanceSquared(player.getLocation());
        return distanceSquared <= radiusSquared;
    }

    private boolean shouldIgnorePlayer(Player player, Player ignoredPlayer) {
        if (ignoredPlayer == null) {
            return false;
        }
        return player.getUniqueId().equals(ignoredPlayer.getUniqueId());
    }

    private Collection<Player> scanPlayersNearby(Predicate<Player> filter,
                                                 @Nullable Player ignoredPlayer) {
        var world = center.getWorld();
        if (world == null) {
            return Collections.emptyList();
        }

        double radiusSquared = calculateRadiusSquared();
        Collection<Entity> nearbyEntities = getNearbyEntities(world);
        List<Player> players = new ArrayList<>(Math.min(nearbyEntities.size(), 10));

        collectPlayers(nearbyEntities, filter, ignoredPlayer, radiusSquared, players);

        return players;
    }

    private void collectPlayers(Collection<Entity> entities,
                                Predicate<Player> filter,
                                Player ignoredPlayer,
                                double radiusSquared,
                                List<Player> players) {
        for (Entity entity : entities) {
            if (!(entity instanceof Player player)) {
                continue;
            }

            if (shouldAddPlayer(player, filter, ignoredPlayer, radiusSquared)) {
                players.add(player);
            }
        }
    }

    private boolean shouldAddPlayer(Player player,
                                    Predicate<Player> filter,
                                    Player ignoredPlayer,
                                    double radiusSquared) {
        if (shouldIgnorePlayer(player, ignoredPlayer)) {
            return false;
        }

        if (!filter.test(player)) {
            return false;
        }

        return isPlayerWithinRadius(player, ignoredPlayer, radiusSquared);
    }
}
