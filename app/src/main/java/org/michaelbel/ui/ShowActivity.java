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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.michaelbel.old.Theme;
import org.michaelbel.realm.RealmDb;
import org.michaelbel.rest.ApiFactory;
import org.michaelbel.rest.ApiService;
import org.michaelbel.rest.model.Season;
import org.michaelbel.rest.model.Show;
import org.michaelbel.seriespicker.R;
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

    public Show showExtra;

    public Toolbar toolbar;
    public TextView toolbarTitle;
    public BackdropView collapsingView;
    public NestedScrollView scrollView;
    public FloatingActionButton followButton;
    public CollapsingToolbarLayout collapsingToolbarLayout;

    private ShowFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        showExtra = (Show) getIntent().getSerializableExtra("show");
        if (showExtra == null) {
            Show s = new Show();
            s.showId = getIntent().getIntExtra("id", 0);
            s.name = getIntent().getStringExtra("name");
            s.overview = getIntent().getStringExtra("overview");
            s.backdropPath = getIntent().getStringExtra("backdropPath");
            showExtra = s;
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(0x33000000);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        scrollView = findViewById(R.id.scroll_view);
        collapsingToolbarLayout = findViewById(R.id.collapsing_layout);

        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(showExtra.name);
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.fullScroll(NestedScrollView.FOCUS_UP);
            }
        });

        collapsingView = findViewById(R.id.backdrop_image);
        collapsingView.setLabel("Loading...");
        collapsingView.setImage("http://image.tmdb.org/t/p/original/" + showExtra.backdropPath);

        followButton = findViewById(R.id.follow_fab);
        followButton.setClickable(false);
        followButton.setLongClickable(false);
        followButton.setOnClickListener(view -> {
            boolean follow = RealmDb.isShowFollow(showExtra.showId);
            followShow(!follow);
            changeFabStyle(!follow);
        });
        changeFabStyle(RealmDb.isShowFollow(showExtra.showId));

        fragment = (ShowFragment) getSupportFragmentManager().findFragmentById(R.id.showFragment);
        fragment.setName(showExtra.name);
        fragment.setOverview(showExtra.overview);

        loadDetails(showExtra.showId);
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
                intent.putExtra(Intent.EXTRA_TEXT, String.format(Locale.US, ApiFactory.TMDB_MOVIE, showExtra.showId));
                startActivity(Intent.createChooser(intent, getString(R.string.ShareVia)));
                return true;
            });

        return super.onCreateOptionsMenu(menu);
    }

    private void changeFabStyle(boolean follow) {
        followButton.setImageDrawable(follow ?
            Theme.getIcon(R.drawable.ic_done, ContextCompat.getColor(this, R.color.iconActive)) :
            Theme.getIcon(R.drawable.ic_plus, ContextCompat.getColor(this, R.color.iconActive))
        );
        followButton.setBackgroundTintList(follow ?
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.md_green_A700)) :
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.accent))
        );
    }

    private void loadDetails(int id) {
        ApiService service = ApiFactory.createService(ApiService.class, ApiFactory.TMDB_API_ENDPOINT);
        service.details(id, ApiFactory.TMDB_API_KEY, ApiFactory.TMDB_EN_US).enqueue(new Callback<Show>() {
            @Override
            public void onResponse(@NonNull Call<Show> call, @NonNull Response<Show> response) {
                if (response.isSuccessful()) {
                    Show show = response.body();
                    if (show != null) {
                        setCollapsingLabel(show.inProduction);

                        if (!RealmDb.isShowExist(showExtra.showId)) {
                            RealmDb.insertOrUpdateShow(show);
                        } else {
                            RealmDb.updateShowViewsCount(showExtra.showId);
                        }

                        followButton.setClickable(true);
                        followButton.setLongClickable(true);

                        fragment.setSeasons(show);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Show> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void followShow(boolean status) {
        RealmDb.followShow(showExtra.showId, status);
    }

    private void setCollapsingLabel(boolean production) {
        // Add air date of last episode here.
        collapsingView.setLabel(production ? "Шоу еще выходит" : "Show is Finished");
    }

    public void startSeason(Season season) {
        Intent intent = new Intent(this, SeasonActivity.class);
        intent.putExtra("show", showExtra);
        intent.putExtra("season", season);
        startActivity(intent);
    }
}