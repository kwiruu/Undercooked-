package PlayerManager;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public class Animations {
    protected Map<String, Animation<TextureRegion>> animations = new HashMap<>();
    protected float stateTime;

    protected boolean isAnimationPlaying = false;
    public Animations() {
        stateTime = 0;
    }

    protected void initializeAnimations(TextureAtlas textureAtlas, String[] animationKeys, String[] animationRegions, float frameDuration) {
        for (int i = 0; i < animationKeys.length; i++) {
            Array<TextureAtlas.AtlasRegion> regions = textureAtlas.findRegions(animationRegions[i]);
            animations.put(animationKeys[i], new Animation<>(frameDuration, regions));
        }
    }

    public Animation<TextureRegion> getAnimation(String key) {
        return animations.get(key);
    }

    public void updateStateTime(float deltaTime) {
        stateTime += deltaTime;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void resetStateTime() {
        stateTime = 0;
    }

    public boolean isAnimationPlaying() {
        return isAnimationPlaying;
    }

    public void setAnimationPlaying(boolean isAnimationPlaying) {
        this.isAnimationPlaying = isAnimationPlaying;
    }

    protected void addAnimation(String key, Animation<TextureRegion> animation) {
        animations.put(key, animation);
    }
}
