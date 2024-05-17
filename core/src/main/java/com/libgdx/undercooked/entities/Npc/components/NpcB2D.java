package com.libgdx.undercooked.entities.Npc.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Sprite;

import static com.libgdx.undercooked.utils.Constants.PPM;

public class NpcB2D implements Component, Pool.Poolable {

    private World world;
    private Body npcBody;
    private float width;
    private float height;

    public NpcB2D(World world) {
        this.world = world;
    }

    @Override
    public void reset() {
        if (npcBody != null) {
            npcBody.getWorld().destroyBody(npcBody);
            npcBody = null;
        }
        height = width = 0;
    }

    public Body getNpcBody() {
        return this.npcBody;
    }

    public void setNpcBody(Body npcBody) {
        this.npcBody = npcBody;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void createNpc(Vector2 npcSpawnLocation, final Sprite sprite, boolean isStatic) {
        BodyDef bodyDef = new BodyDef();
        if (isStatic) {
            bodyDef.type = BodyDef.BodyType.StaticBody;
        } else {
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        }

        bodyDef.position.set(npcSpawnLocation.x, npcSpawnLocation.y);
        bodyDef.fixedRotation = true;
        npcBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / PPM / 2, 16 / PPM / 2); // Assuming NPC size is 32x32
        npcBody.createFixture(shape, 1.0F);
        shape.dispose();
    }
}
