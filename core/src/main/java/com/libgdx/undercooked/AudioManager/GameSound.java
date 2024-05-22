package com.libgdx.undercooked.AudioManager;

public class GameSound {
    private CookingSound cookingSound;
    private ErrorSound errorSound;
    private SlicingSound slicingSound;
    private BellSound bellSound;

    private PoofSound poofSound;

    public GameSound() {
        cookingSound = new CookingSound();
        errorSound = new ErrorSound();
        slicingSound = new SlicingSound();
        bellSound = new BellSound();
        poofSound = new PoofSound();
    }

    public void startCookingSound() {
        cookingSound.start();
    }

    public void stopCookingSound() {
        cookingSound.stop();
    }

    public void startErrorSound() {
        errorSound.start();
    }

    public void stopErrorSound() {
        errorSound.stop();
    }

    public void startSlicingSound() {
        slicingSound.start();
    }

    public void stopSlicingSound() {
        slicingSound.stop();
    }

    public void startBellSound() {
        bellSound.start();
    }

    public void stopBellSound() {
        bellSound.stop();
    }

    public void startPoofSound() {
        poofSound.start();
    }

    public void stopPoofSound() {
        poofSound.stop();
    }

    public void disposeAll() {
        cookingSound.dispose();
        errorSound.dispose();
        slicingSound.dispose();
        bellSound.dispose();
    }
}
