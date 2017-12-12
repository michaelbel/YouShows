package org.michaelbel.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import org.michaelbel.database.DatabaseHelper;
import org.michaelbel.model.Series;
import org.michaelbel.seriespicker.LayoutHelper;
import org.michaelbel.seriespicker.R;
import org.michaelbel.ui.cell.BottomCell;
import org.michaelbel.ui.view.SeriesCompatView;
import org.michaelbel.ui.view.SeriesView;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("all")
public class SeriesAdapter extends RecyclerView.Adapter implements Filterable, ItemBehavior {

    private Context context;
    private ArrayList<Series> seriesList;
    public ArrayList<Series> seriesFilteredList;

    public SeriesAdapter(@NonNull Context context, @NonNull ArrayList<Series> list) {
        this.context = context;
        this.seriesList = list;
        this.seriesFilteredList = list;
    }

    public void removeItem(int position) {
        String title = seriesFilteredList.get(position).title;

        DatabaseHelper database = DatabaseHelper.getInstance(context);
        database.removeSeries(seriesFilteredList.get(position));
        database.close();

        seriesFilteredList.remove(position);
        notifyItemRemoved(position);

        Toast.makeText(context, context.getString(R.string.SeriesDeleted, title), Toast.LENGTH_SHORT).show();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view;

        if (type == 0) {
            view = new SeriesView(context);
        } else if (type == 1) {
            view = new SeriesCompatView(context);
        } else {
            view = new BottomCell(context);
            return new BottomCellHolder(view);
        }

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int type = getItemViewType(position);
        Series series = seriesFilteredList.get(position);

        if (type == 0) {
            SeriesView seriesView = (SeriesView) holder.itemView;
            seriesView.setTitle(series.title);
            seriesView.setSeasons(series.seasonCount);
            seriesView.setEpisodes(series.episodeCount);
            seriesView.setBackdrop(series.backdropPath == null ? "" : series.backdropPath);
            seriesView.setDivider(position != seriesList.size() - 1);
        } else if (type == 1) {
            SeriesCompatView seriesView = (SeriesCompatView) holder.itemView;
            seriesView.setTitle(series.title);
            seriesView.setSeasons(series.seasonCount);
            seriesView.setEpisodes(series.episodeCount);
            seriesView.setBackdrop(series.backdropPath == null ? "" : series.backdropPath);
            seriesView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT,
                    LayoutHelper.WRAP_CONTENT, 6, 2, 6, 0));
        } else {
            BottomCell cell = (BottomCell) holder.itemView;
            cell.setText(context.getString(R.string.SeriesCount, series.seriesCountBottom));
        }
    }

    @Override
    public int getItemCount() {
        return seriesFilteredList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (seriesFilteredList.get(position).seriesCountBottom == 0) {
            SharedPreferences prefs = context.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            return prefs.getInt("view_type", 1);
        } else {
            return 2;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    seriesFilteredList = seriesList;
                } else {

                    ArrayList<Series> filteredList = new ArrayList<>();

                    for (Series series : seriesList) {
                        if (series.title.toLowerCase().contains(charString)/* || androidVersion.getName().toLowerCase().contains(charString) || androidVersion.getVer().toLowerCase().contains(charString)*/) {
                            filteredList.add(series);
                        }
                    }

                    seriesFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = seriesFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                seriesFilteredList = (ArrayList<Series>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onItemSwiped(int position) {
        removeItem(position);
    }

    @Override
    public boolean onItemMoved(int fromPosition, int toPosition) {
        Collections.swap(seriesFilteredList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }
}