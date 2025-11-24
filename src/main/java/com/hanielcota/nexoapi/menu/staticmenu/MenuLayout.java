package com.hanielcota.nexoapi.menu.staticmenu;

import com.hanielcota.nexoapi.menu.property.MenuSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents the layout of a static menu.
 * Maps slots to menu item definitions and provides methods to apply the layout to an inventory.
 *
 * @param itemsBySlot the immutable map of slots to item definitions
 * @since 1.0.0
 */
public record MenuLayout(@NotNull Map<MenuSlot, MenuItemDefinition> itemsBySlot) {

    public MenuLayout {
        Objects.requireNonNull(itemsBySlot, "itemsBySlot cannot be null");
    }

    /**
     * Creates a MenuLayout from a collection of item definitions.
     *
     * @param definitions the collection of menu item definitions
     * @return a new MenuLayout instance
     * @throws NullPointerException if definitions is null
     */
    public static MenuLayout of(@NotNull Collection<MenuItemDefinition> definitions) {
        Objects.requireNonNull(definitions, "definitions cannot be null");

        Map<MenuSlot, MenuItemDefinition> items = new HashMap<>();

        for (MenuItemDefinition definition : definitions) {
            MenuSlot slot = definition.slot();
            items.put(slot, definition);
        }

        Map<MenuSlot, MenuItemDefinition> immutableMap = Map.copyOf(items);
        return new MenuLayout(immutableMap);
    }

    /**
     * Finds a menu item definition by slot.
     * Performance optimized: Direct map lookup with early return.
     *
     * @param slot the slot to search for
     * @return an Optional containing the definition if found, empty otherwise
     */
    public Optional<MenuItemDefinition> findBySlot(@NotNull MenuSlot slot) {
        Objects.requireNonNull(slot, "slot cannot be null");

        // Performance: Direct map lookup (O(1) average case)
        MenuItemDefinition definition = itemsBySlot.get(slot);

        if (definition == null) {
            return Optional.empty();
        }

        return Optional.of(definition);
    }

    /**
     * Applies this layout to an inventory.
     * Places all items in their defined slots.
     * Performance optimized: Direct iteration and index access.
     *
     * @param inventory the inventory to apply the layout to
     * @throws NullPointerException if inventory is null
     */
    public void applyToInventory(@NotNull Inventory inventory) {
        Objects.requireNonNull(inventory, "inventory cannot be null");

        // Performance: Direct iteration over values (no key lookup needed)
        Collection<MenuItemDefinition> values = itemsBySlot.values();

        for (MenuItemDefinition definition : values) {
            MenuSlot slot = definition.slot();
            int index = slot.index();

            ItemStack itemStack = definition.itemStack();
            inventory.setItem(index, itemStack);
        }
    }
}

