package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.R;
import com.example.setcardgame.model.Difficulty;
import com.example.setcardgame.model.Username;
import com.example.setcardgame.model.scoreboard.Scoreboard;
import com.example.setcardgame.service.ScoreboardDataService;
import com.example.setcardgame.viewmodel.scoreboard.ScoreboardActivity;

import org.json.JSONObject;

import java.util.Objects;

public class EndGameScreenActivity extends AppCompatActivity {
    private int finalTime;
    private String finalScore;
    private String finalDifficulty;
    private final String username = Username.getName();
    private final ScoreboardDataService scoreboardDataService = new ScoreboardDataService(EndGameScreenActivity.this);

    private static final String SCORE = "score";
    private static final String DIFF = "diff";
    private static final String DIFF_MODE = "diffMode";
    private static final String TIME = "time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game_screen);

        Intent egs = getIntent();
        finalTime = Integer.parseInt(Objects.requireNonNull(egs.getStringExtra(TIME)));
        finalScore = egs.getStringExtra(SCORE);
        finalDifficulty = egs.getStringExtra(DIFF);
        int seconds = finalTime % 60;
        int minutes = finalTime / 60;
        TextView finalTimeTextView = findViewById(R.id.finalTimeTextView);
        TextView finalScoreTextView = findViewById(R.id.finalPointTextView);
        TextView finalDifficultyTextView = findViewById(R.id.difficultyTextView);

        finalTimeTextView.setText(String.format("%s: %d:%02d", getString(R.string.timeText), minutes, seconds));
        finalScoreTextView.setText(String.format("%s: %s", getString(R.string.pointsText), finalScore));
        if (finalDifficulty.equals(Difficulty.EASY.toString())) {
            finalDifficultyTextView.setText(String.format("%s", getString(R.string.easy)));
        } else {
            finalDifficultyTextView.setText(String.format("%s", getString(R.string.normal)));
        }

        addScoreToDB();
    }

    public void newSingleplayerGame(View view) {
        Intent sp = new Intent(this, SingleplayerActivity.class);
        sp.putExtra(DIFF_MODE, finalDifficulty);
        startActivity(sp);
    }

    public void backToMenu(View view) {
        Intent m = new Intent(this, MainActivity.class);
        startActivity(m);
    }

    public void switchToScoreboard(View view) {
        Intent sb = new Intent(this, ScoreboardActivity.class);
        startActivity(sb);
    }

    public void addScoreToDB() {
        Scoreboard scoreboardModel = new Scoreboard(username, finalDifficulty, Integer.parseInt(finalScore), finalTime);
        scoreboardDataService.addScore(scoreboardModel, new ScoreboardDataService.ScoreAddedResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(EndGameScreenActivity.this, message, Toast.LENGTH_SHORT).show();
                Log.d(SCORE, message);
            }

            @Override
            public void onResponse(JSONObject scoreboardModel) {
                Log.d(SCORE, scoreboardModel.toString());
            }
        });
    }
}