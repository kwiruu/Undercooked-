package com.libgdx.undercooked.AudioManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class MapSound implements Runnable{
    private static Sound startSound;
    public static boolean mapRunning = false;
    private float volume = .5f;

    public MapSound(String songpath) {
        startSound = Gdx.audio.newSound(Gdx.files.internal(songpath));
    }

    @Override
    public void run() {
        if(mapRunning){
            startSound.play(volume);
            long loop = startSound.loop();
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
        if(!mapRunning){
            dispose();
        }
    }

    public static void dispose(){
        startSound.dispose();
    }
}
