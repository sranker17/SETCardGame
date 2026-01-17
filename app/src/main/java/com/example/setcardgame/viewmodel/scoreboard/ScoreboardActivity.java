package com.example.setcardgame.viewmodel.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.setcardgame.R;
import com.example.setcardgame.viewmodel.BaseActivity;

public class ScoreboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        setupToolbar(R.id.toolbar, R.string.scoreboardText);
    }

    public void switchToMyScores(View v) {
        Intent ms = new Intent(this, PlayerScoresActivity.class);
        startActivity(ms);
    }

    public void switchToWorldScores(View v) {
        Intent ws = new Intent(this, WorldScoresActivity.class);
        startActivity(ws);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}