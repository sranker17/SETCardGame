package com.example.setcardgame.viewmodel.scoreboard;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.setcardgame.R;
import com.example.setcardgame.listener.ScoreboardResponseListener;
import com.example.setcardgame.model.Username;
import com.example.setcardgame.model.scoreboard.ScoresFragment;
import com.example.setcardgame.model.scoreboard.TopScores;
import com.example.setcardgame.model.scoreboard.ViewPagerAdapter;
import com.example.setcardgame.service.AuthService;
import com.example.setcardgame.service.ScoreboardService;
import com.google.android.material.tabs.TabLayout;

public class WorldScoresActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private final String username = Username.getName();
    private final ScoreboardService scoreboardService = new ScoreboardService(new AuthService(WorldScoresActivity.this), WorldScoresActivity.this);
    private static final String TAG = "World score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        tabLayout = findViewById(R.id.tabLayoutPlayer);
        viewPager = findViewById(R.id.viewPagerPlayer);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        scoreboardService.getPlayerScores("/top", new ScoreboardResponseListener() {
            @Override
            public void onError(String message) {
                //TODO if message contains response code 403, show a dialog to ask user to login?
                Log.e(TAG, message);
                Toast.makeText(WorldScoresActivity.this, getString(R.string.cantGetScores), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(TopScores topScores) {
                Log.i(TAG, "Top scores received");
                topScores.getEasyScores().forEach(score -> {
                    if (score.getUsername().equals(username)) {
                        score.setUserScore(true);
                    }
                });
                topScores.getNormalScores().forEach(score -> {
                    if (score.getUsername().equals(username)) {
                        score.setUserScore(true);
                    }
                });

                adapter.addFragment(new ScoresFragment(topScores.getEasyScores()), String.format("%s", getString(R.string.easy)));
                adapter.addFragment(new ScoresFragment(topScores.getNormalScores()), String.format("%s", getString(R.string.normal)));

                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }
}