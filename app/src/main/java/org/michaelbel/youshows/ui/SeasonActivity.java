package org.michaelbel.youshows.ui;

import android.content.Context;
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

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.youshows.Theme;
import org.michaelbel.youshows.realm.RealmDb;
import org.michaelbel.shows.R;
import org.michaelbel.youshows.ui.fragment.EpisodesFragment;

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

    private Context context;
    private EpisodesFragment fragment;

    public TextView toolbarTitle;
    public FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);

        context = SeasonActivity.this;

        getWindow().setStatusBarColor(ContextCompat.getColor(context, Theme.primaryDarkColor()));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setBackgroundColor(ContextCompat.getColor(context, Theme.primaryColor()));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        showId = getIntent().getIntExtra("showId", 0);
        seasonId = getIntent().getIntExtra("seasonId", 0);
        seasonNumber = getIntent().getIntExtra("seasonNumber", 0);
        seasonEpisodeCount = getIntent().getIntExtra("seasonEpisodeCount", 0);
        seasonName = getIntent().getStringExtra("seasonName");

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(seasonName);

        fab = findViewById(R.id.fab);
        fab.setClickable(false);
        fab.setLongClickable(false);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(view -> markSeasonsAsWatched());
        changeFabStyle(RealmDb.isSeasonWatched(showId, seasonId, seasonEpisodeCount));

        fragment = new EpisodesFragment();
        startFragment(fragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
    }

    public void startFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_view1, fragment)
            .commit();
    }

    public void changeFabStyle(boolean watched) {
        fab.setImageDrawable(watched ?
            AndroidExtensions.getIcon(context, R.drawable.ic_done, ContextCompat.getColor(context, R.color.white)) :
            AndroidExtensions.getIcon(context, R.drawable.ic_plus, ContextCompat.getColor(context, Theme.fabShowFollowIconColor()))
        );

        fab.setBackgroundTintList(watched ?
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)) :
            ColorStateList.valueOf(ContextCompat.getColor(context, Theme.fabShowFollowColor()))
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