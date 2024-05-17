package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.AudioManager.MapSound;
import com.libgdx.undercooked.Main;

import static com.libgdx.undercooked.AudioManager.MapSound.mapRunning;

public class SelectionScreen implements Screen {

    private final Main context;
    private Stage stage;
    private Skin skin;
    private MapSound mapSound;

    private static String selectedMap;

    public SelectionScreen(final Main context) {
        this.context = context;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json"));

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label titleLabel = new Label("Select Map", skin);

        Table mapTable = new Table();
        for (int i = 1; i <= 5; i++) {
            final int mapNumber = i;
            TextButton mapButton = new TextButton("Map " + mapNumber, skin);
            mapButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String mapText = mapButton.getText().toString().replaceAll("\\s", "");
                    setSelectedMap(mapText);
                    mapSound = new MapSound("assets/audio/" + mapText +"_sound.wav");
                    context.setScreen(ScreenType.GAME);
                    Thread mapSoundThread = new Thread(mapSound);
                    mapRunning = true;
                    mapSoundThread.start();
                }
            });
            mapTable.add(mapButton).width(300F).pad(20);
            mapTable.row();
        }

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.LANDING);
            }
        });

        root.add(titleLabel).top().left().expandX().colspan(2);
        root.row();
        root.add(mapTable).expand().colspan(2).pad(20);
        root.row();
        root.add(backButton).center().colspan(2).pad(20);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
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
    public static String getSelectedMap(){
        return selectedMap;
    }

    private void setSelectedMap(String map){
        SelectionScreen.selectedMap = map;
    }
}
