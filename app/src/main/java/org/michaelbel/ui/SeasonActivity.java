package org.michaelbel.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.Theme;
import org.michaelbel.app.realm.RealmDb;
import org.michaelbel.shows.R;
import org.michaelbel.ui.fragment.EpisodesFragment;

/**
 * Date: 19 MAR 2018
 * Time: 13:20 MSK
 *
 * @author Michael Bel
 */

public class SeasonActivity extends AppCompatActivity {

    public int showId;
    public int seasonId;
    public int seasonNumber;
    public int seasonEpisodeCount;
    public String seasonName;

    public TextView toolbarTitle;
    public FloatingActionButton fabButton;

    private EpisodesFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, Theme.primaryDarkColor()));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, Theme.primaryColor()));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        showId = getIntent().getIntExtra("showId", 0);
        seasonId = getIntent().getIntExtra("seasonId", 0);
        seasonNumber = getIntent().getIntExtra("seasonNumber", 0);
        seasonEpisodeCount = getIntent().getIntExtra("seasonEpisodeCount", 0);
        seasonName = getIntent().getStringExtra("seasonName");

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(seasonName);

        fabButton = findViewById(R.id.fab);
        fabButton.setClickable(false);
        fabButton.setLongClickable(false);
        fabButton.setVisibility(View.INVISIBLE);
        fabButton.setOnClickListener(view -> markSeasonsAsWatched());
        changeFabStyle(RealmDb.isSeasonWatched(showId, seasonId, seasonEpisodeCount));

        fragment = new EpisodesFragment();
        startFragment(fragment);
    }

    public void startFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_view1, fragment)
            .commit();
    }

    public void changeFabStyle(boolean watched) {
        fabButton.setImageDrawable(watched ?
            AndroidExtensions.getIcon(this, R.drawable.ic_done, ContextCompat.getColor(this, R.color.white)) :
            AndroidExtensions.getIcon(this, R.drawable.ic_plus, ContextCompat.getColor(this, Theme.fabShowFollowIconColor()))
        );

        fabButton.setBackgroundTintList(watched ?
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)) :
            ColorStateList.valueOf(ContextCompat.getColor(this, Theme.fabShowFollowColor()))
        );
    }

    public void markSeasonsAsWatched() {
        if (RealmDb.isSeasonWatched(showId, seasonId, seasonEpisodeCount)) {
            fragment.markSeasonAsWatch(false);
            changeFabStyle(false);
        } else {
            fragment.markSeasonAsWatch(true);
            changeFabStyle(true);
        }
    }
}