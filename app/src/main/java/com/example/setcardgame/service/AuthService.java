package com.example.setcardgame.service;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.setcardgame.R;
import com.example.setcardgame.config.RequestQueueSingleton;
import com.example.setcardgame.exception.RefreshException;
import com.example.setcardgame.listener.AuthResponseListener;
import com.example.setcardgame.model.Error;
import com.example.setcardgame.model.ServerStatus;
import com.example.setcardgame.model.UrlConstants;
import com.example.setcardgame.model.auth.AuthUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static final String AUTH = "auth";
    private static final String AUTH_URL = UrlConstants.URL + AUTH;
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String AUTH_SERVICE = "AUTH_SERVICE";
    private static final String TOKEN_TAG = "token";
    private static final String TOKEN_GENERATION_DATE = "tokenGenerationDate";
    private static final String EXPIRES_IN = "expiresIn";
    private static final String SERVER_STATUS = "SERVER_STATUS";
    private final Context context;

    public AuthService(Context context) {
        this.context = context;
    }

    public void login(AuthUser authUser, AuthResponseListener authResponseListener) {
        JSONObject postObj = new JSONObject();
        try {
            postObj.put(USERNAME, authUser.getUsername());
            postObj.put(PASSWORD, authUser.getPassword());
        } catch (JSONException e) {
            Log.e(AUTH_SERVICE, e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AUTH_URL + "/login", postObj,
                authResponseListener::onResponse, error -> handleErrorResponse(error, authResponseListener)) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }

    public void register(AuthUser authUser, AuthResponseListener authResponseListener) {
        JSONObject postObj = new JSONObject();
        try {
            postObj.put(USERNAME, authUser.getUsername());
            postObj.put(PASSWORD, authUser.getPassword());

        } catch (JSONException e) {
            Log.e(AUTH_SERVICE, e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AUTH_URL + "/signup", postObj,
                authResponseListener::onResponse, error -> handleErrorResponse(error, authResponseListener)) {
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

    public boolean isTokenExpired() {
        SharedPreferences sp = context.getSharedPreferences(AUTH, MODE_PRIVATE);
        long expiresIn = sp.getLong(EXPIRES_IN, 0);
        long tokenGenerationDate = sp.getLong(TOKEN_GENERATION_DATE, 0);
        long currentTime = System.currentTimeMillis();
        return currentTime > tokenGenerationDate + expiresIn;
    }

    public void refreshToken() {
        SharedPreferences sp = context.getSharedPreferences(AUTH, MODE_PRIVATE);
        String username = sp.getString(USERNAME, null);
        String password = sp.getString(PASSWORD, null);
        if (username == null || password == null) {
            Log.e(AUTH_SERVICE, "No username or password found");
            throw new RefreshException("No username or password found");
        }

        AuthUser authUser = new AuthUser(username, password);
        login(authUser, new AuthResponseListener() {
            @Override
            public void onError(Error errorResponse) {
                SharedPreferences sp = context.getSharedPreferences(AUTH, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(SERVER_STATUS, ServerStatus.OFFLINE.name());
                editor.apply();
                Log.e(AUTH_SERVICE, errorResponse.toString());
            }

            @Override
            public void onResponse(JSONObject loginResponse) {
                Log.i(AUTH_SERVICE, loginResponse.toString());
                try {
                    String returnedToken = loginResponse.getString(TOKEN_TAG);
                    long expiresIn = loginResponse.getLong(EXPIRES_IN);

                    SharedPreferences sp = context.getSharedPreferences(AUTH, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(TOKEN_TAG, returnedToken);
                    editor.putLong(TOKEN_GENERATION_DATE, System.currentTimeMillis());
                    editor.putLong(EXPIRES_IN, expiresIn);
                    editor.putString(SERVER_STATUS, ServerStatus.ONLINE.name());
                    editor.apply();

                    Log.i(AUTH_SERVICE, "Token stored successfully: " + returnedToken);
                } catch (JSONException e) {
                    Log.e(AUTH_SERVICE, "Error parsing login response", e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void handleErrorResponse(VolleyError error, AuthResponseListener authResponseListener) {
        Error errorResponse = new Error();
        if (error.networkResponse != null && error.networkResponse.statusCode == 503) {
            errorResponse.setTitle(context.getString(R.string.serverUnavailable));
            errorResponse.setDetail(context.getString(R.string.serverUnavailable));
            errorResponse.setDescription(context.getString(R.string.serverUnavailable));
            errorResponse.setStatus(503);
        }
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String errorData = new String(error.networkResponse.data);
                Log.e(AUTH_SERVICE, errorData);
                JSONObject errorJson = new JSONObject(errorData);
                String title = errorJson.optString("title", "Error");
                int statusCode = errorJson.getInt("status");
                String detail = errorJson.optString("detail", "No details provided");
                String instance = errorJson.optString("instance", "");
                String description = errorJson.optString("description", "No description provided");
                errorResponse = new Error(title, statusCode, detail, instance, description);

            } catch (JSONException e) {
                Log.e(AUTH_SERVICE, "Error parsing error response", e);
            }
        }

        Log.e(AUTH_SERVICE, errorResponse.toString());
        authResponseListener.onError(errorResponse);
    }
}
