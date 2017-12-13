package org.michaelbel.ui.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import org.michaelbel.bottomsheet.BottomSheet;
import org.michaelbel.seriespicker.LayoutHelper;
import org.michaelbel.seriespicker.R;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.ui.MainActivity;
import org.michaelbel.ui.adapter.Holder;
import org.michaelbel.ui.cell.EmptyCell;
import org.michaelbel.ui.cell.TextCell;
import org.michaelbel.ui.cell.TextDetailCell;
import org.michaelbel.ui.view.RecyclerListView;
import org.michaelbel.util.ScreenUtils;

@SuppressWarnings("all")
public class SettingsFragment extends Fragment {

    private int rowCount;
    private int themesRow;
    private int emptyRow;
    private int viewTypeRow;
    private int sortRow;
    private int imageQualityRow;
    private int inAppBrowserRow;
    private int bottomCounterRow;
    private int shortNamesRow;
    private int emptyRow2;

    private ListAdapter adapter;
    private MainActivity activity;
    private SharedPreferences prefs;
    private LinearLayoutManager layoutManager;

    private RecyclerListView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();

        FrameLayout fragmentView = new FrameLayout(activity);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));

        activity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        activity.toolbar.setNavigationOnClickListener(view -> activity.finishFragment());
        activity.toolbarTextView.setText(R.string.Settings);

        prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);

        rowCount = 0;
        themesRow = rowCount++;
        emptyRow = rowCount++;
        viewTypeRow = rowCount++;
        sortRow = rowCount++;
        imageQualityRow = rowCount++;
        inAppBrowserRow = rowCount++;
        bottomCounterRow = rowCount++;
        shortNamesRow = rowCount++;
        emptyRow2 = rowCount++;

        adapter = new ListAdapter();
        layoutManager = new LinearLayoutManager(activity);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        recyclerView.setOnItemClickListener((view, position) -> {
            if (position == themesRow) {
                activity.startFragment(new ThemesFragment(), "themesModsFragment");
            } else if (position == viewTypeRow) {
                BottomSheet.Builder builder = new BottomSheet.Builder(activity);
                builder.setDarkTheme(!Theme.getTheme());
                builder.setCellHeight(ScreenUtils.dp(52));
                builder.setItems(new int[] { R.string.ViewList, R.string.ViewCards }, (dialogInterface, i) -> {
                    SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("view_type", i);
                    editor.apply();

                    if (view instanceof TextDetailCell) {
                        ((TextDetailCell) view).setValue(i == 0 ? R.string.ViewList : R.string.ViewCards);
                    }
                });
                builder.show();
            } else if (position == sortRow) {
                BottomSheet.Builder builder = new BottomSheet.Builder(activity);
                builder.setDarkTheme(!Theme.getTheme());
                builder.setCellHeight(ScreenUtils.dp(52));
                builder.setItems(new int[] { R.string.SortByAdded, R.string.SortByTitle, R.string.SortBySeasons }, (dialogInterface, i) -> {
                    SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("sort", i);
                    editor.apply();

                    if (view instanceof TextDetailCell) {
                        if (i == 0) {
                            ((TextDetailCell) view).setValue(R.string.SortByAdded);
                        } else if (i == 1) {
                            ((TextDetailCell) view).setValue(R.string.SortByTitle);
                        } else if (i == 2) {
                            ((TextDetailCell) view).setValue(R.string.SortBySeasons);
                        }
                    }
                });
                builder.show();
            } else if (position == imageQualityRow) {
                BottomSheet.Builder builder = new BottomSheet.Builder(activity);
                builder.setDarkTheme(!Theme.getTheme());
                builder.setCellHeight(ScreenUtils.dp(52));
                builder.setItems(new int[] { R.string.ImageQualityLow, R.string.ImageQualityMedium, R.string.ImageQualityHigh, R.string.ImageQualityOriginal}, (dialogInterface, i) -> {
                    String imageQuality;
                    String imageQualityBackdrop;
                    String imageQualityPoster;

                    if (i == 0) {
                        imageQuality = getString(R.string.ImageQualityLow);
                        imageQualityBackdrop = "w300";
                        imageQualityPoster = "w92";
                    } else if (i == 1) {
                        imageQuality = getString(R.string.ImageQualityMedium);
                        imageQualityBackdrop = "w780";
                        imageQualityPoster = "w342";
                    } else if (i == 2) {
                        imageQuality = getString(R.string.ImageQualityHigh);
                        imageQualityBackdrop = "w1280";
                        imageQualityPoster = "w780";
                    } else {
                        imageQuality = getString(R.string.ImageQualityOriginal);
                        imageQualityBackdrop = "original";
                        imageQualityPoster = "original";
                    }

                    SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("image_quality", imageQuality);
                    editor.putString("image_quality_backdrop", imageQualityBackdrop);
                    editor.putString("image_quality_poster", imageQualityPoster);
                    editor.apply();

                    if (view instanceof TextDetailCell) {
                        ((TextDetailCell) view).setValue(imageQuality);
                    }
                });
                builder.show();
            } else if (position == inAppBrowserRow) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                boolean enable = prefs.getBoolean("in_app_browser", true);
                editor.putBoolean("in_app_browser", !enable);
                editor.apply();
                if (view instanceof TextDetailCell) {
                    ((TextDetailCell) view).setChecked(!enable);
                }
            } else if (position == bottomCounterRow) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                boolean enable = prefs.getBoolean("bottom_counter", false);
                editor.putBoolean("bottom_counter", !enable);
                editor.apply();
                if (view instanceof TextDetailCell) {
                    ((TextDetailCell) view).setChecked(!enable);
                }
            } else if (position == shortNamesRow) {
                SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                boolean enable = prefs.getBoolean("short_format", false);
                editor.putBoolean("short_format", !enable);
                editor.apply();
                if (view instanceof TextDetailCell) {
                    ((TextDetailCell) view).setChecked(!enable);
                }
            }
        });
        fragmentView.addView(recyclerView);

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Parcelable state = layoutManager.onSaveInstanceState();
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.onRestoreInstanceState(state);
    }

    public class ListAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            View cell;

            if (type == 0) {
                cell = new EmptyCell(activity);
            } else if (type == 1) {
                cell = new TextDetailCell(activity);
            } else {
                cell = new TextCell(activity);
            }

            return new Holder(cell);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);

            if (type == 0) {
                EmptyCell cell = (EmptyCell) holder.itemView;

                if (position == emptyRow || position == emptyRow2) {
                    cell.setMode(EmptyCell.MODE_DEFAULT);
                    cell.setHeight(ScreenUtils.dp(12));
                }
            } else if (type == 1) {
                TextDetailCell cell = (TextDetailCell) holder.itemView;
                cell.changeLayoutParams();
                cell.changeSwitchTheme();

                if (position == viewTypeRow) {
                    int viewType = prefs.getInt("view_type", 1);
                    cell.setText(R.string.ViewType);
                    cell.setValue(viewType == 1 ? R.string.ViewCards : R.string.ViewList);
                    cell.setDivider(true);
                } else if (position == sortRow) {
                    int sort = prefs.getInt("sort", 0);
                    cell.setText(R.string.Sort);
                    if (sort == 0) {
                        cell.setValue(R.string.SortByAdded);
                    } else if (sort == 1) {
                        cell.setValue(R.string.SortByTitle);
                    } else if (sort == 2) {
                        cell.setValue(R.string.SortBySeasons);
                    }
                    cell.setDivider(true);
                } else if (position == imageQualityRow) {
                    String imageQuality = prefs.getString("image_quality", getString(R.string.ImageQualityMedium));
                    cell.setText(R.string.ImageQuality);
                    cell.setValue(imageQuality);
                    cell.setDivider(true);
                } else if (position == inAppBrowserRow) {
                    cell.setMode(TextDetailCell.MODE_SWITCH);
                    cell.setText(R.string.InAppBrowser);
                    cell.setValue(R.string.InAppBrowserInfo);
                    cell.setChecked(prefs.getBoolean("in_app_browser", true));
                    cell.setDivider(true);
                } else if (position == bottomCounterRow) {
                    cell.setMode(TextDetailCell.MODE_SWITCH);
                    cell.setText(R.string.BottomCounter);
                    cell.setValue(R.string.BottomCounterInfo);
                    cell.setChecked(prefs.getBoolean("bottom_counter", false));
                    cell.setDivider(true);
                } else if (position == shortNamesRow) {
                    cell.setMode(TextDetailCell.MODE_SWITCH);
                    cell.setText(R.string.ShortNumberFormat);
                    cell.setValue(R.string.ShortNumberFormatInfo);
                    cell.setChecked(prefs.getBoolean("short_format", false));
                }
            } else {
                TextCell cell = (TextCell) holder.itemView;
                cell.changeLayoutParams();

                if (position == themesRow) {
                    cell.setHeight(ScreenUtils.dp(52));
                    cell.setText(R.string.Themes);
                }
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == emptyRow || position == emptyRow2) {
                return 0;
            } else if (position == inAppBrowserRow || position == viewTypeRow || position == sortRow ||
                    position == imageQualityRow || position == bottomCounterRow || position == shortNamesRow) {
                return 1;
            } else {
                return 2;
            }
        }
    }
}