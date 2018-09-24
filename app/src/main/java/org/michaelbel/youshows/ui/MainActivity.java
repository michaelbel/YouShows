package org.michaelbel.youshows.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.michaelbel.youshows.Theme;
import org.michaelbel.youshows.YouShows;
import org.michaelbel.youshows.eventbus.Events;
import org.michaelbel.youshows.realm.RealmDb;
import org.michaelbel.youshows.rest.model.Show;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.material.widget2.FragmentsPagerAdapter;
import org.michaelbel.shows.R;
import org.michaelbel.youshows.ui.fragment.FollowingShowsFragment;
import org.michaelbel.youshows.ui.fragment.MyShowsFragment;
import org.michaelbel.youshows.ui.view.SortView;

/**
 * Date: 06 APR 2018
 * Time: 22:33 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("CheckResult")
public class MainActivity extends AppCompatActivity {

    private final int tab_shows = 0;
    private final int tab_follow = 1;

    private Context context;
    private SharedPreferences prefs;
    private FragmentsPagerAdapter adapter;

    public Toolbar toolbar;
    public SortView sortView;
    public TabLayout tabs;
    public ViewPager viewPager;
    public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_YouShows);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        getWindow().setStatusBarColor(ContextCompat.getColor(context, Theme.primaryDarkColor()));

        prefs = getSharedPreferences("mainconfig", MODE_PRIVATE);
        int currentTab = prefs.getInt("default_tab", tab_shows);
        boolean animations = prefs.getBoolean("animations", true);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(context, Theme.primaryColor()));
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setClickable(!animations);
        fab.setImageResource(R.drawable.ic_plus);
        fab.setOnClickListener(view -> startExplore());
        fab.setTranslationY(Extensions.dp(context, animations ? 88 : 0));
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, Theme.accentColor())));

        viewPager = findViewById(R.id.view_pager);

        adapter = new FragmentsPagerAdapter(context, getSupportFragmentManager());
        adapter.addFragment(new MyShowsFragment(), R.string.MyShows);
        adapter.addFragment(new FollowingShowsFragment(), R.string.Following);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setSortView();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(context, Theme.tabSelectColor()));
        tabs.setTabTextColors(ContextCompat.getColor(context, Theme.tabUnselectColor()), ContextCompat.getColor(context, Theme.tabSelectColor()));
        tabs.setBackgroundColor(ContextCompat.getColor(context, Theme.primaryColor()));

        sortView = findViewById(R.id.sort_view);
        sortView.sortLayout.setOnClickListener(view -> {
            if (viewPager.getCurrentItem() == tab_shows) {
                int checkedItem = prefs.getInt("myshows_sort_type", SortView.SORT_BY_DEFAULT);

                AlertDialog.Builder builder = new AlertDialog.Builder(context, Theme.alertDialogStyle());
                builder.setTitle(R.string.Sort);
                builder.setSingleChoiceItems(R.array.Sorts, checkedItem, (dialog, which) -> {
                    prefs.edit().putInt("myshows_sort_type", which).apply();
                    sortView.setType(which);
                    dialog.dismiss();

                    MyShowsFragment fragment = (MyShowsFragment) adapter.getItem(tab_shows);
                    fragment.refreshLayout();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (viewPager.getCurrentItem() == tab_follow) {
                int checkedItem = prefs.getInt("following_sort_type", SortView.SORT_BY_DEFAULT);

                AlertDialog.Builder builder = new AlertDialog.Builder(context, Theme.alertDialogStyle());
                builder.setTitle(R.string.Sort);
                builder.setSingleChoiceItems(R.array.Sorts, checkedItem, (dialog, which) -> {
                    prefs.edit().putInt("following_sort_type", which).apply();
                    sortView.setType(which);
                    dialog.dismiss();

                    FollowingShowsFragment fragment = (FollowingShowsFragment) adapter.getItem(tab_follow);
                    fragment.refreshLayout();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        sortView.orderLayout.setOnClickListener(view -> {
            if (viewPager.getCurrentItem() == tab_shows) {
                boolean order = prefs.getBoolean("myshows_sort_order", SortView.ORDER_ASCENDING);
                prefs.edit().putBoolean("myshows_sort_order", !order).apply();
                sortView.setOrder(!order);
                MyShowsFragment fragment = (MyShowsFragment) adapter.getItem(tab_shows);
                fragment.refreshLayout();
            } else if (viewPager.getCurrentItem() == tab_follow) {
                boolean order = prefs.getBoolean("following_sort_order", SortView.ORDER_ASCENDING);
                prefs.edit().putBoolean("following_sort_order", !order).apply();
                sortView.setOrder(!order);
                FollowingShowsFragment fragment = (FollowingShowsFragment) adapter.getItem(tab_follow);
                fragment.refreshLayout();
            }
        });
        sortViewVisibility();

        viewPager.setCurrentItem(currentTab);
        setSortView();

        if (currentTab == tab_shows) {
            if (RealmDb.getMyWatchedShowsCount() == 0) {
                if (fab.getTranslationY() == Extensions.dp(context,88)) {
                    floatingButtonAppear();
                }
            }
        } else if (currentTab == tab_follow)  {
            if (RealmDb.getFollowingShowsCount() == 0) {
                if (fab.getTranslationY() == Extensions.dp(context,88)) {
                    floatingButtonAppear();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        ((YouShows) getApplication()).bus().toObservable().subscribe(object -> {
            if (object instanceof Events.ChangeDefaultTab) {
                viewPager.setCurrentItem(prefs.getInt("default_tab", tab_shows));
            } else if (object instanceof Events.EnableSorting) {
                sortViewVisibility();
            } else if (object instanceof Events.ChangeTheme) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, Theme.primaryDarkColor()));
                toolbar.setBackgroundColor(ContextCompat.getColor(context, Theme.primaryColor()));
                tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(context, Theme.tabSelectColor()));
                tabs.setTabTextColors(ContextCompat.getColor(context, Theme.tabUnselectColor()), ContextCompat.getColor(context, Theme.tabSelectColor()));
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, Theme.accentColor())));
                tabs.setBackgroundColor(ContextCompat.getColor(context, Theme.primaryColor()));
                sortView.changeTheme();

                MyShowsFragment fragment1 = (MyShowsFragment) adapter.getItem(tab_shows);
                fragment1.changeEmptyViewTheme();

                FollowingShowsFragment fragment2 = (FollowingShowsFragment) adapter.getItem(tab_follow);
                fragment2.changeEmptyViewTheme();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.Settings)
            .setIcon(R.drawable.ic_settings)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            .setOnMenuItemClickListener(item -> {
                startActivity(new Intent(context, SettingsActivity.class));
                return true;
            });

        return super.onCreateOptionsMenu(menu);
    }

    private void sortViewVisibility() {
        boolean visible = prefs.getBoolean("sorting", false);
        sortView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setSortView() {
        if (viewPager.getCurrentItem() == tab_shows) {
            sortView.setType(prefs.getInt("myshows_sort_type", SortView.SORT_BY_DEFAULT));
            sortView.setOrder(prefs.getBoolean("myshows_sort_order", SortView.ORDER_ASCENDING));
        } else if (viewPager.getCurrentItem() == tab_follow) {
            sortView.setType(prefs.getInt("following_sort_type", SortView.SORT_BY_DEFAULT));
            sortView.setOrder(prefs.getBoolean("following_sort_order", SortView.ORDER_ASCENDING));
        }
    }

    public void floatingButtonAppear() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(fab, "translationY", -0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fab.setClickable(true);
            }
        });
        animator.start();
    }

    public void startExplore() {
        startActivity(new Intent(context, ExploreActivity.class));
    }

    public void startShow(Show show) {
        Intent intent = new Intent(context, ShowActivity.class);
        intent.putExtra("id", show.showId);
        intent.putExtra("name", show.name);
        intent.putExtra("overview", show.overview);
        intent.putExtra("backdropPath", show.backdropPath);
        startActivity(intent);
    }
}