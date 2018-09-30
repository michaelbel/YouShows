package org.michaelbel.youshows.ui.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.youshows.Theme;
import org.michaelbel.youshows.YouShows;
import org.michaelbel.youshows.eventbus.Events;
import org.michaelbel.youshows.realm.RealmDb;
import org.michaelbel.youshows.rest.model.Show;
import org.michaelbel.bottomsheet.BottomSheet;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.material.widget.RecyclerListView;
import org.michaelbel.shows.R;
import org.michaelbel.youshows.ui.MainActivity;
import org.michaelbel.youshows.ui.adapter.ShowsAdapter;
import org.michaelbel.youshows.ui.view.MyShowView;
import org.michaelbel.youshows.ui.view.ShowsEmptyView;
import org.michaelbel.youshows.ui.view.SortView;

import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Date: 19 MAR 2018
 * Time: 12:12 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("CheckResult")
public class FollowingShowsFragment extends Fragment {

    private int prevTop;
    private int prevPosition;
    private boolean scrollUpdated;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();

    private ShowsAdapter adapter;
    //private FollowingAdapter adapter;
    private MainActivity activity;
    private LinearLayoutManager linearLayoutManager;

    private FrameLayout fragmentLayout;
    private ShowsEmptyView emptyView;
    private RecyclerListView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));

        emptyView = new ShowsEmptyView(activity);
        emptyView.setMode(ShowsEmptyView.FOLLOWING_MODE);
        emptyView.setLayoutParams(LayoutHelper.makeFrame(activity, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        fragmentLayout.addView(emptyView);

        adapter = new ShowsAdapter();
        //adapter = new FollowingAdapter();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setClipToPadding(false);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setPadding(0, Extensions.dp(activity,6), 0, Extensions.dp(activity,6));
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(activity, LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        recyclerView.setLayoutAnimation(AndroidExtensions.layoutAnimationController());
        recyclerView.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                activity.floatingButtonAppear();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        recyclerView.setOnItemClickListener((view, position) -> {
            if (view instanceof MyShowView) {
                Show show = adapter.getShows().get(position);
                activity.startShow(show);
            }
        });
        recyclerView.setOnItemLongClickListener((view, position) -> {
            if (view instanceof MyShowView) {
                Show show = adapter.getShows().get(position);

                BottomSheet.Builder builder = new BottomSheet.Builder(activity);
                builder.setCellHeight(Extensions.dp(activity, 52));
                builder.setTitle(show.name);
                builder.setTitleMultiline(true);
                builder.setTitleTextColorRes(Theme.secondaryTextColor());
                builder.setItemTextColorRes(Theme.primaryTextColor());
                builder.setIconColorRes(Theme.iconActiveColor());
                builder.setBackgroundColorRes(Theme.foregroundColor());
                builder.setItems(new int[]{ R.string.Unfollow}, new int[]{R.drawable.ic_eye}, (dialog, whichRow) -> {
                    if (whichRow == 0) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity, Theme.alertDialogStyle());
                        alertBuilder.setTitle(R.string.AppName);
                        alertBuilder.setMessage(R.string.UnfollowMessage);
                        alertBuilder.setNegativeButton(R.string.Cancel, null);
                        alertBuilder.setPositiveButton(R.string.Ok, (d, pos) -> {
                            RealmDb.followShow(show.showId, false);
                            adapter.removeItem(recyclerView.getChildAdapterPosition(view));
                        });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, Theme.accentColor()));
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, Theme.accentColor()));
                    }
                });
                builder.show();
                return true;
            }

            return false;
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                if (activity.fab.getVisibility() != View.GONE) {
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
        refreshLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout();

        ((YouShows) activity.getApplication()).bus().toObservable().subscribe(object -> {
            if (object instanceof Events.ChangeTheme) {
                fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));
            } else if (object instanceof Events.RemoveProgress) {
                refreshLayout();
            }
        });
    }

    public void refreshLayout() {
        SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        int sortFilter = prefs.getInt("following_sort_type", SortView.SORT_BY_DEFAULT);
        Sort sort = prefs.getBoolean("following_sort_order", SortView.ORDER_ASCENDING) ? Sort.ASCENDING : Sort.DESCENDING;

        adapter.getShows().clear();
        adapter.notifyDataSetChanged();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Show> results = null;

        if (sortFilter == SortView.SORT_BY_DEFAULT) {
            results = realm.where(Show.class).equalTo("isFollow", true).findAll();
        } else if (sortFilter == SortView.SORT_BY_NAME) {
            results = realm.where(Show.class).equalTo("isFollow", true).sort("name", sort).findAll();
        } else if (sortFilter == SortView.SORT_BY_FIRST_AIR_DATE) {
            results = realm.where(Show.class).equalTo("isFollow", true).sort("firstAirDate", sort).findAll();
        } else if (sortFilter == SortView.SORT_BY_LAST_AIR_DATE) {
            results = realm.where(Show.class).equalTo("isFollow", true).sort("lastAirDate", sort).findAll();
        } else if (sortFilter == SortView.SORT_BY_STATUS) {
            results = realm.where(Show.class).equalTo("isFollow", true).sort("inProduction", sort).findAll();
        } else if (sortFilter == SortView.SORT_BY_PROGRESS) {
            results = realm.where(Show.class).equalTo("isFollow", true).sort("progress", sort).findAll();
        } else if (sortFilter == SortView.SORT_BY_LAST_CHANGES) {
            results = realm.where(Show.class).equalTo("isFollow", true).sort("lastChangesDate", sort).findAll();
        }

        if (results != null) {
            if (results.isLoaded()) {
                adapter.addShows(results);
                if (sortFilter == SortView.SORT_BY_DEFAULT && sort == Sort.DESCENDING) {
                    Collections.reverse(adapter.getShows());
                }
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void changeEmptyViewTheme() {
        emptyView.changeTheme();
    }

    private void hideFloatingButton(boolean hide) {
        if (floatingHidden == hide) {
            return;
        }
        floatingHidden = hide;
        ObjectAnimator animator = ObjectAnimator.ofFloat(activity.fab, "translationY", floatingHidden ? Extensions.dp(activity,100) : 0).setDuration(300);
        animator.setInterpolator(floatingInterpolator);
        activity.fab.setClickable(!hide);
        animator.start();
    }
}