package org.michaelbel.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.michaelbel.old.ui_old.adapter.Holder;
import org.michaelbel.app.rest.model.Show;
import org.michaelbel.ui.adapter.holder.LoadingHolder;
import org.michaelbel.ui.view.LoadingView;
import org.michaelbel.ui.view.ShowView;

import java.util.List;

/**
 * Date: 02 APR 2018
 * Time: 21:58 MSK
 *
 * @author Michael Bel
 */

public class PaginationShowsAdapter extends PaginationAdapter {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == ITEM) {
            viewHolder = new Holder(new ShowView(parent.getContext()));
        } else if (viewType == LOADING) {
            viewHolder = new LoadingHolder(new LoadingView(parent.getContext()));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Show show = objectList.get(position);

        if (getItemViewType(position) == ITEM) {
            ShowView view = (ShowView) ((Holder) holder).itemView;
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setPoster(show.posterPath);
            view.setRating(String.valueOf(show.voteAverage));
            view.setReleaseDate(show.firstAirDate != null ? show.firstAirDate : "No date");
            view.setTitle(show.name);
            view.setOverview(show.overview);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == objectList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addAll(List<Show> movies) {
        for (Show movie : movies) {
            add(movie);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Show());
    }
}