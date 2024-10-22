package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.setcardgame.R;
import com.example.setcardgame.listener.ServerStatusListener;
import com.example.setcardgame.service.AuthService;
import com.example.setcardgame.viewmodel.multiplayer.SelectMultiplayerTypeActivity;
import com.example.setcardgame.viewmodel.scoreboard.ScoreboardActivity;

public class MainActivity extends AppCompatActivity implements ServerStatusListener {
    private final AuthService authService = new AuthService(MainActivity.this);
    private Button multiBtn;
    private Button scoreboardBtn;
    private static final String AUTH = "auth";
    private static final String USERNAME = "username";
    private static final String TAG = "Main activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        multiBtn = findViewById(R.id.multiplayerBtn);
        scoreboardBtn = findViewById(R.id.scoreboardBtn);

        Log.d(TAG, "start handleLogin in onCreate");
        handleLogin();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d(TAG, "start handleLogin in onNewIntent");
        handleLogin();
    }

    @Override
    public void onServerStatusChecked(boolean isOnline) {
        if (isOnline) {
            Log.i(TAG, "Server is online");
            unblockOnlineFeatures();
        } else {
            Log.i(TAG, "Server is offline");
            blockOnlineFeatures();
        }
    }

    public void switchToAuthentication() {
        Intent a = new Intent(this, AuthenticationActivity.class);
        startActivity(a);
    }

    public void switchToDifficulty(View view) {
        Intent d = new Intent(this, DifficultyActivity.class);
        startActivity(d);
    }

    public void switchToMultiplayer(View view) {
        Intent mp = new Intent(this, SelectMultiplayerTypeActivity.class);
        startActivity(mp);
    }

    public void switchToScoreboard(View view) {
        Intent sb = new Intent(this, ScoreboardActivity.class);
        startActivity(sb);
    }

    public void switchToHowToPage(View view) {
        Intent htp = new Intent(this, HowToPageActivity.class);
        startActivity(htp);
    }

    private void blockOnlineFeatures() {
        Log.i(TAG, "Blocking online features");
        multiBtn.setEnabled(false);
        multiBtn.setTooltipText(getString(R.string.serverUnavailable));
        scoreboardBtn.setEnabled(false);
        scoreboardBtn.setTooltipText(getString(R.string.serverUnavailable));
    }

    private void unblockOnlineFeatures() {
        Log.i(TAG, "Unblocking online features");
        multiBtn.setEnabled(true);
        multiBtn.setTooltipText(null);
        scoreboardBtn.setEnabled(true);
        scoreboardBtn.setTooltipText(null);
    }

    private void handleLogin() {
        //TODO add Android Keystore
        SharedPreferences sp = getSharedPreferences(AUTH, MODE_PRIVATE);
//        ServerStatus serverStatus = ServerStatus.valueOf(sp.getString(SERVER_STATUS, ServerStatus.ONLINE.name()));
//        if (ServerStatus.ONLINE.equals(serverStatus)) {
        if (sp.getString(USERNAME, null) == null) {
            Log.i(TAG, "Switching to authentication page");
            switchToAuthentication();
        } else {
            if (authService.isTokenExpired()) {
                Log.i(TAG, "Token expired, refreshing");
                authService.refreshToken(this);
//                    blockOnlineFeatures();
            } else {
                Log.i(TAG, "Token is still valid");
            }
        }
//        } else {
//            blockOnlineFeatures();
//        }
    }
}