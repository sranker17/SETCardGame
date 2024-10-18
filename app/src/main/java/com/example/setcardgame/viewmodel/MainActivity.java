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

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String USERNAME = "test";
    private static final String TAG = "Main activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //TODO add Android Keystore
        SharedPreferences sp = getSharedPreferences(USERNAME, MODE_PRIVATE);
        //TODO use user login
        if (sp.getString(USERNAME, null) == null) {
            Log.i(TAG, "Registering new user");
            switchTRegister();


            SharedPreferences.Editor editor = sp.edit();
            UUID username = UUID.randomUUID();
            editor.putString(USERNAME, username.toString());
            editor.apply();
        }
        Username.setName(sp.getString(USERNAME, "def"));
    }

    public void switchTRegister() {
        Intent d = new Intent(this, RegisterActivity.class);
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