package com.example.setcardgame.viewmodel.multiplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.R;
import com.example.setcardgame.config.WebSocketClient;
import com.example.setcardgame.model.MultiplayerGame;
import com.example.setcardgame.model.UrlConstants;
import com.example.setcardgame.model.Username;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;

public class CreatePrivateGameActivity extends AppCompatActivity {

    private final String username = Username.getName();
    private MultiplayerGame game;
    private TextView connectionCodeTV;

    private static final String TAG = "privateGame";
    private static final String GAME_ID = "gameId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_private_game);
        connectionCodeTV = findViewById(R.id.connectionCodeTV);

        WebSocketClient.createWebSocket(UrlConstants.WSS_URL + "multiconnect");
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
                e.getMessage();
            }
        }, throwable -> Log.d(TAG, "error at subscribing"));
        WebSocketClient.compositeDisposable.add(topic);

        JSONObject jsonPlayer = new JSONObject();
        try {
            jsonPlayer.put("username", username);
        } catch (JSONException e) {
            e.getMessage();
        }

        WebSocketClient.mStompClient.send("/app/create", jsonPlayer.toString()).subscribe();
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
                e.getMessage();
            }

            WebSocketClient.mStompClient.send("/app/game/destroy", destroyGame.toString()).subscribe();

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
                e.getMessage();
            }

            WebSocketClient.mStompClient.send("/app/game/destroy", destroyGame.toString()).subscribe();

            Log.d(TAG, "Game destroyed");
        }
        WebSocketClient.disconnectWebSocket();
        game = null;
    }
}