package com.hanielcota.nexoapi.menu.pagination;

import com.hanielcota.nexoapi.menu.MenuClickContext;
import com.hanielcota.nexoapi.menu.MenuView;
import com.hanielcota.nexoapi.menu.NexoMenu;
import com.hanielcota.nexoapi.menu.property.MenuSize;
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

    private PageState<T> pageState;
    private final NavigationItems navigationItems;

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
        validateMinimumSize(size);

        this.pageState = PageState.initial(paginatedItems);
        this.navigationItems = NavigationItems.create(
                this::createNextPageItem,
                this::createPreviousPageItem
        );
    }

    private void validateMinimumSize(@NotNull MenuSize size) {
        if (size.rows() < 2) {
            throw new IllegalArgumentException("Paginated menus require at least 2 rows.");
        }
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

        var inventory = view.inventory();
        var maxItemSlots = calculateMaxItemSlots();
        var pageItems = pageState.pageItems();
        var limit = Math.min(pageItems.size(), maxItemSlots);

        fillItemSlots(inventory, pageItems, limit);
        clearRemainingSlots(inventory, limit, maxItemSlots);
    }

    private int calculateMaxItemSlots() {
        int rows = size.rows();
        return (rows - 1) * 9;
    }

    private void fillItemSlots(@NotNull Inventory inventory, @NotNull List<T> items, int limit) {
        for (int index = 0; index < limit; index++) {
            T value = items.get(index);
            ItemStack itemStack = createItem(value);
            inventory.setItem(index, itemStack);
        }
    }

    private void clearRemainingSlots(@NotNull Inventory inventory, int from, int to) {
        if (from >= to) {
            return;
        }

        for (int index = from; index < to; index++) {
            inventory.setItem(index, null);
        }
    }

    private void fillNavigation(@NotNull MenuView view) {
        Objects.requireNonNull(view, "view cannot be null");

        var inventory = view.inventory();
        var positions = calculateNavigationPositions();

        if (pageState.hasPrevious()) {
            inventory.setItem(positions.previous(), navigationItems.previousPage());
        }

        if (pageState.hasNext()) {
            inventory.setItem(positions.next(), navigationItems.nextPage());
        }
    }

    @NotNull
    private NavigationPositions calculateNavigationPositions() {
        int rows = size.rows();
        int totalSlots = rows * 9;
        int lastRowStartIndex = totalSlots - 9;
        int lastSlotIndex = totalSlots - 1;
        return new NavigationPositions(lastRowStartIndex, lastSlotIndex);
    }

    private record NavigationPositions(int previous, int next) {}

    @Override
    public void handleClick(@NotNull MenuClickContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        var slotIndex = context.slot().index();
        var positions = calculateNavigationPositions();

        if (slotIndex == positions.previous()) {
            handlePreviousClick(context);
            return;
        }

        if (slotIndex == positions.next()) {
            handleNextClick(context);
            return;
        }

        handleItemAreaClick(context);
    }

    private void handlePreviousClick(@NotNull MenuClickContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        if (!pageState.hasPrevious()) {
            return;
        }

        pageState = pageState.previousPage();
        refreshMenu(context);
    }

    private void handleNextClick(@NotNull MenuClickContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        if (!pageState.hasNext()) {
            return;
        }

        pageState = pageState.nextPage();
        refreshMenu(context);
    }

    private void refreshMenu(@NotNull MenuClickContext context) {
        var inventory = context.inventory();
        var view = new MenuView(context.player(), inventory);
        populate(view);
    }

    private void handleItemAreaClick(@NotNull MenuClickContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        var slotIndex = context.slot().index();
        var maxItemSlots = calculateMaxItemSlots();

        if (slotIndex >= maxItemSlots) {
            return;
        }

        var pageItems = pageState.pageItems();
        if (slotIndex >= pageItems.size()) {
            return;
        }

        T value = pageItems.get(slotIndex);
        handleItemClick(value, context);
    }
}

