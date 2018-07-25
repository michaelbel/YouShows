package org.michaelbel.youshows.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.youshows.Theme;
import org.michaelbel.youshows.YouShows;
import org.michaelbel.youshows.eventbus.Events;
import org.michaelbel.youshows.realm.RealmDb;
import org.michaelbel.youshows.rest.ApiFactory;
import org.michaelbel.youshows.rest.ApiService;
import org.michaelbel.youshows.rest.model.Season;
import org.michaelbel.youshows.rest.model.Show;
import org.michaelbel.shows.R;
import org.michaelbel.youshows.ui.fragment.ShowFragment;
import org.michaelbel.youshows.ui.view.BackdropView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Date: 19 MAR 2018
 * Time: 13:20 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("CheckResult")
public class ShowActivity extends AppCompatActivity {

    public int extraId;
    public String extraName;
    public String extraBackdrop;
    public String extraOverview;

    private Context context;
    private SharedPreferences prefs;
    private ShowFragment fragment;

    public Toolbar toolbar;
    public TextView toolbarTitle;
    public BackdropView collapsingView;
    public NestedScrollView scrollView;
    public FloatingActionButton followButton;
    public CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        context = ShowActivity.this;

        getWindow().setStatusBarColor(ContextCompat.getColor(context, Theme.primaryDarkColor()));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.transparent20));

        prefs = getSharedPreferences("mainconfig", MODE_PRIVATE);

        extraId = getIntent().getIntExtra("id", 0);
        extraName = getIntent().getStringExtra("name");
        extraOverview = getIntent().getStringExtra("overview");
        extraBackdrop = getIntent().getStringExtra("backdropPath");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        scrollView = findViewById(R.id.scroll_view);
        scrollView.setBackgroundColor(ContextCompat.getColor(context, Theme.backgroundColor()));

        collapsingToolbarLayout = findViewById(R.id.collapsing_layout);
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context, Theme.primaryColor()));
        collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(context, android.R.color.transparent));

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(extraName);
        //toolbarTitle.setOnClickListener(view -> scrollView.fullScroll(NestedScrollView.FOCUS_UP));

        collapsingView = findViewById(R.id.backdrop_image);
        collapsingView.setImage(extraBackdrop);
        collapsingView.labelView.setOnLongClickListener(view -> {
            boolean label = prefs.getBoolean("collapsing_label", true);
            prefs.edit().putBoolean("collapsing_label", !label).apply();
            AndroidExtensions.vibrate(15);
            setCollapsingLabel();
            return true;
        });
        setCollapsingLabel();

        followButton = findViewById(R.id.follow_fab);
        followButton.setClickable(false);
        followButton.setLongClickable(false);
        followButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, Theme.fabShowFollowColor())));
        followButton.setOnClickListener(view -> {
            boolean follow = RealmDb.isShowFollow(extraId);
            followShow(!follow);
            changeFabStyle(!follow);
        });
        followButton.setOnLongClickListener(v -> {
            boolean follow = RealmDb.isShowFollow(extraId);
            collapsingView.showFollowHint(false, !follow);
            AndroidExtensions.vibrate(15);
            return true;
        });
        if (RealmDb.isShowExist(extraId)) {
            changeFabStyle(RealmDb.isShowFollow(extraId));
        } else {
            followButtonSwapAnimation();
        }

        fragment = (ShowFragment) getSupportFragmentManager().findFragmentById(R.id.showFragment);
        fragment.setName(extraName);
        fragment.setOverview(extraOverview);
        setDetails();
        setSeasons();
        loadDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean animations = prefs.getBoolean("animations", true);

        menu.add(R.string.Share)
            .setIcon(animations ? R.drawable.ic_anim_share : R.drawable.ic_share)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            .setOnMenuItemClickListener(menuItem -> {
                Drawable icon = menu.getItem(0).getIcon();
                if (icon instanceof Animatable) {
                    ((Animatable) icon).start();
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, String.format(Locale.US, ApiFactory.TMDB_MOVIE, extraId));
                startActivity(Intent.createChooser(intent, getString(R.string.ShareVia)));
                return true;
            });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((YouShows) getApplication()).bus().toObservable().subscribe(object -> {
            if (object instanceof Events.UpdateProgress) {
                setCollapsingLabel();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
    }

    private void setCollapsingLabel() {
        boolean label = prefs.getBoolean("collapsing_label", true);
        if (RealmDb.isShowExist(extraId)) {
            if (label) {
                boolean production = RealmDb.getShowStatus(extraId);
                collapsingView.setLabel(production ? getString(R.string.ShowInProduction) : getString(R.string.ShowIsFinished));
            } else {
                float progress = RealmDb.getProgress(extraId);
                String formatted = new DecimalFormat("#0.00").format(progress);
                collapsingView.setLabel(getString(R.string.Progress) + " " + formatted + "%");
            }
        }
    }

    private void setDetails() {
        //fragment.setGenres(RealmDb.getGenres(extraId));
        //fragment.setOriginalCountries(RealmDb.getCountries(extraId));
        //fragment.setCompanies(RealmDb.getCompanies(extraId));

        if (RealmDb.isShowExist(extraId)) {
            fragment.setOriginalName(RealmDb.getOriginalName(extraId));
            fragment.setStatus(RealmDb.getStatus(extraId));
            fragment.setType(RealmDb.getType(extraId));
            fragment.setDates(RealmDb.getFirstAirDate(extraId), RealmDb.getLastAirDate(extraId));
            fragment.setHomepage(RealmDb.getHomepage(extraId));
        }
    }

    private void setSeasons() {
        if (!RealmDb.isShowSeasonsEmpty(extraId)) {
            fragment.setSeasons(extraId, RealmDb.getShowSeasons(extraId));
        }
    }

    private void followButtonSwapAnimation() {
        Drawable avd = AnimatedVectorDrawableCompat.create(context, Theme.fabProgressBar());
        followButton.setImageDrawable(avd);
        if (avd != null) {
            ((Animatable) avd).start();
        }
        followButton.setFocusable(false);
        followButton.setClickable(false);
        followButton.setLongClickable(false);
    }

    private void changeFabStyle(boolean follow) {
        followButton.setImageDrawable(follow ?
            AndroidExtensions.getIcon(context, R.drawable.ic_done, ContextCompat.getColor(context, R.color.white)) :
            AndroidExtensions.getIcon(context, R.drawable.ic_eye_plus, ContextCompat.getColor(context, Theme.fabShowFollowIconColor()))
        );
        followButton.setBackgroundTintList(follow ?
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)) :
            ColorStateList.valueOf(ContextCompat.getColor(context, Theme.fabShowFollowColor()))
        );
    }

    private void loadDetails() {
        ApiService service = ApiFactory.createService(ApiService.class, ApiFactory.TMDB_API_ENDPOINT);
        service.details(extraId, ApiFactory.TMDB_API_KEY, ApiFactory.getLanguage()).enqueue(new Callback<Show>() {
            @Override
            public void onResponse(@NonNull Call<Show> call, @NonNull Response<Show> response) {
                if (response.isSuccessful()) {
                    Show show = response.body();
                    if (show != null) {
                        if (!RealmDb.isShowExist(extraId)) {
                            RealmDb.insertOrUpdateShow(show);
                            RealmDb.updateGenres(extraId, show.genres);
                            RealmDb.updateCountries(extraId, show.countries);
                            RealmDb.updateCompanies(extraId, show.companies);

                            setCollapsingLabel();
                            setDetails();
                        }

                        if (RealmDb.isShowSeasonsEmpty(extraId)) {
                            fragment.setSeasons(show);
                        }

                        fragment.setGenres(show.genres);
                        fragment.setOriginalCountries(show.countries);
                        fragment.setCompanies(show.companies);
                        fragmentLoaded();

                        updateData(show);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Show> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateData(Show show) {
        RealmDb.updateShowViewsCount(extraId);
        RealmDb.updateStatus(extraId, show.inProduction);
        RealmDb.updateOriginalName(extraId, show.originalName);
        RealmDb.updateStatus(extraId, show.status);
        RealmDb.updateType(extraId, show.type);
        RealmDb.updateLastAirDate(extraId, show.lastAirDate);
        RealmDb.updateHomepage(extraId, show.homepage);

        List<Season> list = new ArrayList<>();
        for (Season season : show.seasons) {
            if (season.seasonNumber != 0) {
                list.add(season);
            }
        }
        RealmDb.updateSeasonsList(extraId, list);
    }

    private void fragmentLoaded() {
        changeFabStyle(RealmDb.isShowFollow(extraId));
        followButton.setClickable(true);
        followButton.setLongClickable(true);
    }

    private void followShow(boolean status) {
        RealmDb.followShow(extraId, status);
        if (status) {
            RealmDb.updateStartFollowingDate(extraId, AndroidExtensions.getCurrentDateAndTime());
        }
    }

    public void startSeason(Season season) {
        Intent intent = new Intent(context, SeasonActivity.class);
        intent.putExtra("showId", extraId);
        intent.putExtra("seasonId", season.seasonId);
        intent.putExtra("seasonName", season.name);
        intent.putExtra("seasonNumber", season.seasonNumber);
        intent.putExtra("seasonEpisodeCount", season.episodeCount);
        startActivity(intent);
    }
}