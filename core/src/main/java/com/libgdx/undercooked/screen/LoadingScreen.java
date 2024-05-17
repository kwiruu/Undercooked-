package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.AudioManager.MapSound;
import com.libgdx.undercooked.Main;

import static com.libgdx.undercooked.AudioManager.MapSound.mapRunning;

public class LoadingScreen implements Screen {
    public Skin skin;
    public Stage stage;
    private final Main context;

    public LoadingScreen(final Main context) {
        this.context = context;
    }

    @Override
    public void show() {
        skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json")); // Change this to your own skin file

        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Set background image
        Texture backgroundTexture = new Texture(Gdx.files.internal("assets/screens/mainscreen.png"));
        root.setBackground(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));

        root.pad(20);

        TextButton startButton = new TextButton("Start", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton selectMapButton = new TextButton("Back To Menu", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        root.add(startButton).width(100).fillX().uniformX().padBottom(20);
        root.row();
        root.add(optionsButton).width(100).fillX().uniformX().padBottom(20);
        root.row();
        root.add(selectMapButton).width(200).fillX().uniformX().padBottom(20);
        root.row();
        root.add(exitButton).width(100).fillX().uniformX().padBottom(20);
        root.row();

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

        selectMapButton.setDisabled(true);
        selectMapButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                context.setScreen(ScreenType.SELECTMAP);
                Main.deleteScreen(ScreenType.GAME);
                mapRunning = false;
                MapSound.stop();
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
        Gdx.gl.glClearColor(58 / 255f, 58 / 255f, 80 / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            context.setScreen(ScreenType.GAME);
        }
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
