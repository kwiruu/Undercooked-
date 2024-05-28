package com.libgdx.undercooked.screen;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.AudioManager.MainMenuSound;
import com.libgdx.undercooked.AudioManager.MapSound;
import com.libgdx.undercooked.Main;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import database.UserInfo;



import static com.libgdx.undercooked.AudioManager.MapSound.mapRunning;
import static com.libgdx.undercooked.screen.LandingPageScreen.getUsername;
import static database.SQLOperations.getInfo;

public class SelectionScreen implements Screen {

    private final Main context;

    public static int mapId = 0;
    private Stage stage;
    private Skin skin;
    private MapSound mapSound;
    private static String selectedMap = "Map1";

    //This will handle the drag movement sa background
    private Thread mapSoundThread;
    private SpriteBatch spriteBatch;
    private Texture backgroundTexture;
    private OrthographicCamera camera;

    //Buttons for each map
    private Texture map1Texture, map2Texture, map3Texture, map4Texture, map5Texture;
    private TweenManager tweenManager;
    private Sprite blockClouds1;
    private Sprite blockClouds2;

    public SelectionScreen(final Main context) {
        this.context = context;
        startMapSound("assets/audio/spirited_away.wav");
    }

    private void startMapSound(String filePath) {
        mapSound = new MapSound(filePath);
        mapSoundThread = new Thread(mapSound);
        mapSoundThread.start();
        mapRunning = true;
    }

    private void stopMapSound(){
        if (mapSound != null) {
            mapRunning = false;
            mapSoundThread.interrupt();
            MapSound.stop();
        }
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json"));

        // animation my friend
        tweenManager = new TweenManager(); // Ensure tweenManager is initialized here
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());

        Texture blockCloud1Texture = new Texture(Gdx.files.internal("assets/screens/title_screen/block_clouds1.png"));
        Texture blockCloud2Texture = new Texture(Gdx.files.internal("assets/screens/title_screen/block_clouds2.png"));

        blockClouds1 = new Sprite(blockCloud1Texture);
        blockClouds2 = new Sprite(blockCloud2Texture);

        float targetX = Gdx.graphics.getWidth() / 2f - blockClouds1.getWidth() / 2;

        blockClouds1.setSize(blockCloud1Texture.getWidth() * 4, blockCloud1Texture.getHeight() * 4);
        blockClouds2.setSize(blockCloud2Texture.getWidth() * 4, blockCloud2Texture.getHeight() * 4);

        // Set initial positions to the middle of the screen
        blockClouds1.setPosition(Gdx.graphics.getWidth() / 2f - blockClouds1.getWidth() / 2, Gdx.graphics.getHeight() / 2f - blockClouds1.getHeight() / 2);
        blockClouds2.setPosition(Gdx.graphics.getWidth() / 2f - blockClouds2.getWidth() / 2, Gdx.graphics.getHeight() / 2f - blockClouds2.getHeight() / 2);

        // Tween to move to the outside
        Tween.to(blockClouds1, SpriteAccessor.POS_X, 3f)
            .target(-blockClouds1.getWidth()) // Move to the left outside
            .start(tweenManager);
        Tween.to(blockClouds2, SpriteAccessor.POS_X, 3f)
            .target(Gdx.graphics.getWidth()) // Move to the right outside
            .start(tweenManager);

        // end animation!


        backgroundTexture = new Texture(Gdx.files.internal("assets/tilesets/map_selector.png"));
        spriteBatch = new SpriteBatch();

        //For the Unlocked Map Buttons
        map1Texture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/map1_unlocked.png"));
        map2Texture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/map2_unlocked.png"));
        map3Texture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/map3_unlocked.png"));
        map4Texture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/map4_unlocked.png"));
        map5Texture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/map5_unlocked.png"));


        UserInfo userInfo = getInfo(getUsername());
        int userlevel = userInfo.getLevel();
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
        float backgroundWidth = backgroundTexture.getWidth() / 3;
        float backgroundHeight = backgroundTexture.getHeight() / 6;
        camera.position.set(backgroundWidth, backgroundHeight, 0);
        System.out.println("camera at: " + camera.position.x + " " + camera.position.y);
        camera.zoom = .5f;
        camera.update();



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
        tweenManager.update(delta);

        stage.act(Gdx.graphics.getDeltaTime());

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
        spriteBatch.end();

        stage.draw();

        spriteBatch.begin();
        blockClouds1.draw(spriteBatch);
        blockClouds2.draw(spriteBatch);
        spriteBatch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
            String mapText = "Map1";
            setSelectedMap(mapText);
            context.setScreen(ScreenType.GAME);
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            stopMapSound();
            Main.deleteScreen(ScreenType.SELECTMAP);
            context.setScreen(ScreenType.MAINMENUTRANSITION);
        }
    }



    public void setupMapButtons(int userLevel) {
        ImageButton map1Button = createMapButton(map1Texture, "Map1", 1, userLevel);
        ImageButton map2Button = createMapButton(map2Texture, "Map2", 2, userLevel);
        ImageButton map3Button = createMapButton(map3Texture, "Map3", 3, userLevel);
        ImageButton map4Button = createMapButton(map4Texture, "Map4", 4, userLevel);
        ImageButton map5Button = createMapButton(map5Texture, "Map5", 5, userLevel);

        // Set positions for map buttons to align with the background image
        map1Button.setPosition(10 * 2f, (Gdx.graphics.getHeight() - 310) * 2);
        map2Button.setPosition(340 * 2f, (Gdx.graphics.getHeight() - 341) * 2);
        map3Button.setPosition(555 * 2f, (Gdx.graphics.getHeight() - 216) * 2);
        map4Button.setPosition(106 * 2f, (Gdx.graphics.getHeight() - 590) * 2);
        map5Button.setPosition(1004 * 2f, (Gdx.graphics.getHeight() - 632) * 2);

        // Add buttons to the stage
        stage.addActor(map1Button);
        stage.addActor(map2Button);
        stage.addActor(map3Button);
        stage.addActor(map4Button);
        stage.addActor(map5Button);
    }

    private ImageButton createMapButton(Texture texture, String mapName, int mapNumber, int userLevel) {
        Drawable normalDrawable;

        // Load hover and clicked textures
        Texture hoverTexture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/" + mapName.toLowerCase() + "_hovered.png"));
        Texture clickedTexture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/" + mapName.toLowerCase() + "_clicked.png"));
        Texture unlockedTexture = new Texture(Gdx.files.internal("assets/tilesets/mapsButtons - Copy/" + mapName.toLowerCase() + "_locked.png"));
        Drawable hoverDrawable = new TextureRegionDrawable(hoverTexture);
        Drawable clickedDrawable = new TextureRegionDrawable(clickedTexture);

        if(userLevel >= mapNumber){
            normalDrawable = new TextureRegionDrawable(texture);
        } else {
            normalDrawable = new TextureRegionDrawable(unlockedTexture);
        }

        ImageButton mapButton = new ImageButton(normalDrawable);

        // Set initial size based on original texture size
        float originalWidth = texture.getWidth();
        float originalHeight = texture.getHeight();
        float buttonWidth = originalWidth / 3; // Divide by 3 to adjust size as needed
        float buttonHeight = originalHeight / 3; // Divide by 3 to adjust size as needed
        mapButton.setSize(buttonWidth, buttonHeight);

        // Click listener for map selection
        if (mapNumber <= userLevel) {
            mapButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    mapRunning = false;
                    MapSound.stop();
                    setSelectedMap(mapName);
                    mapId = mapNumber;
                    context.setScreen(ScreenType.GAME);
                    startMapSound("assets/audio/" + mapName.replace(" ", "").toLowerCase() + "_sound.wav");
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    mapButton.getStyle().imageUp = hoverDrawable;
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    mapButton.getStyle().imageUp = normalDrawable;
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    mapButton.getStyle().imageUp = clickedDrawable;
                    super.touchDown(event, x, y, pointer, button);
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    mapButton.getStyle().imageUp = normalDrawable;
                    super.touchUp(event, x, y, pointer, button);
                }
            });
        }

        return mapButton;
    }



    private void updateButtonSizes() {
        for (Actor actor : stage.getActors()) {
            if (actor instanceof ImageButton) {
                ImageButton button = (ImageButton) actor;
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
            // Calculate the desired new camera position
            float desiredCameraX = camera.position.x - deltaX;
            float desiredCameraY = camera.position.y + deltaY;

            // Clamp the desired position to the allowed bounds
            float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
            float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

            float minX = effectiveViewportWidth / 2;
            float maxX = (backgroundTexture.getWidth() / 3) - effectiveViewportWidth / 2;
            float minY = effectiveViewportHeight / 2;
            float maxY = backgroundTexture.getHeight() / 3 - effectiveViewportHeight / 2;

            desiredCameraX = Math.max(minX, Math.min(maxX, desiredCameraX));
            desiredCameraY = Math.max(minY, Math.min(maxY, desiredCameraY));

            // Calculate actual movement
            float actualDeltaX = desiredCameraX - camera.position.x;
            float actualDeltaY = desiredCameraY - camera.position.y;

            // Move the camera to the clamped position
            camera.position.x = desiredCameraX;
            camera.position.y = desiredCameraY;

            // Update button positions based on the actual movement
            updateButtonPositions(actualDeltaX, actualDeltaY);

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
                if (deltaX != 0 || deltaY != 0) {
                    actor.moveBy(-deltaX * 2f, -deltaY * 2f);
                }
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
        MainMenuSound.stop();
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
