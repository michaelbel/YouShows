package org.michaelbel.ui.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.michaelbel.bottomsheet.BottomSheet;
import org.michaelbel.sqlite.DatabaseHelper;
import org.michaelbel.rest.model.Series;
import org.michaelbel.seriespicker.AppLoader;
import org.michaelbel.seriespicker.Events;
import org.michaelbel.seriespicker.R;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.ui.MainActivity;
import org.michaelbel.ui.adapter.ItemTouchHelperSimpleCallback;
import org.michaelbel.ui.adapter.SeriesAdapter;
import org.michaelbel.ui.view.RecyclerListView;
import org.michaelbel.util.ScreenUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.functions.Consumer;
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

@SuppressWarnings("all")
public class MainFragment extends SwipeBackFragment {

    private int prevPosition;
    private int prevTop;
    private boolean scrollUpdated;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();

    private MainActivity activity;
    private SeriesAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Series> list = new ArrayList<>();

    private View fragmentView;
    private TextView emptyView;
    private RecyclerListView recyclerView;
    private FloatingActionButton floatingButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();

        fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));
        setHasOptionsMenu(true);

        activity.toolbarTextView.setText(R.string.AppName);

        emptyView = fragmentView.findViewById(R.id.empty_view);
        emptyView.setTextColor(ContextCompat.getColor(activity, Theme.secondaryTextColor()));

        floatingButton = fragmentView.findViewById(R.id.fab);
        floatingButton.setOnClickListener(view -> start(new AddFragment()));

        adapter = new SeriesAdapter(activity, list);
        layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = fragmentView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOnItemClickListener((view, position) -> {
            int id = adapter.seriesFilteredList.get(position).id;
            start(SeriesFragment.newInstance(id));
        });
        recyclerView.setOnItemLongClickListener((view, position) -> {
            BottomSheet.Builder builder = new BottomSheet.Builder(activity);
            builder.setTitle(adapter.seriesFilteredList.get(position).title);
            builder.setDarkTheme(!Theme.getTheme());
            builder.setCellHeight(ScreenUtils.dp(52));
            builder.setItems(new int[] { R.string.Remove }, (dialogInterface, i) -> {
                if (i == 0) {
                    adapter.removeItem(position);
                }
            });
            builder.show();
            return true;
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (floatingButton.getVisibility() != View.GONE) {
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

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelperSimpleCallback(
                adapter,
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.START | ItemTouchHelper.END
        );
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);

        getSwipeBackLayout().setEnableGesture(false);
        return attachToSwipeBack(fragmentView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillList();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        activity.toolbarTextView.setText(R.string.AppName);
        activity.toolbar.setNavigationIcon(null);

        if (recyclerView != null) {
            recyclerView.removeAllViews();
            recyclerView.setAdapter(adapter);
        }

        if (fragmentView != null) {
            fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));
        }

        activity.toolbar.setNavigationIcon(null);

        ((AppLoader) activity.getApplication()).bus().toObservable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object object) throws Exception {
                if (object instanceof Events.DeleteSeries) {
                    fillList();
                    Toast.makeText(getContext(), getString(R.string.SeriesDeleted, ((Events.DeleteSeries) object).getSeriesTitle()), Toast.LENGTH_SHORT).show();
                } else if (object instanceof Events.UpdateSeries) {
                    fillList();
                    Toast.makeText(getContext(), getString(R.string.SeriesUpdated, ((Events.UpdateSeries) object).getSeriesTitle()), Toast.LENGTH_SHORT).show();
                } else if (object instanceof Events.AddSeries) {
                    fillList();
                    Toast.makeText(getContext(), R.string.SeriesAdded, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.Search));
        searchView.setMaxWidth(getResources().getDisplayMetrics().widthPixels);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        Theme.clearCursorDrawable(searchTextView);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, null);
            // mCursorDrawableRes.set(searchTextView, R.drawable.cursor);
            // Theme.clearCursorDrawable(searchTextView);
        } catch (Exception e) {
        }
        searchTextView.setTextColor(ContextCompat.getColor(activity, R.color.foregroundColor));

        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        settingsItem.setOnMenuItemClickListener(menuItem -> {
            start(new SettingsFragment());
            return true;
        });

        MenuItem aboutItem = menu.findItem(R.id.action_about);
        aboutItem.setOnMenuItemClickListener(menuItem -> {
            start(new AboutFragment());
            return true;
        });

        /*menu.add(R.string.Settings)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                .setOnMenuItemClickListener(menuItem -> {
                    activity.startFragment(new SettingsFragment(), "settingsFragment");
                    return true;
                });

        menu.add(R.string.About)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                .setOnMenuItemClickListener(menuItem -> {
                    activity.startFragment(new AboutFragment(), "aboutFragment");
                    return true;
                });*/
    }

    private void fillList() {
        DatabaseHelper database = DatabaseHelper.getInstance(activity);
        if (database.getList().isEmpty()) {
            emptyView.setText(R.string.NoSeries);
        } else {
            if (!list.isEmpty()) {
                list.clear();
            }
            list.addAll(database.getList());
            sortList();
            adapter.notifyDataSetChanged();

            SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            if (prefs.getBoolean("bottom_counter", false)) {
                list.add(new Series(database.getCount()));
            }
            onLoadSuccessful();
        }
        database.close();
    }

    private void sortList() {
        SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        int sort = prefs.getInt("sort", 0);

        switch (sort) {
            case 0:
                Collections.reverse(list);
                break;
            case 1:
                Collections.sort(list, (series1, series2) ->
                        series1.title.compareTo(series2.title)
                );
                break;
            case 2:
                Collections.sort(list, (series1, series2) ->
                        Integer.compare(series1.seasonCount, series2.seasonCount)
                );
                break;
        }
    }

    private void onLoadSuccessful() {
        // Stop ProgressBar here
    }

    private void hideFloatingButton(boolean hide) {
        if (floatingHidden == hide) {
            return;
        }
        floatingHidden = hide;
        ObjectAnimator animator = ObjectAnimator.ofFloat(floatingButton, "translationY", floatingHidden ? ScreenUtils.dp(100) : 0).setDuration(300);
        animator.setInterpolator(floatingInterpolator);
        floatingButton.setClickable(!hide);
        animator.start();
    }
}