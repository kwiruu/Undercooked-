package com.libgdx.undercooked.entities.Npc2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Ghost extends Npc2 {
    float shotFrequency = 2f;
    int shotCount = 1;
    float rotationSpeed = 1f;
    float dist;
    public Ghost(float x, float y, SpriteBatch batch) {
        super(x, y, 32, 32, batch);
        dist = 144f;
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
    public void update(Vector2 v2) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        float angle = (float) (rotationSpeed * deltaTime);
//        setX((v2.x*32)+z);
//        setY((v2.y*32)+z);
        setX((float) ((v2.x * 32) + dist * Math.cos(angle)));
        setY((float) ((v2.y * 32) + dist * Math.sin(angle)));
    }

    public void upgrade() {
        shotCount++;
    }
}
