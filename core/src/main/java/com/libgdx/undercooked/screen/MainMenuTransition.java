package com.libgdx.undercooked.screen;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Sine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.libgdx.undercooked.Main;

public class MainMenuTransition implements Screen {
    private SpriteBatch batch;
    private Sprite bgSprite, cloudSprite, textSprite, cloud1Sprite, cloud2Sprite;
    private Sprite button1, button2, button3;
    private TweenManager tweenManager;
    private final Main context;
    private Sprite pressSprite;
    private boolean keyPressedSinceLastFrame;
    private boolean transitionStarted = false;
    private boolean initialAnimationComplete = false;
    private boolean buttonsVisible = false;

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

        // Load button textures
        Texture playTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/play.png"));
        Texture settingsTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/settings.png"));
        Texture exitTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/exit.png"));

        // Create sprites
        bgSprite = new Sprite(bgTexture);
        cloudSprite = new Sprite(cloudTexture);
        textSprite = new Sprite(textTexture);
        cloud1Sprite = new Sprite(cloud1Texture);
        cloud2Sprite = new Sprite(cloud2Texture);
        pressSprite = new Sprite(pressTexture);
        button1 = new Sprite(playTexture);
        button2 = new Sprite(settingsTexture);
        button3 = new Sprite(exitTexture);

        // Multiply sizes by 4
        bgSprite.setSize(bgTexture.getWidth() * 4, bgTexture.getHeight() * 4);
        cloudSprite.setSize(cloudTexture.getWidth() * 4, cloudTexture.getHeight() * 4);
        textSprite.setSize(textTexture.getWidth() * 4, textTexture.getHeight() * 4);
        cloud1Sprite.setSize(cloud1Texture.getWidth() * 4, cloud1Texture.getHeight() * 4);
        cloud2Sprite.setSize(cloud2Texture.getWidth() * 4, cloud2Texture.getHeight() * 4);
        pressSprite.setSize(pressTexture.getWidth() * 1.5f, pressTexture.getHeight() * 1.5f);
        button1.setSize(playTexture.getWidth() * 2, playTexture.getHeight() * 2);
        button2.setSize(settingsTexture.getWidth() * 2, settingsTexture.getHeight() * 2);
        button3.setSize(exitTexture.getWidth() * 2, exitTexture.getHeight() * 2);

        // Set sprite positions
        bgSprite.setPosition(0, 0);
        cloudSprite.setPosition(Gdx.graphics.getWidth() / 2f - cloudSprite.getWidth() / 2, -cloudSprite.getHeight());
        textSprite.setPosition(Gdx.graphics.getWidth() / 2f - textSprite.getWidth() / 2, Gdx.graphics.getHeight());
        cloud1Sprite.setPosition(-cloud1Sprite.getWidth(), Gdx.graphics.getHeight() / 2f - cloud1Sprite.getHeight() / 2);
        cloud2Sprite.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2f - cloud2Sprite.getHeight() / 2);
        pressSprite.setPosition(Gdx.graphics.getWidth() / 2f - pressSprite.getWidth() / 2, Gdx.graphics.getHeight() / 5f);

        // Position buttons off-screen initially
        button1.setPosition(Gdx.graphics.getWidth() / 2f - button1.getWidth() / 2, -button1.getHeight());
        button2.setPosition(Gdx.graphics.getWidth() / 2f - button2.getWidth() / 2, -button2.getHeight());
        button3.setPosition(Gdx.graphics.getWidth() / 2f - button3.getWidth() / 2, -button3.getHeight());

        // Create animations
        Tween.set(cloudSprite, SpriteAccessor.POS_Y).target(-cloudSprite.getHeight() * 0.8f).start(tweenManager);
        Tween.to(cloudSprite, SpriteAccessor.POS_Y, 2).target(Gdx.graphics.getHeight()).start(tweenManager);

        Tween.set(textSprite, SpriteAccessor.POS_Y).target(Gdx.graphics.getHeight()).start(tweenManager);
        Tween.to(textSprite, SpriteAccessor.POS_Y, 2).target(Gdx.graphics.getHeight() * 0.05f).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                initialAnimationComplete = true;
            }
        }).start(tweenManager);

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

        Tween.set(cloud2Sprite, SpriteAccessor.POS_X)
            .target(cloud2Sprite.getWidth() * 0.1f)
            .start(tweenManager);

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
        Tween.to(pressSprite, SpriteAccessor.ALPHA, 1.5f).target(1).delay(1.5f).start(tweenManager);
        if (keyPressedSinceLastFrame) {
            Tween.to(pressSprite, SpriteAccessor.ALPHA, 1.f).target(0).delay(2.5f).repeatYoyo(Tween.INFINITY, 0).start(tweenManager);
        }

        // Input processor to handle clicks
        // Input processor to handle clicks
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (buttonsVisible) {
                    screenY = Gdx.graphics.getHeight() - screenY; // Invert Y coordinate
                    if (button1.getBoundingRectangle().contains(screenX, screenY)) {
                        // Set clicked texture for button1
                        button1.setTexture(new Texture(Gdx.files.internal("assets/screens/title_screen/play_clicked.png")));
                        context.setScreen(new LandingPageScreen(context));
                        return true;
                    }
                    if (button2.getBoundingRectangle().contains(screenX, screenY)) {
                        // Set clicked texture for button2
                        button2.setTexture(new Texture(Gdx.files.internal("assets/screens/title_screen/settings_clicked.png")));

                        return true;
                    }
                    if (button3.getBoundingRectangle().contains(screenX, screenY)) {
                        // Set clicked texture for button3
                        button3.setTexture(new Texture(Gdx.files.internal("assets/screens/title_screen/exit_clicked.png")));

                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void startTransitionAnimation() {
        float duration = 1.5f;

        // Move textSprite to new position
        Tween.to(textSprite, SpriteAccessor.POS_Y, duration)
            .target(Gdx.graphics.getHeight() * 0.35f)
            .start(tweenManager);
        Tween.to(textSprite, SpriteAccessor.POS_X, duration)
            .target(Gdx.graphics.getWidth() * 0.11f)
            .start(tweenManager);

        // Scale down textSprite
        Tween.to(textSprite, SpriteAccessor.SCALE_XY, duration)
            .target(0.7f, 0.7f)  // Scale to smaller size
            .start(tweenManager);

        // Move cloud1Sprite lower
        Tween.to(cloud1Sprite, SpriteAccessor.POS_Y, duration)
            .target(Gdx.graphics.getHeight() * -0.1f)
            .start(tweenManager);

        // Move cloud2Sprite lower
        Tween.to(cloud2Sprite, SpriteAccessor.POS_Y, duration)
            .target(Gdx.graphics.getHeight() * -0.1f)
            .start(tweenManager);

        // Fade out pressSprite
        Tween.to(pressSprite, SpriteAccessor.ALPHA, duration)
            .target(0)
            .start(tweenManager);

        // Show buttons after transition
        showButtons();
    }

    private void showButtons() {
        float duration = 1.5f;
        float buttonSpacing = 20f; // Space between buttons

        // Calculate positions for buttons
        float button1Y = Gdx.graphics.getHeight() / 3f + button1.getHeight() + buttonSpacing;
        float button2Y = Gdx.graphics.getHeight() / 3f;
        float button3Y = Gdx.graphics.getHeight() / 3f - button1.getHeight() - buttonSpacing;

        // Move buttons to their positions
        Tween.to(button1, SpriteAccessor.POS_Y, duration)
            .target(button1Y)
            .start(tweenManager);
        Tween.to(button2, SpriteAccessor.POS_Y, duration)
            .target(button2Y)
            .start(tweenManager);
        Tween.to(button3, SpriteAccessor.POS_Y, duration)
            .target(button3Y)
            .start(tweenManager);

        buttonsVisible = true;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (initialAnimationComplete && (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY))) {
            keyPressedSinceLastFrame = true;
        }

        if (keyPressedSinceLastFrame && !transitionStarted) {
            transitionStarted = true;
            startTransitionAnimation();
        }

        tweenManager.update(delta);

        batch.begin();
        bgSprite.draw(batch);
        textSprite.draw(batch);
        cloudSprite.draw(batch);
        cloud1Sprite.draw(batch);
        cloud2Sprite.draw(batch);
        pressSprite.draw(batch);

        if (buttonsVisible) {
            button1.draw(batch);
            button2.draw(batch);
            button3.draw(batch);
        }

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
