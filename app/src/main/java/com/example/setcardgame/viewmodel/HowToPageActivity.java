package com.example.setcardgame.viewmodel;

import android.os.Bundle;

import com.example.setcardgame.R;
import com.example.setcardgame.viewmodel.BaseActivity;

public class HowToPageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_page);
        setupStatusBar();
    }
}