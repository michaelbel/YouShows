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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.ShowsApp;
import org.michaelbel.app.Theme;
import org.michaelbel.app.eventbus.Events;
import org.michaelbel.app.realm.RealmDb;
import org.michaelbel.app.rest.ApiFactory;
import org.michaelbel.app.rest.ApiService;
import org.michaelbel.app.rest.model.Episode;
import org.michaelbel.app.rest.model.Season;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ui_old.adapter.Holder;
import org.michaelbel.old.ui_old.view.RecyclerListView;
import org.michaelbel.shows.R;
import org.michaelbel.ui.SeasonActivity;
import org.michaelbel.ui.adapter.holder.LoadingHolder;
import org.michaelbel.ui.view.EpisodeOverview;
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

public class EpisodesFragment extends Fragment {

    private SeasonAdapter adapter;
    private SeasonActivity activity;

    private TextView emptyView;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SeasonActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout fragmentLayout = new FrameLayout(activity);
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.Color.background()));

        LinearLayout contentLayout = new LinearLayout(activity);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentLayout.addView(contentLayout);

        progressBar = new ProgressBar(activity);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, Theme.Color.accent()), PorterDuff.Mode.MULTIPLY);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        fragmentLayout.addView(progressBar);

        emptyView = new TextView(activity);
        emptyView.setText(R.string.NoEpisodes);
        emptyView.setVisibility(View.GONE);
        emptyView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 0, 24, 0));
        fragmentLayout.addView(emptyView);

        adapter = new SeasonAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        RecyclerListView recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnItemClickListener((view, position) -> {
            if (view instanceof EpisodeView) {
                Episode episode = adapter.getEpisodes().get(position);
                addEpisodeToRealm(episode, view);
            }
        });
        recyclerView.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        contentLayout.addView(recyclerView);
        return fragmentLayout;
    }

    public void showEpisodes(int showId, Season s) {
        ApiService service = ApiFactory.createService(ApiService.class, ApiFactory.TMDB_API_ENDPOINT);
        service.season(showId, s.seasonNumber, ApiFactory.TMDB_API_KEY, ApiFactory.getLanguage()).enqueue(new Callback<Season>() {
            @Override
            public void onResponse(@NonNull Call<Season> call, @NonNull Response<Season> response) {
                if (response.isSuccessful()) {
                    Season season = response.body();
                    if (season != null) {
                        if (season.episodes.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                        }

                        Episode ss = new Episode();
                        ss.airDate = AndroidExtensions.formatDate(season.airDate);
                        ss.overview = season.overview;
                        ss.seasonId = s.episodeCount;
                        adapter.addOverview(ss);

                        adapter.addEpisodes(season.episodes);
                        progressBar.setVisibility(View.GONE);
                        emptyView.setVisibility(View.GONE);
                        activity.showFab();

                        if (!RealmDb.isSeasonExist(showId, season.seasonId)) {
                            RealmDb.insertOrUpdateSeason(showId, season);
                            RealmDb.setSeasonEpisodesCount(showId, s.seasonId, s.episodeCount);
                        } else {
                            RealmDb.setSeasonEpisodesCount(showId, s.seasonId, s.episodeCount);
                        }
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Season> call, @NonNull Throwable t) {
                t.printStackTrace();

                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                // todo Update with swipe
            }
        });
    }

    private void addEpisodeToRealm(Episode episode, View checkedView) {
        boolean exist = RealmDb.isEpisodeExist(activity.showId, activity.season.seasonId, episode.episodeId);

        if (exist) {
            boolean watched = RealmDb.isEpisodeWatched(activity.showId, activity.season.seasonId, episode.episodeId);
            RealmDb.markEpisodeAsWatched(activity.showId, activity.season.seasonId, episode.episodeId, !watched);
            ((EpisodeView) checkedView).setChecked(!watched, true);
        } else {
            RealmDb.insertOrUpdateEpisode(activity.showId, activity.season.seasonId, episode);
            RealmDb.markEpisodeAsWatched(activity.showId, activity.season.seasonId, episode.episodeId, true);
            ((EpisodeView) checkedView).setChecked(true, true);
        }

        activity.changeFabStyle();
        ((ShowsApp) activity.getApplication()).bus().send(new Events.UpdateSeasonView());
    }

    public void addEpisodesToRealm() {
        List<Episode> list = adapter.getEpisodes();
        for (Episode episode : list) {
            if (episode != list.get(0)) { // Item != Overview
                boolean exist = RealmDb.isEpisodeExist(activity.showId, activity.season.seasonId, episode.episodeId);

                if (exist) {
                    RealmDb.markEpisodeAsWatched(activity.showId, activity.season.seasonId, episode.episodeId, true);
                } else {
                    RealmDb.insertOrUpdateEpisode(activity.showId, activity.season.seasonId, episode);
                    RealmDb.markEpisodeAsWatched(activity.showId, activity.season.seasonId, episode.episodeId, true);
                }
            }
        }

        adapter.notifyDataSetChanged();
        ((ShowsApp) activity.getApplication()).bus().send(new Events.UpdateSeasonView());
    }

    public void removeEpisodesFromRealm() {
        for (Episode episode : adapter.getEpisodes()) {
            RealmDb.markEpisodeAsWatched(activity.showId, activity.season.seasonId, episode.episodeId, false);
        }

        adapter.notifyDataSetChanged();
        ((ShowsApp) activity.getApplication()).bus().send(new Events.UpdateSeasonView());
    }

    private class SeasonAdapter extends RecyclerView.Adapter {

        private final int ITEM_OVERVIEW = 1;
        private final int ITEM_EPISODE = 2;

        private List<Episode> episodes;

        private SeasonAdapter() {
            episodes = new ArrayList<>();
        }

        public void addOverview(Episode ep) {
            episodes.add(ep);
            notifyItemInserted(episodes.size() - 1);
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
            RecyclerView.ViewHolder viewHolder = null;

            if (type == ITEM_EPISODE) {
                viewHolder = new Holder(new EpisodeView(parent.getContext()));
            } else if (type == ITEM_OVERVIEW) {
                viewHolder = new LoadingHolder(new EpisodeOverview(parent.getContext()));
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Episode episode = episodes.get(position);

            if (getItemViewType(position) == ITEM_EPISODE) {
                EpisodeView view = (EpisodeView) ((Holder) holder).itemView;
                view.setName(episode.name);
                view.setChecked(RealmDb.isEpisodeWatched(activity.showId, activity.season.seasonId, episode.episodeId), false);
                view.setNumber(getString(R.string.EpisodeNumber, episode.episodeNumber));
                view.setDate(AndroidExtensions.formatDate(episode.airDate));
                view.setDivider(position != episodes.size() - 1);
            } else if (getItemViewType(position) == ITEM_OVERVIEW) {
                EpisodeOverview view = (EpisodeOverview) ((LoadingHolder) holder).itemView;
                view.setEpisodes(episodes.get(0).seasonId);
                view.setDate(episodes.get(0).airDate);
                view.setOverview(episodes.get(0).overview);
            }
        }

        @Override
        public int getItemCount() {
            return episodes != null ? episodes.size() : 0;
        }

        @Override
        public int getItemViewType(int position) {
            return (position == 0) ? ITEM_OVERVIEW : ITEM_EPISODE;
        }
    }
}