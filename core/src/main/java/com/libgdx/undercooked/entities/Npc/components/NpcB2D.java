package com.libgdx.undercooked.entities.Npc.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

public class NpcB2D implements Component, Pool.Poolable {

    private Body npcBody;
    private float width;
    private float height;


    @Override
    public void reset() {
        if(npcBody != null){
            npcBody.getWorld().destroyBody(npcBody);
            npcBody = null;
        }
        height = width = 0;
    }

    public Body getNpcBody(){
        return this.npcBody;
    }

    public void setNpcBody(Body npcBody){
        this.npcBody = npcBody;
    }

    public float getWidth(){
        return this.width;
    }

    public float getHeight(){
        return this.height;
    }

    public void setHeight(float height){
        this.height = height;
    }

    public void setWidth(float width){
        this.width = width;
    }


}
