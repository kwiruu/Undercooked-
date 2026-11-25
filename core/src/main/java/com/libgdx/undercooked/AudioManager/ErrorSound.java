package com.libgdx.undercooked.AudioManager;

public class ErrorSound extends BaseSound {

    public ErrorSound() {
        super("audio/errorSound.mp3");
    }

    @Override
    public void run() {
        playSound();
    }
}
