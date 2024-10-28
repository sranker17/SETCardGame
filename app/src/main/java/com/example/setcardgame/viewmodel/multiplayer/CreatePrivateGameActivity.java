package com.example.setcardgame.viewmodel.multiplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.R;
import com.example.setcardgame.config.WebSocketClient;
import com.example.setcardgame.exception.JSONParsingException;
import com.example.setcardgame.model.MultiplayerGame;
import com.example.setcardgame.model.UrlConstants;
import com.example.setcardgame.service.AuthService;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;

public class CreatePrivateGameActivity extends AppCompatActivity {
    private final AuthService authService = new AuthService(CreatePrivateGameActivity.this);
    private MultiplayerGame game;
    private static final String TAG = "privateGame";
    private static final String GAME_ID = "gameId";
    private static final String USERNAME = "username";
    private static final String DESTROY_GAME_TOPIC = "/app/game/destroy";
    private static final String CREATE_GAME_TOPIC = "/app/create";
    private static final String TOKEN = "token";
    private TextView connectionCodeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_private_game);
        connectionCodeTV = findViewById(R.id.connectionCodeTV);

        SharedPreferences sp = authService.getEncryptedSharedPreferences();
        String username = sp.getString(USERNAME, null);
        if (username == null) {
            Log.e(TAG, "Username not found");
            return;
        }
        authService.refreshToken(isOnline -> {
            if (isOnline) {
                String token = sp.getString(TOKEN, null);
                createWebSocket(username, token);
            }
        });

    }

    public void switchToMultiplayer() {
        Intent mp = new Intent(this, MultiplayerActivity.class);
        mp.putExtra(GAME_ID, Integer.toString(game.getGameId()));
        startActivity(mp);
    }

    public void deleteGame(View view) {
        if (game != null) {
            JSONObject destroyGame = new JSONObject();
            try {
                destroyGame.put(GAME_ID, game.getGameId());
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                throw new JSONParsingException(e.getMessage());
            }

            WebSocketClient.mStompClient.send(DESTROY_GAME_TOPIC, destroyGame.toString()).subscribe();

            Log.d(TAG, "Game destroyed");

        }
        WebSocketClient.disconnectWebSocket();
        game = null;

        Intent pg = new Intent(this, PrivateGameActivity.class);
        startActivity(pg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (game != null) {
            JSONObject destroyGame = new JSONObject();
            try {
                destroyGame.put(GAME_ID, game.getGameId());
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                throw new JSONParsingException(e.getMessage());
            }

            WebSocketClient.mStompClient.send(DESTROY_GAME_TOPIC, destroyGame.toString()).subscribe();

            Log.d(TAG, "Game destroyed");
        }
        WebSocketClient.disconnectWebSocket();
        game = null;
    }

    private void createWebSocket(String username, String token) {
        WebSocketClient.createWebSocket(UrlConstants.WSS_URL + "multiconnect", token);
        Disposable topic = WebSocketClient.mStompClient.topic("/topic/waiting").subscribe(topicMessage -> {
            try {
                JSONObject msg = new JSONObject(topicMessage.getPayload());
                if (username.equals(msg.getString("player1"))) {
                    game = new MultiplayerGame(msg);
                    Log.d(TAG, topicMessage.getPayload());

                    runOnUiThread(() -> connectionCodeTV.setText(String.valueOf(game.getGameId())));

                    if (!msg.getString("player2").equals("null")) {
                        switchToMultiplayer();
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                throw new JSONParsingException(e.getMessage());
            }
        }, throwable -> Log.d(TAG, "error at subscribing"));
        WebSocketClient.compositeDisposable.add(topic);

        JSONObject jsonPlayer = new JSONObject();
        try {
            jsonPlayer.put(USERNAME, username);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            throw new JSONParsingException(e.getMessage());
        }

        WebSocketClient.mStompClient.send(CREATE_GAME_TOPIC, jsonPlayer.toString()).subscribe();
    }
}