package org.michaelbel.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.Theme;
import org.michaelbel.app.realm.RealmDb;
import org.michaelbel.app.rest.model.Season;
import org.michaelbel.app.rest.model.Show;
import org.michaelbel.material.widget.Holder;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ui_old.view.RecyclerListView;
import org.michaelbel.shows.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 19 MAR 2018
 * Time: 17:40 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("ClickableViewAccessibility")
public class SeasonsLayout extends FrameLayout {

    private Show show;
    private SeasonsAdapter adapter;
    private List<Season> seasons = new ArrayList<>();

    private TextView seasonsTitle;
    private ProgressBar progressBar;
    public RecyclerListView recyclerView;

    public SeasonsLayout(@NonNull Context context) {
        super(context);

        setBackgroundColor(ContextCompat.getColor(context, Theme.Color.background()));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        addView(linearLayout);

        seasonsTitle = new TextView(context);
        seasonsTitle.setLines(1);
        seasonsTitle.setMaxLines(1);
        seasonsTitle.setSingleLine();
        seasonsTitle.setText(R.string.Seasons);
        seasonsTitle.setGravity(Gravity.CENTER_VERTICAL);
        seasonsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        seasonsTitle.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        seasonsTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        seasonsTitle.setOnLongClickListener(view -> {
            SharedPreferences prefs = getContext().getSharedPreferences("mainconfig", Context.MODE_PRIVATE);
            boolean seasonCountVisible = prefs.getBoolean("seasonsCountVisible", false);
            prefs.edit().putBoolean("seasonsCountVisible", !seasonCountVisible).apply();
            setShowTitle(show);
            AndroidExtensions.startVibrate(20);
            return true;
        });
        seasonsTitle.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, 42, Gravity.START | Gravity.CENTER_VERTICAL, 16, 0, 16, 0));
        linearLayout.addView(seasonsTitle);

        adapter = new SeasonsAdapter();

        recyclerView = new RecyclerListView(context);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(INVISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 8, 0, 6));
        linearLayout.addView(recyclerView);

        progressBar = new ProgressBar(context);
        progressBar.setVisibility(VISIBLE);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 16 + 40, 0, 16));
        addView(progressBar);
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public void setShowTitle(Show show) {
        SharedPreferences prefs = getContext().getSharedPreferences("mainconfig", Context.MODE_PRIVATE);
        boolean seasonCountVisible = prefs.getBoolean("seasonsCountVisible", false);
        seasonsTitle.setText(seasonCountVisible ? getContext().getString(R.string.SeasonsCount, show.numberSeasons) : getContext().getString(R.string.Seasons));
    }

    public void setSeasons(List<Season> items) {
        for (Season season : items) {
            if (season.seasonNumber != 0) {
                seasons.add(season);
            }
        }

        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(VISIBLE);
        removeView(progressBar);
    }

    public void updateAdapter(List<Season> items) {
        seasons.clear();

        for (Season season : items) {
            if (season.seasonNumber != 0) {
                seasons.add(season);
            }
        }

        adapter.notifyDataSetChanged();
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    private class SeasonsAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(new SeasonView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            Season season = seasons.get(position);

            int allEpisodes = season.episodeCount;
            int watchedEpisodes = RealmDb.getWatchedEpisodesInSeason(show.showId, season.seasonId);

            SeasonView view = (SeasonView) holder.itemView;
            view.setName(season.name);
            view.setAirDate(season.airDate);
            view.setEpisodeCount(watchedEpisodes, allEpisodes);
            view.setSelected(allEpisodes != 0 && watchedEpisodes == allEpisodes);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        @Override
        public int getItemCount() {
            return seasons != null ? seasons.size() : 0;
        }
    }
}