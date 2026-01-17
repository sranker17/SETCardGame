package com.example.setcardgame.viewmodel.scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.setcardgame.R;
import com.example.setcardgame.viewmodel.BaseActivity;

public class ScoreboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        // Set status bar color (for Toolbar-based activities, we still need to set the window color)
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        Toolbar toolbar = findViewById(R.id.toolbar);
        
        // Extend Toolbar to cover status bar area
        int statusBarHeight = getStatusBarHeight();
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            toolbar.setPadding(0, statusBarHeight, 0, 0);
            ViewGroup.LayoutParams params = toolbar.getLayoutParams();
            params.height = actionBarHeight + statusBarHeight;
            toolbar.setLayoutParams(params);
        }
        
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.scoreboardText);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // Set navigation icon color to white
        if (toolbar.getNavigationIcon() != null) {
            DrawableCompat.setTint(toolbar.getNavigationIcon(), ContextCompat.getColor(this, R.color.white));
        }
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