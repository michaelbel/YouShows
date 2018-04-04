package org.michaelbel.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.michaelbel.old.ui_old.adapter.Holder;
import org.michaelbel.rest.model.Show;
import org.michaelbel.ui.view.ShowView;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 25 MAR 2018
 * Time: 16:53 MSK
 *
 * @author Michael Bel
 */

public class FollowingEpisodesAdapter extends RecyclerView.Adapter {

    protected List<Show> shows;

    public FollowingEpisodesAdapter() {
        shows = new ArrayList<>();
    }

    public List<Show> getShows() {
        return shows;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(new ShowView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Show show = shows.get(position);

        ShowView view = (ShowView) holder.itemView;
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setPoster(show.posterPath);
        view.setReleaseDate(show.firstAirDate != null ? show.firstAirDate : "");
        view.setTitle(show.name);
        view.setOverview(show.overview);

        if (position == shows.size() - 1) {
            view.addBottomPadding();
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
}