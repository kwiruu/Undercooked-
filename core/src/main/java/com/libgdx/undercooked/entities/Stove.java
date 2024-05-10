package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.awt.Rectangle;

public class Stove extends Station {
    private TextureAtlas floating_iconAtlas;
    private Rectangle floating_icon_rect;
    private TextureRegion floatingIconRegion;
    private float frameDuration; // Duration of each frame in seconds
    private float stateTime; // Time elapsed since the start of the animation
    private Array<TextureAtlas.AtlasRegion> floatingIconFrames;
    private SpriteBatch batch;

    public Stove(float x, float y, int width, int height, SpriteBatch batch) {
        super(x, y, width, height);
        this.batch = batch;
        System.out.println("stove created!");
        System.out.println("X: " + x + " Y: " + y + " WIDTH: " + width + " HEIGHT: " + height);
        floating_iconAtlas = new TextureAtlas("assets/floating_icons/float_icons.atlas");
        floatingIconRegion = floating_iconAtlas.findRegion("clock_icon");
        floating_icon_rect = new Rectangle((int) x, (int) y, 32, 32);

        floatingIconFrames = floating_iconAtlas.findRegions("clock_icon"); // Assuming "clock_icon" is the name of the animation
        frameDuration = 1f; // Set the duration of each frame (adjust as needed)
        stateTime = 0f; // Initialize stateTime
    }

    public void render() {
        // Update stateTime
        stateTime += Gdx.graphics.getDeltaTime();

        // Get the current frame based on stateTime and frame duration
        TextureRegion currentFrame = floatingIconFrames.get((int) (stateTime / frameDuration) % floatingIconFrames.size);

        // Draw the current frame
        batch.draw(currentFrame, getX(), getY());
    }
    // TODO station to continue
}
