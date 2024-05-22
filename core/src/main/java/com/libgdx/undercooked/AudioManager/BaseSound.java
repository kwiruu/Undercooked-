package com.libgdx.undercooked.AudioManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public abstract class BaseSound implements Runnable {
    protected boolean running = true;
    protected float volume = 0.3f;
    protected Thread thread;
    protected Sound sound;

    public BaseSound(String soundFilePath) {
        this.sound = Gdx.audio.newSound(Gdx.files.internal(soundFilePath));
        createThread();
    }

    private void createThread() {
        thread = new Thread(this);
    }

    @Override
    public void run() {
            playSound();
    }

    protected void playSound() {
        sound.play(volume);
    }

    public synchronized void start() {
        if (thread.getState() == Thread.State.NEW) {
            running = true;
            thread.start();
        } else if (!thread.isAlive()) {
            createThread();
            running = true;
            thread.start();
        }
    }

    public synchronized void stop() {
        running = false;
        if (thread.isAlive()) {
            thread.interrupt();
        }
        sound.stop();
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void dispose() {
        stop();
        sound.dispose();
    }
}
