package com.hanielcota.nexoapi.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents the context of a menu close event.
 * Contains all relevant information about the menu being closed.
 *
 * @param player    the player who closed the menu
 * @param inventory the inventory that was closed
 * @param event     the original InventoryCloseEvent
 * @since 1.0.0
 */
public record MenuCloseContext(@NotNull Player player,
                               @NotNull Inventory inventory,
                               @NotNull InventoryCloseEvent event) {

    public MenuCloseContext {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(inventory, "inventory cannot be null");
        Objects.requireNonNull(event, "event cannot be null");
    }
}

