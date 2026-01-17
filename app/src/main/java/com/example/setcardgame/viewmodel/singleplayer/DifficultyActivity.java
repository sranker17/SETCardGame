package com.example.setcardgame.viewmodel.singleplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.setcardgame.R;
import com.example.setcardgame.model.Difficulty;
import com.example.setcardgame.viewmodel.BaseActivity;

public class DifficultyActivity extends BaseActivity {

    private static final String DIFF_MODE = "diffMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        setupStatusBar();
    }

    public void switchToSingleplayer(View v) {
        Intent sp = new Intent(this, SingleplayerActivity.class);
        if (findViewById(v.getId()) == findViewById(R.id.easyBtn)) {
            sp.putExtra(DIFF_MODE, Difficulty.EASY.toString());
        } else {
            sp.putExtra(DIFF_MODE, Difficulty.NORMAL.toString());
        }
        startActivity(sp);
    }
}