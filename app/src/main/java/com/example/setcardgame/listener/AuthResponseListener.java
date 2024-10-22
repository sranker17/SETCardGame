package com.example.setcardgame.listener;

import com.example.setcardgame.model.Error;

import org.json.JSONObject;

public interface AuthResponseListener extends BaseListener {
    void onError(Error errorResponse);
    void onResponse(JSONObject loginResponse);
}