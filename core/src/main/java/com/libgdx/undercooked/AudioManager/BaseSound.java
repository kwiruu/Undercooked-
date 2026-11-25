package com.libgdx.undercooked.AudioManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * A short sound effect. Plays directly on the calling thread (no background threads), which works on
 * desktop and in the browser. Sounds are cached by path, so the many stations that each create a
 * {@link GameSound} share one loaded copy of every effect instead of loading their own.
 */
public abstract class BaseSound {
    private static final ObjectMap<String, Sound> CACHE = new ObjectMap<>();

    protected float volume = 1f;
    protected final Sound sound;
    private long soundId = -1;

    public BaseSound(String soundFilePath) {
        Sound cached = CACHE.get(soundFilePath);
        if (cached == null) {
            cached = Gdx.audio.newSound(Gdx.files.internal(soundFilePath));
            CACHE.put(soundFilePath, cached);
        }
        this.sound = cached;
    }

    protected void playSound() {
        soundId = sound.play(volume);
    }

    /** What this sound does when started. Subclasses can override this to customize it. */
    public void run() {
        playSound();
    }

    public void start() {
        run();
    }

    public void stop() {
        if (soundId != -1) {
            sound.stop(soundId);
            soundId = -1;
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (soundId != -1) {
            sound.setVolume(soundId, volume);
        }
    }

    /** Stops this instance's sound. The shared sound data stays loaded for the other users of it. */
    public void dispose() {
        stop();
    }

    /** Frees every cached sound effect. Only call this when the whole game shuts down. */
    public static void disposeAll() {
        for (Sound s : CACHE.values()) {
            s.dispose();
        }
        CACHE.clear();
    }
}
