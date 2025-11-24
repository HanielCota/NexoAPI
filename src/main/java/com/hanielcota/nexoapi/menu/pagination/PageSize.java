package com.hanielcota.nexoapi.menu.pagination;

/**
 * Represents the number of items per page in a paginated menu.
 *
 * @param value the number of items per page (must be greater than zero)
 * @since 1.0.0
 */
public record PageSize(int value) {

    public PageSize {
        if (value <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero.");
        }
    }

    /**
     * Creates a PageSize with the specified value.
     *
     * @param value the number of items per page
     * @return a new PageSize instance
     * @throws IllegalArgumentException if value is less than or equal to zero
     */
    public static PageSize of(int value) {
        return new PageSize(value);
    }
}

