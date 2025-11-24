package com.hanielcota.nexoapi.menu.property;

/**
 * Represents the size of a menu in rows.
 * Minecraft inventories have 9 columns, so the total slots = rows * 9.
 *
 * @param rows the number of rows (1-6)
 * @since 1.0.0
 */
public record MenuSize(int rows) {

    private static final int MAX_ROWS = 6;
    private static final int COLUMNS = 9;

    public MenuSize {
        if (rows <= 0) {
            throw new IllegalArgumentException("Rows must be greater than zero.");
        }

        if (rows > MAX_ROWS) {
            throw new IllegalArgumentException("Rows must not exceed " + MAX_ROWS + ".");
        }
    }

    /**
     * Creates a MenuSize with the specified number of rows.
     *
     * @param rows the number of rows (1-6)
     * @return a new MenuSize instance
     * @throws IllegalArgumentException if rows is invalid
     */
    public static MenuSize ofRows(int rows) {
        return new MenuSize(rows);
    }

    /**
     * Calculates the total number of slots in this menu.
     *
     * @return the total number of slots (rows * 9)
     */
    public int slots() {
        return rows * COLUMNS;
    }
}

