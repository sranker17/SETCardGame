package com.example.setcardgame.listener;

import org.json.JSONObject;

public interface ScoreAddedResponseListener {
    void onError(String message);

    void onResponse(JSONObject scoreboardModels);
}
