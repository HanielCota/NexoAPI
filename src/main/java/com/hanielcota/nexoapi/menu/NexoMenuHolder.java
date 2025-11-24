package com.hanielcota.nexoapi.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Custom InventoryHolder that associates an Inventory with a NexoMenu.
 * This allows the menu system to identify which menu an inventory belongs to.
 *
 * @since 1.0.0
 */
public final class NexoMenuHolder implements InventoryHolder {

    private final NexoMenu menu;
    private Inventory inventory;

    /**
     * Creates a new NexoMenuHolder for the specified menu.
     *
     * @param menu the menu this holder belongs to
     * @throws NullPointerException if menu is null
     */
    public NexoMenuHolder(@NotNull NexoMenu menu) {
        this.menu = Objects.requireNonNull(menu, "menu cannot be null");
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    /**
     * Binds an inventory to this holder.
     * This is called internally when the menu is opened.
     *
     * @param inventory the inventory to bind
     * @throws NullPointerException if inventory is null
     */
    void bindInventory(@NotNull Inventory inventory) {
        this.inventory = Objects.requireNonNull(inventory, "inventory cannot be null");
    }

    /**
     * Returns the menu associated with this holder.
     *
     * @return the NexoMenu instance
     */
    public NexoMenu menu() {
        return menu;
    }
}

