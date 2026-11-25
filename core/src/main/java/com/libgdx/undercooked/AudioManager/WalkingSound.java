package com.libgdx.undercooked.AudioManager;

public class WalkingSound extends BaseSound{

    public WalkingSound(){
        super("audio/footstepsSound.mp3");
    }
    @Override
    public void run() {
        playSound();
    }
}
