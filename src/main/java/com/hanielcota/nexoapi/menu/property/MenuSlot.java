package com.hanielcota.nexoapi.menu.property;

/**
 * Represents a slot index in a menu inventory.
 * Valid slot indices range from 0 to 53 (6 rows * 9 columns - 1).
 *
 * @param index the slot index (0-53)
 * @since 1.0.0
 */
public record MenuSlot(int index) {

    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX_EXCLUSIVE = 54; // 6 * 9

    public MenuSlot {
        if (index < MIN_INDEX) {
            throw new IllegalArgumentException("Index must be zero or positive.");
        }

        if (index >= MAX_INDEX_EXCLUSIVE) {
            throw new IllegalArgumentException("Index must be less than " + MAX_INDEX_EXCLUSIVE + ".");
        }
    }

    /**
     * Creates a MenuSlot with the specified index.
     *
     * @param index the slot index (0-53)
     * @return a new MenuSlot instance
     * @throws IllegalArgumentException if index is invalid
     */
    public static MenuSlot ofIndex(int index) {
        return new MenuSlot(index);
    }
}

