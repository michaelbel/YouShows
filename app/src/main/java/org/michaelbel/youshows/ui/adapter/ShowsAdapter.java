package org.michaelbel.youshows.ui.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.michaelbel.youshows.realm.RealmDb;
import org.michaelbel.youshows.rest.model.Show;
import org.michaelbel.material.annotation.Beta;
import org.michaelbel.material.widget.Holder;
import org.michaelbel.youshows.ui.adapter.TouchHelper.ItemBehavior;
import org.michaelbel.youshows.ui.view.MyShowView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Date: 25 MAR 2018
 * Time: 16:53 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("CheckResult")
public class ShowsAdapter extends RecyclerView.Adapter implements ItemBehavior {

    private List<Show> shows;

    public ShowsAdapter() {
        shows = new ArrayList<>();
    }

    public List<Show> getShows() {
        return shows;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(new MyShowView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Show show = shows.get(position);

        MyShowView view = (MyShowView) holder.itemView;
        view.setName(show.name);
        view.setPoster(show.posterPath);
        view.setStatus(show.inProduction);
        view.setDates(show.firstAirDate, show.lastAirDate);
        view.setExpandIcon(false);
        view.setDivider(position != shows.size() - 1);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.changeTheme();

        int progress = RealmDb.getShowProgress(show.showId);
        view.setProgress(progress);

        int watchedSeasons = RealmDb.getWatchedSeasonsInShow(show.showId);

        if (watchedSeasons == -1) {
            view.setProgressWatchedText(MyShowView.TYPE_SHOW, -1);
        } else if (watchedSeasons > 0) {
            view.setProgressWatchedText(MyShowView.TYPE_SEASONS, watchedSeasons);
        } else {
            int watchedEpisodes = RealmDb.getWatchedEpisodesInShow(show.showId);
            view.setProgressWatchedText(MyShowView.TYPE_EPISODES, watchedEpisodes);
        }
    }

    @Override
    public int getItemCount() {
        return shows != null ? shows.size() : 0;
    }

    public void addShows(List<Show> list) {
        shows.addAll(list);
        notifyItemInserted(shows.size() - 1);
    }

    public void removeItem(int position) {
        shows.remove(position);
        notifyItemRemoved(position);
    }

    @Beta
    @Override
    public void onItemSwiped(int position) {}

    @Beta
    @Override
    public boolean onItemMoved(int fromPosition, int toPosition) {
        Collections.swap(shows, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }
}