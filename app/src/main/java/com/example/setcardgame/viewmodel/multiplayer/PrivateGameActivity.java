package com.example.setcardgame.viewmodel.multiplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.setcardgame.R;
import com.example.setcardgame.viewmodel.BaseActivity;

public class PrivateGameActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_game);
        setupToolbar(R.id.toolbar, R.string.privateGameText);
    }

    public void switchToJoinGame(View v) {
        Intent jg = new Intent(this, JoinGameActivity.class);
        startActivity(jg);
    }

    public void switchToCreateGame(View v) {
        Intent cpg = new Intent(this, CreatePrivateGameActivity.class);
        startActivity(cpg);
    }
}