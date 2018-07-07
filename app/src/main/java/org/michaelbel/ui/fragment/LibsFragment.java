package org.michaelbel.ui.fragment;

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
import android.widget.Toast;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.Browser;
import org.michaelbel.app.LayoutHelper;
import org.michaelbel.app.Theme;
import org.michaelbel.app.YouShows;
import org.michaelbel.app.model.Source;
import org.michaelbel.bottomsheet.BottomSheet;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.material.widget.Holder;
import org.michaelbel.material.widget.RecyclerListView;
import org.michaelbel.shows.R;
import org.michaelbel.ui.SettingsActivity;
import org.michaelbel.ui.view.cell.EmptyCell;
import org.michaelbel.ui.view.cell.TextDetailCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 08 MAY 2018
 * Time: 14:20 MSK
 *
 * @author Michael Bel
 */

public class LibsFragment extends Fragment {

    private static final String LICENSE_APACHE = "Apache License 2.0";
    private static final String LICENSE_MIT = "The MIT License (MIT)";
    private static final String LICENSE_GPL2 = "GNU GPL v2";

    private LibsAdapter adapter;
    private SettingsActivity activity;
    private LinearLayoutManager linearLayoutManager;

    private RecyclerListView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SettingsActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.toolbar.setNavigationOnClickListener(view -> activity.finishFragment());

        FrameLayout fragmentView = new FrameLayout(activity);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));

        adapter = new LibsAdapter();
        adapter.addSource(null, null, null); // Powered by Themoviedb
        adapter.addSource("BottomSheet", "https://github.com/michaelbel/bottomsheet", LICENSE_APACHE);
        adapter.addSource("Gson", "https://github.com/google/gson", LICENSE_APACHE);
        adapter.addSource("Retrofit", "https://square.github.io/retrofit", LICENSE_APACHE);
        adapter.addSource("RxJava", "https://github.com/reactivex/rxjava", LICENSE_APACHE);
        adapter.addSource("Picasso", "https://square.github.io/picasso", LICENSE_APACHE);
        adapter.addSource("Realm Java", "https://github.com/realm/realm-java", LICENSE_APACHE);
        adapter.addSource("ExpandableTextView", "https://github.com/blogcat/android-expandabletextview", LICENSE_APACHE);
        adapter.addSource("CircleProgressView", "https://github.com/jakob-grabner/circle-progress-view", LICENSE_MIT);
        adapter.addSource("Telegram for Android", "https://github.com/drklo/telegram", LICENSE_GPL2);

        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        recyclerView.setOnItemClickListener((view, position) -> {
            if (view instanceof EmptyCell) {
                Browser.openUrl(activity, YouShows.TMDB_URL);
            } else if (view instanceof TextDetailCell) {
                Browser.openUrl(activity, adapter.sources.get(position).url);
            }
        });
        recyclerView.setOnItemLongClickListener((view, position) -> {
            if (view instanceof TextDetailCell) {
                BottomSheet.Builder builder = new BottomSheet.Builder(activity);
                builder.setCellHeight(Extensions.dp(activity,52));
                builder.setTitle(adapter.sources.get(position).url).setTitleMultiline(true);
                builder.setTitleTextColorRes(Theme.secondaryTextColor());
                builder.setBackgroundColorRes(Theme.foregroundColor());
                builder.setItemTextColorRes(Theme.primaryTextColor());
                builder.setItems(new int[] {R.string.Open, R.string.CopyLink}, (dialogInterface, i) -> {
                    if (i == 0) {
                        Browser.openUrl(activity, adapter.sources.get(position).url);
                    } else if (i == 1) {
                        Extensions.copyToClipboard(activity, adapter.sources.get(position).url);
                        Toast.makeText(activity, R.string.LinkCopied, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                return true;
            }

            return false;
        });
        fragmentView.addView(recyclerView);
        return fragmentView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Parcelable state = linearLayoutManager.onSaveInstanceState();
        linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.onRestoreInstanceState(state);
    }

    private class LibsAdapter extends RecyclerView.Adapter {

        private final int ITEM_POWERED = 0;
        private final int ITEM_SOURCE = 1;

        private List<Source> sources = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            View view;

            if (type == ITEM_POWERED) {
                view = new EmptyCell(parent.getContext());
            } else {
                view = new TextDetailCell(parent.getContext());
            }

            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Source source = sources.get(position);

            if (getItemViewType(position) == ITEM_POWERED) {
                EmptyCell cell = (EmptyCell) holder.itemView;
                cell.setForeground(Extensions.selectableItemBackgroundDrawable(activity));
                cell.setBackgroundColor(ContextCompat.getColor(activity, Theme.foregroundColor()));
                cell.setMode(EmptyCell.MODE_TEXT);
                cell.setDivider(true);
                cell.setText(AndroidExtensions.replaceTags(getString(R.string.PoweredBy)));
                cell.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                cell.textView.setTextColor(ContextCompat.getColor(activity, Theme.primaryTextColor()));
                cell.textView.setPadding(Extensions.dp(activity,16), Extensions.dp(activity,16), Extensions.dp(activity,16), Extensions.dp(activity,16));
            } else if (getItemViewType(position) == ITEM_SOURCE) {
                TextDetailCell cell = (TextDetailCell) holder.itemView;
                cell.changeLayoutParams();
                cell.setText(source.name);
                cell.setValue(source.license);
                cell.setDivider(position != sources.size() - 1);
            }
        }

        @Override
        public int getItemCount() {
            return sources != null ? sources.size() : 0;
        }

        @Override
        public int getItemViewType(int position) {
            return (position == 0) ? ITEM_POWERED : ITEM_SOURCE;
        }

        private void addSource(String name, String url, String license) {
            sources.add(new Source(name, url, license));
            notifyItemInserted(sources.size() - 1);
        }
    }
}