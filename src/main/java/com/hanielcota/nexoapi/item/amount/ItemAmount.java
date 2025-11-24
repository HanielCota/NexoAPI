package com.hanielcota.nexoapi.item.property;

/**
 * Represents a validated item stack amount.
 * Ensures the amount is within valid Minecraft stack limits (1-64).
 *
 * @param value the item amount (1-64)
 * @since 1.0.0
 */
public record ItemAmount(int value) {

    private static final int MIN_AMOUNT = 1;
    private static final int MAX_STACK = 64;

    /**
     * Creates a new ItemAmount with the specified value.
     *
     * @param amount the item amount (must be between 1 and 64)
     * @return a new ItemAmount instance
     * @throws IllegalArgumentException if amount is less than 1 or greater than 64
     */
    public static ItemAmount of(int amount) {
        if (amount < MIN_AMOUNT) {
            throw new IllegalArgumentException("Item amount cannot be less than 1.");
        }
        if (amount > MAX_STACK) {
            throw new IllegalArgumentException("Item amount cannot exceed 64.");
        }
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
}