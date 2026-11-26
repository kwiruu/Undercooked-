package com.libgdx.undercooked.AudioManager;

public class CookingSound extends BaseSound {

    public CookingSound() {
        super("audio/cookingSound.mp3");
    }
    @Override
    public void run() {
        playSound();
    }
}
