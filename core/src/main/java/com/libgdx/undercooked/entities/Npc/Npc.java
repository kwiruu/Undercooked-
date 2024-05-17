package com.libgdx.undercooked.entities.Npc;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.Npc.components.NpcB2D;
import com.libgdx.undercooked.entities.Npc.components.NpcComponent;

public class Npc extends PooledEngine {

    private World world;
    private Sprite sprite;
    private NpcB2D npcB2D;

    public Npc(World world) {
        super();
        this.world = world;
        npcB2D = new NpcB2D(world); // Initialize NpcB2D with the world
    }

    public void createNpc(Vector2 npcSpawnLocation, final Sprite sprite, Boolean isStatic) {
        this.sprite = sprite; // Set the sprite
        System.out.println("Sprite Created");

        final Entity entity = this.createEntity();

        NpcComponent npcComponent = new NpcComponent(sprite);
        entity.add(npcComponent);

        npcB2D.createNpc(npcSpawnLocation, sprite, isStatic);
        entity.add(npcB2D);

        this.addEntity(entity);
    }

    public void render(SpriteBatch batch) {
        // Render the sprite at the NPC's position
        if (sprite != null && npcB2D != null) {
            batch.draw(sprite, npcB2D.getNpcBody().getPosition().x + 32,
                npcB2D.getNpcBody().getPosition().y + 32);
            System.out.println("X: " + npcB2D.getNpcBody().getPosition().x + "\nY: " + npcB2D.getNpcBody().getPosition().y);
        }
    }

    public void update(float deltaTime) {
        // Add any necessary update logic here
        // For example, you could move the NPC horizontally
        float speed = 4.0f; // Adjust speed as needed
        Vector2 velocity = npcB2D.getNpcBody().getLinearVelocity();
        velocity.x = speed; // Move horizontally at constant speed
        npcB2D.getNpcBody().setLinearVelocity(speed * 4, speed * 4);
    }
}
