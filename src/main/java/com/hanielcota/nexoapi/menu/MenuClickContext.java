package com.hanielcota.nexoapi.menu;

import com.hanielcota.nexoapi.menu.property.MenuSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents the context of a menu click event.
 * Contains all relevant information about the click.
 *
 * @param player    the player who clicked
 * @param inventory the inventory that was clicked
 * @param slot      the slot that was clicked
 * @param clickType the type of click (left, right, shift, etc.)
 * @param event     the original InventoryClickEvent
 * @since 1.0.0
 */
public record MenuClickContext(@NotNull Player player,
                               @NotNull Inventory inventory,
                               @NotNull MenuSlot slot,
                               @NotNull ClickType clickType,
                               @NotNull InventoryClickEvent event) {

    public MenuClickContext {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(inventory, "inventory cannot be null");
        Objects.requireNonNull(slot, "slot cannot be null");
        Objects.requireNonNull(clickType, "clickType cannot be null");
        Objects.requireNonNull(event, "event cannot be null");
    }
}

