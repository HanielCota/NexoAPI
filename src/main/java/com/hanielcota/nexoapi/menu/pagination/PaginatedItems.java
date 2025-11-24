package com.hanielcota.nexoapi.menu.pagination;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a collection of items that can be paginated.
 * Provides methods to retrieve items for a specific page and check pagination state.
 *
 * @param <T>      the type of items being paginated
 * @param values   the immutable list of all items
 * @param pageSize the number of items per page
 * @since 1.0.0
 */
public record PaginatedItems<T>(@NotNull List<T> values,
                                @NotNull PageSize pageSize) {

    public PaginatedItems {
        Objects.requireNonNull(values, "values cannot be null");
        Objects.requireNonNull(pageSize, "pageSize cannot be null");
    }

    /**
     * Creates a PaginatedItems from a collection and page size.
     *
     * @param values   the collection of items to paginate
     * @param pageSize the number of items per page
     * @param <T>      the type of items
     * @return a new PaginatedItems instance
     * @throws NullPointerException if values or pageSize is null
     */
    public static <T> PaginatedItems<T> of(@NotNull Collection<T> values,
                                           @NotNull PageSize pageSize) {
        Objects.requireNonNull(values, "values cannot be null");
        Objects.requireNonNull(pageSize, "pageSize cannot be null");

        List<T> immutableList = List.copyOf(values);
        return new PaginatedItems<>(immutableList, pageSize);
    }

    /**
     * Retrieves the items for a specific page.
     *
     * @param pageIndex the index of the page to retrieve
     * @return a list of items for the specified page, or empty list if page is out of bounds
     */
    public List<T> pageItems(@NotNull PageIndex pageIndex) {
        Objects.requireNonNull(pageIndex, "pageIndex cannot be null");

        int pageSizeValue = pageSize.value();
        int startIndex = pageIndex.value() * pageSizeValue;

        if (startIndex >= values.size()) {
            return List.of();
        }

        int endExclusive = Math.min(startIndex + pageSizeValue, values.size());
        // Return immutable copy to maintain record immutability contract
        return List.copyOf(values.subList(startIndex, endExclusive));
    }

    /**
     * Calculates the total number of pages.
     *
     * @return the total number of pages (at least 1)
     */
    public int totalPages() {
        int pageSizeValue = pageSize.value();

        if (values.isEmpty()) {
            return 1;
        }

        int size = values.size();
        return (size + pageSizeValue - 1) / pageSizeValue;
    }

    /**
     * Checks if there is a next page after the given page index.
     *
     * @param pageIndex the current page index
     * @return true if there is a next page, false otherwise
     */
    public boolean hasNext(@NotNull PageIndex pageIndex) {
        Objects.requireNonNull(pageIndex, "pageIndex cannot be null");

        int lastIndex = totalPages() - 1;
        int currentIndex = pageIndex.value();

        return currentIndex < lastIndex;
    }

    /**
     * Checks if there is a previous page before the given page index.
     *
     * @param pageIndex the current page index
     * @return true if there is a previous page, false otherwise
     */
    public boolean hasPrevious(@NotNull PageIndex pageIndex) {
        Objects.requireNonNull(pageIndex, "pageIndex cannot be null");

        int currentIndex = pageIndex.value();
        return currentIndex > 0;
    }
}

