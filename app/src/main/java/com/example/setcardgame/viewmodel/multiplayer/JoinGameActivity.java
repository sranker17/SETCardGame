package com.example.setcardgame.viewmodel.multiplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.setcardgame.R;
import com.example.setcardgame.viewmodel.BaseActivity;
import com.example.setcardgame.config.PropertyReader;
import com.example.setcardgame.config.WebSocketClient;
import com.example.setcardgame.exception.JsonParsingException;
import com.example.setcardgame.model.MultiplayerGame;
import com.example.setcardgame.service.AuthService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Properties;

import io.reactivex.disposables.Disposable;

public class JoinGameActivity extends BaseActivity {
    private final AuthService authService = new AuthService(JoinGameActivity.this);
    private EditText connectionCodeET;
    private MultiplayerGame game;
    private String foundUsername;
    private static final String TAG = "joinGame";
    private static final String GAME_ID = "gameId";
    private static final String PLAYER_ID = "playerId";
    private static final String USERNAME = "username";
    private static final String TOKEN = "token";
    private static final String WSS = "wss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        setupToolbar(R.id.toolbar, R.string.joinGameText);

        connectionCodeET = findViewById(R.id.connectionCodeET);

        SharedPreferences sp = authService.getEncryptedSharedPreferences();
        foundUsername = sp.getString(USERNAME, null);

        if (foundUsername == null) {
            Log.e(TAG, "Username not found");
            return;
        }
        authService.refreshToken(isOnline -> {
            if (isOnline) {
                String token = sp.getString(TOKEN, null);
                createWebSocket(token);
            }
        });
    }

    public void joinGame(View view) {
        if (!connectionCodeET.getText().toString().isEmpty()) {
            JSONObject jsonConnect = new JSONObject();
            try {
                jsonConnect.put(GAME_ID, connectionCodeET.getText());
                jsonConnect.put(PLAYER_ID, foundUsername);
            } catch (JSONException e) {
                Log.e(TAG, "joinGame: " + e.getMessage());
                throw new JsonParsingException(e.getMessage());
            }
            WebSocketClient.mStompClient.send("/app/connect", jsonConnect.toString()).subscribe();
        }
    }

    private void switchToMultiplayer() {
        Intent mp = new Intent(this, MultiplayerActivity.class);
        mp.putExtra(GAME_ID, Integer.toString(game.getGameId()));
        startActivity(mp);
    }

    private void createWebSocket(String token) {
        Properties properties = PropertyReader.getInstance(this).getProperties("application.properties");
        String wss = properties.getProperty(WSS);
        WebSocketClient.createWebSocket(wss + "multiconnect", token);
        Disposable topic = WebSocketClient.mStompClient.topic("/topic/waiting").subscribe(topicMessage -> {
            try {
                JSONObject msg = new JSONObject(topicMessage.getPayload());
                if (foundUsername.equals(msg.getString("player2")) && !msg.getString("player1").equals("null")) {
                    game = new MultiplayerGame(msg);
                    switchToMultiplayer();
                }
            } catch (JSONException e) {
                Log.e(TAG, "createWebSocket: " + e.getMessage());
                throw new JsonParsingException(e.getMessage());
            }
        }, throwable -> Log.d(TAG, "error at subscribing"));
        WebSocketClient.compositeDisposable.add(topic);
    }

}