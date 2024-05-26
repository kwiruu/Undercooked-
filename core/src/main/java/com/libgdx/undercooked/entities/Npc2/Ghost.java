package com.libgdx.undercooked.entities.Npc2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;

public class Ghost extends Npc2 {
    float shotFrequency = 2f;
    double rotationSpeed = 10f;
    float dist;
    public Ghost(float x, float y, int height, int width, SpriteBatch batch) {
        super(x, y, height, width, batch);
        dist = (float) (Math.random()*10f) + 5;
        iconFrames = iconAtlas.findRegions("question_icon");
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        batch.setColor(1, 1, 1, 0.8f);
        TextureRegion currentFrame;
        currentFrame = iconFrames.get((int) deltaTime % iconFrames.size);

        batch.draw(currentFrame, getX(), getY() + 25);
        batch.setColor(1, 1, 1, 1f);
    }

    @Override
    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        setX((float) (getX()+dist*Math.cos(deltaTime * rotationSpeed)));
        setY((float) (getY()+dist*Math.cos(deltaTime * rotationSpeed)));
    }
}
