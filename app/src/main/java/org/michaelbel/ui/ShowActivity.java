package org.michaelbel.ui;

import android.content.Intent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.realm.RealmDb;
import org.michaelbel.app.rest.ApiFactory;
import org.michaelbel.app.rest.ApiService;
import org.michaelbel.app.rest.model.Season;
import org.michaelbel.app.rest.model.Show;
import org.michaelbel.app.Theme;
import org.michaelbel.shows.R;
import org.michaelbel.ui.fragment.ShowFragment;
import org.michaelbel.ui.view.BackdropView;

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

public class ShowActivity extends AppCompatActivity {

    public int extraId;
    public String extraName;
    public String extraBackdrop;
    public String extraOverview;

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

        getWindow().setStatusBarColor(ContextCompat.getColor(this, Theme.Color.primaryDark()));

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));

        extraId = getIntent().getIntExtra("id", 0);
        extraName = getIntent().getStringExtra("name");
        extraOverview = getIntent().getStringExtra("overview");
        extraBackdrop = getIntent().getStringExtra("backdropPath");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        scrollView = findViewById(R.id.scroll_view);

        collapsingToolbarLayout = findViewById(R.id.collapsing_layout);
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, Theme.Color.primary()));
        collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(this, android.R.color.transparent));
        //setCollapsingLabel(RealmDb.getShowStatus(extraId));

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(extraName);
        /*toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.fullScroll(NestedScrollView.FOCUS_UP);
            }
        });*/

        collapsingView = findViewById(R.id.backdrop_image);
        collapsingView.setImage("http://image.tmdb.org/t/p/original/" + extraBackdrop);

        followButton = findViewById(R.id.follow_fab);
        followButton.setClickable(false);
        followButton.setLongClickable(false);
        followButton.setOnClickListener(view -> {
            boolean follow = RealmDb.isShowFollow(extraId);
            followShow(!follow);
            changeFabStyle(!follow);
        });
        followButton.setOnLongClickListener(v -> {
            boolean follow = RealmDb.isShowFollow(extraId);
            collapsingView.showFollowHint(false, !follow);
            AndroidExtensions.startVibrate(15);
            return true;
        });
        followButtonSwapAnimation();

        fragment = (ShowFragment) getSupportFragmentManager().findFragmentById(R.id.showFragment);
        fragment.setName(extraName);
        fragment.setOverview(extraOverview);

        loadDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.Share)
            .setIcon(R.drawable.ic_anim_share)
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

    private void changeFabStyle(boolean follow) {
        followButton.setImageDrawable(follow ?
            Theme.getIcon(R.drawable.ic_done, ContextCompat.getColor(this, R.color.iconActive)) :
            Theme.getIcon(R.drawable.ic_eye, ContextCompat.getColor(this, R.color.iconActive))
        );
        followButton.setBackgroundTintList(follow ?
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green)) :
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.accent))
        );
    }

    private void followButtonSwapAnimation() {
        Drawable avd = AnimatedVectorDrawableCompat.create(this, R.drawable.ic_anim_progressbar);
        followButton.setImageDrawable(avd);
        if (avd != null) {
            ((Animatable) avd).start();
        }
    }

    private void loadDetails() {
        ApiService service = ApiFactory.createService(ApiService.class, ApiFactory.TMDB_API_ENDPOINT);
        service.details(extraId, ApiFactory.TMDB_API_KEY, ApiFactory.getLanguage()).enqueue(new Callback<Show>() {
            @Override
            public void onResponse(@NonNull Call<Show> call, @NonNull Response<Show> response) {
                if (response.isSuccessful()) {
                    Show show = response.body();
                    if (show != null) {
                        setCollapsingLabel(show.inProduction);

                        if (!RealmDb.isShowExist(extraId)) {
                            RealmDb.insertOrUpdateShow(show);
                        } else {
                            RealmDb.updateShowViewsCount(extraId);
                        }

                        fragment.setSeasons(show);
                        fragment.setInfo(show);

                        fragmentLoaded();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Show> call, @NonNull Throwable t) {
                Log.e("2580", t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void fragmentLoaded() {
        changeFabStyle(RealmDb.isShowFollow(extraId));
        followButton.setClickable(true);
        followButton.setLongClickable(true);
    }

    private void followShow(boolean status) {
        RealmDb.followShow(extraId, status);
    }

    private void setCollapsingLabel(boolean production) {
        collapsingView.setLabel(production ? getString(R.string.ShowInProduction) : getString(R.string.ShowIsFinished));
    }

    public void startSeason(Season season) {
        Intent intent = new Intent(this, SeasonActivity.class);
        intent.putExtra("showId", extraId);
        intent.putExtra("season", season);
        startActivity(intent);
    }
}