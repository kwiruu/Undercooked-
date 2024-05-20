package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.Main;
import com.libgdx.undercooked.AudioManager.MainMenuSound;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Sine;

import static database.SQLOperations.userSignIn;

public class LandingPageScreen implements Screen {
    private final Main context;
    private TweenManager tweenManager;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private static String username = null;
    private Texture imgTexture; // Declare imgTexture here
    private MainMenuSound mainMenuSound;
    private Sprite blockClouds1, blockClouds2;

    public LandingPageScreen(final Main context) {
        this.context = context;
        this.mainMenuSound = new MainMenuSound();
    }

    @Override
    public void show() {
        tweenManager = new TweenManager(); // Ensure tweenManager is initialized here
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);

        imgTexture = new Texture(Gdx.files.internal("assets/sprites_raw/login_box2.png"));

        Texture blockCloud1Texture = new Texture(Gdx.files.internal("assets/screens/title_screen/block_clouds1.png"));
        Texture blockCloud2Texture = new Texture(Gdx.files.internal("assets/screens/title_screen/block_clouds2.png"));


        blockClouds1 = new Sprite(blockCloud1Texture);
        blockClouds2 = new Sprite(blockCloud2Texture);

        float targetX = Gdx.graphics.getWidth() / 2f - blockClouds1.getWidth() / 2;

        blockClouds1.setSize(blockCloud1Texture.getWidth() * 4, blockCloud1Texture.getHeight() * 4);
        blockClouds2.setSize(blockCloud2Texture.getWidth() * 4, blockCloud2Texture.getHeight() * 4);

        // Set initial positions to the middle of the screen
        blockClouds1.setPosition(Gdx.graphics.getWidth() / 2f - blockClouds1.getWidth() / 2, Gdx.graphics.getHeight() / 2f - blockClouds1.getHeight() / 2);
        blockClouds2.setPosition(Gdx.graphics.getWidth() / 2f - blockClouds2.getWidth() / 2, Gdx.graphics.getHeight() / 2f - blockClouds2.getHeight() / 2);

        // Tween to move to the outside
        Tween.to(blockClouds1, SpriteAccessor.POS_X, 3f)
            .target(-blockClouds1.getWidth()) // Move to the left outside
            .start(tweenManager);
        Tween.to(blockClouds2, SpriteAccessor.POS_X, 3f)
            .target(Gdx.graphics.getWidth()) // Move to the right outside
            .start(tweenManager);


        Table root = new Table();
        root.setBackground(new TextureRegionDrawable(new TextureRegion(imgTexture)));
        stage.addActor(root);

        // Calculate table size based on the stage's viewport dimensions
        float tableWidth = stage.getWidth() * 0.2f;
        float tableHeight = stage.getHeight() * 0.15f;
        root.setSize(tableWidth, tableHeight);
        // Center the table on the stage
        root.setPosition((stage.getWidth() - tableWidth) / 2f, (stage.getHeight() - tableHeight) / 2f);

        // Set the number of columns for the table
        root.defaults().colspan(2);
        root.top().left(); // Align content to the top-left corner

        // Adjust padding
        root.pad(20);
        root.defaults().space(10);
        // Load the skin
        skin = new Skin(Gdx.files.internal("assets/metal-ui.json"));

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        BitmapFont yourFont = skin.getFont("font");
        yourFont.getData().setScale(16/ yourFont.getCapHeight());
        textFieldStyle.font = yourFont;
        textFieldStyle.fontColor = Color.BROWN; // Change the font color if needed
        textFieldStyle.background = null; // Set background drawable to null

        final TextField usernameField = new TextField("", textFieldStyle);


        MainMenuSound.running = true;
        new Thread(mainMenuSound).start();


        // Create a TextButtonStyle without specifying a skin
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = yourFont; // Assign your font directly here
        buttonStyle.fontColor = Color.WHITE; // Change the font color if needed

        TextButton okButton = new TextButton("", buttonStyle); // Create the button without specifying a skin

        root.add(okButton).width(100).height(45).pad(3).padTop(0);
        root.add(usernameField).height(100).fillX().uniformX().padBottom(20).pad(3);
        root.row();

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                username = usernameField.getText();

                if (username.isEmpty()) {
                    Dialog warningDialog = new Dialog("Warning", skin);
                    warningDialog.text("Please enter a username.");
                    warningDialog.button("OK", true);
                    warningDialog.show(stage);
                }
                else if(!username.isEmpty()){
                    if(userSignIn(username)){
                        context.setScreen(ScreenType.SELECTMAP);
                        mainMenuSound.stop();
                    }
                }
            }
        });

        // Start the sound in a new thread

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tweenManager.update(delta);

        batch.begin();
        blockClouds1.draw(batch);
        blockClouds2.draw(batch);

        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        //mainMenuSound.stop(); // Stop the sound when disposing
    }

    public static String getUsername(){
        return username;
    }
}
