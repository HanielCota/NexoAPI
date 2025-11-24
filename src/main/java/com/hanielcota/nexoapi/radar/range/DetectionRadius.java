package com.hanielcota.nexoapi.radar.range;

/**
 * Represents the detection radius of a radar in blocks.
 * Encapsulates the radius value and provides validation.
 *
 * @param blocks the radius in blocks (must be greater than zero)
 * @since 1.0.0
 */
public record DetectionRadius(double blocks) {

    /**
     * Creates a new DetectionRadius with the specified value in blocks.
     *
     * @param blocks the radius in blocks (must be greater than zero)
     * @return a new DetectionRadius instance
     * @throws IllegalArgumentException if blocks is less than or equal to zero
     */
    public static DetectionRadius ofBlocks(double blocks) {
        if (blocks <= 0) {
            throw new IllegalArgumentException("Radius must be greater than zero");
        }

        return new DetectionRadius(blocks);
    }

    /**
     * Creates a short-range detection radius (5 blocks).
     *
     * @return a DetectionRadius with 5 blocks
     */
    public static DetectionRadius shortRange() {
        return new DetectionRadius(5.0);
    }

    /**
     * Creates a long-range detection radius (50 blocks).
     *
     * @return a DetectionRadius with 50 blocks
     */
    public static DetectionRadius longRange() {
        return new DetectionRadius(50.0);
    }
}
