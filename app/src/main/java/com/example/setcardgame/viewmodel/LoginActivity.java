package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.R;
import com.example.setcardgame.model.Difficulty;

public class LoginActivity extends AppCompatActivity {

    private static final String DIFF_MODE = "diffMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
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