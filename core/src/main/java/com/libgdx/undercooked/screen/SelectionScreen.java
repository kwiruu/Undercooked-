package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.AudioManager.MainMenuSound;
import com.libgdx.undercooked.AudioManager.MapSound;
import com.libgdx.undercooked.Main;
import database.GameData;

import static com.libgdx.undercooked.AudioManager.MapSound.mapRunning;
import static com.libgdx.undercooked.screen.LandingPageScreen.getUsername;
import static database.GameData.getInfo;

/**
 * The city map where the player picks a kitchen. Every map is a building on the city picture: it
 * gets a pin that says whether it is cleared, ready to play or still locked, and clicking it opens a
 * small card with the details and a Play button.
 */
public class SelectionScreen implements Screen {

    public static int mapId = 0;
    private static String selectedMap = "Map1";

    /** Size of tilesets/map_selector.png in pixels. All building positions below use this space. */
    private static final float BG_W = 4192f, BG_H = 2400f;
    private static final int MAP_COUNT = 5;
    private static final String[] MAP_NAMES = {
        "Corner House", "Yellow Apartments", "City Hospital", "Fire Station", "Billboard Block"
    };
    /** Where each map's building sits on the city picture: x, y from the top, width, height. */
    private static final float[][] BUILDINGS = {
        {27, 603, 202, 330},
        {1019, 635, 330, 394},
        {1659, -5, 906, 650},
        {315, 1339, 458, 426},
        {3003, 1467, 266, 426},
    };
    private static final String BUTTON_DIR = "tilesets/mapsButtons - Copy/";

    // Colours shared by pins, legend and card.
    private static final Color CLEARED = new Color(0.36f, 0.75f, 0.36f, 1f);
    private static final Color READY = new Color(1f, 0.62f, 0.15f, 1f);
    private static final Color LOCKED = new Color(0.45f, 0.45f, 0.5f, 1f);
    private static final Color PANEL = new Color(0.10f, 0.08f, 0.12f, 0.82f);
    private static final Color CREAM = new Color(1f, 0.95f, 0.84f, 1f);

    private final Main context;
    private MapSound mapSound;

    private OrthographicCamera worldCamera;
    private OrthographicCamera uiCamera;
    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private Stage stage;
    private Skin skin;

    private Texture background, cloudLeft, cloudRight;
    private final Texture[] unlockedTex = new Texture[MAP_COUNT];
    private final Texture[] lockedTex = new Texture[MAP_COUNT];
    private final Texture[] hoverTex = new Texture[MAP_COUNT];

    private int level;
    private final int[] bestScores = new int[MAP_COUNT];
    private int hovered = -1;
    private float time;
    private float fadeIn;

    private Table card;
    private int cardMap = -1;

    // Drag and zoom state.
    private float fitZoom, minZoom;
    private boolean dragging;
    private int downX, downY, lastX, lastY;

    public SelectionScreen(final Main context) {
        this.context = context;
    }

    private void startMapSound(String filePath) {
        mapRunning = true;
        mapSound = new MapSound(filePath);
        mapSound.run();
    }

    private void stopMapSound() {
        if (mapSound != null) {
            mapRunning = false;
            MapSound.stop();
        }
    }

    @Override
    public void show() {
        if (batch == null) {
            loadAssets();
        }
        if (!mapRunning) {
            startMapSound("audio/spirited_away.mp3");
        }

        String user = getUsername();
        level = getInfo(user).getLevel();
        for (int i = 0; i < MAP_COUNT; i++) {
            bestScores[i] = GameData.getPersonalBest(user, i + 1);
        }

        stage.clear();
        buildHud();
        closeCard();
        time = 0f;
        fadeIn = 0f;
        resetCamera();

        InputMultiplexer input = new InputMultiplexer();
        input.addProcessor(stage);
        input.addProcessor(new MapInput());
        Gdx.input.setInputProcessor(input);
    }

    private void loadAssets() {
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        font = new BitmapFont(Gdx.files.internal("fonts/Text_font_white.fnt"));
        font.setUseIntegerPositions(true);
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("ui/ui-skin.json"));

        background = new Texture(Gdx.files.internal("tilesets/map_selector.png"));
        cloudLeft = new Texture(Gdx.files.internal("screens/title_screen/block_clouds1.png"));
        cloudRight = new Texture(Gdx.files.internal("screens/title_screen/block_clouds2.png"));
        for (int i = 0; i < MAP_COUNT; i++) {
            String base = BUTTON_DIR + "map" + (i + 1);
            unlockedTex[i] = new Texture(Gdx.files.internal(base + "_unlocked.png"));
            lockedTex[i] = new Texture(Gdx.files.internal(base + "_locked.png"));
            hoverTex[i] = new Texture(Gdx.files.internal(base + "_hovered.png"));
        }
        worldCamera = new OrthographicCamera();
        uiCamera = new OrthographicCamera();
    }

    // ------------------------------------------------------------------ map state

    private boolean isUnlocked(int i) { return level >= i + 1; }
    private boolean isCleared(int i) { return level > i + 1 || bestScores[i] > 0; }
    /** The first unlocked map that hasn't been cleared yet: the one the game nudges you towards. */
    private int nextMap() {
        for (int i = 0; i < MAP_COUNT; i++) {
            if (isUnlocked(i) && !isCleared(i)) return i;
        }
        return -1;
    }

    // ------------------------------------------------------------------ camera

    private void resetCamera() {
        float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
        worldCamera.setToOrtho(false, w, h);
        uiCamera.setToOrtho(false, w, h);
        // "Cover" the screen with the city so there are no empty edges, then allow zooming in 2.5x.
        fitZoom = Math.min(BG_W / w, BG_H / h);
        minZoom = fitZoom / 2.5f;
        worldCamera.zoom = fitZoom;
        worldCamera.position.set(BG_W / 2f, BG_H / 2f, 0);
        clampCamera();
    }

    private void clampCamera() {
        worldCamera.zoom = MathUtils.clamp(worldCamera.zoom, minZoom, fitZoom);
        float halfW = worldCamera.viewportWidth * worldCamera.zoom / 2f;
        float halfH = worldCamera.viewportHeight * worldCamera.zoom / 2f;
        worldCamera.position.x = halfW * 2 >= BG_W ? BG_W / 2f : MathUtils.clamp(worldCamera.position.x, halfW, BG_W - halfW);
        worldCamera.position.y = halfH * 2 >= BG_H ? BG_H / 2f : MathUtils.clamp(worldCamera.position.y, halfH, BG_H - halfH);
        worldCamera.update();
    }

    /** Building rectangle in world space (y up): x, y, w, h. */
    private float[] worldRect(int i) {
        float[] b = BUILDINGS[i];
        return new float[]{b[0], BG_H - b[1] - b[3], b[2], b[3]};
    }

    private final Vector3 tmp = new Vector3();

    /** Screen position (y up) of the point above a building where its pin sits. */
    private Vector3 pinScreenPos(int i) {
        float[] r = worldRect(i);
        tmp.set(r[0] + r[2] / 2f, r[1] + r[3] * 0.62f, 0);
        worldCamera.project(tmp);
        // Keep pins inside the screen, below the title banner.
        // Keep the pin and its name plate on screen and clear of the title banner (bottom at h - 100).
        tmp.x = MathUtils.clamp(tmp.x, 110, Gdx.graphics.getWidth() - 110);
        tmp.y = MathUtils.clamp(tmp.y, 90, Gdx.graphics.getHeight() - 210);
        return tmp;
    }

    private int buildingAt(int screenX, int screenY) {
        // Pins first: they are what the player aims at.
        for (int i = MAP_COUNT - 1; i >= 0; i--) {
            Vector3 p = pinScreenPos(i);
            float py = Gdx.graphics.getHeight() - screenY;
            if (Math.abs(screenX - p.x) < 34 && py > p.y - 30 && py < p.y + 70) return i;
        }
        tmp.set(screenX, screenY, 0);
        worldCamera.unproject(tmp);
        for (int i = MAP_COUNT - 1; i >= 0; i--) {
            float[] r = worldRect(i);
            if (tmp.x >= r[0] && tmp.x <= r[0] + r[2] && tmp.y >= r[1] && tmp.y <= r[1] + r[3]) return i;
        }
        return -1;
    }

    // ------------------------------------------------------------------ HUD and card

    private void buildHud() {
        TextButton back = new TextButton("  Back  ", skin);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goBack();
            }
        });
        TextButton howTo = new TextButton("  How to play  ", skin);
        howTo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen.forceTour = true;
                startMap(0);
            }
        });
        Table corner = new Table();
        corner.setFillParent(true);
        corner.bottom().left().pad(20);
        corner.add(back).height(44).padRight(10);
        corner.add(howTo).height(44);
        stage.addActor(corner);
    }

    private void openCard(final int i) {
        closeCard();
        cardMap = i;
        boolean unlocked = isUnlocked(i);

        card = new Table();
        card.setBackground(skin.getDrawable("window_box"));
        card.pad(26, 34, 22, 34);
        card.defaults().padBottom(8);

        Label title = new Label("Map " + (i + 1) + "  -  " + MAP_NAMES[i], skin);
        title.setFontScale(1.4f);
        card.add(title).colspan(2).row();

        String status;
        if (!unlocked) status = "Locked. Clear Map " + i + " to open this kitchen.";
        else if (isCleared(i)) status = "Cleared! Best score: " + bestScores[i];
        else status = "Ready to cook. Serve every order before the 3:00 timer runs out.";
        Label statusLabel = new Label(status, skin);
        statusLabel.setWrap(true);
        statusLabel.setAlignment(Align.center);
        card.add(statusLabel).width(380).colspan(2).padBottom(18).row();

        TextButton close = new TextButton("  Close  ", skin);
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeCard();
            }
        });
        if (unlocked) {
            TextButton play = new TextButton("  Play  ", skin);
            play.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    startMap(i);
                }
            });
            card.add(play).height(44).padRight(12);
            card.add(close).height(44);
        } else {
            card.add(close).height(44).colspan(2);
        }

        card.pack();
        card.setPosition((Gdx.graphics.getWidth() - card.getWidth()) / 2f, (Gdx.graphics.getHeight() - card.getHeight()) / 2f);
        stage.addActor(card);
    }

    private void closeCard() {
        if (card != null) card.remove();
        card = null;
        cardMap = -1;
    }

    private void startMap(int i) {
        String mapName = "Map" + (i + 1);
        mapRunning = false;
        MapSound.stop();
        setSelectedMap(mapName);
        mapId = i + 1;
        closeCard();
        context.setScreen(ScreenType.GAME);
        startMapSound("audio/" + mapName.toLowerCase() + "_sound.mp3");
    }

    private void goBack() {
        stopMapSound();
        Main.deleteScreen(ScreenType.SELECTMAP);
        context.setScreen(ScreenType.MAINMENUTRANSITION);
    }

    // ------------------------------------------------------------------ rendering

    @Override
    public void render(float delta) {
        time += delta;
        fadeIn = Math.min(1f, fadeIn + delta * 2.5f);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (card != null) closeCard(); else { goBack(); return; }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (card != null && isUnlocked(cardMap)) { startMap(cardMap); return; }
            if (card == null && nextMap() >= 0) openCard(nextMap());
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // City and buildings.
        worldCamera.update();
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        batch.draw(background, 0, 0, BG_W, BG_H);
        int next = nextMap();
        for (int i = 0; i < MAP_COUNT; i++) {
            float[] r = worldRect(i);
            Texture t;
            if (!isUnlocked(i)) t = lockedTex[i];
            else if (i == hovered || i == cardMap) t = hoverTex[i];
            else t = unlockedTex[i];
            if (i == next && i != hovered && i != cardMap) {
                // Gently flash the outline of the next kitchen to play.
                batch.draw(unlockedTex[i], r[0], r[1], r[2], r[3]);
                batch.setColor(1, 1, 1, 0.5f + 0.5f * MathUtils.sin(time * 4f));
                t = hoverTex[i];
            }
            batch.draw(t, r[0], r[1], r[2], r[3]);
            batch.setColor(Color.WHITE);
        }
        batch.end();

        // Screen-space overlays.
        uiCamera.update();
        shapes.setProjectionMatrix(uiCamera.combined);
        batch.setProjectionMatrix(uiCamera.combined);
        if (card != null) dimScreen(0.45f);
        drawBanner();
        for (int i = 0; i < MAP_COUNT; i++) drawPin(i, i == next);
        drawLegend();

        stage.act(delta);
        stage.draw();

        drawCloudReveal();
    }

    /** The menu closes two cloud banks over the screen; here they part again to reveal the city. */
    private void drawCloudReveal() {
        float t = Math.min(1f, time / 1.6f);
        if (t >= 1f) return;
        float ease = t * t * (3 - 2 * t);
        float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
        float cw = Math.max(w, cloudLeft.getWidth() * 4f), ch = Math.max(h, cloudLeft.getHeight() * 4f);
        float y = (h - ch) / 2f;
        batch.begin();
        batch.draw(cloudLeft, (w - cw) / 2f - ease * (w + cw) / 2f, y, cw, ch);
        batch.draw(cloudRight, (w - cw) / 2f + ease * (w + cw) / 2f, y, cw, ch);
        batch.end();
    }

    private void dimScreen(float alpha) {
        beginShapes(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0, 0, 0, alpha);
        shapes.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapes.end();
    }

    private void drawBanner() {
        float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
        float bw = Math.min(920, w - 40), bh = 84, bx = (w - bw) / 2f, by = h - bh - 16;
        beginShapes(ShapeRenderer.ShapeType.Filled);
        roundRect(bx, by, bw, bh, 14, PANEL);
        shapes.end();

        batch.begin();
        font.getData().setScale(1.25f);
        font.setColor(CREAM);
        drawCentered("CHOOSE A KITCHEN", w / 2f, by + bh - 14);
        font.getData().setScale(0.7f);
        font.setColor(0.85f, 0.82f, 0.75f, 1f);
        drawCentered("Click a pin to play  -  Drag to look around  -  Scroll to zoom  -  Esc to go back", w / 2f, by + 30);
        batch.end();
    }

    private void drawPin(int i, boolean isNext) {
        Vector3 p = pinScreenPos(i);
        float x = p.x, y = p.y;
        boolean unlocked = isUnlocked(i), cleared = isCleared(i);
        Color c = !unlocked ? LOCKED : (isNext ? READY : CLEARED);
        float bob = isNext ? 6f * MathUtils.sin(time * 5f) : 0f;
        boolean hot = i == hovered;
        float r = hot ? 28 : 24;
        float cy = y + 34 + bob;

        beginShapes(ShapeRenderer.ShapeType.Filled);
        // Shadow and pointer.
        shapes.setColor(0, 0, 0, 0.35f);
        shapes.ellipse(x - 14, y - 6, 28, 10);
        shapes.setColor(0.08f, 0.06f, 0.1f, 1f);
        shapes.circle(x, cy, r + 4, 28);
        shapes.triangle(x - 12, cy - r + 2, x + 12, cy - r + 2, x, y - 2 + bob);
        shapes.setColor(c);
        shapes.circle(x, cy, r, 28);
        shapes.triangle(x - 9, cy - r + 4, x + 9, cy - r + 4, x, y + 3 + bob);
        if (isNext) {
            // Pulsing ring.
            float pulse = (time * 1.2f) % 1f;
            shapes.end();
            beginShapes(ShapeRenderer.ShapeType.Line);
            shapes.setColor(READY.r, READY.g, READY.b, 1f - pulse);
            shapes.circle(x, cy, r + 6 + pulse * 22, 32);
            shapes.end();
            beginShapes(ShapeRenderer.ShapeType.Filled);
        }
        if (!unlocked) drawPadlock(x, cy);
        shapes.end();

        batch.begin();
        if (unlocked) {
            font.getData().setScale(1.1f);
            font.setColor(Color.WHITE);
            drawCentered(String.valueOf(i + 1), x, cy + 11);
        }
        // Name plate under the pin.
        String text = !unlocked ? "Locked" : (isNext ? "PLAY" : (cleared ? "Cleared" : "Play"));
        font.getData().setScale(0.7f);
        String name = "Map " + (i + 1) + ": " + text;
        layout.setText(font, name);
        batch.end();

        float pw = layout.width + 22, ph = 26, px = x - pw / 2f, py = cy + r + 10;
        beginShapes(ShapeRenderer.ShapeType.Filled);
        roundRect(px, py, pw, ph, 8, hot || isNext ? new Color(c.r * 0.55f, c.g * 0.55f, c.b * 0.55f, 0.95f) : PANEL);
        shapes.end();
        batch.begin();
        font.setColor(isNext ? Color.WHITE : CREAM);
        drawCentered(name, x, py + ph - 6);
        if (unlocked && cleared && bestScores[i] > 0) {
            font.getData().setScale(0.6f);
            font.setColor(1f, 0.9f, 0.45f, 1f);
            drawCentered("Best " + bestScores[i], x, py + ph + 18);
        }
        batch.end();
    }

    private void drawPadlock(float x, float y) {
        shapes.setColor(0.95f, 0.95f, 0.95f, 1f);
        shapes.rect(x - 9, y - 9, 18, 13);
        shapes.rect(x - 7, y + 4, 3, 7);
        shapes.rect(x + 4, y + 4, 3, 7);
        shapes.rect(x - 7, y + 9, 14, 3);
        shapes.setColor(LOCKED);
        shapes.rect(x - 1.5f, y - 5, 3, 5);
    }

    private void drawLegend() {
        String[] names = {"Play next", "Cleared", "Locked"};
        Color[] colors = {READY, CLEARED, LOCKED};
        font.getData().setScale(0.7f);
        // Measure first so dots and words never overlap, whatever the font size.
        float[] widths = new float[names.length];
        float lw = 16;
        for (int i = 0; i < names.length; i++) {
            layout.setText(font, names[i]);
            widths[i] = layout.width;
            lw += 26 + widths[i] + 16;
        }
        float w = Gdx.graphics.getWidth();
        float lh = 40, lx = w - lw - 20, ly = 20;
        beginShapes(ShapeRenderer.ShapeType.Filled);
        roundRect(lx, ly, lw, lh, 10, PANEL);
        float x = lx + 16;
        for (int i = 0; i < names.length; i++) {
            shapes.setColor(colors[i]);
            shapes.circle(x + 8, ly + lh / 2, 8, 16);
            x += 26 + widths[i] + 16;
        }
        shapes.end();
        batch.begin();
        font.setColor(CREAM);
        x = lx + 16;
        for (int i = 0; i < names.length; i++) {
            font.draw(batch, names[i], x + 26, ly + lh / 2 + 8);
            x += 26 + widths[i] + 16;
        }
        batch.end();
    }

    private void drawCentered(String text, float cx, float top) {
        layout.setText(font, text);
        font.draw(batch, layout, cx - layout.width / 2f, top);
    }

    /** SpriteBatch.end() turns blending off, so turn it back on before drawing see-through shapes. */
    private void beginShapes(ShapeRenderer.ShapeType type) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.begin(type);
    }

    private void roundRect(float x, float y, float w, float h, float r, Color c) {
        shapes.setColor(c);
        shapes.rect(x + r, y, w - 2 * r, h);
        shapes.rect(x, y + r, r, h - 2 * r);
        shapes.rect(x + w - r, y + r, r, h - 2 * r);
        shapes.arc(x + r, y + r, r, 180, 90, 8);
        shapes.arc(x + w - r, y + r, r, 270, 90, 8);
        shapes.arc(x + w - r, y + h - r, r, 0, 90, 8);
        shapes.arc(x + r, y + h - r, r, 90, 90, 8);
    }

    // ------------------------------------------------------------------ input

    private class MapInput extends InputAdapter {
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            downX = lastX = screenX;
            downY = lastY = screenY;
            dragging = false;
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (Math.abs(screenX - downX) + Math.abs(screenY - downY) > 8) dragging = true;
            if (dragging && card == null) {
                worldCamera.position.x -= (screenX - lastX) * worldCamera.zoom;
                worldCamera.position.y += (screenY - lastY) * worldCamera.zoom;
                clampCamera();
            }
            lastX = screenX;
            lastY = screenY;
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (dragging) return true;
            int hit = buildingAt(screenX, screenY);
            if (card != null) {
                // Clicking outside the card closes it.
                closeCard();
                return true;
            }
            if (hit >= 0) openCard(hit);
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            hovered = card == null ? buildingAt(screenX, screenY) : -1;
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            if (card != null) return true;
            // Zoom towards the mouse pointer.
            tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            worldCamera.unproject(tmp);
            float wx = tmp.x, wy = tmp.y;
            worldCamera.zoom *= amountY > 0 ? 1.15f : 1 / 1.15f;
            clampCamera();
            tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            worldCamera.unproject(tmp);
            worldCamera.position.x += wx - tmp.x;
            worldCamera.position.y += wy - tmp.y;
            clampCamera();
            return true;
        }
    }

    // ------------------------------------------------------------------ lifecycle

    @Override
    public void resize(int width, int height) {
        if (stage == null) return;
        stage.getViewport().update(width, height, true);
        resetCamera();
        if (cardMap >= 0) openCard(cardMap);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { hovered = -1; }

    @Override
    public void dispose() {
        if (batch == null) return;
        stage.dispose();
        skin.dispose();
        batch.dispose();
        shapes.dispose();
        font.dispose();
        background.dispose();
        cloudLeft.dispose();
        cloudRight.dispose();
        for (int i = 0; i < MAP_COUNT; i++) {
            unlockedTex[i].dispose();
            lockedTex[i].dispose();
            hoverTex[i].dispose();
        }
        MainMenuSound.stop();
    }

    public static String getSelectedMap() {
        return selectedMap;
    }

    private void setSelectedMap(String map) {
        SelectionScreen.selectedMap = map;
    }
}
