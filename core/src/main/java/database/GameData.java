package database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * Saves profiles, unlocked levels and personal best scores on the player's own device using libGDX
 * {@link Preferences}: browser storage on the web, a small file in the user folder on desktop.
 * Replaces the old MySQL/XAMPP database, so the game no longer needs a database server to run.
 * The worldwide leaderboard lives online instead, see {@link Leaderboard}.
 */
public final class GameData {
    private static final String PREFS_NAME = "undercooked";
    private static final String KEY_PROFILES = "profiles";
    private static final String KEY_LAST_PLAYED = "lastPlayed";
    private static final String PROFILE_SEPARATOR = "\n";
    public static final int MAX_NAME_LENGTH = 16;

    private static Preferences prefs;

    private GameData() {}

    private static Preferences prefs() {
        if (prefs == null) {
            prefs = Gdx.app.getPreferences(PREFS_NAME);
        }
        return prefs;
    }

    /** Keeps only letters, digits, spaces and _ . - and trims the name to 16 characters. */
    public static String cleanName(String name) {
        if (name == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length() && sb.length() < MAX_NAME_LENGTH; i++) {
            char c = name.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                || c == ' ' || c == '_' || c == '.' || c == '-') {
                sb.append(c);
            }
        }
        return sb.toString().trim();
    }

    /** Signs in to a profile, creating it if it doesn't exist yet. Returns false if the name is empty. */
    public static boolean userSignIn(String userName) {
        String name = cleanName(userName);
        if (name.isEmpty()) return false;

        List<String> profiles = getAccounts();
        if (!profiles.contains(name)) {
            profiles.add(name);
            prefs().putString(KEY_PROFILES, join(profiles));
            prefs().putInteger(levelKey(name), 1);
            prefs().putString(playerIdKey(name), newPlayerId());
        }
        prefs().putString(KEY_LAST_PLAYED, name);
        prefs().flush();
        return true;
    }

    /** The profile that played last, or an empty string if nobody has played on this device yet. */
    public static String userDefaultSignIn() {
        return prefs().getString(KEY_LAST_PLAYED, "");
    }

    public static List<String> getAccounts() {
        List<String> result = new ArrayList<>();
        String stored = prefs().getString(KEY_PROFILES, "");
        if (!stored.isEmpty()) {
            for (String name : stored.split(PROFILE_SEPARATOR)) {
                if (!name.isEmpty()) result.add(name);
            }
        }
        return result;
    }

    public static UserInfo getInfo(String username) {
        String name = cleanName(username);
        return new UserInfo(findLevel(name), name);
    }

    public static int findLevel(String username) {
        if (username == null || username.isEmpty()) return 0;
        return prefs().getInteger(levelKey(username), 0);
    }

    /** Beating a map unlocks the next one. */
    public static void levelUp(String username, int mapId) {
        if (username == null || username.isEmpty()) return;
        int level = findLevel(username);
        if (mapId + 1 > level) {
            prefs().putInteger(levelKey(username), mapId + 1);
            prefs().flush();
        }
    }

    /** Saves the score as a personal best if it beats the old one, and sends it to the online leaderboard. */
    public static void insertScore(String username, int mapId, int score) {
        if (username == null || username.isEmpty()) return;
        String key = bestKey(username, mapId);
        if (score > prefs().getInteger(key, -1)) {
            prefs().putInteger(key, score);
            prefs().flush();
        }
        Leaderboard.submitScore(getPlayerId(username), username, mapId, score);
    }

    /** Whether this profile has finished or skipped the first-time kitchen tour. */
    public static boolean hasSeenTour(String username) {
        return prefs().getBoolean(tourKey(username), false);
    }

    public static void setSeenTour(String username) {
        prefs().putBoolean(tourKey(username), true);
        prefs().flush();
    }

    private static String tourKey(String name) { return "tourDone." + name; }

    public static int getPersonalBest(String username, int mapId) {
        return prefs().getInteger(bestKey(username, mapId), 0);
    }

    /**
     * A random id that identifies this profile on the online leaderboard, so two people who both call
     * themselves "kwiruu" don't overwrite each other's scores. It is never shown to other players.
     */
    public static String getPlayerId(String username) {
        String key = playerIdKey(username);
        String id = prefs().getString(key, "");
        if (id.isEmpty()) {
            id = newPlayerId();
            prefs().putString(key, id);
            prefs().flush();
        }
        return id;
    }

    private static String levelKey(String name) { return "level." + name; }
    private static String playerIdKey(String name) { return "playerId." + name; }
    private static String bestKey(String name, int mapId) { return "best." + name + "." + mapId; }

    private static String join(List<String> names) {
        Array<String> arr = new Array<>();
        for (String n : names) arr.add(n);
        return arr.toString(PROFILE_SEPARATOR);
    }

    /** Random version-4 UUID. Built by hand because java.util.UUID isn't available in the browser build. */
    private static String newPlayerId() {
        final String hex = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(36);
        for (int i = 0; i < 36; i++) {
            if (i == 8 || i == 13 || i == 18 || i == 23) {
                sb.append('-');
            } else if (i == 14) {
                sb.append('4');
            } else if (i == 19) {
                sb.append(hex.charAt(8 + MathUtils.random(3)));
            } else {
                sb.append(hex.charAt(MathUtils.random(15)));
            }
        }
        return sb.toString();
    }
}
