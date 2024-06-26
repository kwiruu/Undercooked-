package com.libgdx.undercooked.entities.Stations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.PlayerManager.Player;

public class SpeedBooster extends Station {
    public SpeedBooster(World world, float x, float y, int width, int height, SpriteBatch batch) {
        super(world, x, y, width, height, batch);
        floatingIconFrames[0] = floating_iconAtlas.findRegions("question_icon");
    }
    public void render() {
        stateTime += Gdx.graphics.getDeltaTime();
        stateTime += .2f;
        TextureRegion currentFrame = floatingIconFrames[0].get((int) (stateTime / frameDuration) % floatingIconFrames[0].size);
        batch.draw(currentFrame, getX(), getY());
    }
    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with a " + this);
        p.speedUp();
        return false;
    }

    @Override
    public String toString() {
        return "SpeedBooster";
    }
}
