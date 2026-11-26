package com.libgdx.undercooked.AudioManager;

public class PoofSound extends BaseSound {

    public PoofSound() {
        super("audio/poofSound.mp3");
    }

    @Override
    public void run() {
        // Polymorphism: Overrides run method to define specific behavior for ErrorSound
        playSound(); // Simply play the sound once
    }
}
