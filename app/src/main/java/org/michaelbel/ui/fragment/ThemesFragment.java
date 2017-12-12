package org.michaelbel.ui.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.michaelbel.seriespicker.R;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.ui.MainActivity;
import org.michaelbel.ui.cell.RadioCell;

@SuppressWarnings("all")
public class ThemesFragment extends Fragment implements View.OnClickListener {

    private MainActivity activity;

    private RadioCell radioCell1;
    private RadioCell radioCell2;
    private LinearLayout fragmentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();

        fragmentView = new LinearLayout(activity);
        fragmentView.setOrientation(LinearLayout.VERTICAL);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));

        activity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        activity.toolbar.setNavigationOnClickListener(view -> activity.finishFragment());
        activity.toolbarTextView.setText(R.string.Themes);

        radioCell1 = new RadioCell(activity);
        radioCell1.setDivider(true);
        radioCell1.setOnClickListener(this);
        radioCell1.setText(R.string.ThemeLight);
        radioCell1.setChecked(Theme.getTheme());

        radioCell2 = new RadioCell(activity);
        radioCell2.setOnClickListener(this);
        radioCell2.setText(R.string.ThemeNight);
        radioCell2.setChecked(!Theme.getTheme());

        fragmentView.addView(radioCell1);
        fragmentView.addView(radioCell2);
        return fragmentView;
    }

    @Override
    public void onClick(View view) {
        SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (view == radioCell1) {
            if (!Theme.getTheme()) {
                editor.putBoolean("theme", true);
                editor.apply();

                radioCell1.setChecked(true);
                radioCell2.setChecked(false);
                changeTheme();
            }
        } else if (view == radioCell2) {
            if (Theme.getTheme()) {
                editor.putBoolean("theme", false);
                editor.apply();

                radioCell1.setChecked(false);
                radioCell2.setChecked(true);
                changeTheme();
            }
        }
    }

    private void changeTheme() {
        /*ActionBar bar = activity.getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xFF009688));

        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(0xFF00796B);*/

        activity.toolbar.setPopupTheme(Theme.popupTheme()); // fixme: do not work
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));
        radioCell1.changeTheme();
        radioCell2.changeTheme();
    }
}