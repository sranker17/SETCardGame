package com.example.setcardgame.viewmodel.scoreboard;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.setcardgame.R;
import com.example.setcardgame.model.Difficulty;
import com.example.setcardgame.model.Username;
import com.example.setcardgame.model.scoreboard.Scoreboard;
import com.example.setcardgame.model.scoreboard.ScoresFragment;
import com.example.setcardgame.model.scoreboard.ViewPagerAdapter;
import com.example.setcardgame.service.ScoreboardDataService;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class WorldScoresActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private final String username = Username.getName();
    private final ScoreboardDataService scoreboardDataService = new ScoreboardDataService(WorldScoresActivity.this);
    private static final String TAG = "World score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        tabLayout = findViewById(R.id.tabLayoutPlayer);
        viewPager = findViewById(R.id.viewPagerPlayer);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        scoreboardDataService.getPlayerScores(false, username, new ScoreboardDataService.ScoreboardResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(WorldScoresActivity.this, getString(R.string.cantGetScores), Toast.LENGTH_SHORT).show();
                Log.d(TAG, getString(R.string.cantGetScores));
            }

            @Override
            public void onResponse(List<Scoreboard> scoreboardModels) {
                int easyCounter = 0;
                int normalCounter = 0;
                List<Scoreboard> easyScoreList = new ArrayList<>();
                List<Scoreboard> normalScoreList = new ArrayList<>();

                for (Scoreboard score : scoreboardModels) {
                    if (score.getPlayerId().toString().equals(username)) {
                        score.setMyScore(true);
                    }
                    if (score.getDifficulty() == Difficulty.EASY && easyCounter < 100) {
                        easyCounter++;
                        score.setPlacement(easyCounter);
                        easyScoreList.add(score);
                    }
                    if (score.getDifficulty() == Difficulty.NORMAL && normalCounter < 100) {
                        normalCounter++;
                        score.setPlacement(normalCounter);
                        normalScoreList.add(score);
                    }
                }

                adapter.addFragment(new ScoresFragment(easyScoreList), String.format("%s", getString(R.string.easy)));
                adapter.addFragment(new ScoresFragment(normalScoreList), String.format("%s", getString(R.string.normal)));

                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }
}