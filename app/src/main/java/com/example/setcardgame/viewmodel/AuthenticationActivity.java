package com.example.setcardgame.viewmodel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.setcardgame.R;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
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