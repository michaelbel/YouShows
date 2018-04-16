package org.michaelbel.ui.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.michaelbel.rest.ApiFactory;
import org.michaelbel.rest.ApiService;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.seriespicker.R;
import org.michaelbel.ui.MainActivity;
import org.michaelbel.ui.adapter.FollowingEpisodesAdapter;
import org.michaelbel.old.ui_old.view.RecyclerListView;

/**
 * Date: 19 MAR 2018
 * Time: 12:12 MSK
 *
 * @author Michael Bel
 */

public class FollowingFragment extends Fragment {

    private MainActivity activity;
    private FollowingEpisodesAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private TextView emptyView;
    private ProgressBar progressBar;
    private FrameLayout fragmentLayout;
    private RecyclerListView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {}

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        fragmentLayout = new FrameLayout(activity);
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.background));

        progressBar = new ProgressBar(activity);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.accent), PorterDuff.Mode.MULTIPLY);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        fragmentLayout.addView(progressBar);

        emptyView = new TextView(activity);
        emptyView.setText("No following episodes");
        emptyView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 0, 24, 0));
        fragmentLayout.addView(emptyView);

        adapter = new FollowingEpisodesAdapter();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        fragmentLayout.addView(recyclerView);

        return fragmentLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar.setVisibility(View.GONE);

        /*Realm realm = Realm.getDefaultInstance();
        RealmResults<ShowRealm> results = realm.where(ShowRealm.class).findAll();
        if (results.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            adapter.addShows(results);
            Collections.reverse(adapter.getShows());
        }

        adapter.notifyDataSetChanged();*/

        getNextEpisodes();
    }

    private void getNextEpisodes() {
        ApiService service = ApiFactory.createService(ApiService.class, ApiFactory.TRAKT_API_ENDPOINT);
        service.nextEpisode(ApiFactory.TRAKT_CLIENT_ID, ApiFactory.TRAKT_API_VERSION, 1402); // imdb showId, trakt tv showId
    }

    public void showSnackbar(boolean enable) {
        Snackbar snackbar = Snackbar.make(fragmentLayout, enable ? "Уведомления включены" : "Уведомления отключены", Snackbar.LENGTH_SHORT);
        //snackbar.setActionTextColor(ContextCompat.getColor(getContext(), R.color.snackbar_action_text));
        //snackbar.setAction(R.string.Retry, view1 -> {
        //
        //});
        //snackbar.addCallback(new Snackbar.Callback() {
        //    @Override
        //    public void onShown(Snackbar sb) {
        //        super.onShown(sb);
        //});
        snackbar.show();
    }
}