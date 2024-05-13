package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.Main;

public class LandingPageScreen implements Screen {
    private final Main context;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private static String username = null;
    private Texture imgTexture; // Declare imgTexture here
    public LandingPageScreen(final Main context) {
        this.context = context;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);

        imgTexture = new Texture(Gdx.files.internal("assets/sprites_raw/login_box2.png"));

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

                }
                else if(!username.isEmpty()){
                    context.setScreen(ScreenType.GAME);
                }
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

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
    }

    public static String getUsername(){
        return username;
    }
}
