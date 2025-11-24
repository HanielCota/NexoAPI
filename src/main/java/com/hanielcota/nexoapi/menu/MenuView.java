package com.hanielcota.nexoapi.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a view of a menu being displayed to a player.
 * Contains the player viewing the menu and the inventory instance.
 *
 * @param viewer    the player viewing the menu
 * @param inventory the inventory instance representing the menu
 * @since 1.0.0
 */
public record MenuView(@NotNull Player viewer,
                       @NotNull Inventory inventory) {

    public MenuView {
        Objects.requireNonNull(viewer, "viewer cannot be null");
        Objects.requireNonNull(inventory, "inventory cannot be null");
    }
}

