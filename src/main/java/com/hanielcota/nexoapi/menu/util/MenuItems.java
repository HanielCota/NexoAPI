package com.hanielcota.nexoapi.menu.util;

import com.hanielcota.nexoapi.item.NexoItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Utility class for creating common menu items using NexoItem.
 * Provides helper methods to create navigation items, decorative items, and more.
 *
 * @since 1.0.0
 */
public final class MenuItems {

    private MenuItems() {
        throw new UnsupportedOperationException("Utility class.");
    }

    /**
     * Creates a navigation item for "next page" using NexoItem.
     *
     * @param name the display name (supports MiniMessage)
     * @param lore the lore lines (supports MiniMessage), may be null
     * @return the ItemStack for the next page button
     */
    public static ItemStack nextPage(@NotNull String name, @Nullable List<String> lore) {
        return NexoItem.from(Material.ARROW)
                .withName(name)
                .withLore(lore)
                .build();
    }

    /**
     * Creates a navigation item for "next page" with default name.
     *
     * @return the ItemStack for the next page button
     */
    public static ItemStack nextPage() {
        return nextPage("<green>Próxima Página", List.of(
                "<gray>Clique para ir para",
                "<gray>a próxima página"
        ));
    }

    /**
     * Creates a navigation item for "previous page" using NexoItem.
     *
     * @param name the display name (supports MiniMessage)
     * @param lore the lore lines (supports MiniMessage), may be null
     * @return the ItemStack for the previous page button
     */
    public static ItemStack previousPage(@NotNull String name, @Nullable List<String> lore) {
        return NexoItem.from(Material.ARROW)
                .withName(name)
                .withLore(lore)
                .build();
    }

    /**
     * Creates a navigation item for "previous page" with default name.
     *
     * @return the ItemStack for the previous page button
     */
    public static ItemStack previousPage() {
        return previousPage("<green>Página Anterior", List.of(
                "<gray>Clique para voltar",
                "<gray>para a página anterior"
        ));
    }

    /**
     * Creates a decorative filler item (usually glass panes).
     *
     * @param material the material to use (defaults to GRAY_STAINED_GLASS_PANE)
     * @return the ItemStack for the filler
     */
    public static ItemStack filler(@NotNull Material material) {
        return NexoItem.from(material)
                .withName(" ")
                .build();
    }

    /**
     * Creates a decorative filler item with default material.
     *
     * @return the ItemStack for the filler
     */
    public static ItemStack filler() {
        return filler(Material.GRAY_STAINED_GLASS_PANE);
    }

    /**
     * Creates a close button item.
     *
     * @param name the display name (supports MiniMessage)
     * @param lore the lore lines (supports MiniMessage), may be null
     * @return the ItemStack for the close button
     */
    public static ItemStack close(@NotNull String name, @Nullable List<String> lore) {
        return NexoItem.from(Material.BARRIER)
                .withName(name)
                .withLore(lore)
                .build();
    }

    /**
     * Creates a close button with default name.
     *
     * @return the ItemStack for the close button
     */
    public static ItemStack close() {
        return close("<red>Fechar", List.of(
                "<gray>Clique para fechar",
                "<gray>este menu"
        ));
    }

    /**
     * Creates a back button item.
     *
     * @param name the display name (supports MiniMessage)
     * @param lore the lore lines (supports MiniMessage), may be null
     * @return the ItemStack for the back button
     */
    public static ItemStack back(@NotNull String name, @Nullable List<String> lore) {
        return NexoItem.from(Material.ARROW)
                .withName(name)
                .withLore(lore)
                .build();
    }

    /**
     * Creates a back button with default name.
     *
     * @return the ItemStack for the back button
     */
    public static ItemStack back() {
        return back("<yellow>Voltar", List.of(
                "<gray>Clique para voltar",
                "<gray>ao menu anterior"
        ));
    }

    /**
     * Creates a custom item using NexoItem builder.
     * This is a convenience method that returns the builder for chaining.
     *
     * @param material the material for the item
     * @return a NexoItem builder instance
     */
    public static NexoItem custom(@NotNull Material material) {
        return NexoItem.from(material);
    }
}

