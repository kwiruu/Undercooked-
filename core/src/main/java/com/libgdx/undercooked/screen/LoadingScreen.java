package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.Main;

public class LoadingScreen implements Screen {
    public Skin skin;
    public Stage stage;
    private final Main context;

    public LoadingScreen(final Main context) {
        this.context = context;
    }

    @Override
    public void show() {
        skin = new Skin(Gdx.files.internal("metal-ui.json")); // Change this to your own skin file

        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.pad(20);

        TextButton startButton = new TextButton("Start", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        root.add(startButton).fillX().uniformX().padBottom(20);
        root.row();
        root.add(optionsButton).fillX().uniformX().padBottom(20);
        root.row();
        root.add(exitButton).fillX().uniformX();

        startButton.setDisabled(true);
        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.GAME);
            }
        });

        optionsButton.setDisabled(true);
        optionsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                context.setScreen(ScreenType.OPTIONS);
            }
        });

        exitButton.setDisabled(true);
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event,float x, float y){
                Gdx.app.exit();
            }
        });

        // Add stage to input processor
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {

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
    }
}
