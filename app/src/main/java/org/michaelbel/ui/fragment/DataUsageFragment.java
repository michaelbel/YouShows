package org.michaelbel.ui.fragment;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.LayoutHelper;
import org.michaelbel.app.Theme;
import org.michaelbel.app.realm.RealmDb;
import org.michaelbel.material.widget.Holder;
import org.michaelbel.material.widget.RecyclerListView;
import org.michaelbel.app.ScreenUtils;
import org.michaelbel.shows.R;
import org.michaelbel.ui.SettingsActivity;
import org.michaelbel.ui.view.cell.EmptyCell;
import org.michaelbel.ui.view.cell.HeaderCell;
import org.michaelbel.ui.view.cell.TextCell;
import org.michaelbel.ui.view.cell.TextDetailCell;

/**
 * Date: 14 MAY 2018
 * Time: 20:09 MSK
 *
 * @author Michael Bel
 */

public class DataUsageFragment extends Fragment {

    private int rowCount;
    private int unfollowAllRow;
    private int deleteAllRow;
    private int cachedRow;
    private int emptyRow1;

    private int myshowsCount = 0;
    private int followingCount = 0;
    private int cachedCount = 0;

    private boolean calculating = true;

    private DateUsageAdapter adapter;
    private SettingsActivity activity;
    private LinearLayoutManager linearLayoutManager;

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
        activity.toolbarTitle.setText(R.string.DataUsage);

        FrameLayout fragmentLayout = new FrameLayout(activity);
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.Color.background()));

        rowCount = 0;
        deleteAllRow = rowCount++;
        unfollowAllRow = rowCount++;
        //cachedRow = rowCount++;
        emptyRow1 = rowCount++;

        adapter = new DateUsageAdapter();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnItemClickListener((view, position) -> {
            if (position == deleteAllRow) {
                if (myshowsCount == 0) {
                    Toast.makeText(activity, R.string.YourShowsListEmpty, Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, Theme.alertTheme());
                    builder.setTitle(R.string.AppName);
                    builder.setMessage(R.string.DeleteAllShowsMessage);
                    builder.setNegativeButton(R.string.Cancel, null);
                    builder.setPositiveButton(R.string.Ok, (dialog, which) -> {
                        RealmDb.deleteAllWatchedShows();
                        loadInfo();
                        adapter.updateRows();
                        Toast.makeText(activity, R.string.Done, Toast.LENGTH_SHORT).show();
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, Theme.Color.accent()));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, Theme.Color.accent()));
                }
            } else if (position == unfollowAllRow) {
                if (followingCount == 0) {
                    Toast.makeText(activity, R.string.YouDoNotFollowShows, Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, Theme.alertTheme());
                    builder.setTitle(R.string.AppName);
                    builder.setMessage(R.string.UnfollowFromAllShowsMessage);
                    builder.setNegativeButton(R.string.Cancel, null);
                    builder.setPositiveButton(R.string.Ok, (dialog, which) -> {
                        RealmDb.unfollowFromAllShows();
                        loadInfo();
                        adapter.updateRows();
                        Toast.makeText(activity, R.string.Done, Toast.LENGTH_SHORT).show();
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, Theme.Color.accent()));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, Theme.Color.accent()));
                }
            } else if (position == cachedRow) {
                activity.startFragment(new CachedShowsFragment(), "cachedFragment");
            }
        });
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        fragmentLayout.addView(recyclerView);
        return fragmentLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadInfo();
    }

    private void loadInfo() {
        AndroidExtensions.runOnUIThread(() -> {
            myshowsCount = RealmDb.getMyWatchedShowsCount();
            followingCount = RealmDb.getFollowingShowsCount();
            cachedCount = RealmDb.getCachedShowsCount();

            calculating = false;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Parcelable state = linearLayoutManager.onSaveInstanceState();
        linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.onRestoreInstanceState(state);
    }

    private class DateUsageAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            View cell;

            if (type == 0) {
                cell = new EmptyCell(activity);
            } else if (type == 1) {
                cell = new HeaderCell(activity);
            } else if (type == 2) {
                cell = new TextDetailCell(activity);
            } else {
                cell = new TextCell(activity);
            }

            return new Holder(cell);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);

            if (type == 0) {
                EmptyCell cell = (EmptyCell) holder.itemView;

                if (position == emptyRow1) {
                    cell.setMode(EmptyCell.MODE_DEFAULT);
                    cell.setHeight(ScreenUtils.dp(12));
                }
            } else if (type == 1) {
                HeaderCell cell = (HeaderCell) holder.itemView;
            } else if (type == 2) {
                TextDetailCell cell = (TextDetailCell) holder.itemView;
                cell.setMode(TextDetailCell.MODE_DEFAULT);
                cell.changeLayoutParams();

                if (position == deleteAllRow) {
                    cell.setText(R.string.DeleteAllShows);
                    cell.setValue(calculating ? getString(R.string.Calculating) : getResources().getQuantityString(R.plurals.SavedShows, myshowsCount, myshowsCount));
                    cell.setDivider(true);
                } else if (position == unfollowAllRow) {
                    cell.setText(R.string.UnfollowFromShows);
                    cell.setValue(calculating ? getString(R.string.Calculating) : getResources().getQuantityString(R.plurals.FollowingShows, followingCount, followingCount));
                    cell.setDivider(false);
                }
            } else {
                TextCell cell = (TextCell) holder.itemView;
                cell.changeLayoutParams();

                if (position == cachedRow) {
                    cell.setMode(TextCell.MODE_VALUE_TEXT);
                    cell.setText(R.string.AllCachedShows);
                    cell.setValue(calculating ? getString(R.string.Calculating) : String.valueOf(cachedCount));
                    cell.setDivider(false);
                }
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == emptyRow1) {
                return 0;
            } else if (position == -100) {
                return 1;
            } else if (position == unfollowAllRow || position == deleteAllRow) {
                return 2;
            } else {
                return 3;
            }
        }

        private void updateRows() {
            int count = recyclerView.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = recyclerView.getChildAt(i);
                Holder holder = (Holder) recyclerView.getChildViewHolder(child);
                int type = holder.getItemViewType();
                int pos = holder.getAdapterPosition();
                if (pos == deleteAllRow) {
                    TextDetailCell cell = (TextDetailCell) holder.itemView;
                    cell.setValue(calculating ? getString(R.string.Calculating) : getResources().getQuantityString(R.plurals.SavedShows, myshowsCount, myshowsCount));
                } else if (pos == unfollowAllRow) {
                    TextDetailCell cell = (TextDetailCell) holder.itemView;
                    cell.setValue(calculating ? getString(R.string.Calculating) : getResources().getQuantityString(R.plurals.FollowingShows, followingCount, followingCount));
                }
            }
        }
    }
}