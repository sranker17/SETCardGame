package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.setcardgame.R;
import com.example.setcardgame.model.Username;
import com.example.setcardgame.viewmodel.multiplayer.SelectMultiplayerTypeActivity;
import com.example.setcardgame.viewmodel.scoreboard.ScoreboardActivity;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String USERNAME = "username";
    private static final String TAG = "Main activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        SharedPreferences sp = getSharedPreferences(USERNAME, MODE_PRIVATE);
        //TODO use user login
        if ("default".equals(sp.getString(USERNAME, "default"))) {
            SharedPreferences.Editor editor = sp.edit();
            UUID username = UUID.randomUUID();
            editor.putString(USERNAME, username.toString());
            editor.apply();
        }
        Username.setName(sp.getString(USERNAME, "def"));
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