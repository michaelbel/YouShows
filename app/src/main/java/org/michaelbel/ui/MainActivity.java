package org.michaelbel.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.michaelbel.material.widget2.FragmentsPagerAdapter;
import org.michaelbel.old.Theme;
import org.michaelbel.seriespicker.R;
import org.michaelbel.ui.fragment.FollowingFragment;
import org.michaelbel.ui.fragment.MyShowsFragment;

public class MainActivity extends AppCompatActivity {

    private final int NOTIFICATION_ICON_INDEX = 1;

    private final int tab_shows = 0;
    private final int tab_follow = 1;

    private boolean iconNotificationMode;

    private Menu menu;
    private ViewPager viewPager;

    public Toolbar toolbar;
    public TabLayout tabLayout;
    public FloatingActionButton floatingButton;

    private MyShowsFragment myShowsFragment;
    private FollowingFragment followingEpisodesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        floatingButton = findViewById(R.id.fab);

        setSupportActionBar(toolbar);

        myShowsFragment = new MyShowsFragment();
        followingEpisodesFragment = new FollowingFragment();

        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(this, getSupportFragmentManager());
        adapter.addFragment(myShowsFragment, R.string.MyShows);
        //adapter.addFragment(followingEpisodesFragment, R.string.Following);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                notificationIconVisibility(position);

                if (position == tab_shows) {
                    floatingButton.show();
                } else if (position == tab_follow) {
                    floatingButton.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, Theme.primaryColor()));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.primaryText));
        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.secondaryText), ContextCompat.getColor(this, R.color.primaryText));

        floatingButton.setImageResource(R.drawable.ic_plus);
        floatingButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ExploreActivity.class)));

        SharedPreferences prefs = getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        iconNotificationMode = prefs.getBoolean("following_notifications", true);

        // В настройки добавить какую страницу показывать при запуске.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        menu.add(R.string.Search)
            .setIcon(R.drawable.ic_search)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            .setOnMenuItemClickListener(item -> {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                return true;
            });

        menu.add(null)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            .setOnMenuItemClickListener(item -> {
                if (viewPager.getCurrentItem() == tab_shows) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                } else if (viewPager.getCurrentItem() == tab_follow) {
                    SharedPreferences prefs = getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("following_notifications", !iconNotificationMode);
                    editor.apply();

                    followingEpisodesFragment.showSnackbar(!iconNotificationMode);
                    iconNotificationMode = prefs.getBoolean("following_notifications", true);
                    changeNotificationIcon();
                }

                return true;
            });

        notificationIconVisibility(viewPager.getCurrentItem());
        return super.onCreateOptionsMenu(menu);
    }

    private void notificationIconVisibility(int position) {
        if (menu != null) {
            if (position == tab_follow) {
                menu.getItem(NOTIFICATION_ICON_INDEX).setIcon(iconNotificationMode ? R.drawable.ic_notifications_active : R.drawable.ic_notifications_off);
            } else {
                menu.getItem(NOTIFICATION_ICON_INDEX).setIcon(R.drawable.ic_settings);
            }
        }
    }

    private void changeNotificationIcon() {
        if (menu != null) {
            if (iconNotificationMode) {
                menu.getItem(NOTIFICATION_ICON_INDEX).setIcon(R.drawable.ic_notifications_active);
            } else {
                menu.getItem(NOTIFICATION_ICON_INDEX).setIcon(R.drawable.ic_notifications_off);
            }
        }
    }
}