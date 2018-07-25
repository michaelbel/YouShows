package org.michaelbel.youshows.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.youshows.Theme;
import org.michaelbel.youshows.YouShows;
import org.michaelbel.youshows.eventbus.Events;
import org.michaelbel.shows.R;
import org.michaelbel.youshows.ui.SettingsActivity;
import org.michaelbel.youshows.ui.view.cell.ThemeCell;

/**
 * Date: 08 MAY 2018
 * Time: 18:28 MSK
 *
 * @author Michael Bel
 */

public class ThemeFragment extends Fragment implements View.OnClickListener {

    private String[] themes;
    private SettingsActivity activity;

    private ThemeCell lightCell;
    private ThemeCell nightCell;
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

        fragmentView = new LinearLayout(activity);
        fragmentView.setOrientation(LinearLayout.VERTICAL);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));

        lightCell = new ThemeCell(activity, Theme.THEME_LIGHT);
        lightCell.setDivider(true);
        lightCell.setText(themes[0]);
        lightCell.setOnClickListener(this);
        lightCell.setIconChecked(Theme.getTheme() == Theme.THEME_LIGHT);
        lightCell.setIcon(AndroidExtensions.getIcon(activity, R.drawable.ic_theme_light, ContextCompat.getColor(activity, Theme.iconActiveColor())));
        fragmentView.addView(lightCell);

        nightCell = new ThemeCell(activity, Theme.THEME_NIGHT);
        nightCell.setDivider(false);
        nightCell.setText(themes[1]);
        nightCell.setOnClickListener(this);
        nightCell.setIconChecked(Theme.getTheme() == Theme.THEME_NIGHT);
        nightCell.setIcon(AndroidExtensions.getIcon(activity, R.drawable.ic_theme_night, ContextCompat.getColor(activity, Theme.iconActiveColor())));
        fragmentView.addView(nightCell);
        return fragmentView;
    }

    @Override
    public void onClick(View view) {
        if (view == lightCell) {
            if (!Theme.themeLight()) {
                activity.prefs.edit().putInt("theme", view == lightCell ? Theme.THEME_LIGHT : Theme.THEME_NIGHT).apply();
                changeCellTheme();
                changeFragmentTheme();
            }
        } else {
            if (Theme.themeLight()) {
                activity.prefs.edit().putInt("theme", view == lightCell ? Theme.THEME_LIGHT : Theme.THEME_NIGHT).apply();
                changeCellTheme();
                changeFragmentTheme();
            }
        }
    }

    private void changeCellTheme() {
        lightCell.changeTheme();
        lightCell.setIconChecked(Theme.getTheme() == Theme.THEME_LIGHT);
        lightCell.setIcon(AndroidExtensions.getIcon(activity, R.drawable.ic_theme_light, ContextCompat.getColor(activity, Theme.iconActiveColor())));

        nightCell.changeTheme();
        nightCell.setIconChecked(Theme.getTheme() == Theme.THEME_NIGHT);
        nightCell.setIcon(AndroidExtensions.getIcon(activity, R.drawable.ic_theme_night, ContextCompat.getColor(activity, Theme.iconActiveColor())));
    }

    private void changeFragmentTheme() {
        int statusBarColorStart = ContextCompat.getColor(activity, Theme.themeLight() ? R.color.n_primaryDark : R.color.primaryDark);
        int statusBarColorEnd = ContextCompat.getColor(activity, Theme.themeLight() ? R.color.primaryDark : R.color.n_primaryDark);

        int toolbarColorStart = ContextCompat.getColor(activity, Theme.themeLight() ? R.color.n_primary : R.color.primary);
        int toolbarColorEnd = ContextCompat.getColor(activity, Theme.themeLight() ? R.color.primary : R.color.n_primary);

        int fragmentColorStart = ContextCompat.getColor(activity, Theme.themeLight() ? R.color.n_background : R.color.background);
        int fragmentColorEnd = ContextCompat.getColor(activity, Theme.themeLight() ? R.color.background : R.color.n_background);

        ArgbEvaluator evaluator = new ArgbEvaluator();

        ObjectAnimator statusBarAnim = ObjectAnimator.ofObject(activity.getWindow(), "statusBarColor", evaluator, 0, 0);
        statusBarAnim.setObjectValues(statusBarColorStart, statusBarColorEnd);

        ObjectAnimator toolbarAnim = ObjectAnimator.ofObject(activity.toolbar, "backgroundColor", evaluator, 0, 0);
        toolbarAnim.setObjectValues(toolbarColorStart, toolbarColorEnd);

        ObjectAnimator fragmentAnim = ObjectAnimator.ofObject(fragmentView, "backgroundColor", evaluator, 0, 0);
        fragmentAnim.setObjectValues(fragmentColorStart, fragmentColorEnd);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(statusBarAnim, toolbarAnim, fragmentAnim);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new DecelerateInterpolator(2));
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                activity.changeSettingsFragmentTheme();
                ((YouShows) activity.getApplication()).bus().send(new Events.ChangeTheme());
            }
        });
        AndroidExtensions.runOnUIThread(animatorSet:: start);
    }
}