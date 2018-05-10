package org.michaelbel.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.michaelbel.app.ShowsApp;
import org.michaelbel.app.Theme;
import org.michaelbel.app.eventbus.Events;
import org.michaelbel.app.rest.model.Show;
import org.michaelbel.material.widget2.FragmentsPagerAdapter;
import org.michaelbel.old.ScreenUtils;
import org.michaelbel.shows.R;
import org.michaelbel.ui.fragment.FollowingShowsFragment;
import org.michaelbel.ui.fragment.MyShowsFragment;
import org.michaelbel.ui.view.SortView;

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

    private SharedPreferences prefs;
    private FragmentsPagerAdapter adapter;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();

    public Toolbar toolbar;
    public SortView sortView;
    public TabLayout tabLayout;
    public ViewPager viewPager;
    public FloatingActionButton floatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, Theme.Color.primaryDark()));

        prefs = getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean animations = prefs.getBoolean("animations", true);
        int currentTab = prefs.getInt("default_tab", tab_shows);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, Theme.Color.primary()));
        setSupportActionBar(toolbar);

        floatingButton = findViewById(R.id.fab);
        floatingButton.setClickable(!animations);
        floatingButton.setImageResource(R.drawable.ic_plus);
        floatingButton.setTranslationY(ScreenUtils.dp(animations ? 88 : 0));
        floatingButton.setOnClickListener(v -> startExplore());

        viewPager = findViewById(R.id.view_pager);

        adapter = new FragmentsPagerAdapter(this, getSupportFragmentManager());
        adapter.addFragment(new MyShowsFragment(), R.string.MyShows);
        adapter.addFragment(new FollowingShowsFragment(), R.string.Following);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setSortView();
                //notificationIconVisibility(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, Theme.Color.primary()));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, Theme.Color.primaryText()));
        tabLayout.setTabTextColors(ContextCompat.getColor(this, Theme.Color.secondaryText()), ContextCompat.getColor(this, Theme.Color.primaryText()));

        sortView = findViewById(R.id.sort_view);
        sortView.sortLayout.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == tab_shows) {
                int checkedItem = prefs.getInt("myshows_sort_type", SortView.SORT_BY_DEFAULT);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, Theme.alertTheme());
                builder.setTitle(R.string.Sort);
                builder.setSingleChoiceItems(R.array.Sorts, checkedItem, (dialog, which) -> {
                    SharedPreferences prefs1 = getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                    prefs1.edit().putInt("myshows_sort_type", which).apply();
                    sortView.setType(which);
                    dialog.dismiss();

                    MyShowsFragment fragment = (MyShowsFragment) adapter.getItem(tab_shows);
                    fragment.refreshLayout();
                });
                builder.setNegativeButton(R.string.Cancel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MainActivity.this, Theme.Color.accent()));
            } else if (viewPager.getCurrentItem() == tab_follow) {
                int checkedItem = prefs.getInt("following_sort_type", SortView.SORT_BY_DEFAULT);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, Theme.alertTheme());
                builder.setTitle(R.string.Sort);
                builder.setSingleChoiceItems(R.array.Sorts, checkedItem, (dialog, which) -> {
                    SharedPreferences prefs1 = getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                    prefs1.edit().putInt("following_sort_type", which).apply();
                    sortView.setType(which);
                    dialog.dismiss();

                    FollowingShowsFragment fragment = (FollowingShowsFragment) adapter.getItem(tab_follow);
                    fragment.refreshLayout();
                });
                builder.setNegativeButton(R.string.Cancel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MainActivity.this, Theme.Color.accent()));
            }
        });
        sortView.orderLayout.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == tab_shows) {
                boolean order = prefs.getBoolean("myshows_sort_order", SortView.ORDER_ASCENDING);
                prefs.edit().putBoolean("myshows_sort_order", !order).apply();
                sortView.setOrder(!order);

                //final int[] stateSet = {android.R.attr.state_checked * (!order ? 1 : -1)};
                //sortView.orderArrowIcon.setImageState(stateSet, true);

                MyShowsFragment fragment = (MyShowsFragment) adapter.getItem(tab_shows);
                fragment.refreshLayout();
            } else if (viewPager.getCurrentItem() == tab_follow) {
                boolean order = prefs.getBoolean("following_sort_order", SortView.ORDER_ASCENDING);
                prefs.edit().putBoolean("following_sort_order", !order).apply();
                sortView.setOrder(!order);

                //final int[] stateSet = {android.R.attr.state_checked * (!order ? 1 : -1)};
                //sortView.orderArrowIcon.setImageState(stateSet, true);

                FollowingShowsFragment fragment = (FollowingShowsFragment) adapter.getItem(tab_follow);
                fragment.refreshLayout();
            }
        });
        sortViewVisibility();

        viewPager.setCurrentItem(currentTab);
        setSortView();

        //iconNotificationMode = prefs.getBoolean("following_notifications", true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ((ShowsApp) getApplication()).bus().toObservable().subscribe(object -> {
            if (object instanceof Events.ChangeDefaultTab) {
                SharedPreferences prefs = getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                viewPager.setCurrentItem(prefs.getInt("default_tab", 0));
            } else if (object instanceof Events.EnableSorting) {
                sortViewVisibility();
            } else if (object instanceof Events.ChangeTheme) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this, Theme.Color.primaryDark()));
                toolbar.setBackgroundColor(ContextCompat.getColor(this, Theme.Color.primary()));
                tabLayout.setBackgroundColor(ContextCompat.getColor(this, Theme.Color.primary()));
                tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, Theme.Color.primaryText()));
                tabLayout.setTabTextColors(ContextCompat.getColor(this, Theme.Color.secondaryText()), ContextCompat.getColor(this, Theme.Color.primaryText()));
                sortView.changeTheme();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //this.menu = menu;

        /*menu.add(R.string.Tune)
            .setIcon(R.drawable.ic_tune)
            .setVisible(false)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            .setOnMenuItemClickListener(item -> {
                //sortViewVisible();
                return true;
            });*/

        menu.add(R.string.Settings)
            .setIcon(R.drawable.ic_settings)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            .setOnMenuItemClickListener(item -> {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            });

        //notificationIconVisibility(viewPager.getCurrentItem());
        //notificationIconVisibility(tab_shows);
        return super.onCreateOptionsMenu(menu);
    }

    private void sortViewVisibility() {
        SharedPreferences prefs = getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean visible = prefs.getBoolean("sorting", false);
        sortView.setVisibility(visible ? View.VISIBLE : View.GONE);
        /*if (menu != null) {
            menu.getItem(MENU_ICON_SORTING).setVisible(enable);
        }*/
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
        ObjectAnimator animator = ObjectAnimator.ofFloat(floatingButton, "translationY", -0);
        animator.setInterpolator(floatingInterpolator);
        animator.setDuration(300);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                floatingButton.setClickable(true);
            }
        });
    }

    /*private void notificationIconVisibility(int position) {
        if (menu != null) {
            if (position == tab_follow) {
                menu.getItem(NOTIFICATION_ICON_INDEX).setIcon(iconNotificationMode ? R.drawable.ic_notifications_active : R.drawable.ic_notifications_off);
            } else {
                menu.getItem(NOTIFICATION_ICON_INDEX).setIcon(R.drawable.ic_settings);
            }
        }
    }*/

    /*private void changeNotificationIcon() {
        if (menu != null) {
            if (iconNotificationMode) {
                menu.getItem(NOTIFICATION_ICON_INDEX).setIcon(R.drawable.ic_notifications_active);
            } else {
                menu.getItem(NOTIFICATION_ICON_INDEX).setIcon(R.drawable.ic_notifications_off);
            }
        }
    }*/

    public void startExplore() {
        startActivity(new Intent(MainActivity.this, ExploreActivity.class));
    }

    public void startShow(Show show) {
        Intent intent = new Intent(this, ShowActivity.class);
        intent.putExtra("id", show.showId);
        intent.putExtra("name", show.name);
        intent.putExtra("overview", show.overview);
        intent.putExtra("backdropPath", show.backdropPath);
        startActivity(intent);
    }
}