package org.michaelbel.ui.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ui_old.adapter.Holder;
import org.michaelbel.old.ui_old.view.RecyclerListView;
import org.michaelbel.realm.RealmDb;
import org.michaelbel.rest.ApiFactory;
import org.michaelbel.rest.ApiService;
import org.michaelbel.rest.model.Episode;
import org.michaelbel.rest.model.SeasonDetails;
import org.michaelbel.seriespicker.R;
import org.michaelbel.ui.SeasonActivity;
import org.michaelbel.ui.view.EpisodeView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Date: 19 MAR 2018
 * Time: 12:12 MSK
 *
 * @author Michael Bel
 */

public class SeasonFragment extends Fragment {

    private SeasonActivity activity;
    private SeasonAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private TextView emptyView;
    private ProgressBar progressBar;
    private RecyclerListView recyclerView;

    private Menu menu;
    private boolean iconSelectMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SeasonActivity) getActivity();
        //setHasOptionsMenu(true);

        //SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        iconSelectMode = false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        this.menu = menu;

        menu.add("Select all")
            .setIcon(iconSelectMode ? R.drawable.ic_check_box : R.drawable.ic_check_box_outline)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            .setOnMenuItemClickListener(menuItem -> {
                changeSelectIcon();
                iconSelectMode = !iconSelectMode;

                if (iconSelectMode) {
                    // Убрать все галки
                } else {
                    // Всех выделить
                }
                return true;
            });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout fragmentLayout = new FrameLayout(activity);
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.background));

        progressBar = new ProgressBar(activity);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.accent), PorterDuff.Mode.MULTIPLY);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        fragmentLayout.addView(progressBar);

        emptyView = new TextView(activity);
        emptyView.setText("No episodes");
        emptyView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 0, 24, 0));
        //fragmentLayout.addView(emptyView);

        adapter = new SeasonAdapter();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnItemClickListener((view, position) -> {
            if (view instanceof EpisodeView) {
                Episode episode = adapter.getEpisodes().get(position);
                boolean exist = RealmDb.isEpisodeExist(activity.showRealm.showId, activity.season.id, episode.id);
                if (!exist) {
                    RealmDb.addEpisode(activity.showRealm.showId, activity.season.id, episode);
                } else {
                    RealmDb.removeEpisode(activity.showRealm.showId, activity.season.id, episode.id);
                }

                if (!RealmDb.isShowExist(activity.showRealm.showId)) {
                    RealmDb.addShow(activity.showRealm);
                }

                ((EpisodeView) view).setChecked(!exist);
            }
        });
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        fragmentLayout.addView(recyclerView);
        return fragmentLayout;
    }

    private void changeSelectIcon() {
        if (menu != null) {
            if (iconSelectMode) {
                menu.getItem(0).setIcon(R.drawable.ic_check_box_outline);
            } else {
                menu.getItem(0).setIcon(R.drawable.ic_check_box);
            }
        }
    }

    public void showEpisodes(int showId, int seasonNumber) {
        ApiService service = ApiFactory.createService(ApiService.class, ApiFactory.TMDB_API_ENDPOINT);
        service.season(showId, seasonNumber, ApiFactory.TMDB_API_KEY, "en").enqueue(new Callback<SeasonDetails>() {
            @Override
            public void onResponse(@NonNull Call<SeasonDetails> call, @NonNull Response<SeasonDetails> response) {
                if (response.isSuccessful()) {
                    SeasonDetails seasonDetails = response.body();
                    if (seasonDetails != null) {
                        adapter.addEpisodes(seasonDetails.episodes);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SeasonDetails> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private class SeasonAdapter extends RecyclerView.Adapter {

        private List<Episode> episodes;

        private SeasonAdapter() {
            episodes = new ArrayList<>();
        }

        private void addEpisodes(List<Episode> results) {
            episodes.addAll(results);
            notifyItemRangeInserted(episodes.size() + 1, results.size());
        }

        public List<Episode> getEpisodes() {
            return episodes;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            return new Holder(new EpisodeView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Episode episode = episodes.get(position);

            EpisodeView view = (EpisodeView) holder.itemView;
            view.setName(episode.name);
            view.setNumberAndDate("Episode #" + episode.episodeNumber + " | " + episode.airDate);
            view.setChecked(RealmDb.isEpisodeExist(activity.showRealm.showId, activity.season.id, episode.id));
            view.setDivider(position != episodes.size() - 1);
        }

        @Override
        public int getItemCount() {
            return episodes != null ? episodes.size() : 0;
        }
    }
}