package PlayerManager;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class PlayerAnimations extends Animations {
    private Animation<TextureAtlas.AtlasRegion> smokeAnimation;

    public PlayerAnimations(TextureAtlas playerAtlas, TextureAtlas smokeAtlas) {
        initializePlayerAnimations(playerAtlas);
        initializeSmokeAnimation(smokeAtlas);
    }

    private void initializePlayerAnimations(TextureAtlas textureAtlas) {
        // running anim
        addAnimation("running_down", new Animation<>(0.09f, textureAtlas.findRegions("running_down")));
        addAnimation("running_top", new Animation<>(0.09f, textureAtlas.findRegions("running_top")));
        addAnimation("running_left", new Animation<>(0.09f, textureAtlas.findRegions("running_left")));
        addAnimation("running_right", new Animation<>(0.09f, textureAtlas.findRegions("running_right")));
        // idle anim
        addAnimation("idle_down", new Animation<>(0.09f, textureAtlas.findRegions("idle_down")));
        addAnimation("idle_top", new Animation<>(0.09f, textureAtlas.findRegions("idle_up")));
        addAnimation("idle_left", new Animation<>(0.09f, textureAtlas.findRegions("idle_left")));
        addAnimation("idle_right", new Animation<>(0.09f, textureAtlas.findRegions("idle_right")));
        // lifting anim
        addAnimation("lifting_down", new Animation<>(0.12f, textureAtlas.findRegions("lifting_down")));
        addAnimation("lifting_top", new Animation<>(0.12f, textureAtlas.findRegions("lifting_top")));
        addAnimation("lifting_left", new Animation<>(0.12f, textureAtlas.findRegions("lifting_left")));
        addAnimation("lifting_right", new Animation<>(0.12f, textureAtlas.findRegions("lifting_right")));
        // lifting idle anim
        addAnimation("idle_lifting_top", new Animation<>(0.09f, textureAtlas.findRegions("idle_lifting_top")));
        addAnimation("idle_lifting_down", new Animation<>(0.12f, textureAtlas.findRegions("idle_lifting_down")));
        addAnimation("idle_lifting_left", new Animation<>(0.12f, textureAtlas.findRegions("idle_lifting_left")));
        addAnimation("idle_lifting_right", new Animation<>(0.12f, textureAtlas.findRegions("idle_lifting_right")));
        // lifting running anim
        addAnimation("running_lifting_down", new Animation<>(0.12f, textureAtlas.findRegions("running_lifting_down")));
        addAnimation("running_lifting_top", new Animation<>(0.09f, textureAtlas.findRegions("running_lifting_top")));
        addAnimation("running_lifting_left", new Animation<>(0.12f, textureAtlas.findRegions("running_lifting_left")));
        addAnimation("running_lifting_right", new Animation<>(0.12f, textureAtlas.findRegions("running_lifting_right")));
    }

    private void initializeSmokeAnimation(TextureAtlas smokeAtlas) {
        smokeAnimation = new Animation<>(0.15f, smokeAtlas.findRegions("poof"));
    }

    public Animation<TextureAtlas.AtlasRegion> getSmokeAnimation() {
        return smokeAnimation;
    }
}