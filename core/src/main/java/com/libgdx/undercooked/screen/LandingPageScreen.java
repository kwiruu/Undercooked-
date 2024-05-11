package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.Main;

import static database.SQLOperations.userSignIn;

public class LandingPageScreen implements Screen {
    private final Main context;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private Texture background;
    private static String username = null;
    public LandingPageScreen(final Main context) {
        this.context = context;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("metal-ui.json")); // Change this to your own skin file
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("libgdx.png")); // Change this to your background image

        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.pad(20);

        final Label enterUserLabel = new Label("Username:", skin);
        final TextField usernameField = new TextField("", skin);
        TextButton okButton = new TextButton("OK", skin);

        root.add(enterUserLabel).padBottom(20).colspan(2);
        root.row();
        root.add(usernameField).fillX().uniformX().padBottom(20).colspan(2);
        root.row();
        root.add(okButton).fillX().uniformX().colspan(2);

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                username = usernameField.getText();

                if (username.isEmpty()) {
                    enterUserLabel.setText("Username can't be null");
                }
                else if(userSignIn(username) && !username.isEmpty()){
                    context.setScreen(ScreenType.GAME);
                }
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
        background.dispose();
    }

    public static String getUsername(){
        return username;
    }
}
