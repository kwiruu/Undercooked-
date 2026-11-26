package database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * The worldwide leaderboard, stored in Supabase (table public.undercooked_scores).
 *
 * The game never touches the table directly. It calls two database functions over HTTP:
 *  - undercooked_submit_score: saves a score, keeping only each player's best per map, and rejects
 *    impossible values (score outside 0-5000, bad map id, bad name).
 *  - undercooked_top_scores: returns the top 10 names and scores for a map.
 *
 * The key below is Supabase's publishable key. It is meant to be public: on its own it can only call
 * those two functions, because row level security blocks everything else in the project.
 *
 * Works the same on desktop and in the browser. All callbacks run on the render thread.
 */
public final class Leaderboard {
    private static final String RPC_URL = "https://bhdijxchrjqpseudzfxq.supabase.co/rest/v1/rpc/";
    private static final String PUBLISHABLE_KEY = "sb_publishable_Obv4PDHu5T63o979SffVAQ_WVTSkGKd";
    private static final int TIMEOUT_MS = 10000;

    public interface Callback {
        void onResult(List<HighScore> scores);
        void onError(String message);
    }

    private Leaderboard() {}

    /** Sends a score in the background. Failures (e.g. offline) are only logged; the local best is already saved. */
    public static void submitScore(String playerId, String name, int mapId, int score) {
        String body = "{\"p_player_id\":" + quote(playerId)
            + ",\"p_name\":" + quote(name)
            + ",\"p_map_id\":" + mapId
            + ",\"p_score\":" + score + "}";
        post("undercooked_submit_score", body, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String result = httpResponse.getResultAsString();
                if (status >= 200 && status < 300) {
                    Gdx.app.log("Leaderboard", "Score submitted, best is now " + result);
                } else {
                    Gdx.app.error("Leaderboard", "Score rejected (" + status + "): " + result);
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("Leaderboard", "Could not submit score: " + t);
            }

            @Override
            public void cancelled() {}
        });
    }

    /** Fetches the top 10 for a map and hands the result to the callback on the render thread. */
    public static void fetchTopScores(int mapId, final Callback callback) {
        post("undercooked_top_scores", "{\"p_map_id\":" + mapId + "}", new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final int status = httpResponse.getStatus().getStatusCode();
                final String result = httpResponse.getResultAsString();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (status < 200 || status >= 300) {
                            callback.onError("Leaderboard unavailable (" + status + ")");
                            return;
                        }
                        try {
                            List<HighScore> scores = new ArrayList<>();
                            JsonValue root = new JsonReader().parse(result);
                            for (JsonValue row = root.child; row != null; row = row.next) {
                                scores.add(new HighScore(row.getString("display_name"), row.getInt("best_score")));
                            }
                            callback.onResult(scores);
                        } catch (Exception e) {
                            callback.onError("Couldn't read the leaderboard");
                        }
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError("Couldn't reach the leaderboard. Are you online?");
                    }
                });
            }

            @Override
            public void cancelled() {}
        });
    }

    private static void post(String function, String body, Net.HttpResponseListener listener) {
        Net.HttpRequest request = new HttpRequestBuilder()
            .newRequest()
            .method(Net.HttpMethods.POST)
            .url(RPC_URL + function)
            .header("apikey", PUBLISHABLE_KEY)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .timeout(TIMEOUT_MS)
            .content(body)
            .build();
        Gdx.net.sendHttpRequest(request, listener);
    }

    /** JSON string literal with the characters that need escaping escaped. */
    private static String quote(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' || c == '\\') {
                sb.append('\\').append(c);
            } else if (c < 0x20) {
                sb.append(' ');
            } else {
                sb.append(c);
            }
        }
        return sb.append('"').toString();
    }
}
