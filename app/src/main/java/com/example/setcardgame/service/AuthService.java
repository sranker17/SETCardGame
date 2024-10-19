package com.example.setcardgame.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.setcardgame.config.RequestQueueSingleton;
import com.example.setcardgame.listener.LoginResponseListener;
import com.example.setcardgame.model.Error;
import com.example.setcardgame.model.UrlConstants;
import com.example.setcardgame.model.auth.AuthUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthService {
    private static final String AUTH_URL = UrlConstants.URL + "auth";
    private static final String LOGIN = "/login";
    private static final String REGISTER = "/register";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private final Context context;

    public void login(AuthUser authUser, LoginResponseListener loginResponseListener) {
        JSONObject postObj = new JSONObject();
        try {
            postObj.put(USERNAME, authUser.getUsername());
            postObj.put(PASSWORD, authUser.getPassword());
        } catch (JSONException e) {
            e.getMessage();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AUTH_URL + LOGIN, postObj,
                loginResponseListener::onResponse, error -> handleErrorResponse(error, loginResponseListener)) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }

    //TODO
    public void register(AuthUser authUser, LoginResponseListener loginResponseListener) {
        JSONObject postObj = new JSONObject();
        try {
            postObj.put(USERNAME, authUser.getUsername());
            postObj.put(PASSWORD, authUser.getPassword());

        } catch (JSONException e) {
            e.getMessage();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AUTH_URL + REGISTER, postObj,
                loginResponseListener::onResponse, error -> handleErrorResponse(error, loginResponseListener)) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }

    public void logout() {
        //TODO create logout button
    }

    public void handleToken() {
        //TODO check if token is still valid, if not login again
    }

    private void handleErrorResponse(VolleyError error, LoginResponseListener loginResponseListener) {
        Error errorResponse = new Error();
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String errorData = new String(error.networkResponse.data);
                JSONObject errorJson = new JSONObject(errorData);
                String title = errorJson.optString("title", "Error");
                int statusCode = errorJson.getInt("status");
                String detail = errorJson.optString("detail", "No details provided");
                String instance = errorJson.optString("instance", "");
                String description = errorJson.optString("description", "No description provided");
                errorResponse = new Error(title, statusCode, detail, instance, description);

            } catch (JSONException e) {
                Log.e("AuthService", "Error parsing error response", e);
            }
        }

        Log.e("AuthService", errorResponse.toString());
        loginResponseListener.onError(errorResponse);
    }
}
