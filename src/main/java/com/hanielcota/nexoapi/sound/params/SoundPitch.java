package com.hanielcota.nexoapi.sound.params;

public record SoundPitch(float pitch) {

    private static final float MIN_PITCH = 0.5f;
    private static final float MAX_PITCH = 2.0f;

    public static final SoundPitch LOW = SoundPitch.of(0.5f);
    public static final SoundPitch NORMAL = SoundPitch.of(1.0f);
    public static final SoundPitch HIGH = SoundPitch.of(2.0f);

    public SoundPitch {
        if (pitch < MIN_PITCH || pitch > MAX_PITCH) {
            throw new IllegalArgumentException("Pitch must be between 0.5 and 2.0");
        }
    }

    public static SoundPitch of(float pitch) {
        return new SoundPitch(pitch);
    }
}
