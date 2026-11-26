package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.libgdx.undercooked.GameManager;
import com.libgdx.undercooked.entities.FoodType;
import com.libgdx.undercooked.entities.Orders;
import com.libgdx.undercooked.entities.PlayerManager.Player;
import com.libgdx.undercooked.entities.Stations.ChoppingBoard;
import com.libgdx.undercooked.entities.Stations.Counter;
import com.libgdx.undercooked.entities.Stations.FoodSource;
import com.libgdx.undercooked.entities.Stations.Station;
import com.libgdx.undercooked.entities.Stations.Stove;
import database.GameData;

/**
 * The first-time kitchen tour on Map 1. Each step tells the player one thing to do, points an arrow
 * at the station to use and moves on as soon as the player has actually done it. The 3:00 timer is
 * paused until the tour is over. After the tour (or in any later game) it only shows a small
 * reminder of the controls in the bottom-left corner.
 */
public class TourOverlay implements Disposable {

    private static final int MOVE = 0, GRAB = 1, CHOP = 2, SERVE = 3, COOK = 4, BOOK = 5, DONE = 6;
    private static final int STEP_COUNT = 6;

    private static final Color PANEL = new Color(0.10f, 0.08f, 0.12f, 0.88f);
    private static final Color CREAM = new Color(1f, 0.95f, 0.84f, 1f);
    private static final Color ACCENT = new Color(1f, 0.62f, 0.15f, 1f);
    private static final Color GOOD = new Color(0.45f, 0.85f, 0.45f, 1f);

    private final GameManager game;
    private final Viewport gameViewport;
    private final String username;

    private final SpriteBatch batch = new SpriteBatch();
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final OrthographicCamera screenCamera = new OrthographicCamera();
    private final Vector3 tmp = new Vector3();

    private int step;
    private float time, stepTime;
    private float doneFlash;     // > 0 while "Nice!" is showing before the next step
    private float goBanner;      // > 0 while "GO!" is showing after the tour
    private final Vector2 startPos = new Vector2();
    private int ordersAtServeStart;
    private float skipX, skipY, skipW, skipH;

    public TourOverlay(GameManager game, Viewport gameViewport, String username, boolean runTour) {
        this.game = game;
        this.gameViewport = gameViewport;
        this.username = username;
        font = new BitmapFont(Gdx.files.internal("fonts/Text_font_white.fnt"));
        font.setUseIntegerPositions(true);
        step = runTour ? MOVE : DONE;
        GameUI.timerPaused = runTour;
        startPos.set(game.getPlayerManager().getPosition());
    }

    public boolean isRunning() {
        return step < DONE;
    }

    // ------------------------------------------------------------------ logic

    public void update(float delta) {
        time += delta;
        if (goBanner > 0) goBanner -= delta;
        if (!isRunning()) return;
        stepTime += delta;

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) || clickedSkip()) {
            finish(false);
            return;
        }
        if (doneFlash > 0) {
            doneFlash -= delta;
            if (doneFlash <= 0) nextStep();
            return;
        }

        Player p = game.getPlayerManager();
        FoodType held = p.getHeldItem();
        switch (step) {
            case MOVE:
                if (p.getPosition().dst(startPos) > 2.5f && stepTime > 1f) complete();
                break;
            case GRAB:
                if (held != null && (held == FoodType.pickle || find(FoodType.pickle) == null)) complete();
                break;
            case CHOP:
                if (held != null && held.name().startsWith("chopped_")) complete();
                break;
            case SERVE:
                if (Orders.totalOrders < ordersAtServeStart) complete();
                break;
            case COOK:
            case BOOK:
                if (stepTime > 0.4f && (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))) {
                    if (step == BOOK) finish(true); else nextStep();
                }
                break;
        }
    }

    private void complete() {
        doneFlash = 0.9f;
    }

    private void nextStep() {
        step++;
        stepTime = 0;
        if (step == SERVE) ordersAtServeStart = Orders.totalOrders;
    }

    private void finish(boolean completed) {
        step = DONE;
        GameUI.timerPaused = false;
        GameData.setSeenTour(username);
        goBanner = completed ? 1.6f : 1.0f;
    }

    private boolean clickedSkip() {
        if (!Gdx.input.justTouched() || skipW == 0) return false;
        float x = Gdx.input.getX(), y = Gdx.graphics.getHeight() - Gdx.input.getY();
        return x >= skipX && x <= skipX + skipW && y >= skipY && y <= skipY + skipH;
    }

    /** The station the current step is about, or null. */
    private Station target() {
        switch (step) {
            case GRAB: {
                Station s = find(FoodType.pickle);
                return s != null ? s : nearest(FoodSource.class);
            }
            case CHOP: return nearest(ChoppingBoard.class);
            case SERVE: return nearest(Counter.class);
            case COOK: return nearest(Stove.class);
            default: return null;
        }
    }

    private Station find(FoodType source) {
        for (Station s : game.getEntityList().getStations()) {
            if (s instanceof FoodSource && s.toString().equals("FoodSource(" + source + ")")) return s;
        }
        return null;
    }

    private Station nearest(Class<? extends Station> type) {
        Vector2 p = game.getPlayerManager().getPosition();
        float px = p.x * com.libgdx.undercooked.utils.Constants.PPM, py = p.y * com.libgdx.undercooked.utils.Constants.PPM;
        Station best = null;
        float bestD = Float.MAX_VALUE;
        for (Station s : game.getEntityList().getStations()) {
            if (s.getClass() != type) continue;
            float d = Vector2.dst2(px, py, s.getX(), s.getY());
            if (d < bestD) { bestD = d; best = s; }
        }
        return best;
    }

    // ------------------------------------------------------------------ drawing

    public void render() {
        float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
        screenCamera.setToOrtho(false, w, h);
        shapes.setProjectionMatrix(screenCamera.combined);
        batch.setProjectionMatrix(screenCamera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (!isRunning()) {
            drawControlsHint();
            if (goBanner > 0) drawGoBanner(w, h);
            skipW = 0;
            return;
        }

        Station t = target();
        if (t != null && doneFlash <= 0) drawPointer(t.getX() + 16, t.getY() + 40, w, h);
        if (step == BOOK) drawBookPointer(h);
        drawPanel(w);
    }

    private void drawPanel(float w) {
        String title, hint;
        String[] keys;
        switch (step) {
            case MOVE:
                title = "Welcome to the kitchen! Walk around.";
                hint = "Hold SHIFT to run.";
                keys = new String[]{"W", "A", "S", "D"};
                break;
            case GRAB:
                title = "Grab a pickle from its crate.";
                hint = "Walk up to the glowing arrow, face it and press SPACE.";
                keys = new String[]{"SPACE"};
                break;
            case CHOP:
                title = "Chop it on the cutting board.";
                hint = "Face the board and press SPACE, then wait for the knife to finish.";
                keys = new String[]{"SPACE"};
                break;
            case SERVE:
                title = "Serve the order at the counter.";
                hint = "Orders are listed top right. Chopped Pickle is one of them!";
                keys = new String[]{"SPACE"};
                break;
            case COOK:
                title = "Some dishes need cooking.";
                hint = "Stoves cook meat and fish, the rice cooker makes rice. Mix them on a plate for meals.";
                keys = new String[]{"ENTER"};
                break;
            default:
                title = "Forgot a recipe? Open the recipe book.";
                hint = "Serve every order before the 3:00 clock runs out. Good luck, chef!";
                keys = new String[]{"ENTER"};
                break;
        }
        boolean nice = doneFlash > 0;

        float pw = Math.min(820, w - 40), ph = 128, px = (w - pw) / 2f, py = 18;
        beginShapes(ShapeRenderer.ShapeType.Filled);
        roundRect(px, py, pw, ph, 14, PANEL);
        // Progress dots.
        for (int i = 0; i < STEP_COUNT; i++) {
            shapes.setColor(i < step || (i == step && nice) ? GOOD : (i == step ? ACCENT : new Color(1, 1, 1, 0.2f)));
            shapes.circle(px + 24 + i * 16, py + ph - 20, 5, 12);
        }
        shapes.end();

        // Key caps on the right.
        float kx = px + pw - 24;
        font.getData().setScale(0.8f);
        for (int i = keys.length - 1; i >= 0; i--) {
            layout.setText(font, keys[i]);
            float kw = Math.max(34, layout.width + 18);
            kx -= kw;
            drawKeyCap(keys[i], kx, py + ph / 2f - 18, kw, 36);
            kx -= 6;
        }

        batch.begin();
        font.getData().setScale(0.65f);
        font.setColor(0.8f, 0.78f, 0.72f, 1f);
        font.draw(batch, "KITCHEN TOUR  " + Math.min(step + 1, STEP_COUNT) + " / " + STEP_COUNT, px + 24 + STEP_COUNT * 16, py + ph - 12);

        float textW = kx - px - 40;
        font.getData().setScale(1.1f);
        font.setColor(nice ? GOOD : CREAM);
        font.draw(batch, nice ? "Nice!" : title, px + 24, py + ph - 38, textW, com.badlogic.gdx.utils.Align.left, false);
        font.getData().setScale(0.72f);
        font.setColor(0.85f, 0.82f, 0.75f, 1f);
        if (!nice) font.draw(batch, hint, px + 24, py + 50, textW, com.badlogic.gdx.utils.Align.left, true);

        String skip = "Skip tour (TAB)";
        layout.setText(font, skip);
        skipW = layout.width + 16;
        skipH = 24;
        skipX = px + pw - skipW - 12;
        skipY = py + ph - 32;
        font.setColor(0.75f, 0.72f, 0.68f, 1f);
        font.draw(batch, skip, skipX + 8, skipY + 19);
        batch.end();
    }

    private void drawKeyCap(String label, float x, float y, float w, float h) {
        float press = MathUtils.sin(time * 6f) > 0.6f ? 3 : 0;
        beginShapes(ShapeRenderer.ShapeType.Filled);
        roundRect(x, y - 4, w, h, 6, new Color(0.45f, 0.38f, 0.33f, 1f));
        roundRect(x, y - press, w, h, 6, new Color(0.96f, 0.90f, 0.78f, 1f));
        shapes.end();
        batch.begin();
        font.setColor(0.25f, 0.18f, 0.16f, 1f);
        layout.setText(font, label);
        font.draw(batch, layout, x + (w - layout.width) / 2f, y - press + h / 2f + layout.height / 2f);
        batch.end();
    }

    /** Bouncing arrow over a station, or an arrow at the screen edge if the station is off screen. */
    private void drawPointer(float worldX, float worldY, float w, float h) {
        tmp.set(worldX, worldY, 0);
        gameViewport.project(tmp);
        float sx = tmp.x, sy = tmp.y; // Viewport.project already returns y-up screen coordinates.
        float margin = 50;
        boolean onScreen = sx > margin && sx < w - margin && sy > 160 && sy < h - margin;

        beginShapes(ShapeRenderer.ShapeType.Filled);
        if (onScreen) {
            float bob = 8 * MathUtils.sin(time * 6f);
            float pulse = (time * 1.3f) % 1f;
            shapes.end();
            beginShapes(ShapeRenderer.ShapeType.Line);
            shapes.setColor(ACCENT.r, ACCENT.g, ACCENT.b, 1f - pulse);
            shapes.ellipse(sx - 26 - pulse * 14, sy - 46 - pulse * 6, 52 + pulse * 28, 22 + pulse * 12);
            shapes.end();
            beginShapes(ShapeRenderer.ShapeType.Filled);
            float ay = sy + 48 + bob;
            shapes.setColor(0.1f, 0.06f, 0.08f, 1f);
            shapes.triangle(sx - 20, ay + 2, sx + 20, ay + 2, sx, ay - 30);
            shapes.rect(sx - 9, ay, 18, 24);
            shapes.setColor(ACCENT);
            shapes.triangle(sx - 15, ay + 4, sx + 15, ay + 4, sx, ay - 24);
            shapes.rect(sx - 5, ay + 2, 10, 20);
        } else {
            float cx = w / 2f, cy = h / 2f;
            float ang = MathUtils.atan2(sy - cy, sx - cx);
            float ex = MathUtils.clamp(sx, margin, w - margin), ey = MathUtils.clamp(sy, 170, h - margin);
            float c = MathUtils.cos(ang), s = MathUtils.sin(ang);
            float len = 26, wid = 16;
            shapes.setColor(0.1f, 0.06f, 0.08f, 1f);
            shapes.circle(ex, ey, 28, 24);
            shapes.setColor(ACCENT);
            shapes.triangle(ex + c * len, ey + s * len,
                ex - c * 10 - s * wid, ey - s * 10 + c * wid,
                ex - c * 10 + s * wid, ey - s * 10 - c * wid);
        }
        shapes.end();
    }

    /** Points at the recipe book button in the top-left corner of the game UI. */
    private void drawBookPointer(float h) {
        float bob = 8 * MathUtils.sin(time * 6f);
        float tx = 97, ty = h - 44; // centre of the info (book) button
        float ax = tx + 60 + bob;
        beginShapes(ShapeRenderer.ShapeType.Line);
        float pulse = (time * 1.3f) % 1f;
        shapes.setColor(ACCENT.r, ACCENT.g, ACCENT.b, 1f - pulse);
        shapes.circle(tx, ty, 30 + pulse * 16, 32);
        shapes.end();
        beginShapes(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.1f, 0.06f, 0.08f, 1f);
        shapes.triangle(ax - 2, ty + 20, ax - 2, ty - 20, ax - 32, ty);
        shapes.rect(ax - 2, ty - 9, 26, 18);
        shapes.setColor(ACCENT);
        shapes.triangle(ax - 4, ty + 15, ax - 4, ty - 15, ax - 27, ty);
        shapes.rect(ax - 4, ty - 5, 24, 10);
        shapes.end();
    }

    private void drawControlsHint() {
        String text = "WASD move   SHIFT run   SPACE use   ESC pause";
        font.getData().setScale(0.62f);
        layout.setText(font, text);
        float pw = layout.width + 24, ph = 28, px = 16, py = 14;
        beginShapes(ShapeRenderer.ShapeType.Filled);
        roundRect(px, py, pw, ph, 8, new Color(0.10f, 0.08f, 0.12f, 0.55f));
        shapes.end();
        batch.begin();
        font.setColor(0.92f, 0.9f, 0.85f, 0.9f);
        font.draw(batch, layout, px + 12, py + ph - 8);
        batch.end();
    }

    private void drawGoBanner(float w, float h) {
        float a = Math.min(1f, goBanner * 2f);
        batch.begin();
        font.getData().setScale(3f + (1.6f - goBanner) * 0.4f);
        font.setColor(ACCENT.r, ACCENT.g, ACCENT.b, a);
        layout.setText(font, "GO!");
        font.draw(batch, layout, (w - layout.width) / 2f, h / 2f + layout.height / 2f + 40);
        batch.end();
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

    @Override
    public void dispose() {
        GameUI.timerPaused = false;
        batch.dispose();
        shapes.dispose();
        font.dispose();
    }
}
