package org.michaelbel.youshows.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.youshows.Theme;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.shows.R;
import org.michaelbel.youshows.ui.fragment.SettingsFragment;

/**
 * Date: 19 MAR 2018
 * Time: 13:20 MSK
 *
 * @author Michael Bel
 */

public class SettingsActivity extends AppCompatActivity {

    private Context context;
    public SharedPreferences prefs;
    private SettingsFragment settingsFragment;

    public Toolbar toolbar;
    public TextView toolbarTitle;
    public TextView toolbarTitle2;

    public FrameLayout frameLayout1;
    public FrameLayout frameLayout2;
    public FrameLayout shadowLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = SettingsActivity.this;

        prefs = getSharedPreferences("mainconfig", MODE_PRIVATE);
        getWindow().setStatusBarColor(ContextCompat.getColor(context, Theme.primaryDarkColor()));

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setBackgroundColor(ContextCompat.getColor(context, Theme.primaryColor()));
        setSupportActionBar(toolbar);

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.Settings);

        toolbarTitle2 = findViewById(R.id.toolbar_title2);
        toolbarTitle2.setAlpha(0);
        toolbarTitle2.setX(Extensions.dp(context, 56));
        toolbarTitle2.setVisibility(View.INVISIBLE);

        frameLayout1 = findViewById(R.id.fragment_view1);

        frameLayout2 = findViewById(R.id.fragment_view2);
        frameLayout2.setVisibility(View.INVISIBLE);
        frameLayout2.setElevation(Extensions.dp(context, 2));

        settingsFragment = new SettingsFragment();
        setRootFragment(settingsFragment);

        shadowLayout = findViewById(R.id.shadow_layout);
        shadowLayout.setAlpha(0);
        shadowLayout.setVisibility(View.INVISIBLE);
        shadowLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent20));
    }

    @Override
    public void onBackPressed() {
        if (frameLayout2.getVisibility() == View.VISIBLE) {
            finishFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
    }

    public void setRootFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(frameLayout1.getId(), fragment).commit();
    }

    public void startFragment(Fragment fragment, @StringRes int textId) {
        toolbarTitle2.setText(getString(textId));
        toolbarTitle2.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction().replace(frameLayout2.getId(), fragment).commit();
        frameLayout2.setVisibility(View.VISIBLE);
        shadowLayout.setVisibility(View.VISIBLE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(shadowLayout, "alpha", 0, 1),
            ObjectAnimator.ofFloat(frameLayout1, "translationX", 0, -Extensions.dp(context, 100)),
            ObjectAnimator.ofFloat(frameLayout2, "translationX", frameLayout2.getMeasuredWidth(), 0),

            ObjectAnimator.ofFloat(toolbarTitle, "alpha", 1, 0),
            ObjectAnimator.ofFloat(toolbarTitle, "translationX", 0, -Extensions.dp(context, 56)),

            ObjectAnimator.ofFloat(toolbarTitle2, "alpha", 0, 1),
            ObjectAnimator.ofFloat(toolbarTitle2, "translationX", Extensions.dp(context, 56), 0)
        );
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(300);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                toolbarTitle.setVisibility(View.INVISIBLE);
            }
        });
        AndroidExtensions.runOnUIThread(animatorSet:: start);
    }

    public void finishFragment() {
        if (frameLayout2.getVisibility() != View.VISIBLE) {
            finish();
            return;
        }

        toolbarTitle.setVisibility(View.VISIBLE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(shadowLayout, "alpha", 1, 0),
            ObjectAnimator.ofFloat(frameLayout1, "translationX", -Extensions.dp(context, 100), 0),
            ObjectAnimator.ofFloat(frameLayout2, "translationX", 0, frameLayout2.getMeasuredWidth()),

            ObjectAnimator.ofFloat(toolbarTitle, "alpha", 0, 1),
            ObjectAnimator.ofFloat(toolbarTitle, "translationX", -Extensions.dp(context, 56), 0),

            ObjectAnimator.ofFloat(toolbarTitle2, "alpha", 1, 0),
            ObjectAnimator.ofFloat(toolbarTitle2, "translationX", 0, Extensions.dp(context, 56))
        );
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                frameLayout2.setVisibility(View.INVISIBLE);
                shadowLayout.setVisibility(View.INVISIBLE);
                toolbarTitle2.setVisibility(View.INVISIBLE);
            }
        });
        AndroidExtensions.runOnUIThread(animatorSet:: start);
    }

    public void changeSettingsFragmentTheme() {
        settingsFragment.changeTheme();
    }
}