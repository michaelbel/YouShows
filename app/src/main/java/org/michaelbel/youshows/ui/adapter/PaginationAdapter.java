package org.michaelbel.youshows.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.michaelbel.youshows.rest.model.Show;

import java.util.ArrayList;

/**
 * Date: 25 FEB 2018
 * Time: 17:31 MSK
 *
 * @author Michael Bel
 */

@SuppressWarnings("all")
public class PaginationAdapter extends RecyclerView.Adapter {

    public final int ITEM = 0;
    public final int LOADING = 1;

    protected ArrayList<Show> objectList;
    protected boolean isLoadingAdded = false;

    public PaginationAdapter() {
        objectList = new ArrayList<>();
    }

    public ArrayList<Show> getList() {
        return objectList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {}

    @Override
    public int getItemCount() {
        return objectList != null ? objectList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == objectList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(Show object) {
        objectList.add(object);
        notifyItemInserted(objectList.size() - 1);
    }

    private void remove(Show show) {
        int position = objectList.indexOf(show);

        if (position > -1) {
            objectList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;

        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    private Show getItem(int position) {
        return objectList.get(position);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = objectList.size() - 1;
        Show result = getItem(position);

        if (result != null) {
            objectList.remove(position);
            notifyItemRemoved(position);
        }
    }
}