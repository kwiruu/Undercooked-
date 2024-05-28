package com.libgdx.undercooked.AudioManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class MainMenuSound implements Runnable {
    private static Sound startSound;
    public static boolean running = false;
    private float volume = .5f;

    public MainMenuSound() {
        startSound = Gdx.audio.newSound(Gdx.files.internal("assets/audio/mainTrack.mp3"));
    }

    @Override
    public void run() {
        if(running){
            startSound.play(volume);
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    private long startSoundDuration() {
        long startTime = System.currentTimeMillis();
        startSound.play(volume);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // startSound.stop();
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        return duration;
    }

    public static void stop() {
        running = false;
        dispose();
    }

    public static void dispose(){
        if (startSound != null) {
            startSound.dispose();
        }
    }
}
