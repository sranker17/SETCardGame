package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.setcardgame.R;
import com.example.setcardgame.exception.RefreshException;
import com.example.setcardgame.service.AuthService;
import com.example.setcardgame.viewmodel.multiplayer.SelectMultiplayerTypeActivity;
import com.example.setcardgame.viewmodel.scoreboard.ScoreboardActivity;

public class MainActivity extends AppCompatActivity {
    private static final String AUTH = "auth";
    private static final String TOKEN = "token";
    private static final String USERNAME = "username";
    private static final String TAG = "Main activity";
    private final AuthService authService = new AuthService(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //TODO add Android Keystore
        SharedPreferences sp = getSharedPreferences(AUTH, MODE_PRIVATE);
        if (sp.getString(USERNAME, null) == null) {
            Log.i(TAG, "Switching to authentication");
            switchToAuthentication();
        } else {
            try {
                String refreshToken = authService.refreshToken();
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(TOKEN, refreshToken);
                editor.apply();
            } catch (RefreshException e) {
                //TODO if cant login block multiplayer and scoreboard buttons (server probably unavailable)
            }
        }
    }

    public void switchToAuthentication() {
        Intent d = new Intent(this, AuthenticationActivity.class);
        startActivity(d);
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
}