package org.michaelbel.ui.fragment;

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

import org.michaelbel.app.Browser;
import org.michaelbel.app.ShowsApp;
import org.michaelbel.app.eventbus.Events;
import org.michaelbel.bottomsheet.BottomSheet;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ScreenUtils;
import org.michaelbel.old.Theme;
import org.michaelbel.old.ui_old.adapter.Holder;
import org.michaelbel.old.ui_old.view.RecyclerListView;
import org.michaelbel.ui.view.cell.EmptyCell;
import org.michaelbel.ui.view.cell.HeaderCell;
import org.michaelbel.ui.view.cell.TextCell;
import org.michaelbel.ui.view.cell.TextDetailCell;
import org.michaelbel.shows.BuildConfig;
import org.michaelbel.shows.R;
import org.michaelbel.ui.SettingsActivity;

/**
 * Date: 19 MAR 2018
 * Time: 12:12 MSK
 *
 * @author Michael Bel
 */

public class SettingsFragment extends Fragment {

    //public final int LANG_EN = 0;
    //public final int LANG_RU = 1;

    private int rowCount;
    //private int languageRow;
    private int defaultTabRow;
    private int enableSortingRow;
    private int inAppBrowserRow;
    private int emptyRow1;
    private int aboutRow;
    private int appInfoRow;
    private int feedbackRow;
    private int rateGooglePlay;
    private int forkGithubRow;
    private int libsRow;
    private int shareFriendsRow;
    private int donatePaypalRow;
    private int changelogsRow;
    private int emptyRow2;

    private int pressCount = 0;

    private String[] tabs;
    //private String[] viewTypes;
    private SharedPreferences prefs;
    private SettingsActivity activity;
    private LinearLayoutManager linearLayoutManager;

    private RecyclerListView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SettingsActivity) getActivity();

        tabs = getResources().getStringArray(R.array.Tabs);
        //viewTypes = getResources().getStringArray(R.array.ViewTypes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.toolbar.setNavigationOnClickListener(view -> activity.finish());
        activity.toolbarTitle.setText(R.string.Settings);

        FrameLayout fragmentLayout = new FrameLayout(activity);
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.background));

        rowCount = 0;
        //languageRow = rowCount++;
        defaultTabRow = rowCount++;
        enableSortingRow = rowCount++;
        inAppBrowserRow = rowCount++;
        emptyRow1 = rowCount++;
        aboutRow = rowCount++;
        appInfoRow = rowCount++;
        feedbackRow = rowCount++;
        rateGooglePlay = rowCount++;
        forkGithubRow = rowCount++;
        //libsRow = rowCount++;
        shareFriendsRow = rowCount++;
        donatePaypalRow = rowCount++;
        changelogsRow = rowCount++;
        emptyRow2 = rowCount++;

        prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new SettingsAdapter());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnItemClickListener((view, position) -> {
            /*if (position == languageRow) {
                SharedPreferences.Editor editor = prefs.edit();

                if (AndroidExtensions.getLanguage() == LANG_EN) {
                    editor.putInt("language", LANG_RU);
                } else if (AndroidExtensions.getLanguage() == LANG_RU) {
                    editor.putInt("language", LANG_EN);
                }

                editor.apply();

                if (view instanceof TextDetailCell) {
                    if (AndroidExtensions.getLanguage() == LANG_EN) {
                        ((TextDetailCell) view).setValue(R.string.LangEnglish);
                    } else if (AndroidExtensions.getLanguage() == LANG_RU) {
                        ((TextDetailCell) view).setValue(R.string.LangRussian);
                    }
                }
            } else*/ if (position == defaultTabRow) {
                BottomSheet.Builder builder = new BottomSheet.Builder(activity);
                builder.setCellHeight(ScreenUtils.dp(52));
                builder.setItemTextColorRes(R.color.primaryText);
                builder.setBackgroundColorRes(R.color.foreground);
                builder.setItems(new int[] { R.string.MyShows, R.string.Following }, (dialog, i) -> {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("default_tab", i);
                    editor.apply();

                    if (view instanceof TextDetailCell) {
                        ((TextDetailCell) view).setValue(tabs[i]);
                    }

                    ((ShowsApp) activity.getApplication()).bus().send(new Events.ChangeDefaultTab());
                });
                builder.show();
            } else if (position == enableSortingRow) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                boolean enable = prefs.getBoolean("sorting", false);
                editor.putBoolean("sorting", !enable);
                editor.apply();
                if (view instanceof TextDetailCell) {
                    ((TextDetailCell) view).setChecked(!enable);
                }

                ((ShowsApp) activity.getApplication()).bus().send(new Events.EnableSorting());
            } else if (position == inAppBrowserRow) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                boolean enable = prefs.getBoolean("in_app_browser", true);
                editor.putBoolean("in_app_browser", !enable);
                editor.apply();
                if (view instanceof TextDetailCell) {
                    ((TextDetailCell) view).setChecked(!enable);
                }
            } else if (position == feedbackRow) {
                try {
                    PackageManager packageManager = activity.getPackageManager();
                    PackageInfo packageInfo = packageManager.getPackageInfo("org.telegram.messenger", 0);
                    if (packageInfo != null) {
                        Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse(ShowsApp.TELEGRAM_URL));
                        startActivity(telegram);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_EMAIL, ShowsApp.EMAIL);
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.Subject));
                        intent.putExtra(Intent.EXTRA_TEXT, "");
                        startActivity(Intent.createChooser(intent, getString(R.string.Feedback)));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (position == rateGooglePlay) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(ShowsApp.APP_MARKET));
                    startActivity(intent);
                } catch (Exception e) {
                    Browser.openUrl(activity, ShowsApp.APP_WEB);
                }
            } else if (position == forkGithubRow) {
                Browser.openUrl(activity, ShowsApp.GITHUB_URL);
            } else if (position == libsRow) {
                //activity.startFragment(new LibsFragment(), R.id.fragment_view, "libsFragment");
            } else if (position == shareFriendsRow) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, ShowsApp.APP_WEB);
                startActivity(Intent.createChooser(intent, getString(R.string.ShareVia)));
            } else if (position == donatePaypalRow) {
                Browser.openUrl(activity, ShowsApp.PAYPAL_ME);
            } else if (position == changelogsRow) {
                activity.startFragment(new ChangelogsFragment(), "changelogsFragment");
            }
        });
        recyclerView.setOnItemLongClickListener((view, position) -> {
            if (position == appInfoRow) {
                Toast.makeText(activity, "¯\\_(ツ)_/¯", Toast.LENGTH_SHORT).show();
            } else if (position == feedbackRow) {
                pressCount++;
                if (pressCount >= 2) {
                    Extensions.copyToClipboard(activity, ShowsApp.TELEGRAM_URL);
                    Toast.makeText(activity, R.string.CopiedToClipboard, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "You are awesome!", Toast.LENGTH_SHORT).show();
                }
            } else if (position == rateGooglePlay) {
                Extensions.copyToClipboard(activity, ShowsApp.APP_WEB);
                Toast.makeText(activity, R.string.CopiedToClipboard, Toast.LENGTH_SHORT).show();
            } else if (position == forkGithubRow) {
                Extensions.copyToClipboard(activity, ShowsApp.GITHUB_URL);
                Toast.makeText(activity, R.string.CopiedToClipboard, Toast.LENGTH_SHORT).show();
            } else if (position == donatePaypalRow) {
                Extensions.copyToClipboard(activity, ShowsApp.PAYPAL_ME);
                Toast.makeText(activity, R.string.CopiedToClipboard, Toast.LENGTH_SHORT).show();
            }

            return true;
        });
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
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
                    cell.setHeight(ScreenUtils.dp(12));
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

                /*if (position == languageRow) {
                    cell.setMode(TextDetailCell.MODE_DEFAULT);
                    cell.setText(R.string.Language);
                    if (AndroidExtensions.getLanguage() == LANG_EN) {
                        cell.setValue(R.string.LangEnglish);
                    } else if (AndroidExtensions.getLanguage() == LANG_RU) {
                        cell.setValue(R.string.LangRussian);
                    }
                    cell.setDivider(true);
                } else*/ if (position == defaultTabRow) {
                    cell.setMode(TextDetailCell.MODE_DEFAULT);
                    cell.setText(R.string.DefaultTab);
                    cell.setValue(tabs[prefs.getInt("default_tab",0)]);
                    cell.setDivider(true);
                } else if (position == enableSortingRow) {
                    cell.setMode(TextDetailCell.MODE_SWITCH);
                    cell.setText(R.string.EnableSorting);
                    cell.setValue(R.string.EnableSortingInfo);
                    cell.setChecked(prefs.getBoolean("sorting", false));
                    cell.setDivider(true);
                } else if (position == inAppBrowserRow) {
                    cell.setMode(TextDetailCell.MODE_SWITCH);
                    cell.setText(R.string.InAppBrowser);
                    cell.setValue(R.string.InAppBrowserInfo);
                    cell.setChecked(prefs.getBoolean("in_app_browser", true));
                    cell.setDivider(false);
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
                    cell.setValue(ShowsApp.TELEGRAM_FDL);
                    cell.setEndIcon(Theme.getIcon(R.drawable.ic_telegram, ContextCompat.getColor(activity, R.color.telegram)));
                    cell.setDivider(true);
                }
            } else {
                TextCell cell = (TextCell) holder.itemView;
                cell.changeLayoutParams();

                if (position == rateGooglePlay) {
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
                    cell.setMode(TextCell.MODE_DEFAULT);
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
                } else if (position == changelogsRow) {
                    cell.setMode(TextCell.MODE_ICON);
                    cell.setIcon(R.drawable.ic_file_xml);
                    cell.setText(R.string.Changelogs);
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
            } else if (/*position == languageRow || */position == defaultTabRow || position == enableSortingRow || position == inAppBrowserRow || position == appInfoRow || position == feedbackRow) {
                return 2;
            } else {
                return 3;
            }
        }
    }
}