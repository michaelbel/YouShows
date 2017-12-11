package org.michaelbel.ui.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import org.michaelbel.ui.adapter.ItemBehavior;
import org.michaelbel.ui.adapter.ItemTouchHelperSimpleCallback;
import org.michaelbel.database.DatabaseHelper;
import org.michaelbel.model.Series;
import org.michaelbel.seriespicker.AppLoader;
import org.michaelbel.seriespicker.Events;
import org.michaelbel.seriespicker.LayoutHelper;
import org.michaelbel.seriespicker.R;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.ui.MainActivity;
import org.michaelbel.ui.adapter.BottomCellHolder;
import org.michaelbel.ui.adapter.Holder;
import org.michaelbel.ui.cell.BottomCell;
import org.michaelbel.ui.view.RecyclerListView;
import org.michaelbel.ui.view.SeriesCompatView;
import org.michaelbel.ui.view.SeriesView;
import org.michaelbel.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.functions.Consumer;

public class MainFragment extends Fragment {

    private int prevPosition;
    private int prevTop;
    private boolean scrollUpdated;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();

    private MainActivity activity;
    private SeriesAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Series> list = new ArrayList<>();

    private TextView emptyView;
    private FloatingActionButton floatingButton;
    private ItemBehavior moveAndSwipedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();

        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));
        setHasOptionsMenu(true);

        activity.toolbarTextView.setText(R.string.AppName);

        emptyView = fragmentView.findViewById(R.id.empty_view);
        emptyView.setTextColor(ContextCompat.getColor(activity, Theme.secondaryTextColor()));

        floatingButton = fragmentView.findViewById(R.id.fab);
        floatingButton.setOnClickListener(view ->
                activity.startFragment(new AddFragment(), "addFragment")
        );

        adapter = new SeriesAdapter(list);

        layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerListView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOnItemClickListener((view, position) -> {
            int id = adapter.seriesFilteredList.get(position).id;
            activity.startFragment(SeriesFragment.newInstance(id), "seriesFragment");
        });
        recyclerView.setOnItemLongClickListener((view, position) -> {
            /*BottomSheet.Builder builder = new BottomSheet.Builder(activity);
            builder.setDarkTheme(!Theme.getTheme());
            builder.setItems(new CharSequence[] { "Remove" }, (dialogInterface, i) -> {
                if (i == 0) {
                    //removeItem(adapter.seriesFilteredList.get(position), position);
                    adapter.removeItem(position);
                }
            });
            builder.show();
            return true;*/
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

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addList();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.toolbar.setNavigationIcon(null);
        addList();

        ((AppLoader) activity.getApplication()).bus().toObservable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object object) throws Exception {
                if (object instanceof Events.DeleteSeries) {
                    Toast.makeText(getContext(), getString(R.string.SeriesDeleted, ((Events.DeleteSeries) object).getSeriesTitle()), Toast.LENGTH_SHORT).show();
                } else if (object instanceof Events.UpdateSeries) {
                    Toast.makeText(getContext(), getString(R.string.SeriesUpdated, ((Events.UpdateSeries) object).getSeriesTitle()), Toast.LENGTH_SHORT).show();
                } else if (object instanceof Events.AddSeries) {
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

        /*EditText etSearch = ((EditText) searchItem.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(etSearch, null);// set textCursorDrawable to null
        } catch (Exception e) {
            e.printStackTrace();
        }
        etSearch.setTextColor(ContextCompat.getColor(activity, R.color.foregroundColor));*/

        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        settingsItem.setOnMenuItemClickListener(menuItem -> {
            activity.startFragment(new SettingsFragment(), "settingsFragment");
            return true;
        });

        MenuItem aboutItem = menu.findItem(R.id.action_about);
        aboutItem.setOnMenuItemClickListener(menuItem -> {
            activity.startFragment(new AboutFragment(), "aboutFragment");
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

    private void addList() {
        DatabaseHelper database = DatabaseHelper.getInstance(activity);
        if (database.getList().isEmpty()) {
            emptyView.setText(R.string.NoSeries);
        } else {
            if (!list.isEmpty()) {
                list.clear();
            }
            list.addAll(database.getList());

            SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            int sort = prefs.getInt("sort", 0);
            if (sort == 0) {
                Collections.reverse(list);
            } else if (sort == 1) {
                Collections.sort(list, (series1, series2) ->
                        series1.title.compareTo(series2.title)
                );
            } else if (sort == 2) {
                Collections.sort(list, (series1, series2) ->
                        Integer.compare(series1.seasonCount, series2.seasonCount)
                );
            }

            adapter.notifyDataSetChanged();

            if (prefs.getBoolean("bottom_counter", false)) {
                list.add(new Series(database.getCount()));
            }

            onLoadSuccessful();
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

    public class SeriesAdapter extends RecyclerView.Adapter implements Filterable, ItemBehavior {

        private ArrayList<Series> seriesList;
        private ArrayList<Series> seriesFilteredList;

        private SeriesAdapter(ArrayList<Series> list) {
            this.seriesList = list;
            this.seriesFilteredList = list;
        }

        private void removeItem(int position) {
            String title = adapter.seriesFilteredList.get(position).title;

            DatabaseHelper database = DatabaseHelper.getInstance(activity);
            database.removeSeries(adapter.seriesFilteredList.get(position));

            seriesFilteredList.remove(position);
            notifyItemRemoved(position);

            Toast.makeText(getContext(), getString(R.string.SeriesDeleted, title), Toast.LENGTH_SHORT).show();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            View view;

            if (type == 0) {
                view = new SeriesView(activity);
            } else if (type == 1) {
                view = new SeriesCompatView(activity);
            } else {
                view = new BottomCell(activity);
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
                cell.setText(getString(R.string.SeriesCount, series.seriesCountBottom));
            }
        }

        @Override
        public int getItemCount() {
            return seriesFilteredList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (seriesFilteredList.get(position).seriesCountBottom == 0) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
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
}