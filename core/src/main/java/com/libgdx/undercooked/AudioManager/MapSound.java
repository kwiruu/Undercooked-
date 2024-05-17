package com.libgdx.undercooked.AudioManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class MapSound implements Runnable{
    private Sound startSound;
    private boolean running = true;
    private float volume = .5f;

    public MapSound(String songpath) {
        startSound = Gdx.audio.newSound(Gdx.files.internal(songpath));
    }

    @Override
    public void run() {
        startSound.play(volume);
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

    public void stop() {
        running = false;
        dispose();
    }

    public void dispose(){
        startSound.dispose();
        stop();
    }
}
