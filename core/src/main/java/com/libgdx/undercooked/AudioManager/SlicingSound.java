package com.libgdx.undercooked.AudioManager;

public class SlicingSound extends BaseSound {

    public SlicingSound() {
        super("audio/choppingSound.mp3");
    }

    @Override
    public void run() {
        playSound();
    }
}
