package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.AudioManager.MapSound;
import com.libgdx.undercooked.Main;
import database.UserInfo;
import database.SQLOperations;
import database.HighScore;

import java.util.ArrayList;
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
    private static String selectedMap = "Map1";

    //This will handle the drag movement sa background
    private SpriteBatch spriteBatch;
    private Texture backgroundTexture;
    private OrthographicCamera camera;

    //Buttons for each map
    private Sprite map1, map2, map3, map4, map5;
    private Texture map1Texture, map2Texture, map3Texture, map4Texture, map5Texture;

    public SelectionScreen(final Main context) {
        this.context = context;
        mapSound = new MapSound("assets/audio/spirited_away.wav");
        Thread mapSoundThread = new Thread(mapSound);
        mapRunning = true;
        //mapSoundThread.start();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json"));


        backgroundTexture = new Texture(Gdx.files.internal("assets/tilesets/map_selector.png"));
        spriteBatch = new SpriteBatch();

        //For the Unlocked Map Buttons
        map1Texture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/map1_unlocked.png"));
        map2Texture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/map2_unlocked.png"));
        map3Texture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/map3_unlocked.png"));
        map4Texture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/map4_unlocked.png"));
        //map5Texture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons/map1_unlocked"));

        //For the Locked Map Buttons
        //Wala pani
        UserInfo userInfo = getInfo(getUsername());
        //Label titleLabel = new Label("Select Map - Level: " + userInfo.getLevel() + ", Info: " + userInfo.getUserName(), skin);
        setupMapButtons(userInfo.getLevel());

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.LANDING);
            }
        });

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = .5f; //Adjust lang ni if you want to zoom in or zoom less

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

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            String mapText = "Map1";
            setSelectedMap(mapText);
            context.setScreen(ScreenType.GAME);
        }
    }



    public void setupMapButtons(int userLevel) {
        ImageButton map1Button = createMapButton(map1Texture, "Map1", 1, userLevel);
        ImageButton map2Button = createMapButton(map2Texture, "Map2", 2, userLevel);
        ImageButton map3Button = createMapButton(map3Texture, "Map3", 3, userLevel);
        ImageButton map4Button = createMapButton(map4Texture, "Map4", 4, userLevel);
        // ImageButton map5Button = createMapButton(map5Texture, "Map5", 5, userLevel);

        // Set positions for map buttons to align with the background image
        map1Button.setPosition(10 * 2f, (Gdx.graphics.getHeight() - 310) * 2);
        map2Button.setPosition(340 * 2f, (Gdx.graphics.getHeight() - 341) * 2);
        map3Button.setPosition(555 * 2f, (Gdx.graphics.getHeight() - 216) * 2);
        map4Button.setPosition(106 * 2f, (Gdx.graphics.getHeight() - 590) * 2);
        // map5Button.setPosition(1050, Gdx.graphics.getHeight() - 250);

        // Add buttons to the stage
        stage.addActor(map1Button);
        stage.addActor(map2Button);
        stage.addActor(map3Button);
        stage.addActor(map4Button);
        // stage.addActor(map5Button);
    }


    private List<ImageButton> mapButtons = new ArrayList<>();

    private ImageButton createMapButton(Texture texture, String mapName, int mapNumber, int userLevel) {
        Drawable drawable = new TextureRegionDrawable(texture);
        ImageButton mapButton = new ImageButton(drawable);

        // Set initial size based on original texture size
        float originalWidth = texture.getWidth();
        float originalHeight = texture.getHeight();
        float buttonWidth = originalWidth / 3; // Divide by 3 to adjust size as needed
        float buttonHeight = originalHeight / 3; // Divide by 3 to adjust size as needed
        mapButton.setSize(buttonWidth, buttonHeight);

        // Click listener for map selection
        mapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Na click ko");
                if (mapNumber <= userLevel) {
                    setSelectedMap(mapName);
                    mapId = mapNumber;
                    mapSound = new MapSound("assets/audio/" + mapName.replace(" ", "").toLowerCase() + "_sound.wav");
                    context.setScreen(ScreenType.GAME);
                    Thread mapSoundThread = new Thread(mapSound);
                    mapRunning = true;
                    //mapSoundThread.start();
                }
            }
        });

        // Hover listener for changing appearance
        mapButton.addListener(new InputListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                mapButton.setColor(1, 1, 1, 1); // Reset to original opacity
            }
        });

        return mapButton;
    }

    private void updateButtonSizes() {
        for (Actor actor : stage.getActors()) {
            if (actor instanceof ImageButton) {
                ImageButton button = (ImageButton) actor;
                // Update button size based on camera zoom
                button.setSize(button.getWidth() / camera.zoom, button.getHeight() / camera.zoom);
            }
        }
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
            camera.translate(-deltaX , deltaY );
            clampCameraPosition();
            if(camera.position.x >= 0){
                updateButtonPositions(0, deltaY);
            } else if (camera.position.y <= 0) {
                updateButtonPositions(-deltaX, 0);
            }else{
                updateButtonPositions(-deltaX, deltaY);
            }
            return true;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            float ratio = initialDistance / distance;
            camera.zoom = initialScale * ratio;
            clampCameraPosition();
            updateButtonSizes();
            //updateButtonPositions();
            return true;
        }

        private void clampCameraPosition() {
            float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
            float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

            float minX = effectiveViewportWidth / 2;
            float maxX = backgroundTexture.getWidth()/3 - effectiveViewportWidth / 2;
            float minY = effectiveViewportHeight / 2;
            float maxY = backgroundTexture.getHeight() /3- effectiveViewportHeight / 2;

            if (camera.position.x < minX) camera.position.x = minX;
            if (camera.position.x > maxX) camera.position.x = maxX;
            if (camera.position.y < minY) camera.position.y = minY;
            if (camera.position.y > maxY) camera.position.y = maxY;
        }
    }

    private void updateButtonPositions(float deltaX, float deltaY) {
        // Iterate over all actors in the stage
        for (Actor actor : stage.getActors()) {
            if (actor instanceof ImageButton) {
                // Adjust the position of the button relative to the camera's movement
                actor.moveBy(-(deltaX * 2f), -(deltaY * 2f));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false, width, height);
        clampCameraPosition();
        updateButtonSizes();
        //updateButtonPositions();
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
