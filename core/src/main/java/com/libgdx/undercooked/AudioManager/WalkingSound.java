package com.libgdx.undercooked.AudioManager;

public class WalkingSound extends BaseSound{

    public WalkingSound(){
        super("assets/audio/footstepsSound.wav");
    }
    @Override
    public void run() {
        playSound();
    }
}
