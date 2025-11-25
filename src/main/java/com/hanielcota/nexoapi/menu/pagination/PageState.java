package com.hanielcota.nexoapi.menu.pagination;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Value object encapsulating pagination state.
 * Manages current page and provides navigation operations.
 *
 * @param <T> the type of items being paginated
 * @since 1.0.0
 */
public final class PageState<T> {
    private final PageIndex currentPage;
    private final PaginatedItems<T> paginatedItems;

    private PageState(@NotNull PageIndex currentPage, @NotNull PaginatedItems<T> paginatedItems) {
        this.currentPage = Objects.requireNonNull(currentPage, "Current page cannot be null.");
        this.paginatedItems = Objects.requireNonNull(paginatedItems, "Paginated items cannot be null.");
    }

    /**
     * Creates initial page state.
     *
     * @param paginatedItems the paginated items
     * @param <T>            the type of items
     * @return a new PageState starting at the first page
     * @throws NullPointerException if paginatedItems is null
     */
    public static <T> PageState<T> initial(@NotNull PaginatedItems<T> paginatedItems) {
        Objects.requireNonNull(paginatedItems, "Paginated items cannot be null.");
        return new PageState<>(PageIndex.first(), paginatedItems);
    }

    /**
     * Returns the next page state.
     *
     * @return a new PageState with the next page
     */
    @NotNull
    public PageState<T> nextPage() {
        return new PageState<>(currentPage.next(), paginatedItems);
    }

    /**
     * Returns the previous page state.
     *
     * @return a new PageState with the previous page
     */
    @NotNull
    public PageState<T> previousPage() {
        return new PageState<>(currentPage.previous(), paginatedItems);
    }

    @NotNull
    public PageIndex currentPage() {
        return currentPage;
    }

    public boolean hasNext() {
        return paginatedItems.hasNext(currentPage);
    }

    public boolean hasPrevious() {
        return paginatedItems.hasPrevious(currentPage);
    }

    @NotNull
    public List<T> pageItems() {
        return paginatedItems.pageItems(currentPage);
    }
}

