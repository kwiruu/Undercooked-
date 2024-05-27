package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.Main;

import static com.libgdx.undercooked.GameManager.timesUp;
import static com.libgdx.undercooked.screen.GameScreen.finishGame;

public class PauseScreen implements Screen {
    public Stage stage;
    private final Main context;

    public PauseScreen(final Main context) {
        this.context = context;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        Texture backgroundTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/bg.png"));
        root.setBackground(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        stage.addActor(root);

        root.pad(20);

        // Load button textures
        Texture playTexture = new Texture(Gdx.files.internal("assets/screens/pause_screen/play.png"));
        Texture playClickedTexture = new Texture(Gdx.files.internal("assets/screens/pause_screen/play_clicked.png"));
        Texture settingsTexture = new Texture(Gdx.files.internal("assets/screens/pause_screen/settings.png"));
        Texture settingsClickedTexture = new Texture(Gdx.files.internal("assets/screens/pause_screen/settings_clicked.png"));
        Texture menuTexture = new Texture(Gdx.files.internal("assets/screens/pause_screen/menu.png"));
        Texture menuClickedTexture = new Texture(Gdx.files.internal("assets/screens/pause_screen/menu_clicked.png"));
        Texture exitTexture = new Texture(Gdx.files.internal("assets/screens/pause_screen/exit.png"));
        Texture exitClickedTexture = new Texture(Gdx.files.internal("assets/screens/pause_screen/exit_clicked.png"));

        // Create ImageButtons
        ImageButton startButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(playTexture)),
            new TextureRegionDrawable(new TextureRegion(playClickedTexture)));
        ImageButton optionsButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(settingsTexture)),
            new TextureRegionDrawable(new TextureRegion(settingsClickedTexture)));
        ImageButton selectMapButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(menuTexture)),
            new TextureRegionDrawable(new TextureRegion(menuClickedTexture)));
        ImageButton exitButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(exitTexture)),
            new TextureRegionDrawable(new TextureRegion(exitClickedTexture)));

        // Add buttons to the table
        root.add(startButton).width(200).fillX().uniformX().padBottom(20);
        root.row();
        root.add(optionsButton).width(200).fillX().uniformX().padBottom(20);
        root.row();
        root.add(selectMapButton).width(200).fillX().uniformX().padBottom(20);
        root.row();
        root.add(exitButton).width(200).fillX().uniformX().padBottom(20);
        root.row();

        // Add listeners to buttons
        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.GAME);
            }
        });

        optionsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                context.setScreen(ScreenType.OPTIONS);
            }
        });

        selectMapButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                finishGame();
                context.setScreen(ScreenType.SELECTMAP);
            }
        });

        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                finishGame();
                context.setScreen(ScreenType.MAINMENUTRANSITION);
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
    }
}
