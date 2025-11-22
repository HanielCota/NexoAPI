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

public record NexoRadar(@NotNull Location center,
                        @NotNull DetectionRadius radius) {

    public static NexoRadar around(@NotNull Entity entity, double radiusBlocks) {
        return new NexoRadar(entity.getLocation(), DetectionRadius.ofBlocks(radiusBlocks));
    }

    public static NexoRadar at(@NotNull Location location, double radiusBlocks) {
        return new NexoRadar(location, DetectionRadius.ofBlocks(radiusBlocks));
    }

    public Collection<Player> scanPlayers() {
        return scanPlayersNearby(player -> true, null);
    }

    public Collection<Player> scanPlayersExcluding(@Nullable Player ignoredPlayer) {
        return scanPlayersNearby(player -> true, ignoredPlayer);
    }

    public boolean hasNearbyPlayers(@Nullable Player ignoredPlayer) {
        var world = center.getWorld();
        if (world == null) {
            return false;
        }

        double radiusBlocks = radius.blocks();
        double radiusSquared = radiusBlocks * radiusBlocks;

        Collection<Entity> nearbyEntities = world.getNearbyEntities(center, radiusBlocks, radiusBlocks, radiusBlocks);

        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof Player player)) {
                continue;
            }

            boolean shouldIgnore = ignoredPlayer != null
                    && player.getUniqueId().equals(ignoredPlayer.getUniqueId());

            double distanceSquared = center.distanceSquared(player.getLocation());
            boolean isInRange = distanceSquared <= radiusSquared;

            if (!shouldIgnore && isInRange) {
                return true;
            }
        }

        return false;
    }

    private Collection<Player> scanPlayersNearby(Predicate<Player> filter,
                                                 @Nullable Player ignoredPlayer) {
        var world = center.getWorld();
        if (world == null) {
            return Collections.emptyList();
        }

        double radiusBlocks = radius.blocks();
        double radiusSquared = radiusBlocks * radiusBlocks;

        Collection<Entity> nearbyEntities = world.getNearbyEntities(center, radiusBlocks, radiusBlocks, radiusBlocks);
        List<Player> players = new ArrayList<>(Math.min(nearbyEntities.size(), 10));

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player player) {
                boolean isNotIgnored = ignoredPlayer == null
                        || !player.getUniqueId().equals(ignoredPlayer.getUniqueId());

                boolean passesFilter = filter.test(player);

                boolean isInRange = center.distanceSquared(player.getLocation()) <= radiusSquared;

                if (isNotIgnored && passesFilter && isInRange) {
                    players.add(player);
                }
            }
        }

        return players;
    }
}
