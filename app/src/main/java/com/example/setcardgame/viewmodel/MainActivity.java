package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.setcardgame.R;
import com.example.setcardgame.model.Username;
import com.example.setcardgame.viewmodel.multiplayer.SelectMultiplayerTypeActivity;
import com.example.setcardgame.viewmodel.scoreboard.ScoreboardActivity;

public class MainActivity extends AppCompatActivity {
    private static final String AUTH = "auth";
    private static final String TAG = "Main activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //TODO add Android Keystore
        SharedPreferences sp = getSharedPreferences(AUTH, MODE_PRIVATE);
        //TODO use user login or register activity
        if (sp.getString("username", null) == null) {
            Log.i(TAG, "Registering new user");
            switchToLogin();
        } else {
            //TODO login with user credentials from sp
            //TODO if cant login block multiplayer and scoreboard buttons (server probably unavailable)
        }
        //TODO remove this and use sp instead everywhere
        Username.setName(sp.getString("username", null));
    }

    public void switchToLogin() {
        Intent d = new Intent(this, LoginActivity.class);
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