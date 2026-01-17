package com.example.setcardgame.viewmodel;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.setcardgame.R;

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Sets up the status bar with blue background color and adjusts statusBarView height if present.
     * Call this method after setContentView() in your Activity's onCreate() method.
     */
    protected void setupStatusBar() {
        // Set status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        // Set status bar view height to cover status bar area
        View statusBarView = findViewById(R.id.statusBarView);
        if (statusBarView != null) {
            int statusBarHeight = getStatusBarHeight();
            ViewGroup.LayoutParams params = statusBarView.getLayoutParams();
            params.height = statusBarHeight;
            statusBarView.setLayoutParams(params);
        }
    }

    /**
     * Gets the status bar height in pixels.
     * @return Status bar height in pixels, or estimated value if system resource is not available.
     */
    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        if (result == 0) {
            // Fallback: estimate status bar height (usually around 24-48dp)
            result = (int) (24 * getResources().getDisplayMetrics().density);
        }
        return result;
    }
}
