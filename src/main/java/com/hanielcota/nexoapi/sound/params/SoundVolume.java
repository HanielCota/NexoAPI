package com.hanielcota.nexoapi.sound.params;

public record SoundVolume(float volume) {

    private static final float MIN_VOLUME = 0.0f;

    public static final SoundVolume SILENT = SoundVolume.of(0.0f);
    public static final SoundVolume NORMAL = SoundVolume.of(1.0f);
    public static final SoundVolume LOUD = SoundVolume.of(2.0f); // Heard from further away

    public SoundVolume {
        if (volume < MIN_VOLUME) {
            throw new IllegalArgumentException("Volume cannot be negative");
        }
    }

    public static SoundVolume of(float volume) {
        return new SoundVolume(volume);
    }
}
