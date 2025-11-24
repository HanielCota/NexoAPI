package com.hanielcota.nexoapi.item.amount;

/**
 * Represents a validated amount for an item stack.
 * Ensures the value is within valid Minecraft limits (1–64).
 *
 * @param amount the item amount (1-64)
 * @since 1.0.0
 */
public record ItemAmount(int amount) {

    private static final int MIN = 1;
    private static final int MAX = 64;

    public ItemAmount {
        if (amount < MIN) {
            throw new IllegalArgumentException("Item amount cannot be less than 1.");
        }
        if (amount > MAX) {
            throw new IllegalArgumentException("Item amount cannot exceed 64.");
        }
    }

    /**
     * Creates a new ItemAmount with the specified value.
     *
     * @param amount the item amount (must be between 1 and 64)
     * @return a new ItemAmount instance
     * @throws IllegalArgumentException if amount is less than 1 or greater than 64
     */
    public static ItemAmount of(int amount) {
        return new ItemAmount(amount);
    }

    /**
     * Creates an ItemAmount with value 1.
     *
     * @return an ItemAmount instance with value 1
     */
    public static ItemAmount one() {
        return new ItemAmount(1);
    }

    /**
     * Creates an ItemAmount with the maximum value (64).
     *
     * @return an ItemAmount instance with value 64
     */
    public static ItemAmount max() {
        return new ItemAmount(MAX);
    }
}
