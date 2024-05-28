package com.libgdx.undercooked.AudioManager;

public class BellSound extends BaseSound {

    public BellSound() {
        super("assets/audio/bellSound.wav");
    }

    @Override
    public void run() {
            playSound();
    }
}
