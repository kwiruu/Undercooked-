package com.libgdx.undercooked.AudioManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class MapSound implements Runnable {
    private static Sound startSound;
    public static boolean mapRunning = false;
    private float volume = 0.3f;
    private long soundId;

    public MapSound(String songpath) {
        startSound = Gdx.audio.newSound(Gdx.files.internal(songpath));
    }

    @Override
    public void run() {
        if (mapRunning) {
            if (startSound != null) {
                soundId = startSound.loop(volume);
                setVolume(volume);
            }
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (startSound != null && mapRunning) {
            startSound.setVolume(soundId, volume);
        }
    }

    private long startSoundDuration() {
        if (startSound == null) return 0;
        long startTime = System.currentTimeMillis();
        soundId = startSound.play(volume);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        return duration;
    }

    public static void stop() {
        if (!mapRunning) {
            dispose();
        }
    }

    public static void dispose() {
        if (startSound != null) {
            startSound.dispose();
        }
    }
}
