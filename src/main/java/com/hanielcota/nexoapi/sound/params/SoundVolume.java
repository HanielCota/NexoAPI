package com.hanielcota.nexoapi.sound.params;

/**
 * Represents the volume of a sound.
 * Volume controls how far away the sound can be heard.
 * Valid range is from 0.0 (silent) to any positive value.
 *
 * @param volume the volume value (must be >= 0.0)
 * @since 1.0.0
 */
public record SoundVolume(float volume) {

    /**
     * Silent volume (0.0).
     */
    public static final SoundVolume SILENT = SoundVolume.of(0.0f);
    /**
     * Normal volume (1.0).
     */
    public static final SoundVolume NORMAL = SoundVolume.of(1.0f);
    /**
     * Loud volume (2.0). Heard from further away.
     */
    public static final SoundVolume LOUD = SoundVolume.of(2.0f);
    private static final float MIN_VOLUME = 0.0f;

    /**
     * Compact constructor that validates the volume.
     *
     * @param volume the volume value
     * @throws IllegalArgumentException if volume is negative
     */
    public SoundVolume {
        if (volume < MIN_VOLUME) {
            throw new IllegalArgumentException("Volume cannot be negative");
        }
    }

    /**
     * Creates a new SoundVolume with the specified value.
     *
     * @param volume the volume value (must be >= 0.0)
     * @return a new SoundVolume instance
     * @throws IllegalArgumentException if volume is negative
     */
    public static SoundVolume of(float volume) {
        return new SoundVolume(volume);
    }
}
