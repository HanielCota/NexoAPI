package com.hanielcota.nexoapi.radar.range;

public record DetectionRadius(double blocks) {

    public static DetectionRadius ofBlocks(double blocks) {
        if (blocks <= 0) {
            throw new IllegalArgumentException("Radius must be greater than zero");
        }

        return new DetectionRadius(blocks);
    }

    public static DetectionRadius shortRange() {
        return new DetectionRadius(5.0);
    }

    public static DetectionRadius longRange() {
        return new DetectionRadius(50.0);
    }
}
