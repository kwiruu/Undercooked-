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
    private static String username = null;
    public LandingPageScreen(final Main context) {
        this.context = context;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json")); // Change this to your own skin file
        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.pad(20);
        root.defaults().space(10);

        Texture imgTexture = new Texture(Gdx.files.internal("assets/sprites_raw/login_box2.png"));
        Image img = new Image(imgTexture);
        root.add(img).size(imgTexture.getWidth()*2, imgTexture.getHeight()*2).pad(3).colspan(2);

        final Label enterUserLabel = new Label("Username:", skin);
        final TextField usernameField = new TextField("", skin);
        TextButton okButton = new TextButton("OK", skin);

        // Position the enterUserLabel at the top-left corner
        root.add(enterUserLabel).top().left().padTop(20).padLeft(20).colspan(2);
        root.row();
        root.add(usernameField).height(40).fillX().uniformX().padBottom(20).pad(3).colspan(2);
        root.row();
        root.add(okButton).width(100).height(45).pad(3).padTop(0).colspan(2);
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
        Gdx.gl.glClearColor(58 / 255f, 58 / 255f, 80 / 255f, 1f);
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
