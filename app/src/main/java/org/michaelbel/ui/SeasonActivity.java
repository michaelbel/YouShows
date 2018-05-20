package org.michaelbel.ui;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.michaelbel.app.Theme;
import org.michaelbel.app.realm.RealmDb;
import org.michaelbel.app.rest.model.Season;
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
    public Season season;

    public FloatingActionButton fabButton;

    private EpisodesFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, Theme.Color.primaryDark()));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, Theme.Color.primary()));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        showId = getIntent().getIntExtra("showId", 0);
        season = (Season) getIntent().getSerializableExtra("season");

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(season.name);

        fabButton = findViewById(R.id.fab);
        fabButton.setVisibility(View.INVISIBLE);
        fabButton.setClickable(false);
        fabButton.setLongClickable(false);
        fabButton.setOnClickListener(view -> markSeasonsAsWatched(showId, season));
        changeFabStyle(RealmDb.isSeasonWatched(showId, season));

        fragment = (EpisodesFragment) getSupportFragmentManager().findFragmentById(R.id.seasonFragment);
        fragment.showEpisodes(showId, season);
    }

    public void changeFabStyle(boolean watched) {
        fabButton.setImageDrawable(watched ?
            Theme.getIcon(R.drawable.ic_done, ContextCompat.getColor(this, R.color.iconActive)) :
            Theme.getIcon(R.drawable.ic_plus, ContextCompat.getColor(this, R.color.iconActive))
        );
        fabButton.setBackgroundTintList(watched ?
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)) :
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.accent))
        );
    }

    public void changeFabStyle() {
        boolean watched = RealmDb.isSeasonWatched(showId, season);

        fabButton.setImageDrawable(watched ?
            Theme.getIcon(R.drawable.ic_done, ContextCompat.getColor(this, R.color.iconActive)) :
            Theme.getIcon(R.drawable.ic_plus, ContextCompat.getColor(this, R.color.iconActive))
        );
        fabButton.setBackgroundTintList(watched ?
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)) :
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.accent))
        );
    }

    public void markSeasonsAsWatched(int showId, Season season) {
        if (RealmDb.isSeasonWatched(showId, season)) {
            fragment.removeEpisodesFromRealm();
            changeFabStyle(false);
        } else {
            fragment.addEpisodesToRealm();
            changeFabStyle(true);
        }
    }
}