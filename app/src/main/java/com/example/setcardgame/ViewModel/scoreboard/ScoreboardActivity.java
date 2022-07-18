package com.example.setcardgame.ViewModel.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.R;

public class ScoreboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
    }

    public void switchToMyScores(View v){
        Intent ms = new Intent(this, PlayerScoresActivity.class);
        startActivity(ms);
    }

    public void switchToWorldScores(View v){
        Intent ws = new Intent(this, WorldScoresActivity.class);
        startActivity(ws);
    }
}