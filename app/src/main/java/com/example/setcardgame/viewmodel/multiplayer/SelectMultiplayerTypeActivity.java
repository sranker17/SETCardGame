package com.example.setcardgame.viewmodel.multiplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.setcardgame.R;
import com.example.setcardgame.viewmodel.BaseActivity;

public class SelectMultiplayerTypeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_multiplayer_type);
        setupToolbar(R.id.toolbar, R.string.chooseMultiTypeText);
    }

    public void switchToPrivateGame(View view) {
        Intent sb = new Intent(this, PrivateGameActivity.class);
        startActivity(sb);
    }

    public void switchToRandomGame(View view) {
        Intent wfg = new Intent(this, WaitingForGameActivity.class);
        startActivity(wfg);
    }
}