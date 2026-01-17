package com.example.setcardgame.viewmodel;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.setcardgame.R;

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Sets up a Toolbar with the specified title.
     * The status bar color is handled by the theme (android:statusBarColor).
     * Call this method after setContentView() in your Activity's onCreate() method.
     *
     * @param toolbarId The resource ID of the Toolbar to set up
     * @param titleId   The resource ID of the title string to display
     */
    protected void setupToolbar(int toolbarId, int titleId) {
        Toolbar toolbar = findViewById(toolbarId);
        if (toolbar == null) {
            return;
        }

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
            if (titleId != 0) {
                getSupportActionBar().setTitle(titleId);
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Set navigation icon color to white
        if (toolbar.getNavigationIcon() != null) {
            DrawableCompat.setTint(toolbar.getNavigationIcon(), ContextCompat.getColor(this, R.color.white));
        }
    }

    /**
     * Gets the status bar height in pixels using WindowInsets API.
     * Uses the root view to get window insets.
     *
     * @return Status bar height in pixels, or estimated value if insets are not available.
     */
    protected int getStatusBarHeight() {
        View rootView = getWindow().getDecorView().getRootView();
        if (rootView != null) {
            WindowInsetsCompat windowInsets = ViewCompat.getRootWindowInsets(rootView);
            if (windowInsets != null) {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
                if (insets.top > 0) {
                    return insets.top;
                }
            }
        }
        // Fallback: estimate status bar height (usually around 24-48dp)
        return (int) (24 * getResources().getDisplayMetrics().density);
    }
}
