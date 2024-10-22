package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.setcardgame.R;
import com.example.setcardgame.model.ServerStatus;
import com.example.setcardgame.service.AuthService;
import com.example.setcardgame.viewmodel.multiplayer.SelectMultiplayerTypeActivity;
import com.example.setcardgame.viewmodel.scoreboard.ScoreboardActivity;

public class MainActivity extends AppCompatActivity {
    private static final String AUTH = "auth";
    private static final String USERNAME = "username";
    private static final String TAG = "Main activity";
    private static final String SERVER_STATUS = "SERVER_STATUS";
    private final AuthService authService = new AuthService(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //TODO add Android Keystore
        SharedPreferences sp = getSharedPreferences(AUTH, MODE_PRIVATE);
        ServerStatus serverStatus = ServerStatus.valueOf(sp.getString(SERVER_STATUS, ServerStatus.ONLINE.name()));
        if (ServerStatus.ONLINE.equals(serverStatus)) {
            if (sp.getString(USERNAME, null) == null) {
                Log.i(TAG, "Switching to authentication page");
                switchToAuthentication();
            } else {
                if (authService.isTokenExpired()) {
                    authService.refreshToken();
                }
            }
        } else {
            blockOnlineFeatures();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Handle the new intent here
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
        findViewById(R.id.multiplayerBtn).setEnabled(false);
        findViewById(R.id.scoreboardBtn).setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getSharedPreferences(AUTH, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SERVER_STATUS, ServerStatus.ONLINE.name());
        editor.apply();
    }
}