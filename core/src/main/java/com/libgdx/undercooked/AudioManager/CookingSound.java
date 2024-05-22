package com.libgdx.undercooked.AudioManager;

public class CookingSound extends BaseSound {

    public CookingSound() {
        super("assets/audio/cookingSound.wav");
    }
    @Override
    public void run() {
        playSound();
    }
}
