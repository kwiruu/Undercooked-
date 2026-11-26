package com.libgdx.undercooked.AudioManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * Looping background music for the map selector and each map. Uses {@link Music}, which streams the
 * track instead of decoding all of it into memory, and runs without a background thread so it works
 * in the browser.
 */
public class MapSound {
    private static Music music;
    public static boolean mapRunning = false;
    private float volume = 0.3f;

    public MapSound(String songpath) {
        dispose();
        music = Gdx.audio.newMusic(Gdx.files.internal(songpath));
        music.setLooping(true);
        music.setVolume(volume);
    }

    /** Starts the track if music is currently enabled. */
    public void run() {
        if (mapRunning && music != null && !music.isPlaying()) {
            music.play();
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (music != null) {
            music.setVolume(volume);
        }
    }

    public static void stop() {
        if (!mapRunning) {
            dispose();
        }
    }

    public static void dispose() {
        if (music != null) {
            music.stop();
            music.dispose();
            music = null;
        }
    }
}
