package org.michaelbel.youshows.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.youshows.Browser;
import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.youshows.Theme;
import org.michaelbel.youshows.YouShows;
import org.michaelbel.youshows.eventbus.Events;
import org.michaelbel.bottomsheet.BottomSheet;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.material.widget.Holder;
import org.michaelbel.material.widget.RecyclerListView;
import org.michaelbel.shows.BuildConfig;
import org.michaelbel.shows.R;
import org.michaelbel.youshows.ui.SettingsActivity;
import org.michaelbel.youshows.ui.view.cell.EmptyCell;
import org.michaelbel.youshows.ui.view.cell.HeaderCell;
import org.michaelbel.youshows.ui.view.cell.TextCell;
import org.michaelbel.youshows.ui.view.cell.TextDetailCell;

/**
 * Date: 19 MAR 2018
 * Time: 12:12 MSK
 *
 * @author Michael Bel
 */

public class SettingsFragment extends Fragment {

    private int rowCount;
    private int themeRow;
    private int defaultTabRow;
    private int dateFormatRow;
    private int sortingRow;
    private int animationsRow;
    private int inAppBrowserRow;
    private int seasonScrollPosRow;
    private int searchHistoryRow;
    private int dataUsageRow;
    private int emptyRow1;
    private int aboutRow;
    private int appInfoRow;
    private int feedbackRow;
    private int rateGooglePlayRow;
    private int forkGithubRow;
    private int libsRow;
    private int shareFriendsRow;
    private int donatePaypalRow;
    private int changelogRow;
    private int emptyRow2;

    private int pressCount = 0;

    private String[] dateFormats = new String[] {
        "d MMM yyyy",
        "MMM d, yyyy",
        "dd.MM.yyyy"
    };

    private String[] tabs;
    private String[] themes;
    private SharedPreferences prefs;
    private SettingsAdapter adapter;
    private SettingsActivity activity;
    private LinearLayoutManager linearLayoutManager;

    private FrameLayout fragmentLayout;
    private RecyclerListView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SettingsActivity) getActivity();
        tabs = getResources().getStringArray(R.array.Tabs);
        themes = getResources().getStringArray(R.array.Themes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.toolbar.setNavigationOnClickListener(view -> activity.finish());

        fragmentLayout = new FrameLayout(activity);
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));

        rowCount = 0;
        themeRow = rowCount++;
        defaultTabRow = rowCount++;
        dateFormatRow = rowCount++;
        sortingRow = rowCount++;
        animationsRow = rowCount++;
        inAppBrowserRow = rowCount++;
        seasonScrollPosRow = rowCount++;
        searchHistoryRow = rowCount++;
        dataUsageRow = rowCount++;
        emptyRow1 = rowCount++;
        aboutRow = rowCount++;
        appInfoRow = rowCount++;
        feedbackRow = rowCount++;
        rateGooglePlayRow = rowCount++;
        forkGithubRow = rowCount++;
        libsRow = rowCount++;
        shareFriendsRow = rowCount++;
        donatePaypalRow = rowCount++;
        changelogRow = rowCount++;
        emptyRow2 = rowCount++;

        adapter = new SettingsAdapter();
        prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnItemClickListener((view, position) -> {
            if (position == themeRow) {
                activity.startFragment(new ThemeFragment(), R.string.Theme);
            } else if (position == defaultTabRow) {
                BottomSheet.Builder builder = new BottomSheet.Builder(activity);
                builder.setCellHeight(Extensions.dp(activity,52));
                builder.setItemTextColorRes(Theme.primaryTextColor());
                builder.setBackgroundColorRes(Theme.foregroundColor());
                builder.setItems(new int[] { R.string.MyShows, R.string.Following }, (dialog, pos) -> {
                    prefs.edit().putInt("default_tab", pos).apply();
                    if (view instanceof TextDetailCell) {
                        ((TextDetailCell) view).setValue(tabs[pos]);
                    }
                    ((YouShows) activity.getApplication()).bus().send(new Events.ChangeDefaultTab());
                });
                builder.show();
            } else if (position == dateFormatRow) {
                BottomSheet.Builder builder = new BottomSheet.Builder(activity);
                builder.setCellHeight(Extensions.dp(activity,52));
                builder.setDarkTheme(true);
                builder.setItemTextColorRes(Theme.primaryTextColor());
                builder.setBackgroundColorRes(Theme.foregroundColor());
                builder.setItems(dateFormats, (dialog, pos) -> {
                    prefs.edit().putString("date_format", dateFormats[pos]).apply();
                    if (view instanceof TextDetailCell) {
                        ((TextDetailCell) view).setValue(dateFormats[pos]);
                    }
                });
                builder.show();
            } else if (position == sortingRow) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                boolean enable = prefs.getBoolean("sorting", false);
                prefs.edit().putBoolean("sorting", !enable).apply();
                if (view instanceof TextDetailCell) {
                    ((TextDetailCell) view).setChecked(!enable);
                }
                ((YouShows) activity.getApplication()).bus().send(new Events.EnableSorting());
            } else if (position == animationsRow) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                boolean enable = prefs.getBoolean("animations", true);
                prefs.edit().putBoolean("animations", !enable).apply();
                if (view instanceof TextDetailCell) {
                    ((TextDetailCell) view).setChecked(!enable);
                }
            } else if (position == inAppBrowserRow) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                boolean enable = prefs.getBoolean("in_app_browser", true);
                prefs.edit().putBoolean("in_app_browser", !enable).apply();
                if (view instanceof TextDetailCell) {
                    ((TextDetailCell) view).setChecked(!enable);
                }
            } else if (position == seasonScrollPosRow) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                boolean enable = prefs.getBoolean("season_scroll_position", true);
                prefs.edit().putBoolean("season_scroll_position", !enable).apply();
                if (view instanceof TextDetailCell) {
                    ((TextDetailCell) view).setChecked(!enable);
                }
            } else if (position == searchHistoryRow) {
                activity.startFragment(new SearchHistoryFragment(), R.string.SearchHistory);
            } else if (position == dataUsageRow) {
                activity.startFragment(new DataUsageFragment(), R.string.DataUsage);
            } else if (position == feedbackRow) {
                try {
                    PackageManager packageManager = activity.getPackageManager();
                    PackageInfo packageInfo = packageManager.getPackageInfo("org.telegram.messenger", 0);
                    if (packageInfo != null) {
                        Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse(YouShows.TELEGRAM_URL));
                        startActivity(telegram);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_EMAIL, YouShows.EMAIL);
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.Subject));
                        intent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(Intent.createChooser(intent, getString(R.string.Feedback)));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (position == rateGooglePlayRow) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(YouShows.APP_MARKET));
                    startActivity(intent);
                } catch (Exception e) {
                    Browser.openUrl(activity, YouShows.APP_WEB);
                }
            } else if (position == forkGithubRow) {
                Browser.openUrl(activity, YouShows.GITHUB_URL);
            } else if (position == libsRow) {
                activity.startFragment(new LibsFragment(), R.string.OpenSourceLibs);
            } else if (position == shareFriendsRow) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, YouShows.APP_WEB);
                startActivity(Intent.createChooser(intent, getString(R.string.ShareVia)));
            } else if (position == donatePaypalRow) {
                Browser.openUrl(activity, YouShows.PAYPAL_ME);
            } else if (position == changelogRow) {
                activity.startFragment(new ChangelogsFragment(), R.string.Changelog);
            }
        });
        recyclerView.setOnItemLongClickListener((view, position) -> {
            if (position == appInfoRow) {
                Toast.makeText(activity, "¯\\_(ツ)_/¯", Toast.LENGTH_SHORT).show();
            } else if (position == feedbackRow) {
                pressCount++;
                if (pressCount >= 2) {
                    Extensions.copyToClipboard(activity, YouShows.TELEGRAM_URL);
                    Toast.makeText(activity, R.string.LinkCopied, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "You are awesome!", Toast.LENGTH_SHORT).show();
                }
            } else if (position == rateGooglePlayRow) {
                Extensions.copyToClipboard(activity, YouShows.APP_WEB);
                Toast.makeText(activity, R.string.LinkCopied, Toast.LENGTH_SHORT).show();
            } else if (position == forkGithubRow) {
                Extensions.copyToClipboard(activity, YouShows.GITHUB_URL);
                Toast.makeText(activity, R.string.LinkCopied, Toast.LENGTH_SHORT).show();
            } else if (position == shareFriendsRow) {
                Extensions.copyToClipboard(activity, YouShows.APP_WEB);
                Toast.makeText(activity, R.string.LinkCopied, Toast.LENGTH_SHORT).show();
            } else if (position == donatePaypalRow) {
                Extensions.copyToClipboard(activity, YouShows.PAYPAL_ME);
                Toast.makeText(activity, R.string.LinkCopied, Toast.LENGTH_SHORT).show();
            }

            return true;
        });
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(activity, LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        fragmentLayout.addView(recyclerView);
        return fragmentLayout;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Parcelable state = linearLayoutManager.onSaveInstanceState();
        linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.onRestoreInstanceState(state);
    }

    public void changeTheme() {
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private class SettingsAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            View cell;

            if (type == 0) {
                cell = new EmptyCell(activity);
            } else if (type == 1) {
                cell = new HeaderCell(activity);
            } else if (type == 2) {
                cell = new TextDetailCell(activity);
            } else {
                cell = new TextCell(activity);
            }

            return new Holder(cell);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);

            if (type == 0) {
                EmptyCell cell = (EmptyCell) holder.itemView;

                if (position == emptyRow1 || position == emptyRow2) {
                    cell.setMode(EmptyCell.MODE_DEFAULT);
                    cell.setHeight(Extensions.dp(activity,12));
                } else if (position == aboutRow) {
                    cell.setMode(EmptyCell.MODE_TEXT);
                    cell.setText(R.string.About);
                    cell.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            } else if (type == 1) {
                HeaderCell cell = (HeaderCell) holder.itemView;
                if (position == aboutRow) {
                    cell.setText(R.string.About);
                }
            } else if (type == 2) {
                TextDetailCell cell = (TextDetailCell) holder.itemView;
                cell.changeLayoutParams();
                cell.changeSwitchTheme();

                if (position == themeRow) {
                    cell.setMode(TextDetailCell.MODE_DEFAULT);
                    cell.setText(R.string.Theme);
                    cell.setValue(themes[prefs.getInt("theme", 1)]);
                    cell.setDivider(true);
                } else if (position == defaultTabRow) {
                    cell.setMode(TextDetailCell.MODE_DEFAULT);
                    cell.setText(R.string.DefaultTab);
                    cell.setValue(tabs[prefs.getInt("default_tab",0)]);
                    cell.setDivider(true);
                } else if (position == dateFormatRow) {
                    cell.setMode(TextDetailCell.MODE_DEFAULT);
                    cell.setText(R.string.DateFormat);
                    cell.setValue(prefs.getString("date_format", dateFormats[0]));
                    cell.setDivider(true);
                } else if (position == sortingRow) {
                    cell.setMode(TextDetailCell.MODE_SWITCH);
                    cell.setText(R.string.EnableSorting);
                    cell.setValue(R.string.EnableSortingInfo);
                    cell.setChecked(prefs.getBoolean("sorting", false));
                    cell.setDivider(true);
                } else if (position == animationsRow) {
                    cell.setMode(TextDetailCell.MODE_SWITCH);
                    cell.setText(R.string.EnableAnimations);
                    cell.setValue(R.string.EnableAnimationsInfo);
                    cell.setChecked(prefs.getBoolean("animations", true));
                    cell.setDivider(true);
                } else if (position == inAppBrowserRow) {
                    cell.setMode(TextDetailCell.MODE_SWITCH);
                    cell.setText(R.string.InAppBrowser);
                    cell.setValue(R.string.InAppBrowserInfo);
                    cell.setChecked(prefs.getBoolean("in_app_browser", true));
                    cell.setDivider(true);
                } else if (position == seasonScrollPosRow) {
                    cell.setMode(TextDetailCell.MODE_SWITCH);
                    cell.setText(R.string.SeasonScrollPosition);
                    cell.setValue(R.string.SeasonScrollPositionInfo);
                    cell.setChecked(prefs.getBoolean("season_scroll_position", true));
                    cell.setDivider(true);
                } else if (position == appInfoRow) {
                    cell.setMode(TextDetailCell.MODE_ICONS);
                    cell.setStartIcon(R.drawable.ic_about);
                    cell.setText(getString(R.string.AppForAndroid, getString(R.string.AppName)));
                    cell.setValue(getString(R.string.VersionBuildDate, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.VERSION_DATE));
                    cell.setDivider(true);
                } else if (position == feedbackRow) {
                    cell.setMode(TextDetailCell.MODE_ICONS);
                    cell.setStartIcon(R.drawable.ic_message_alert);
                    cell.setText(R.string.Feedback);
                    cell.setValue(YouShows.TELEGRAM_FDL);
                    cell.setEndIcon(AndroidExtensions.getIcon(activity, R.drawable.ic_telegram, ContextCompat.getColor(activity, R.color.telegram)));
                    cell.setDivider(true);
                }
            } else {
                TextCell cell = (TextCell) holder.itemView;
                cell.changeLayoutParams();

                if (position == searchHistoryRow) {
                    cell.setMode(TextCell.MODE_DEFAULT);
                    cell.setText(R.string.SearchHistory);
                    cell.setDivider(true);
                } else if (position == dataUsageRow) {
                    cell.setMode(TextCell.MODE_DEFAULT);
                    cell.setText(R.string.DataUsage);
                    cell.setDivider(false);
                } else if (position == rateGooglePlayRow) {
                    cell.setMode(TextCell.MODE_ICON);
                    cell.setIcon(R.drawable.ic_google_play);
                    cell.setText(R.string.RateGooglePlay);
                    cell.setDivider(true);
                } else if (position == forkGithubRow) {
                    cell.setMode(TextCell.MODE_ICON);
                    cell.setIcon(R.drawable.ic_github);
                    cell.setText(R.string.ForkGithub);
                    cell.setDivider(true);
                } else if (position == libsRow) {
                    cell.setMode(TextCell.MODE_ICON);
                    cell.setIcon(R.drawable.ic_storage);
                    cell.setText(R.string.OpenSourceLibs);
                    cell.setDivider(true);
                } else if (position == shareFriendsRow) {
                    cell.setMode(TextCell.MODE_ICON);
                    cell.setIcon(R.drawable.ic_share);
                    cell.setText(R.string.ShareWithFriends);
                    cell.setDivider(true);
                } else if (position == donatePaypalRow) {
                    cell.setMode(TextCell.MODE_ICON);
                    cell.setIcon(R.drawable.ic_paypal);
                    cell.setText(R.string.DonatePaypal);
                    cell.setDivider(true);
                } else if (position == changelogRow) {
                    cell.setMode(TextCell.MODE_ICON);
                    cell.setIcon(R.drawable.ic_file_xml);
                    cell.setText(R.string.Changelog);
                    cell.setDivider(false);
                }
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == emptyRow1 || position == emptyRow2) {
                return 0;
            } else if (position == aboutRow) {
                return 1;
            } else if (position == themeRow || position == defaultTabRow || position == dateFormatRow || position == sortingRow || position == animationsRow || position == inAppBrowserRow || position == seasonScrollPosRow || position == appInfoRow || position == feedbackRow) {
                return 2;
            } else {
                return 3;
            }
        }
    }
}