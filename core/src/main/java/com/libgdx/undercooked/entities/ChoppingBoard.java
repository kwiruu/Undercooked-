package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ChoppingBoard extends Station {
    private SpriteBatch batch;

    public ChoppingBoard(float x, float y, int width, int height, SpriteBatch batch) {
        super(x, y, width, height);
        this.batch = batch;
        // different classes different icons!
        floatingIconFrames = floating_iconAtlas.findRegions("chop_icon"); // Assuming "clock_icon" is the name of the animation
    }
    public void render() {
        // Update stateTime
        stateTime += (float) (Gdx.graphics.getDeltaTime() + .5);
        TextureRegion currentFrame = floatingIconFrames.get((int) (stateTime / frameDuration) % floatingIconFrames.size);
        batch.draw(currentFrame, getX(), getY());
    }

    // TODO station to continue
}
