package org.michaelbel.ui.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.michaelbel.app.Changelog;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ScreenUtils;
import org.michaelbel.old.Theme;
import org.michaelbel.old.ui_old.adapter.Holder;
import org.michaelbel.old.ui_old.view.RecyclerListView;
import org.michaelbel.shows.R;
import org.michaelbel.ui.SettingsActivity;
import org.michaelbel.ui.view.ChangelogView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Date: 02 MAR 2018
 * Time: 00:20 MSK
 *
 * @author Michael Bel
 */

public class ChangelogsFragment extends Fragment {

    public static final int MENU_ICON_DATES = 0;

    private ChangesAdapter adapter;
    private SharedPreferences prefs;
    private SettingsActivity activity;
    private LinearLayoutManager layoutManager;
    private List<Changelog> changes = new ArrayList<>();

    private Menu actionMenu;
    private RecyclerListView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SettingsActivity) getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean enableDates = prefs.getBoolean("changelogDates", false);

        actionMenu = menu;

        menu.add(R.string.EnableDates)
            .setIcon(enableDates ? R.drawable.ic_calendar_check : R.drawable.ic_calendar_text)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            .setOnMenuItemClickListener(item -> {
                boolean enable = prefs.getBoolean("changelogDates", false);
                prefs.edit().putBoolean("changelogDates", !enable).apply();

                changes.clear();
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

                readJsonFile();

                changeMenuIcon();
                return true;
            });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.toolbar.setNavigationOnClickListener(view -> activity.finishFragment());
        activity.toolbarTitle.setText(R.string.Changelogs);

        FrameLayout fragmentView = new FrameLayout(activity);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));

        adapter = new ChangesAdapter();
        layoutManager = new LinearLayoutManager(activity);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setPadding(0, 0, 0, ScreenUtils.dp(16));
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        fragmentView.addView(recyclerView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readJsonFile();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Parcelable state = layoutManager.onSaveInstanceState();
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.onRestoreInstanceState(state);
    }

    private void readJsonFile() {
        String json;
        try {
            InputStream inputStream = activity.getAssets().open("changelogs.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

            JSONArray versionsArray = new JSONArray(json);
            for (int i = 0; i < versionsArray.length(); i++) {
                JSONObject versionObj = versionsArray.getJSONObject(i);
                Changelog log = new Changelog();
                log.version = versionObj.getString("version");
                log.build = versionObj.getInt("build");
                log.date = versionObj.getString("date");

                JSONArray changesArray = versionObj.getJSONArray("changes");
                for (int j = 0; j < changesArray.length(); j++) {
                    String change = changesArray.getString(j);
                    log.changes.add(change);
                }

                changes.add(log);
            }

            Collections.reverse(changes);
            adapter.notifyDataSetChanged();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void changeMenuIcon() {
        SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean dates = prefs.getBoolean("changelogDates", false);
        if (actionMenu != null) {
            actionMenu.getItem(MENU_ICON_DATES).setIcon(dates ? R.drawable.ic_calendar_check : R.drawable.ic_calendar_text);
        }
    }

    private class ChangesAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            return new Holder(new ChangelogView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Changelog log = changes.get(position);

            ChangelogView view = (ChangelogView) holder.itemView;
            view.setVersion(log.version);
            view.setDate(log.date);
            view.setChanges(log.changes);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        @Override
        public int getItemCount() {
            return changes != null ? changes.size() : 0;
        }
    }
}