package com.example.setcardgame.listener;

import com.example.setcardgame.model.Error;

import org.json.JSONObject;

public interface ScoreAddedResponseListener extends BaseListener {
    void onError(Error message);

    void onResponse(JSONObject scoreboardModels);
}
