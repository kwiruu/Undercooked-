package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.Main;

public class OptionsScreen implements Screen {

    private final Main context;
    private Stage stage;
    private Skin skin;

    public OptionsScreen(final Main context) {
        this.context = context;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("metal-ui.json"));

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);


        CheckBox audioCheckBox = new CheckBox("Audio On", skin);
        Slider brightnessSlider = new Slider(0, 100, 1, false, skin);
        Slider audioSlider = new Slider(0, 100, 1, false, skin);
        TextButton saveButton = new TextButton("Save", skin);
        Label brightnessLabel = new Label("Brightness", skin);
        Label audioLabel = new Label("Audio Volume", skin);
        Label titleLabel = new Label("Options", skin);

        root.pad(20);
        root.add(titleLabel).top().left().expandX().colspan(2);
        root.row();
        root.add(new Label(" ", skin)).expand().colspan(2).row(); // Empty row for spacing
        root.add(audioCheckBox).left().expandX().colspan(2).padBottom(10);
        root.row();
        root.add(brightnessLabel).left().expandX().colspan(2).padBottom(10);
        root.row();
        root.add(brightnessSlider).left().expandX().colspan(2).padBottom(10);
        root.row();
        root.add(audioLabel).left().expandX().colspan(2).padBottom(10);
        root.row();
        root.add(audioSlider).left().expandX().colspan(2).padBottom(10);
        root.row();
        root.add(saveButton).fillX().uniformX().colspan(2).padBottom(10);


        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });


        ImageButton closeButton = new ImageButton(skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.GAME);
            }
        });
        root.add(closeButton).top().left().expand().colspan(2).padTop(10);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
