package com.hanielcota.nexoapi.menu.staticmenu;

import com.hanielcota.nexoapi.menu.property.MenuSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Defines a menu item with its slot, item stack, and click handler.
 * Used in static menus to define fixed menu layouts.
 *
 * @param slot         the slot where this item should be placed
 * @param itemStack    the ItemStack to display
 * @param clickHandler the handler for click events on this item
 * @since 1.0.0
 */
public record MenuItemDefinition(@NotNull MenuSlot slot,
                                 @NotNull ItemStack itemStack,
                                 @NotNull MenuClickHandler clickHandler) {

    public MenuItemDefinition {
        Objects.requireNonNull(slot, "slot cannot be null");
        Objects.requireNonNull(itemStack, "itemStack cannot be null");
        Objects.requireNonNull(clickHandler, "clickHandler cannot be null");
    }
}

