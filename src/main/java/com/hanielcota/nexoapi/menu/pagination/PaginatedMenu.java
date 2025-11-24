package com.hanielcota.nexoapi.menu.pagination;

import com.hanielcota.nexoapi.menu.MenuClickContext;
import com.hanielcota.nexoapi.menu.MenuView;
import com.hanielcota.nexoapi.menu.NexoMenu;
import com.hanielcota.nexoapi.menu.property.MenuSize;
import com.hanielcota.nexoapi.menu.property.MenuSlot;
import com.hanielcota.nexoapi.menu.property.MenuTitle;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for paginated menus.
 * Paginated menus display items across multiple pages with navigation controls.
 * <p>
 * Subclasses must implement:
 * </p>
 * <ul>
 *   <li>{@link #createItem(Object)} - Creates an ItemStack for each item</li>
 *   <li>{@link #createNextPageItem()} - Creates the "next page" navigation item</li>
 *   <li>{@link #createPreviousPageItem()} - Creates the "previous page" navigation item</li>
 *   <li>{@link #handleItemClick(Object, MenuClickContext)} - Handles clicks on items</li>
 * </ul>
 *
 * @param <T> the type of items being paginated
 * @since 1.0.0
 */
public abstract class PaginatedMenu<T> extends NexoMenu {

    private final PaginatedItems<T> paginatedItems;
    // Performance: Cache navigation items to avoid recreation
    private final ItemStack cachedNextPageItem;
    private final ItemStack cachedPreviousPageItem;
    private PageIndex currentPage;

    /**
     * Creates a new PaginatedMenu with the specified title, size, and items.
     *
     * @param title          the menu title
     * @param size           the menu size (must be at least 2 rows)
     * @param paginatedItems the items to paginate
     * @throws NullPointerException     if any parameter is null
     * @throws IllegalArgumentException if size has less than 2 rows
     */
    protected PaginatedMenu(@NotNull MenuTitle title,
                            @NotNull MenuSize size,
                            @NotNull PaginatedItems<T> paginatedItems) {
        super(title, size);

        Objects.requireNonNull(paginatedItems, "paginatedItems cannot be null");
        this.paginatedItems = paginatedItems;
        this.currentPage = PageIndex.first();

        int rows = size.rows();

        if (rows < 2) {
            throw new IllegalArgumentException("Paginated menus require at least 2 rows.");
        }

        // Performance: Pre-create navigation items once
        this.cachedNextPageItem = createNextPageItem();
        this.cachedPreviousPageItem = createPreviousPageItem();
    }

    @Override
    protected void populate(@NotNull MenuView view) {
        Objects.requireNonNull(view, "view cannot be null");

        Inventory inventory = view.inventory();
        inventory.clear();

        fillItems(view);
        fillNavigation(view);
    }

    /**
     * Creates an ItemStack representation of a paginated item.
     *
     * @param value the item value
     * @return the ItemStack to display
     */
    protected abstract ItemStack createItem(@NotNull T value);

    /**
     * Creates the "next page" navigation item.
     *
     * @return the ItemStack for the next page button
     */
    protected abstract ItemStack createNextPageItem();

    /**
     * Creates the "previous page" navigation item.
     *
     * @return the ItemStack for the previous page button
     */
    protected abstract ItemStack createPreviousPageItem();

    /**
     * Handles a click on a paginated item.
     *
     * @param value   the item that was clicked
     * @param context the click context
     */
    protected abstract void handleItemClick(@NotNull T value,
                                            @NotNull MenuClickContext context);

    private void fillItems(@NotNull MenuView view) {
        Objects.requireNonNull(view, "view cannot be null");

        Inventory inventory = view.inventory();

        MenuSize menuSize = size;
        int rows = menuSize.rows();
        int maxItemSlots = (rows - 1) * 9;

        List<T> pageValues = paginatedItems.pageItems(currentPage);
        int limit = Math.min(pageValues.size(), maxItemSlots);

        // Performance: Direct array access and batch operations
        for (int index = 0; index < limit; index++) {
            T value = pageValues.get(index);
            ItemStack itemStack = createItem(value);
            inventory.setItem(index, itemStack);
        }

        // Performance: Clear remaining slots in one pass if needed
        if (limit < maxItemSlots) {
            for (int index = limit; index < maxItemSlots; index++) {
                inventory.setItem(index, null);
            }
        }
    }

    private void fillNavigation(@NotNull MenuView view) {
        Objects.requireNonNull(view, "view cannot be null");

        Inventory inventory = view.inventory();

        MenuSize menuSize = size;
        int rows = menuSize.rows();
        int totalSlots = rows * 9;
        int lastRowStartIndex = totalSlots - 9;

        boolean hasPrevious = paginatedItems.hasPrevious(currentPage);

        if (hasPrevious) {
            // Performance: Use cached item instead of recreating
            inventory.setItem(lastRowStartIndex, cachedPreviousPageItem);
        }

        boolean hasNext = paginatedItems.hasNext(currentPage);

        if (hasNext) {
            int lastSlotIndex = totalSlots - 1;
            // Performance: Use cached item instead of recreating
            inventory.setItem(lastSlotIndex, cachedNextPageItem);
        }
    }

    @Override
    public void handleClick(@NotNull MenuClickContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        MenuSlot slot = context.slot();
        int slotIndex = slot.index();

        MenuSize menuSize = size;
        int rows = menuSize.rows();
        int totalSlots = rows * 9;
        int lastRowStartIndex = totalSlots - 9;
        int lastSlotIndex = totalSlots - 1;

        if (slotIndex == lastRowStartIndex) {
            handlePreviousClick(context);
            return;
        }

        if (slotIndex == lastSlotIndex) {
            handleNextClick(context);
            return;
        }

        handleItemAreaClick(context);
    }

    private void handlePreviousClick(@NotNull MenuClickContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        boolean hasPrevious = paginatedItems.hasPrevious(currentPage);

        if (!hasPrevious) {
            return;
        }

        currentPage = currentPage.previous();

        // Performance: Reuse existing inventory instead of creating new MenuView
        Inventory inventory = context.inventory();
        MenuView view = new MenuView(context.player(), inventory);
        populate(view);
    }

    private void handleNextClick(@NotNull MenuClickContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        boolean hasNext = paginatedItems.hasNext(currentPage);

        if (!hasNext) {
            return;
        }

        currentPage = currentPage.next();

        // Performance: Reuse existing inventory instead of creating new MenuView
        Inventory inventory = context.inventory();
        MenuView view = new MenuView(context.player(), inventory);
        populate(view);
    }

    private void handleItemAreaClick(@NotNull MenuClickContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        MenuSize menuSize = size;
        int rows = menuSize.rows();
        int maxItemSlots = (rows - 1) * 9;

        MenuSlot slot = context.slot();
        int slotIndex = slot.index();

        if (slotIndex >= maxItemSlots) {
            return;
        }

        List<T> pageValues = paginatedItems.pageItems(currentPage);

        if (slotIndex >= pageValues.size()) {
            return;
        }

        T value = pageValues.get(slotIndex);
        handleItemClick(value, context);
    }
}

