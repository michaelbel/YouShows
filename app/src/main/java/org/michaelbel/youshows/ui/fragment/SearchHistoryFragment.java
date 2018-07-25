package org.michaelbel.youshows.ui.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.youshows.Theme;
import org.michaelbel.youshows.model.SearchItem;
import org.michaelbel.youshows.realm.RealmDb;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.material.widget.Holder;
import org.michaelbel.material.widget.RecyclerListView;
import org.michaelbel.shows.R;
import org.michaelbel.youshows.ui.SettingsActivity;
import org.michaelbel.youshows.ui.view.EmptyView;
import org.michaelbel.youshows.ui.view.EmptyViewMode;
import org.michaelbel.youshows.ui.view.cell.EmptyCell;
import org.michaelbel.youshows.ui.view.cell.SearchItemCell;
import org.michaelbel.youshows.ui.view.cell.TextCell;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

/**
 * Date: 18 MAY 2018
 * Time: 18:04 MSK
 *
 * @author Michael Bel
 */

public class SearchHistoryFragment extends Fragment {

    private SettingsActivity activity;
    private SearchHistoryAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<SearchItem> searchItems = new ArrayList<>();

    private EmptyView emptyView;
    private RecyclerListView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SettingsActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.toolbar.setNavigationOnClickListener(view -> activity.finishFragment());

        FrameLayout fragmentView = new FrameLayout(activity);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));

        adapter = new SearchHistoryAdapter();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setClipToPadding(false);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(activity, LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        recyclerView.setOnItemClickListener((view, position) -> {
            if (position == adapter.ROW_ENABLE_HISTORY) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                boolean enable = prefs.getBoolean("search_history", true);
                prefs.edit().putBoolean("search_history", !enable).apply();
                if (view instanceof TextCell) {
                    ((TextCell) view).setChecked(!enable);
                }
            } else if (position == adapter.ROW_SHOW_SUGGESTIONS) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                boolean enable = prefs.getBoolean("enable_suggestions", true);
                prefs.edit().putBoolean("enable_suggestions", !enable).apply();
                if (view instanceof TextCell) {
                    ((TextCell) view).setChecked(!enable);
                }
            } else if (position == adapter.ROW_CLEAR_HISTORY) {
                if (!RealmDb.isSearchItemsExist()) {
                    Toast.makeText(activity, R.string.SearchHistoryEmpty, Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, Theme.alertDialogStyle());
                    builder.setTitle(R.string.AppName);
                    builder.setMessage(R.string.ClearHistoryMessage);
                    builder.setNegativeButton(R.string.Cancel, null);
                    builder.setPositiveButton(R.string.Ok, (dialog, which) -> {
                        RealmDb.clearSearchHistory();
                        refreshLayout();
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, Theme.dialogButtonText()));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, Theme.dialogButtonText()));
                }
            }
        });
        fragmentView.addView(recyclerView);

        emptyView = new EmptyView(activity);
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setMode(EmptyViewMode.MODE_SEARCH_HISTORY);
        emptyView.setLayoutParams(LayoutHelper.makeFrame(activity, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 0, 52, 0, 0));
        fragmentView.addView(emptyView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout();
    }

    private void refreshLayout() {
        searchItems.clear();
        adapter.notifyDataSetChanged();

        searchItems.add(new SearchItem());
        searchItems.add(new SearchItem());
        searchItems.add(new SearchItem());
        searchItems.add(new SearchItem());

        RealmResults<SearchItem> results = RealmDb.getSearchItems();
        if (results != null) {
            emptyView.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
            if (results.isLoaded()) {
                searchItems.addAll(results);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Parcelable state = linearLayoutManager.onSaveInstanceState();
        linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.onRestoreInstanceState(state);
    }

    private class SearchHistoryAdapter extends RecyclerView.Adapter {

        private final int ROW_ENABLE_HISTORY = 0;
        private final int ROW_SHOW_SUGGESTIONS = 1;
        private final int ROW_CLEAR_HISTORY = 2;
        private final int ROW_EMPTY_CELL = 3;
        private final int ROW_SEARCH_ITEM = 4;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            View cell;

            if (type == ROW_ENABLE_HISTORY || type == ROW_SHOW_SUGGESTIONS || type == ROW_CLEAR_HISTORY) {
                cell = new TextCell(activity);
            } else if (type == ROW_EMPTY_CELL) {
                cell = new EmptyCell(activity);
            } else {
                cell = new SearchItemCell(activity);
            }

            return new Holder(cell);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            SearchItem item = searchItems.get(position);

            if (getItemViewType(position) == ROW_ENABLE_HISTORY) {
                TextCell cell = (TextCell) holder.itemView;
                cell.changeLayoutParams();
                cell.changeSwitchTheme();
                cell.setMode(TextCell.MODE_SWITCH);
                cell.setText(getString(R.string.EnableHistory));
                cell.setChecked(activity.prefs.getBoolean("search_history", true));
                cell.setDivider(true);
            } else if (getItemViewType(position) == ROW_SHOW_SUGGESTIONS) {
                TextCell cell = (TextCell) holder.itemView;
                cell.changeLayoutParams();
                cell.changeSwitchTheme();
                cell.setMode(TextCell.MODE_SWITCH);
                cell.setText(getString(R.string.ShowSuggestions));
                cell.setChecked(activity.prefs.getBoolean("enable_suggestions", true));
                cell.setDivider(true);
            } else if (getItemViewType(position) == ROW_CLEAR_HISTORY) {
                TextCell cell = (TextCell) holder.itemView;
                cell.changeLayoutParams();
                cell.setMode(TextCell.MODE_DEFAULT);
                cell.setText(R.string.ClearHistory);
                cell.setDivider(false);
            } else if (getItemViewType(position) == ROW_EMPTY_CELL) {
                EmptyCell cell = (EmptyCell) holder.itemView;
                cell.setMode(EmptyCell.MODE_DEFAULT);
                cell.setHeight(Extensions.dp(activity,12));
            } else {
                SearchItemCell cell = (SearchItemCell) holder.itemView;
                cell.changeLayoutParams();
                cell.setQuery(item.query);
                cell.setDate(item.date);
                cell.setDivider(position != searchItems.size() - 1);
                //cell.setVoiceQuery(item.voice);
                /*cell.setOnIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(item.date, holder.getAdapterPosition());
                    }
                });*/
            }
        }

        @Override
        public int getItemCount() {
            return searchItems != null ? searchItems.size() : 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return ROW_ENABLE_HISTORY;
            } else if (position == 1) {
                return ROW_SHOW_SUGGESTIONS;
            } else if (position == 2) {
                return ROW_CLEAR_HISTORY;
            } else if (position == 3) {
                return ROW_EMPTY_CELL;
            } else {
                return ROW_SEARCH_ITEM;
            }
        }

        /*private void removeItem(String date, int position) {
            searchItems.remove(position);
            notifyItemRemoved(position);
            RealmDb.removeSearchItem(date);
        }*/
    }
}