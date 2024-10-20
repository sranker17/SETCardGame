package com.example.setcardgame.service;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.setcardgame.config.RequestQueueSingleton;
import com.example.setcardgame.listener.ScoreAddedResponseListener;
import com.example.setcardgame.listener.ScoreboardResponseListener;
import com.example.setcardgame.model.UrlConstants;
import com.example.setcardgame.model.scoreboard.Scoreboard;
import com.example.setcardgame.model.scoreboard.TopScores;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ScoreboardService {
    private AuthService authService;
    private static final String SCOREBOARD_URL = UrlConstants.URL + "scoreboard";
    private static final String DIFFICULTY = "difficulty";
    private static final String SCORE = "score";
    private static final String TIME = "time";
    private static final String USERNAME = "username";
    private static final String USER_SCORE = "userScore";
    private final Context context;

    public void getPlayerScores(boolean usesUsername, ScoreboardResponseListener scoreboardResponseListener) {
        String url;
        if (usesUsername) {
            url = SCOREBOARD_URL + "/user";
        } else {
            url = SCOREBOARD_URL + "/top";
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        TopScores topScores = new TopScores();
                        topScores.setEasyScores(new ArrayList<>());
                        topScores.setNormalScores(new ArrayList<>());
                        Log.i("Response", String.valueOf(response));

                        JSONArray easyScores = response.getJSONArray("easyScores");
                        JSONArray normalScores = response.getJSONArray("normalScores");

                        for (int j = 0; j < easyScores.length(); j++) {
                            JSONObject easyScore = easyScores.getJSONObject(j);
                            Scoreboard score = new Scoreboard(
                                    easyScore.getString(USERNAME),
                                    easyScore.getString(DIFFICULTY),
                                    easyScore.getInt(SCORE),
                                    easyScore.getInt(TIME),
                                    easyScore.getBoolean(USER_SCORE));
                            topScores.getEasyScores().add(score);
                        }

                        for (int j = 0; j < normalScores.length(); j++) {
                            JSONObject normalScore = normalScores.getJSONObject(j);
                            Scoreboard score = new Scoreboard(
                                    normalScore.getString(USERNAME),
                                    normalScore.getString(DIFFICULTY),
                                    normalScore.getInt(SCORE),
                                    normalScore.getInt(TIME),
                                    normalScore.getBoolean(USER_SCORE));
                            topScores.getNormalScores().add(score);
                        }

                        scoreboardResponseListener.onResponse(topScores);
                    } catch (JSONException e) {
                        scoreboardResponseListener.onError("Failed to parse scores");
                    }
                }, error -> scoreboardResponseListener.onError("Did not get score")) {
            @Override
            public Map<String, String> getHeaders() {
                SharedPreferences sp = context.getSharedPreferences("auth", MODE_PRIVATE);
                String token = sp.getString("token", null);
                if (token == null) {
                    Log.d("Scoreboard", "Refreshing token");
                    token = authService.refreshToken();
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };

        RequestQueueSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void addScore(Scoreboard scoreboardModel, ScoreAddedResponseListener scoreAddedResponseListener) {
        JSONObject postObj = new JSONObject();
        try {
            postObj.put(USERNAME, scoreboardModel.getUsername());
            postObj.put(DIFFICULTY, scoreboardModel.getDifficulty().toString());
            postObj.put(SCORE, scoreboardModel.getScore());
            postObj.put(TIME, scoreboardModel.getTime());

        } catch (JSONException e) {
            e.getMessage();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SCOREBOARD_URL, postObj,
                scoreAddedResponseListener::onResponse, error -> scoreAddedResponseListener.onError(error.getMessage())) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }
}
