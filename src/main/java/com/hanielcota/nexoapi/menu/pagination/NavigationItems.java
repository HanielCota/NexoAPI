package com.hanielcota.nexoapi.menu.pagination;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Value object encapsulating navigation items for pagination.
 * Caches navigation items to avoid recreation.
 *
 * @since 1.0.0
 */
public final class NavigationItems {
    private final ItemStack nextPageItem;
    private final ItemStack previousPageItem;

    private NavigationItems(@NotNull ItemStack nextPageItem, @NotNull ItemStack previousPageItem) {
        this.nextPageItem = Objects.requireNonNull(nextPageItem, "Next page item cannot be null.");
        this.previousPageItem = Objects.requireNonNull(previousPageItem, "Previous page item cannot be null.");
    }

    /**
     * Creates NavigationItems using factory functions.
     *
     * @param nextPageFactory     factory for creating next page item
     * @param previousPageFactory factory for creating previous page item
     * @return a new NavigationItems instance
     * @throws NullPointerException if any parameter is null
     */
    public static NavigationItems create(
            @NotNull ItemStackFactory nextPageFactory,
            @NotNull ItemStackFactory previousPageFactory
    ) {
        Objects.requireNonNull(nextPageFactory, "Next page factory cannot be null.");
        Objects.requireNonNull(previousPageFactory, "Previous page factory cannot be null.");

        return new NavigationItems(
                nextPageFactory.create(),
                previousPageFactory.create()
        );
    }

    @NotNull
    public ItemStack nextPage() {
        return nextPageItem;
    }

    @NotNull
    public ItemStack previousPage() {
        return previousPageItem;
    }

    /**
     * Functional interface for creating ItemStacks.
     */
    @FunctionalInterface
    public interface ItemStackFactory {
        @NotNull ItemStack create();
    }
}

