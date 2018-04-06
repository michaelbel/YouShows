package org.michaelbel.ui.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ScreenUtils;
import org.michaelbel.old.ui_old.view.RecyclerListView;
import org.michaelbel.rest.model.Show;
import org.michaelbel.seriespicker.R;
import org.michaelbel.ui.MainActivity;
import org.michaelbel.ui.adapter.MyShowsAdapter;
import org.michaelbel.ui.view.MyShowView;
import org.michaelbel.ui.view.MyShowsEmptyView;

import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Date: 19 MAR 2018
 * Time: 12:12 MSK
 *
 * @author Michael Bel
 */

public class MyShowsFragment extends Fragment {

    private int prevTop;
    private int prevPosition;
    private boolean scrollUpdated;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();

    private MainActivity activity;
    private MyShowsAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private MyShowsEmptyView emptyView;
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

        FrameLayout fragmentLayout = new FrameLayout(activity);
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.background));

        emptyView = new MyShowsEmptyView(activity);
        emptyView.setOnClickListener(v -> activity.startExplore());
        emptyView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        fragmentLayout.addView(emptyView);

        adapter = new MyShowsAdapter();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        recyclerView.setOnItemClickListener((view, position) -> {
            if (view instanceof MyShowView) {
                Show show = adapter.getShows().get(position);
                activity.startShow(show);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                if (activity.floatingButton.getVisibility() != View.GONE) {
                    final View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean goingDown;
                    boolean changed = true;
                    if (prevPosition == firstVisibleItem) {
                        final int topDelta = prevTop - firstViewTop;
                        goingDown = firstViewTop < prevTop;
                        changed = Math.abs(topDelta) > 1;
                    } else {
                        goingDown = firstVisibleItem > prevPosition;
                    }
                    if (changed && scrollUpdated) {
                        hideFloatingButton(goingDown);
                    }
                    prevPosition = firstVisibleItem;
                    prevTop = firstViewTop;
                    scrollUpdated = true;
                }
            }
        });
        fragmentLayout.addView(recyclerView);
        return fragmentLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter.getShows().clear();
        adapter.notifyDataSetChanged();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Show> results = realm.where(Show.class).equalTo("isFollow", true).findAll();
        if (results.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            adapter.addShows(results);
            Collections.reverse(adapter.getShows());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.getShows().clear();
        adapter.notifyDataSetChanged();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Show> results = realm.where(Show.class).equalTo("isFollow", true).findAll();
        if (results.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            adapter.addShows(results);
            Collections.reverse(adapter.getShows());
        }
    }

    private void hideFloatingButton(boolean hide) {
        if (floatingHidden == hide) {
            return;
        }
        floatingHidden = hide;
        ObjectAnimator animator = ObjectAnimator.ofFloat(activity.floatingButton, "translationY", floatingHidden ? ScreenUtils.dp(100) : 0).setDuration(300);
        animator.setInterpolator(floatingInterpolator);
        activity.floatingButton.setClickable(!hide);
        animator.start();
    }
}