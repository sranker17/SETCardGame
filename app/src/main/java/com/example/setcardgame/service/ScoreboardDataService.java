package com.example.setcardgame.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.setcardgame.config.RequestQueueSingleton;
import com.example.setcardgame.model.UrlConstants;
import com.example.setcardgame.model.scoreboard.Scoreboard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardDataService {

    public static final String SCOREBOARD_URL = UrlConstants.URL + "scoreboard";
    private static final String PLAYER_ID = "playerId";
    private static final String DIFFICULTY = "difficulty";
    private static final String SCORE = "score";
    private static final String TIME = "time";
    Context context;

    public ScoreboardDataService(Context context) {
        this.context = context;
    }

    public void getPlayerScores(boolean usesUsername, String username, ScoreboardResponseListener scoreboardResponseListener) {
        List<Scoreboard> scores = new ArrayList<>();
        String url;
        if (usesUsername) {
            url = SCOREBOARD_URL + "/player/" + username;
        } else {
            url = SCOREBOARD_URL + "/top";
        }

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {

                    try {
                        for (int i = 0; response.length() > i; i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Scoreboard score = new Scoreboard(
                                    jsonObject.getString(PLAYER_ID),
                                    jsonObject.getString(DIFFICULTY),
                                    jsonObject.getInt(SCORE),
                                    jsonObject.getInt(TIME));
                            scores.add(score);
                        }
                        scoreboardResponseListener.onResponse(scores);

                    } catch (JSONException e) {
                        e.getMessage();
                    }
                }, error -> scoreboardResponseListener.onError("Did not get score"));

        RequestQueueSingleton.getInstance(context).addToRequestQueue(arrayRequest);
    }

    public void addScore(Scoreboard scoreboardModel, ScoreAddedResponseListener scoreAddedResponseListener) {
        JSONObject postObj = new JSONObject();
        try {
            postObj.put(PLAYER_ID, scoreboardModel.getPlayerId());
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

    public interface ScoreboardResponseListener {
        void onError(String message);

        void onResponse(List<Scoreboard> scoreboardModels);
    }

    public interface ScoreAddedResponseListener {
        void onError(String message);

        void onResponse(JSONObject scoreboardModels);
    }
}
