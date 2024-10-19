package com.example.setcardgame.listener;

import com.example.setcardgame.model.Error;

import org.json.JSONObject;

public interface LoginResponseListener {
    void onError(Error errorResponse);
    void onResponse(JSONObject loginResponse);
}