package com.libgdx.undercooked.AudioManager;

public class SlicingSound extends BaseSound {

    public SlicingSound() {
        super("assets/audio/choppingSound.wav");
    }

    @Override
    public void run() {
        playSound();
    }
}
