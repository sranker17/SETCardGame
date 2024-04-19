package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.setcardgame.R;
import com.example.setcardgame.model.UrlConstants;
import com.example.setcardgame.model.Username;
import com.example.setcardgame.viewmodel.multiplayer.SelectMultiplayerTypeActivity;
import com.example.setcardgame.viewmodel.scoreboard.ScoreboardActivity;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sp;

    private static final String USERNAME = "username";
    private static final String TAG = "Main activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if ("default".equals(sp.getString(USERNAME, "default"))) {
            SharedPreferences.Editor editor = sp.edit();
            UUID username = UUID.randomUUID();
            editor.putString(USERNAME, username.toString());
            editor.apply();
        }
        Username.setName(sp.getString(USERNAME, "def"));
        checkServer();
    }

    private void checkServer() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = UrlConstants.URL + "available";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> Log.d(TAG, "Server available"), error -> {
            Log.d(TAG, "Server not available");
            Toast.makeText(MainActivity.this, getString(R.string.serverUnavailable), Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);
    }

    public void switchToDifficulty(View v) {
        Intent d = new Intent(this, DifficultyActivity.class);
        startActivity(d);
    }

    public void switchToMultiplayer(View v) {
        Intent mp = new Intent(this, SelectMultiplayerTypeActivity.class);
        startActivity(mp);
    }

    public void switchToScoreboard(View v) {
        Intent sb = new Intent(this, ScoreboardActivity.class);
        startActivity(sb);
    }

    public void switchToHowToPage(View v) {
        Intent htp = new Intent(this, HowToPageActivity.class);
        startActivity(htp);
    }
}