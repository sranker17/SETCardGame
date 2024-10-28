package com.example.setcardgame.service;

import static com.example.setcardgame.service.ErrorHandlerService.handleErrorResponse;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.setcardgame.config.RequestQueueSingleton;
import com.example.setcardgame.exception.EncryptException;
import com.example.setcardgame.exception.JSONParsingException;
import com.example.setcardgame.exception.RefreshException;
import com.example.setcardgame.listener.AuthResponseListener;
import com.example.setcardgame.listener.ServerStatusListener;
import com.example.setcardgame.model.Error;
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
            Log.e(AUTH_SERVICE, e.toString());
            throw new JSONParsingException(e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AUTH_URL + "/login", postObj,
                authResponseListener::onResponse, error -> handleErrorResponse(error, authResponseListener, context)) {
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
            Log.e(AUTH_SERVICE, e.toString());
            throw new JSONParsingException(e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, AUTH_URL + "/signup", postObj,
                authResponseListener::onResponse, error -> handleErrorResponse(error, authResponseListener, context)) {
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
        SharedPreferences sp = getEncryptedSharedPreferences();
        long expiresIn = sp.getLong(EXPIRES_IN, 0);
        long tokenGenerationDate = sp.getLong(TOKEN_GENERATION_DATE, 0);
        long currentTime = System.currentTimeMillis();
        return currentTime > tokenGenerationDate + expiresIn;
    }

    public void refreshToken(ServerStatusListener serverStatusListener) {
        SharedPreferences sp = getEncryptedSharedPreferences();
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
                serverStatusListener.onServerStatusChecked(false);
                Log.e(AUTH_SERVICE, "Error response in refreshToken: " + errorResponse.toString());
            }

            @Override
            public void onResponse(JSONObject loginResponse) {
                Log.i(AUTH_SERVICE, loginResponse.toString());
                try {
                    String returnedToken = loginResponse.getString(TOKEN_TAG);
                    long expiresIn = loginResponse.getLong(EXPIRES_IN);

                    SharedPreferences sp = getEncryptedSharedPreferences();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(TOKEN_TAG, returnedToken);
                    editor.putLong(TOKEN_GENERATION_DATE, System.currentTimeMillis());
                    editor.putLong(EXPIRES_IN, expiresIn);
                    editor.apply();

                    serverStatusListener.onServerStatusChecked(true);
                    Log.i(AUTH_SERVICE, "Token stored successfully: " + returnedToken);
                } catch (JSONException e) {
                    Log.e(AUTH_SERVICE, "Error parsing login response in refreshToken", e);
                    throw new JSONParsingException(e.getMessage());
                }
            }
        });
    }

    public SharedPreferences getEncryptedSharedPreferences() {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return EncryptedSharedPreferences.create(
                    context,
                    AUTH,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e(AUTH_SERVICE, "getEncryptedSharedPreferences: " + e);
            throw new EncryptException(e.getMessage());
        }
    }
}
