package com.libgdx.undercooked.AudioManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/** Main menu theme. Loaded only when it is actually played, without a background thread. */
public class MainMenuSound {
    private static Music music;
    public static boolean running = false;
    private float volume = .5f;

    public void run() {
        if (running) {
            if (music == null) {
                music = Gdx.audio.newMusic(Gdx.files.internal("audio/mainTrack.mp3"));
                music.setLooping(true);
            }
            music.setVolume(volume);
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
        running = false;
        dispose();
    }

    public static void dispose() {
        if (music != null) {
            music.stop();
            music.dispose();
            music = null;
        }
    }
}
