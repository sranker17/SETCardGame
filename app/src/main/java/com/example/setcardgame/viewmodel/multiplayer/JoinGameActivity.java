package com.example.setcardgame.viewmodel.multiplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.R;
import com.example.setcardgame.config.WebSocketClient;
import com.example.setcardgame.model.MultiplayerGame;
import com.example.setcardgame.model.UrlConstants;
import com.example.setcardgame.model.Username;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;

public class JoinGameActivity extends AppCompatActivity {

    private static final String TAG = "joinGame";
    private static final String GAME_ID = "gameId";
    private static final String PLAYER_ID = "playerId";
    private final String username = Username.getName();
    private EditText connectionCodeET;
    private MultiplayerGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        connectionCodeET = findViewById(R.id.connectionCodeET);

        WebSocketClient.createWebSocket(UrlConstants.WSS_URL + "multiconnect");
        Disposable topic = WebSocketClient.mStompClient.topic("/topic/waiting").subscribe(topicMessage -> {
            try {
                JSONObject msg = new JSONObject(topicMessage.getPayload());
                if (username.equals(msg.getString("player2")) && !msg.getString("player1").equals("null")) {
                    game = new MultiplayerGame(msg);
                    switchToMultiplayer();
                }
            } catch (JSONException e) {
                e.getMessage();
            }
        }, throwable -> Log.d(TAG, "error at subscribing"));
        WebSocketClient.compositeDisposable.add(topic);
    }

    public void joinGame(View view) {
        if (!connectionCodeET.getText().toString().isEmpty()) {
            JSONObject jsonConnect = new JSONObject();
            try {
                jsonConnect.put(GAME_ID, connectionCodeET.getText());
                jsonConnect.put(PLAYER_ID, username);
            } catch (JSONException e) {
                e.getMessage();
            }
            WebSocketClient.mStompClient.send("/app/connect", jsonConnect.toString()).subscribe();
        }
    }

    private void switchToMultiplayer() {
        Intent mp = new Intent(this, MultiplayerActivity.class);
        mp.putExtra(GAME_ID, Integer.toString(game.getGameId()));
        startActivity(mp);
    }
}