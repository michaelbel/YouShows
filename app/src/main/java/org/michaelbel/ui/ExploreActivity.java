package org.michaelbel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.Theme;
import org.michaelbel.app.rest.model.Show;
import org.michaelbel.shows.R;
import org.michaelbel.ui.fragment.NowPlayingShowsFragment;
import org.michaelbel.ui.fragment.PopularShowsFragment;
import org.michaelbel.ui.fragment.TopRatedShowsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 19 MAR 2018
 * Time: 13:20 MSK
 *
 * @author Michael Bel
 */

public class ExploreActivity extends AppCompatActivity {

    private final int tab_popular = 0;
    private final int tab_now_playing = 1;
    private final int tab_top_rated = 2;

    public TabLayout tabLayout;
    private FragmentsPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, Theme.Color.primaryDark()));

        AppBarLayout appBar = findViewById(R.id.app_bar);
        appBar.setBackgroundColor(ContextCompat.getColor(this, Theme.Color.primary()));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLayoutParams(AndroidExtensions.setScrollFlags(toolbar));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, Theme.Color.primary()));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.ExploreShows);

        adapter = new FragmentsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PopularShowsFragment(), R.string.Popular);
        adapter.addFragment(new NowPlayingShowsFragment(), R.string.NowPlaying);
        adapter.addFragment(new TopRatedShowsFragment(), R.string.TopRated);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
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
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, Theme.Color.primary()));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, Theme.Color.primaryText()));
        tabLayout.setTabTextColors(ContextCompat.getColor(this, Theme.Color.secondaryText()), ContextCompat.getColor(this, Theme.Color.primaryText()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.Search)
            .setIcon(R.drawable.ic_search)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            .setOnMenuItemClickListener(item -> {
                startActivity(new Intent(ExploreActivity.this, SearchActivity.class));
                return true;
            });

        return super.onCreateOptionsMenu(menu);
    }

    public void startShow(Show show) {
        Intent intent = new Intent(this, ShowActivity.class);
        intent.putExtra("id", show.showId);
        intent.putExtra("name", show.name);
        intent.putExtra("overview", show.overview);
        intent.putExtra("backdropPath", show.backdropPath);
        startActivity(intent);
    }

    private class FragmentsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<CharSequence> titles = new ArrayList<>();

        private FragmentsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        private void addFragment(Fragment fragment, CharSequence title) {
            fragments.add(fragment);
            titles.add(title);
        }

        private void addFragment(Fragment fragment, @StringRes int stringId) {
            addFragment(fragment, getText(stringId));
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
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // For Save Instance Fragments
            //super.destroyItem(container, position, object);
        }
    }
}