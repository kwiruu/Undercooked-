package com.libgdx.undercooked.entities.Npc;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.libgdx.undercooked.entities.Npc.components.NpcB2D;
import com.libgdx.undercooked.entities.Npc.components.NpcComponent;

import static com.libgdx.undercooked.utils.Constants.PPM;

public class Npc extends PooledEngine {

    private World world;
    private NpcB2D npcB2D;
    private Sprite sprite;

    public Npc(World world) {
        super();
        this.world = world;
    }

    public void createNpc(Vector2 npcSpawnLocation, final Sprite sprite) {
        this.sprite = sprite; // Set the sprite

        final Entity entity = this.createEntity();

        NpcComponent npcComponent = new NpcComponent(sprite);
        entity.add(npcComponent);

        npcB2D = this.createComponent(NpcB2D.class);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // Use DynamicBody instead of StaticBody
        bodyDef.position.set(npcSpawnLocation.x, npcSpawnLocation.y);
        Body npcBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / PPM, 16 / PPM); // Assuming NPC size is 32x32
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        npcBody.createFixture(fixtureDef);
        shape.dispose();

        npcB2D.setNpcBody(npcBody);
        npcB2D.setWidth(32);
        npcB2D.setHeight(32);

        entity.add(npcB2D);

        this.addEntity(entity);
    }

    public void render(SpriteBatch batch) {
        // Render the sprite at the NPC's position
        if (sprite != null && npcB2D != null) {
            batch.draw(sprite, npcB2D.getNpcBody().getPosition().x + 32,
                npcB2D.getNpcBody().getPosition().y + 32);
            System.out.println("X: " + npcB2D.getNpcBody().getPosition().x + "\nY : " + npcB2D.getNpcBody().getPosition().y);
        }
    }

    public void update(float deltaTime) {
        // You can add any necessary update logic here
        // For example, you could move the NPC horizontally
        // You should adjust this based on your game's logic
        float speed = 4.0f; // Adjust speed as needed
        Vector2 velocity = npcB2D.getNpcBody().getLinearVelocity();
        velocity.x = speed; // Move horizontally at constant speed
        npcB2D.getNpcBody().setLinearVelocity(speed * 4,speed * 4);
    }

}
