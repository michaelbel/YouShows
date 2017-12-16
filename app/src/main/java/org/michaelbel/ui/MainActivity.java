package org.michaelbel.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.michaelbel.seriespicker.R;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.ui.fragment.MainFragment;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import me.yokeyword.fragmentation_swipeback.SwipeBackActivity;

@SuppressWarnings("all")
public class MainActivity extends SwipeBackActivity {

    public Toolbar toolbar;
    public TextView toolbarTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setPopupTheme(Theme.popupTheme());
        setSupportActionBar(toolbar);

        toolbarTextView = findViewById(R.id.toolbar_title);

        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.fragment_layout, new MainFragment());
        }

        getSwipeBackLayout().setEnableGesture(false);
    }

    @Override
    public void setTheme(int resid) {
        super.setTheme(Theme.getTheme() ? R.style.ThemeLight : R.style.ThemeNight);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean swipeBackPriority() {
        return super.swipeBackPriority();
    }

    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }
}