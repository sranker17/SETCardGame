package com.example.setcardgame.viewmodel.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.setcardgame.R;
import com.example.setcardgame.viewmodel.BaseActivity;

public class AuthenticationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        setupStatusBar();
    }

    public void switchToLogin(View v) {
        Intent sp = new Intent(this, LoginActivity.class);
        startActivity(sp);
    }

    public void switchToRegister(View v) {
        Intent sp = new Intent(this, RegisterActivity.class);
        startActivity(sp);
    }
}