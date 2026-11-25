package com.libgdx.undercooked.AudioManager;

public class BellSound extends BaseSound {

    public BellSound() {
        super("audio/bellSound.mp3");
    }

    @Override
    public void run() {
            playSound();
    }
}
