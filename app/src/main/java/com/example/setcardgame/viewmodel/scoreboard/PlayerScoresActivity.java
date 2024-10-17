package com.example.setcardgame.viewmodel.scoreboard;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.setcardgame.R;
import com.example.setcardgame.model.Username;
import com.example.setcardgame.model.scoreboard.ScoresFragment;
import com.example.setcardgame.model.scoreboard.TopScores;
import com.example.setcardgame.model.scoreboard.ViewPagerAdapter;
import com.example.setcardgame.service.ScoreboardDataService;
import com.google.android.material.tabs.TabLayout;

public class PlayerScoresActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private final String username = Username.getName();
    private final ScoreboardDataService scoreboardDataService = new ScoreboardDataService(PlayerScoresActivity.this);
    private static final String TAG = "Player score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        tabLayout = findViewById(R.id.tabLayoutPlayer);
        viewPager = findViewById(R.id.viewPagerPlayer);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        scoreboardDataService.getPlayerScores(true, new ScoreboardDataService.ScoreboardResponseListener() {
            @Override
            public void onError(String message) {
                Log.e(TAG, message);
                Toast.makeText(PlayerScoresActivity.this, getString(R.string.cantGetScores), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(TopScores topScores) {
                Log.i(TAG, "Top user scores received");
                adapter.addFragment(new ScoresFragment(topScores.getEasyScores()), String.format("%s", getString(R.string.easy)));
                adapter.addFragment(new ScoresFragment(topScores.getNormalScores()), String.format("%s", getString(R.string.normal)));

                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }
}