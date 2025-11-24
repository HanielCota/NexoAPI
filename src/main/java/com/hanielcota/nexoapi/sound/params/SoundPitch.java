package com.hanielcota.nexoapi.sound.params;

/**
 * Represents the pitch of a sound.
 * Pitch controls the frequency of the sound.
 * Valid range is from 0.5 (low) to 2.0 (high).
 *
 * @param pitch the pitch value (must be between 0.5 and 2.0)
 * @since 1.0.0
 */
public record SoundPitch(float pitch) {

    /**
     * Low pitch (0.5).
     */
    public static final SoundPitch LOW = SoundPitch.of(0.5f);
    /**
     * Normal pitch (1.0).
     */
    public static final SoundPitch NORMAL = SoundPitch.of(1.0f);
    /**
     * High pitch (2.0).
     */
    public static final SoundPitch HIGH = SoundPitch.of(2.0f);
    private static final float MIN_PITCH = 0.5f;
    private static final float MAX_PITCH = 2.0f;

    /**
     * Compact constructor that validates the pitch.
     *
     * @param pitch the pitch value
     * @throws IllegalArgumentException if pitch is outside the valid range
     */
    public SoundPitch {
        if (pitch < MIN_PITCH || pitch > MAX_PITCH) {
            throw new IllegalArgumentException("Pitch must be between 0.5 and 2.0");
        }
    }

    /**
     * Creates a new SoundPitch with the specified value.
     *
     * @param pitch the pitch value (must be between 0.5 and 2.0)
     * @return a new SoundPitch instance
     * @throws IllegalArgumentException if pitch is outside the valid range
     */
    public static SoundPitch of(float pitch) {
        return new SoundPitch(pitch);
    }
}
