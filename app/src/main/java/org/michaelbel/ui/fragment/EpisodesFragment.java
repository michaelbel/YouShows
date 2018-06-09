package org.michaelbel.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.LayoutHelper;
import org.michaelbel.app.Theme;
import org.michaelbel.app.YouShows;
import org.michaelbel.app.eventbus.Events;
import org.michaelbel.app.realm.RealmDb;
import org.michaelbel.app.rest.ApiFactory;
import org.michaelbel.app.rest.ApiService;
import org.michaelbel.app.rest.model.Episode;
import org.michaelbel.app.rest.model.Season;
import org.michaelbel.material.widget.Holder;
import org.michaelbel.material.widget.RecyclerListView;
import org.michaelbel.shows.R;
import org.michaelbel.ui.SeasonActivity;
import org.michaelbel.ui.view.EmptyView;
import org.michaelbel.ui.view.EmptyViewMode;
import org.michaelbel.ui.view.EpisodeOverview;
import org.michaelbel.ui.view.EpisodePeekView;
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

    private String seasonAirDate;
    private String seasonOverview;

    private SeasonAdapter adapter;
    private SeasonActivity activity;
    private LinearLayoutManager linearLayoutManager;

    private EmptyView emptyView;
    private ProgressBar progressBar;
    private FrameLayout episodeLayout;
    private RecyclerListView recyclerView;
    private EpisodePeekView episodePeekView;

    private int scrollPosition;
    private boolean peekViewVisible = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SeasonActivity) getActivity();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.toolbarTitle.setOnClickListener(view -> recyclerView.smoothScrollToPosition(0));

        FrameLayout fragmentLayout = new FrameLayout(activity);
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));

        LinearLayout contentLayout = new LinearLayout(activity);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentLayout.addView(contentLayout);

        progressBar = new ProgressBar(activity);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, Theme.Color.accentColor()), PorterDuff.Mode.MULTIPLY);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        fragmentLayout.addView(progressBar);

        emptyView = new EmptyView(activity);
        emptyView.setVisibility(View.GONE);
        emptyView.setOnClickListener(view -> showEpisodes());
        emptyView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        fragmentLayout.addView(emptyView);

        episodeLayout = new FrameLayout(activity);
        episodeLayout.setAlpha(0);
        episodeLayout.setVisibility(View.GONE);
        episodeLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent50));
        episodeLayout.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        fragmentLayout.addView(episodeLayout);

        episodePeekView = new EpisodePeekView(activity);
        episodePeekView.setAlpha(0);
        episodePeekView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 24, 56, 24, 56));
        episodeLayout.addView(episodePeekView);

        adapter = new SeasonAdapter();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnItemClickListener((view, position) -> {
            if (view instanceof EpisodeView) {
                Episode episode = adapter.episodes.get(position);
                addEpisodeToRealm(episode, view);
            }
        });
        recyclerView.setOnItemLongClickListener((view, position) -> {
            if (view instanceof EpisodeView) {
                Episode episode = adapter.episodes.get(position);
                setEpisodeDetails(episode);
                showHidePeekView(true);
                AndroidExtensions.vibrate(10);
                return false;
            }
            return false;
        });
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                episodePeekView.onTouchEvent(e);

                if (e.getAction() == MotionEvent.ACTION_UP) {
                    if (peekViewVisible) {
                        showHidePeekView(false);
                    }
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
        recyclerView.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        contentLayout.addView(recyclerView);
        return fragmentLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showEpisodes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        scrollPosition = linearLayoutManager.findFirstVisibleItemPosition();
        RealmDb.setSeasonScrollPosition(activity.showId, activity.seasonId, scrollPosition);
    }

    private void setEpisodeDetails(Episode episode) {
        episodePeekView.setTitle(episode.name);
        episodePeekView.setOverview(episode.overview);
        episodePeekView.setNumber(episode.episodeNumber);
        episodePeekView.setStillImage(episode.still_path);
        episodePeekView.setAirDate(AndroidExtensions.formatDate(episode.airDate));
    }

    private void showHidePeekView(boolean state) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(episodeLayout, "alpha", state ? 0 : 1, state ? 1 : 0),
            ObjectAnimator.ofFloat(episodePeekView, "alpha", state ? 0 : 1, state ? 1 : 0)
        );
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (state) {
                    episodeLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (state) {
                    peekViewVisible = true;
                } else {
                    episodeLayout.setVisibility(View.GONE);
                    peekViewVisible = false;
                }
            }
        });
        AndroidExtensions.runOnUIThread(animatorSet:: start);
    }

    public void showEpisodes() {
        emptyView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if (!RealmDb.isSeasonEpisodesEmpty(activity.showId, activity.seasonId)) {
            seasonAirDate = AndroidExtensions.formatDate(RealmDb.getSeasonAirDate(activity.showId, activity.seasonId));
            seasonOverview = RealmDb.getSeasonOverview(activity.showId, activity.seasonId);
            adapter.addOverview();

            adapter.addEpisodes(RealmDb.getSeasonEpisodes(activity.showId, activity.seasonId));
            progressBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            activity.fabButton.show();

            SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            if (prefs.getBoolean("season_scroll_position", true)) {
                scrollPosition = RealmDb.getSeasonScrollPosition(activity.showId, activity.seasonId);
                recyclerView.scrollToPosition(scrollPosition);
            }
        }

        ApiService service = ApiFactory.createService(ApiService.class, ApiFactory.TMDB_API_ENDPOINT);
        service.season(activity.showId, activity.seasonNumber, ApiFactory.TMDB_API_KEY, ApiFactory.getLanguage()).enqueue(new Callback<Season>() {
            @Override
            public void onResponse(@NonNull Call<Season> call, @NonNull Response<Season> response) {
                if (response.isSuccessful()) {
                    Season season = response.body();
                    if (season != null) {
                        if (RealmDb.isSeasonEpisodesEmpty(activity.showId, activity.seasonId)) {
                            if (season.episodes.isEmpty()) {
                                showError(EmptyViewMode.MODE_NO_EPISODES);
                            }

                            seasonAirDate = AndroidExtensions.formatDate(season.airDate);
                            seasonOverview = season.overview;
                            adapter.addOverview();

                            adapter.addEpisodes(season.episodes);
                            progressBar.setVisibility(View.GONE);
                            emptyView.setVisibility(View.GONE);
                            activity.fabButton.show();
                        }

                        updateData(season);
                    } else {
                        showError(EmptyViewMode.MODE_NO_EPISODES);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Season> call, @NonNull Throwable t) {
                if (adapter.episodes.isEmpty()) {
                    showError(EmptyViewMode.MODE_NO_CONNECTION);
                }
            }
        });
    }

    private void addEpisodeToRealm(Episode episode, View checkedView) {
        boolean exist = RealmDb.isEpisodeExist(activity.showId, activity.seasonId, episode.episodeId);

        if (exist) {
            boolean watched = RealmDb.isEpisodeWatched(activity.showId, activity.seasonId, episode.episodeId);
            RealmDb.markEpisodeAsWatched(activity.showId, activity.seasonId, episode.episodeId, !watched);
            ((EpisodeView) checkedView).setChecked(!watched, true);
        } else {
            RealmDb.insertOrUpdateEpisode(activity.showId, activity.seasonId, episode);
            RealmDb.setEpisodeWatchDate(activity.showId, activity.seasonId, episode.episodeId, AndroidExtensions.getCurrentDateAndTime());
            RealmDb.markEpisodeAsWatched(activity.showId, activity.seasonId, episode.episodeId, true);
            ((EpisodeView) checkedView).setChecked(true, true);
        }

        boolean watched = RealmDb.isSeasonWatched(activity.showId, activity.seasonId, activity.seasonEpisodeCount);
        activity.changeFabStyle(watched);

        updateRealmDb();
    }

    public void markSeasonAsWatch(boolean watched) {
        if (watched) {
            List<Episode> episodes = adapter.episodes;
            for (Episode episode : episodes) {
                if (episode != episodes.get(adapter.ITEM_OVERVIEW)) {
                    boolean exist = RealmDb.isEpisodeExist(activity.showId, activity.seasonId, episode.episodeId);

                    if (exist) {
                        RealmDb.markEpisodeAsWatched(activity.showId, activity.seasonId, episode.episodeId, true);
                    } else {
                        RealmDb.insertOrUpdateEpisode(activity.showId, activity.seasonId, episode);
                        RealmDb.setEpisodeWatchDate(activity.showId, activity.seasonId, episode.episodeId, AndroidExtensions.getCurrentDateAndTime());
                        RealmDb.markEpisodeAsWatched(activity.showId, activity.seasonId, episode.episodeId, true);
                    }
                }
            }

            adapter.notifyDataSetChanged();
            updateRealmDb();
        } else {
            for (Episode episode : adapter.episodes) {
                RealmDb.markEpisodeAsWatched(activity.showId, activity.seasonId, episode.episodeId, false);
            }

            adapter.notifyDataSetChanged();
            updateRealmDb();
        }
    }

    private void updateData(Season season) {
        RealmDb.insertOrUpdateSeason(activity.showId, season);
        RealmDb.setSeasonEpisodesCount(activity.showId, activity.seasonId, activity.seasonEpisodeCount);
        RealmDb.updateEpisodesList(activity.showId, activity.seasonId, season.episodes);
    }

    private void updateRealmDb() {
        int allEpisodes = RealmDb.getShowEpisodesCount(activity.showId);
        int watchedEpisodes = RealmDb.getWatchedEpisodesInShow(activity.showId);
        float percent = (watchedEpisodes * 100F) / allEpisodes;

        RealmDb.updateProgress(activity.showId, percent);
        RealmDb.updateLastChangesDate(activity.showId, AndroidExtensions.getCurrentDateAndTime());
        ((YouShows) activity.getApplication()).bus().send(new Events.UpdateProgress());
        ((YouShows) activity.getApplication()).bus().send(new Events.UpdateSeasonsView());
    }

    public void showError(int mode) {
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setMode(mode);
    }

    private class SeasonAdapter extends RecyclerView.Adapter {

        private final int ITEM_OVERVIEW = 0;
        private final int ITEM_EPISODE = 1;

        public List<Episode> episodes = new ArrayList<>();

        public void addOverview() {
            episodes.add(new Episode());
            notifyItemInserted(episodes.size() - 1);
        }

        private void addEpisodes(List<Episode> results) {
            episodes.addAll(results);
            notifyItemRangeInserted(episodes.size() + 1, results.size());
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            View view;

            if (type == ITEM_OVERVIEW) {
                view = new EpisodeOverview(parent.getContext());
            } else {
                view = new EpisodeView(parent.getContext());
            }

            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Episode episode = episodes.get(position);

            if (getItemViewType(position) == ITEM_OVERVIEW) {
                EpisodeOverview view = (EpisodeOverview) holder.itemView;
                view.setEpisodes(activity.seasonEpisodeCount);
                view.setDate(seasonAirDate);
                view.setOverview(seasonOverview);
            } else {
                EpisodeView view = (EpisodeView) holder.itemView;
                view.setName(episode.name);
                view.setChecked(RealmDb.isEpisodeWatched(activity.showId, activity.seasonId, episode.episodeId), false);
                view.setNumber(getString(R.string.EpisodeNumber, episode.episodeNumber));
                view.setDate(AndroidExtensions.formatDate(episode.airDate));
                view.setDivider(position != episodes.size() - 1);
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