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
import database.UserInfo;
import database.SQLOperations;
import database.HighScore;

import java.util.List;

import static com.libgdx.undercooked.AudioManager.MapSound.mapRunning;
import static database.SQLOperations.getInfo;

public class SelectionScreen implements Screen {

    private final Main context;
    private Stage stage;
    private Skin skin;
    private MapSound mapSound;
    private Table mapTable;
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

        UserInfo userInfo = getInfo("kwiru");  // Adjust "valceven" as needed to dynamically fetch the current user
        Label titleLabel = new Label("Select Map - Level: " + userInfo.getLevel() + ", Info: " + userInfo.getUserName(), skin);

        mapTable = new Table();
        setupMapButtons(userInfo.getLevel(), skin);

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

    public void setupMapButtons(int userLevel, Skin skin) {
        mapTable.clear();

        for (int i = 1; i <= 5; i++) {
            final int mapNumber = i;
            TextButton mapButton = new TextButton("Map " + mapNumber, skin);
            if (mapNumber > userLevel) {
                mapButton.setDisabled(true);
            } else {
                mapButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String mapText = mapButton.getText().toString().replaceAll("\\s", "");
                        setSelectedMap(mapText);
                        mapSound = new MapSound("assets/audio/" + mapText + "_sound.wav");
                        context.setScreen(ScreenType.GAME);
                        Thread mapSoundThread = new Thread(mapSound);
                        mapRunning = true;
                        mapSoundThread.start();
                    }
                });
            }

            TextButton highScoreButton = new TextButton("High Scores", skin);
            highScoreButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showHighScoresDialog(mapNumber);
                }
            });

            mapTable.add(mapButton).width(200F).pad(10);
            mapTable.add(highScoreButton).width(200F).pad(10);
            mapTable.row();
        }
    }

    private void showHighScoresDialog(int mapId) {
        List<HighScore> highScores = SQLOperations.getTopHighScores(mapId);

        Dialog dialog = new Dialog("Top 10 High Scores", skin);
        dialog.getContentTable().add(new Label("Username", skin)).pad(10);
        dialog.getContentTable().add(new Label("Score", skin)).pad(10);
        dialog.getContentTable().row();

        for (HighScore highScore : highScores) {
            dialog.getContentTable().add(new Label(highScore.getUserName(), skin)).pad(10);
            dialog.getContentTable().add(new Label(String.valueOf(highScore.getHighScore()), skin)).pad(10);
            dialog.getContentTable().row();
        }

        dialog.button("Close", true);
        dialog.show(stage);
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

    public static String getSelectedMap() {
        return selectedMap;
    }

    private void setSelectedMap(String map) {
        SelectionScreen.selectedMap = map;
    }
}
