package com.hanielcota.nexoapi.menu.property;

import com.hanielcota.nexoapi.validation.NumericValidation;

/**
 * Represents a slot index in a menu inventory.
 * Valid slot indices range from 0 to 53 (6 rows * 9 columns - 1).
 *
 * @param index the slot index (0-53)
 * @since 1.0.0
 */
public record MenuSlot(int index) {

    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 53;

    public MenuSlot {
        NumericValidation.validateRange(index, MIN_INDEX, MAX_INDEX, "Slot index");
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

