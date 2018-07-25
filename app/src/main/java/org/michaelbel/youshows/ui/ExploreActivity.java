package org.michaelbel.youshows.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.youshows.Theme;
import org.michaelbel.youshows.model.SearchItem;
import org.michaelbel.youshows.rest.model.Show;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.material.widget.Holder;
import org.michaelbel.beta.searchview.Search;
import org.michaelbel.beta.searchview.widget.SearchView;
import org.michaelbel.shows.R;
import org.michaelbel.youshows.ui.fragment.NowPlayingShowsFragment;
import org.michaelbel.youshows.ui.fragment.PopularShowsFragment;
import org.michaelbel.youshows.ui.fragment.TopRatedShowsFragment;
import org.michaelbel.youshows.ui.view.TabView;
import org.michaelbel.youshows.ui.view.cell.TextCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Date: 19 MAR 2018
 * Time: 13:20 MSK
 *
 * @author Michael Bel
 */

public class ExploreActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 101;

    private final int tab_now_playing = 0;
    private final int tab_popular = 1;
    private final int tab_top_rated = 2;

    private Context context;
    private MenuItem searchItem;

    public TabLayout tabLayout;
    private SearchView searchView;
    private FragmentsPagerAdapter adapter;

    private SuggestionsAdapter suggestionsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        context = ExploreActivity.this;

        getWindow().setStatusBarColor(ContextCompat.getColor(context, Theme.primaryDarkColor()));

        AppBarLayout appBar = findViewById(R.id.app_bar);
        appBar.setBackgroundColor(ContextCompat.getColor(context, Theme.primaryColor()));

        Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setLayoutParams(AndroidExtensions.setScrollFlags(toolbar));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setBackgroundColor(ContextCompat.getColor(context, Theme.primaryColor()));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        adapter = new FragmentsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NowPlayingShowsFragment());
        adapter.addFragment(new PopularShowsFragment());
        adapter.addFragment(new TopRatedShowsFragment());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setTabs(position);

                if (position == tab_popular) {
                    PopularShowsFragment fragment = (PopularShowsFragment) adapter.getItem(tab_popular);
                    if (fragment.isAdapterEmpty()) {
                        fragment.loadFirstPage();
                    }
                } else if (position == tab_now_playing) {
                    NowPlayingShowsFragment fragment = (NowPlayingShowsFragment) adapter.getItem(tab_now_playing);
                    if (fragment.isAdapterEmpty()) {
                        fragment.loadFirstPage();
                    }
                } else if (position == tab_top_rated) {
                    TopRatedShowsFragment fragment = (TopRatedShowsFragment) adapter.getItem(tab_top_rated);
                    if (fragment.isAdapterEmpty()) {
                        fragment.loadFirstPage();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(context, Theme.tabSelectColor()));

        int tab = viewPager.getCurrentItem();

        Objects.requireNonNull(tabLayout.getTabAt(tab_now_playing)).setCustomView(new TabView(context)
                .setTab(R.string.NowPlaying, R.drawable.ic_play_circle, tab == tab_now_playing));
        Objects.requireNonNull(tabLayout.getTabAt(tab_popular)).setCustomView(new TabView(context)
                .setTab(R.string.Popular, R.drawable.ic_fire, tab == tab_popular));
        Objects.requireNonNull(tabLayout.getTabAt(tab_top_rated)).setCustomView(new TabView(context)
                .setTab(R.string.TopRated, R.drawable.ic_star_circle, tab == tab_top_rated));

        suggestionsAdapter = new SuggestionsAdapter();
        showSuggestions();

        searchView = findViewById(R.id.search_view);
        searchView.setVersion(Search.Version.MENU_ITEM);
        searchView.setVersionMargins(Search.VersionMargins.MENU_ITEM);
        searchView.setBackgroundColor(ContextCompat.getColor(context, Theme.foregroundColor()));
        searchView.setLogoIcon(AndroidExtensions.getIcon(context, R.drawable.ic_arrow_back, ContextCompat.getColor(context, Theme.iconActiveColor())));
        searchView.setClearIcon(AndroidExtensions.getIcon(context, R.drawable.ic_clear, ContextCompat.getColor(context, Theme.iconActiveColor())));
        searchView.setMicIcon(AndroidExtensions.getIcon(context, R.drawable.ic_mic_color, ContextCompat.getColor(context, Theme.iconActiveColor())));
        searchView.setOnMicClickListener(new Search.OnMicClickListener() {
            @Override
            public void onMicClick() {

            }
        });
        searchView.setHint(R.string.Search);
        searchView.setTextColor(ContextCompat.getColor(context, Theme.primaryTextColor()));
        searchView.setHintColor(ContextCompat.getColor(context, Theme.secondaryTextColor()));
        searchView.setOnOpenCloseListener(new Search.OnOpenCloseListener() {
            @Override
            public void onOpen() {
                tabLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onClose() {
                tabLayout.setVisibility(View.VISIBLE);
            }
        });
        searchView.setAdapter(suggestionsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        searchItem = menu.add(R.string.Search).setIcon(R.drawable.ic_search)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            .setOnMenuItemClickListener(item -> {
                startActivity(new Intent(context, SearchActivity.class));
                //searchView.open(searchItem);
                return true;
            });

        return super.onCreateOptionsMenu(menu);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (results != null && results.size() > 0) {
                    String textResults = results.get(0);
                    if (!TextUtils.isEmpty(textResults)) {
                        *//*if (searchEditText != null) {
                            searchEditText.setText(textResults);
                            searchEditText.setSelection(searchEditText.getText().length());
                            changeActionIcon();
                            searchFragment.search(textResults);
                            searchFragment.addToSearchHistory(textResults, true);
                        }*//*
                    }
                }
            }
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
    }

    public void startShow(Show show) {
        Intent intent = new Intent(context, ShowActivity.class);
        intent.putExtra("id", show.showId);
        intent.putExtra("name", show.name);
        intent.putExtra("overview", show.overview);
        intent.putExtra("backdropPath", show.backdropPath);
        startActivity(intent);
    }

    private void setTabs(int currentTab) {
        ((TabView) Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(tab_now_playing)).getCustomView())).setSelect(currentTab == tab_now_playing);
        ((TabView) Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(tab_popular)).getCustomView())).setSelect(currentTab == tab_popular);
        ((TabView) Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(tab_top_rated)).getCustomView())).setSelect(currentTab == tab_top_rated);
    }

    private void showSuggestions() {
        /*SharedPreferences prefs = getSharedPreferences("mainconfig", MODE_PRIVATE);
        boolean suggestions = prefs.getBoolean("enable_suggestions", true);
        if (suggestions) {
            RealmResults<SearchItem> results = RealmDb.getSearchItems();
            if (results != null) {
                if (results.isLoaded()) {
                    suggestionsAdapter.addSuggestions(results);
                    //suggestionsRecyclerView.setVisibility(View.VISIBLE);
                    if (!suggestionsAdapter.suggestions.isEmpty()) {
                        //emptyView.setVisibility(View.GONE);
                    }
                }
            }
        }*/

        SearchItem item = new SearchItem();
        item.query = "Мстители";
        item.date = "05.06.2018";
        item.voice = true;

        SearchItem item2 = new SearchItem();
        item2.query = "Мстители 2";
        item2.date = "05.06.2018";
        item2.voice = true;

        SearchItem item3 = new SearchItem();
        item3.query = "Мстители 3";
        item3.date = "05.06.2018";
        item3.voice = true;

        List<SearchItem> results = new ArrayList<>();
        results.add(item);
        results.add(item2);
        results.add(item3);

        suggestionsAdapter.addSuggestions(results);
    }

    private class FragmentsPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();

        private FragmentsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        private void addFragment(Fragment fragment) {
            fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
        }
    }

    private class SuggestionsAdapter extends RecyclerView.Adapter {

        private List<SearchItem> suggestions = new ArrayList<>();

        private void addSuggestions(List<SearchItem> results) {
            for (SearchItem item : results) {
                if (suggestions.size() < 5){
                    suggestions.add(item);
                }
            }

            notifyItemRangeInserted(suggestions.size() + 1, results.size());
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            return new Holder(new TextCell(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            SearchItem item = suggestions.get(position);

            TextCell cell = (TextCell) holder.itemView;
            cell.setMode(TextCell.MODE_ICON);
            cell.setHeight(Extensions.dp(context, 48));
            cell.setText(item.query);
            cell.setIcon(R.drawable.ic_history);
        }

        @Override
        public int getItemCount() {
            return suggestions != null ? suggestions.size() : 0;
        }
    }
}