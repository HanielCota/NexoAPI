package com.hanielcota.nexoapi.menu.pagination;

/**
 * Represents the index of a page in a paginated menu.
 * Page indices start at 0 (first page).
 *
 * @param value the page index (0 or greater)
 * @since 1.0.0
 */
public record PageIndex(int value) {

    public PageIndex {
        if (value < 0) {
            throw new IllegalArgumentException("Page index must be zero or positive.");
        }
    }

    /**
     * Creates the first page index (0).
     *
     * @return a PageIndex representing the first page
     */
    public static PageIndex first() {
        return new PageIndex(0);
    }

    /**
     * Returns the next page index.
     *
     * @return a new PageIndex with value incremented by 1
     */
    public PageIndex next() {
        int nextValue = value + 1;
        return new PageIndex(nextValue);
    }

    /**
     * Returns the previous page index.
     * If this is the first page (0), returns itself.
     *
     * @return a new PageIndex with value decremented by 1, or itself if already at first page
     */
    public PageIndex previous() {
        if (value == 0) {
            return this;
        }

        int previousValue = value - 1;
        return new PageIndex(previousValue);
    }
}

