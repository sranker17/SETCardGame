package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.R;
import com.example.setcardgame.listener.ScoreAddedResponseListener;
import com.example.setcardgame.model.Difficulty;
import com.example.setcardgame.model.Error;
import com.example.setcardgame.model.scoreboard.Scoreboard;
import com.example.setcardgame.service.AuthService;
import com.example.setcardgame.service.ScoreboardService;
import com.example.setcardgame.viewmodel.scoreboard.ScoreboardActivity;

import org.json.JSONObject;

import java.util.Objects;

public class EndGameScreenActivity extends AppCompatActivity {
    private final AuthService authService = new AuthService(EndGameScreenActivity.this);
    private final ScoreboardService scoreboardService = new ScoreboardService(EndGameScreenActivity.this);
    private int finalTime;
    private String finalScore;
    private String finalDifficulty;
    private static final String AUTH = "auth";
    private static final String USERNAME = "username";
    private static final String END_GAME_SCREEN = "EndGameScreen";
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

        if (authService.isTokenExpired()) {
            authService.refreshToken(isOnline -> {
                if (isOnline) {
                    Log.i(END_GAME_SCREEN, "Saving score with refreshed token");
                    saveScore();
                } else {
                    Log.e(END_GAME_SCREEN, "Server offline");
                }
            });
        } else {
            Log.i(END_GAME_SCREEN, "Saving score with current token");
            saveScore();
        }
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

    private void saveScore() {
        SharedPreferences sp = getSharedPreferences(AUTH, MODE_PRIVATE);
        String username = sp.getString(USERNAME, null);
        Scoreboard scoreboardModel = new Scoreboard(username, finalDifficulty, Integer.parseInt(finalScore), finalTime, null);
        scoreboardService.addScore(scoreboardModel, new ScoreAddedResponseListener() {
            @Override
            public void onError(Error errorResponse) {
                Log.e(END_GAME_SCREEN, errorResponse.toString());
                String toastMessage;
                switch (errorResponse.getStatus()) {
                    case 400:
                        toastMessage = getString(R.string.invalidParameters);
                        break;
                    case 401:
                        toastMessage = getString(R.string.badCredentials);
                        break;
                    case 403:
                        toastMessage = getString(R.string.accountError);
                        break;
                    case 409:
                        toastMessage = getString(R.string.takenUsername);
                        break;
                    case 500:
                        toastMessage = getString(R.string.internalServerError);
                        break;
                    case 503:
                        toastMessage = getString(R.string.serverUnavailable);
                        break;
                    default:
                        toastMessage = errorResponse.getDescription();
                }
                Toast.makeText(EndGameScreenActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONObject scoreboardResponse) {
                Log.d(SCORE, scoreboardResponse.toString());
            }
        });
    }
}