package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
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
import static com.libgdx.undercooked.screen.LandingPageScreen.getUsername;
import static database.SQLOperations.getInfo;

public class SelectionScreen implements Screen {

    private final Main context;

    public static int mapId = 0;
    private Stage stage;
    private Skin skin;
    private MapSound mapSound;
    private Table mapTable;
    private static String selectedMap;

    //This will handle the drag movement sa background
    private SpriteBatch spriteBatch;
    private Texture backgroundTexture;
    private OrthographicCamera camera;

    public SelectionScreen(final Main context) {
        this.context = context;
        mapSound = new MapSound("assets/audio/spirited_away.wav");
        Thread mapSoundThread = new Thread(mapSound);
        mapRunning = true;
        mapSoundThread.start();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json"));

        backgroundTexture = new Texture(Gdx.files.internal("assets/tilesets/map_selector.png"));
        spriteBatch = new SpriteBatch();

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        UserInfo userInfo = getInfo(getUsername());
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

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f; //Adjust lang ni if you want to zoom in or zoom less

        //Sets the camera pos
        camera.position.set((backgroundTexture.getWidth()/3) * camera.zoom - 600, Gdx.graphics.getHeight() - ((backgroundTexture.getHeight()/3) * camera.zoom) - 200, 0);
        System.out.println(backgroundTexture.getHeight());
        System.out.println(backgroundTexture.getWidth());
        System.out.println("Camera position: (" + camera.position.x + ", " + camera.position.y + ")");
        camera.update();

        //Gdx.input.setInputProcessor(new GestureDetector(new MyGestureListener()));
        //Gdx.input.setInputProcessor(stage);

        //Since mag handle man tag stage and gesture InputMultiplexer ato gamiton to handle both at the same time
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(new GestureDetector(new MyGestureListener()));
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

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
                        mapId = mapText.charAt(3);
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

    private class MyGestureListener extends GestureDetector.GestureAdapter {
        private float initialScale = 1f;
        private float initialCameraX, initialCameraY;

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            initialScale = camera.zoom;
            initialCameraX = camera.position.x;
            initialCameraY = camera.position.y;
            return true;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            camera.translate(-deltaX * camera.zoom, deltaY * camera.zoom);
            clampCameraPosition();
            return true;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            float ratio = initialDistance / distance;
            camera.zoom = initialScale * ratio;
            clampCameraPosition();
            return true;
        }

        private void clampCameraPosition() {
            float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
            float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

            float minX = effectiveViewportWidth / 2;
            float maxX = backgroundTexture.getWidth()/3 - effectiveViewportWidth / 2;
            float minY = effectiveViewportHeight / 2;
            float maxY = backgroundTexture.getHeight()/3 - effectiveViewportHeight / 2;

            if (camera.position.x < minX) camera.position.x = minX;
            if (camera.position.x > maxX) camera.position.x = maxX;
            if (camera.position.y < minY) camera.position.y = minY;
            if (camera.position.y > maxY) camera.position.y = maxY;
        }

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false, width, height);
        clampCameraPosition();
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
        backgroundTexture.dispose();
        spriteBatch.dispose();
    }

    public static String getSelectedMap() {
        return selectedMap;
    }

    private void setSelectedMap(String map) {
        SelectionScreen.selectedMap = map;
    }

    private void clampCameraPosition() {
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        float minX = effectiveViewportWidth / 2;
        float maxX = (backgroundTexture.getWidth()/3) - effectiveViewportWidth / 2;
        float minY = effectiveViewportHeight / 2;
        float maxY = (backgroundTexture.getHeight()/3) - effectiveViewportHeight / 2;

        if (camera.position.x < minX) camera.position.x = minX;
        if (camera.position.x > maxX) camera.position.x = maxX;
        if (camera.position.y < minY) camera.position.y = minY;
        if (camera.position.y > maxY) camera.position.y = maxY;
    }
}
