package com.example.setcardgame.ViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.Model.Difficulty;
import com.example.setcardgame.Model.Username;
import com.example.setcardgame.Model.scoreboard.Scoreboard;
import com.example.setcardgame.R;
import com.example.setcardgame.Service.ScoreboardDataService;
import com.example.setcardgame.ViewModel.scoreboard.ScoreboardActivity;

import org.json.JSONObject;

public class EndGameScreenActivity extends AppCompatActivity {
    private int finalTime;
    private String finalScore;
    private String finalDifficulty;
    private final String username = Username.getUsername();
    private final ScoreboardDataService scoreboardDataService = new ScoreboardDataService(EndGameScreenActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game_screen);

        Intent egs = getIntent();
        finalTime = Integer.parseInt(egs.getStringExtra("time"));
        finalScore = egs.getStringExtra("score");
        finalDifficulty = egs.getStringExtra("diff");
        int seconds = finalTime % 60;
        int minutes = finalTime / 60;
        TextView finalTimeTextView = (TextView) findViewById(R.id.finalTimeTextView);
        TextView finalScoreTextView = (TextView) findViewById(R.id.finalPointTextView);
        TextView finalDifficultyTextView = (TextView) findViewById(R.id.difficultyTextView);

        finalTimeTextView.setText(String.format("%s: %d:%02d", getString(R.string.timeText), minutes, seconds));
        finalScoreTextView.setText(String.format("%s: %s", getString(R.string.pointsText) ,finalScore));
        if(finalDifficulty.equals(Difficulty.EASY.toString())){
            finalDifficultyTextView.setText(String.format("%s", getString(R.string.easy)));
        }
        else {
            finalDifficultyTextView.setText(String.format("%s", getString(R.string.normal)));
        }

        addScoreToDB();
    }

    public void newSingleplayerGame(View v) {
        Intent sp = new Intent(this, SingleplayerActivity.class);
        sp.putExtra("diffMode", finalDifficulty);
        startActivity(sp);
    }

    public void backToMenu(View v) {
        Intent m = new Intent(this, MainActivity.class);
        startActivity(m);
    }

    public void switchToScoreboard(View v) {
        Intent sb = new Intent(this, ScoreboardActivity.class);
        startActivity(sb);
    }

    public void addScoreToDB() {
        Scoreboard scoreboardModel = new Scoreboard(username, finalDifficulty, Integer.parseInt(finalScore), finalTime);
        scoreboardDataService.addScore(scoreboardModel, new ScoreboardDataService.ScoreAddedResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(EndGameScreenActivity.this, message, Toast.LENGTH_SHORT).show();
                Log.d("score", message);
            }

            @Override
            public void onResponse(JSONObject scoreboardModel) {
                Log.d("score", scoreboardModel.toString());
            }
        });
    }
}