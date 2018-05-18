package org.michaelbel.ui.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.michaelbel.app.YouShows;
import org.michaelbel.app.Theme;
import org.michaelbel.app.eventbus.Events;
import org.michaelbel.app.LayoutHelper;
import org.michaelbel.shows.R;
import org.michaelbel.ui.SettingsActivity;
import org.michaelbel.ui.view.cell.ThemeCell;

/**
 * Date: 08 MAY 2018
 * Time: 18:28 MSK
 *
 * @author Michael Bel
 */

public class ThemeFragment extends Fragment implements View.OnClickListener {

    private String[] themes;
    private SettingsActivity activity;

    private ThemeCell nightBlueCell;
    private ThemeCell nightBlackCell;
    private LinearLayout fragmentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SettingsActivity) getActivity();
        themes = getResources().getStringArray(R.array.Themes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.toolbar.setNavigationOnClickListener(view -> activity.finishFragment());
        activity.toolbarTitle.setText(R.string.Theme);

        fragmentView = new LinearLayout(activity);
        fragmentView.setOrientation(LinearLayout.VERTICAL);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.Color.background()));

        nightBlueCell = new ThemeCell(activity, Theme.THEME_NIGHT_BLUE);
        nightBlueCell.setDivider(true);
        nightBlueCell.setText(themes[0]);
        nightBlueCell.setOnClickListener(this);
        fragmentView.addView(nightBlueCell);

        nightBlackCell = new ThemeCell(activity, Theme.THEME_NIGHT_BLACK);
        nightBlackCell.setDivider(false);
        nightBlackCell.setText(themes[1]);
        nightBlackCell.setOnClickListener(this);
        fragmentView.addView(nightBlackCell);

        changeIcon();
        return fragmentView;
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            nightBlueCell.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 56, 0, 56, 0));
            nightBlackCell.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 56, 0, 56, 0));
        } else {
            nightBlueCell.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            nightBlackCell.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }
    }

    @Override
    public void onClick(View view) {
        SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);

        if (view == nightBlueCell) {
            if (Theme.getTheme() != Theme.THEME_NIGHT_BLUE) {
                prefs.edit().putInt("theme", Theme.THEME_NIGHT_BLUE).apply();
                changeIcon();
                changeTheme();
            }
        } else if (view == nightBlackCell) {
            if (Theme.getTheme() != Theme.THEME_NIGHT_BLACK) {
                prefs.edit().putInt("theme", Theme.THEME_NIGHT_BLACK).apply();
                changeIcon();
                changeTheme();
            }
        }
    }

    private void changeIcon() {
        nightBlueCell.setIconChecked(Theme.getTheme() == Theme.THEME_NIGHT_BLUE);
        nightBlackCell.setIconChecked(Theme.getTheme() == Theme.THEME_NIGHT_BLACK);
    }

    private void changeTheme() {
        nightBlueCell.changeTheme();
        nightBlackCell.changeTheme();

        changeStatusBarColor();
        changeToolbarBackground();
        changeFragmentViewBackground();

        ((YouShows) activity.getApplication()).bus().send(new Events.ChangeTheme());
    }

    private void changeStatusBarColor() {
        if (Theme.getTheme() == Theme.THEME_NIGHT_BLUE) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.nb_primaryDark));
        } else if (Theme.getTheme() == Theme.THEME_NIGHT_BLACK) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.primaryDark));
        }
    }

    private void changeToolbarBackground() {
        ArgbEvaluator evaluator = new ArgbEvaluator();
        ObjectAnimator fragmentBackgroundAnimator = ObjectAnimator.ofObject(activity.toolbar, "backgroundColor", evaluator, 0, 0);

        int startColor = 0, endColor = 0;

        if (Theme.getTheme() == Theme.THEME_NIGHT_BLUE) {
            startColor = ContextCompat.getColor(activity, R.color.primary);
            endColor = ContextCompat.getColor(activity, R.color.nb_primary);
        } else if (Theme.getTheme() == Theme.THEME_NIGHT_BLACK) {
            startColor = ContextCompat.getColor(activity, R.color.nb_primary);
            endColor = ContextCompat.getColor(activity, R.color.primary);
        }

        fragmentBackgroundAnimator.setObjectValues(startColor, endColor);
        fragmentBackgroundAnimator.setDuration(300);
        fragmentBackgroundAnimator.start();
    }

    private void changeFragmentViewBackground() {
        ArgbEvaluator evaluator = new ArgbEvaluator();
        ObjectAnimator fragmentBackgroundAnimator = ObjectAnimator.ofObject(fragmentView, "backgroundColor", evaluator, 0, 0);

        int startColor = 0, endColor = 0;

        if (Theme.getTheme() == Theme.THEME_NIGHT_BLUE) {
            startColor = ContextCompat.getColor(activity, R.color.background);
            endColor = ContextCompat.getColor(activity, R.color.nb_background);
        } else if (Theme.getTheme() == Theme.THEME_NIGHT_BLACK) {
            startColor = ContextCompat.getColor(activity, R.color.nb_background);
            endColor = ContextCompat.getColor(activity, R.color.background);
        }

        fragmentBackgroundAnimator.setObjectValues(startColor, endColor);
        fragmentBackgroundAnimator.setDuration(300);
        fragmentBackgroundAnimator.start();
    }
}