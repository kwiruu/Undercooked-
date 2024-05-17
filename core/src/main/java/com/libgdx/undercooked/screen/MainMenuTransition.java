package com.libgdx.undercooked.screen;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Sine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.libgdx.undercooked.Main;

public class MainMenuTransition implements Screen {
    private SpriteBatch batch;
    private Sprite bgSprite, cloudSprite, textSprite, cloud1Sprite, cloud2Sprite;
    private TweenManager tweenManager;
    private final Main context;
    private Sprite pressSprite;
    private boolean keyPressedSinceLastFrame;
    private boolean transitionStarted = false;

    public MainMenuTransition(final Main context) {
        this.context = context;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        tweenManager = new TweenManager();
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());

        // Load textures
        Texture bgTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/bg.png"));
        Texture cloudTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/clouds.png"));
        Texture textTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/undercooked_text.png"));
        Texture cloud1Texture = new Texture(Gdx.files.internal("assets/screens/title_screen/clouds1.png"));
        Texture cloud2Texture = new Texture(Gdx.files.internal("assets/screens/title_screen/clouds2.png"));
        Texture pressTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/press_text.png"));

        // Create sprites
        bgSprite = new Sprite(bgTexture);
        cloudSprite = new Sprite(cloudTexture);
        textSprite = new Sprite(textTexture);
        cloud1Sprite = new Sprite(cloud1Texture);
        cloud2Sprite = new Sprite(cloud2Texture);
        pressSprite = new Sprite(pressTexture);

        // Multiply sizes by 4
        bgSprite.setSize(bgTexture.getWidth() * 4, bgTexture.getHeight() * 4);
        cloudSprite.setSize(cloudTexture.getWidth() * 4, cloudTexture.getHeight() * 4);
        textSprite.setSize(textTexture.getWidth() * 4, textTexture.getHeight() * 4);
        cloud1Sprite.setSize(cloud1Texture.getWidth() * 4, cloud1Texture.getHeight() * 4);
        cloud2Sprite.setSize(cloud2Texture.getWidth() * 4, cloud2Texture.getHeight() * 4);
        pressSprite.setSize(pressTexture.getWidth()*1.5f, pressTexture.getHeight()*1.5f);

        // Set sprite positions
        bgSprite.setPosition(0, 0);
        cloudSprite.setPosition(Gdx.graphics.getWidth() / 2f - cloudSprite.getWidth() / 2, -cloudSprite.getHeight());
        textSprite.setPosition(Gdx.graphics.getWidth() / 2f - textSprite.getWidth() / 2, Gdx.graphics.getHeight());
        cloud1Sprite.setPosition(-cloud1Sprite.getWidth(), Gdx.graphics.getHeight() / 2f - cloud1Sprite.getHeight() / 2);
        cloud2Sprite.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2f - cloud2Sprite.getHeight() / 2);
        pressSprite.setPosition(Gdx.graphics.getWidth() / 2f - pressSprite.getWidth() / 2, Gdx.graphics.getHeight() / 6f);

        // Create animations
        Tween.set(cloudSprite, SpriteAccessor.POS_Y).target(-cloudSprite.getHeight() * 0.8f).start(tweenManager);
        Tween.to(cloudSprite, SpriteAccessor.POS_Y, 2).target(Gdx.graphics.getHeight()).start(tweenManager);

        Tween.set(textSprite, SpriteAccessor.POS_Y).target(Gdx.graphics.getHeight()).start(tweenManager);
        Tween.to(textSprite, SpriteAccessor.POS_Y, 2).target(Gdx.graphics.getHeight()*0.009f).start(tweenManager);

        // Tilt the textSprite left and right infinitely with a twisting effect
        Tween.to(textSprite, SpriteAccessor.ROTATION, 1)
            .target(1)
            .ease(Sine.INOUT)
            .repeatYoyo(Tween.INFINITY, 0)
            .start(tweenManager);
        Tween.to(textSprite, SpriteAccessor.ROTATION, 2)
            .target(-1)
            .ease(Sine.INOUT)
            .repeatYoyo(Tween.INFINITY, 0)
            .start(tweenManager);

        // Move cloud1 from right to left and stop on the left side of the screen
        float cloud1EndX = Gdx.graphics.getWidth() - cloud1Sprite.getWidth() * 8;

        Tween.set(cloud1Sprite, SpriteAccessor.POS_X)
            .target(-cloud1Sprite.getWidth() * 0.1f)
            .start(tweenManager);

        Tween.to(cloud1Sprite, SpriteAccessor.POS_X, 2)
            .target(cloud1EndX)
            .start(tweenManager);
        Tween.to(cloud1Sprite, SpriteAccessor.POS_X, 2)
            .target(-1f)
            .ease(Sine.INOUT)
            .repeatYoyo(Tween.INFINITY, 0)
            .start(tweenManager);

        // Move cloud2 from left to right and stop on the right side of the screen
        float cloud2EndX = Gdx.graphics.getWidth() - cloud2Sprite.getWidth() * 8;
        //start ma nuigga
        Tween.set(cloud2Sprite, SpriteAccessor.POS_X)
            .target(cloud2Sprite.getWidth() * 0.1f)
            .start(tweenManager);
        // end ma lmao
        Tween.to(cloud2Sprite, SpriteAccessor.POS_X, 2)
            .target(cloud2EndX)
            .start(tweenManager);
        Tween.to(cloud2Sprite, SpriteAccessor.POS_X, 2)
            .target(-1f)
            .ease(Sine.INOUT)
            .repeatYoyo(Tween.INFINITY, 0)
            .start(tweenManager);

        // text press any key
        Tween.set(pressSprite, SpriteAccessor.ALPHA).target(0).start(tweenManager);
        Tween.to(pressSprite, SpriteAccessor.ALPHA, 2).target(1).delay(2.5f).start(tweenManager);
        Tween.to(pressSprite, SpriteAccessor.ALPHA, 2).target(0).delay(5).repeatYoyo(Tween.INFINITY, 0).start(tweenManager);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            keyPressedSinceLastFrame = true;
        }

        if (keyPressedSinceLastFrame && !transitionStarted) {
            transitionStarted = true;
            context.setScreen(new LandingPageScreen(context));  // Replace with your actual screen transition
        }

        tweenManager.update(delta);

        batch.begin();
        bgSprite.draw(batch);
        textSprite.draw(batch);
        cloudSprite.draw(batch);
        cloud1Sprite.draw(batch);
        cloud2Sprite.draw(batch);
        pressSprite.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
    }
}
