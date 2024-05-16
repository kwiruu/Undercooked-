package com.libgdx.undercooked.entities.Npc.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class NpcComponent implements Component, Pool.Poolable {

    private boolean isWaiting;
    private boolean isOrdering;
    private boolean isDone;

    // Getter and setter methods

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    public boolean isOrdering() {
        return isOrdering;
    }

    public void setOrdering(boolean ordering) {
        isOrdering = ordering;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public void reset() {
        isWaiting = false;
        isOrdering = false;
        isDone = false;
    }
}
