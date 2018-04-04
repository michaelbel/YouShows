package org.michaelbel.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.michaelbel.rest.model.Season;
import org.michaelbel.rest.model.Show;
import org.michaelbel.seriespicker.R;
import org.michaelbel.ui.fragment.SeasonFragment;

/**
 * Date: 19 MAR 2018
 * Time: 13:20 MSK
 *
 * @author Michael Bel
 */

public class SeasonActivity extends AppCompatActivity {

    public Show show;
    public Season season;

    public Toolbar toolbar;
    public TextView toolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);

        toolbar.setNavigationIcon(R.drawable.ic_clear);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        show = (Show) getIntent().getSerializableExtra("show");
        season = (Season) getIntent().getSerializableExtra("season");

        toolbarTitle.setText(season.name);

        SeasonFragment fragment = (SeasonFragment) getSupportFragmentManager().findFragmentById(R.id.seasonFragment);
        fragment.showEpisodes(show.showId, season.seasonNumber);
    }
}