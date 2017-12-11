package org.michaelbel.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.michaelbel.seriespicker.R;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.ui.fragment.MainFragment;

@SuppressWarnings("all")
public class MainActivity extends AppCompatActivity {

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
        setRootFragment(new MainFragment());
    }

    @Override
    public void setTheme(int resid) {
        super.setTheme(Theme.getTheme() ? R.style.ThemeLight : R.style.ThemeNight);
    }

    public void setRootFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout, fragment)
                .commit();
    }

    public void startFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fragment_layout, fragment)
                .commit();
    }

    public void startFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.fragment_layout, fragment)
                .addToBackStack(tag)
                .commit();
    }

    public void finishFragment() {
        getSupportFragmentManager().popBackStack();
    }
}
