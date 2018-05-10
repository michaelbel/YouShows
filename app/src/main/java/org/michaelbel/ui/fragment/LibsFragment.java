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

import org.michaelbel.app.Browser;
import org.michaelbel.app.Source;
import org.michaelbel.app.Theme;
import org.michaelbel.bottomsheet.BottomSheet;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.material.widget.Holder;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ScreenUtils;
import org.michaelbel.old.ui_old.view.RecyclerListView;
import org.michaelbel.shows.R;
import org.michaelbel.ui.SettingsActivity;
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

    private SettingsActivity activity;
    private LinearLayoutManager linearLayoutManager;
    private List<Source> sources = new ArrayList<>();

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
        activity.toolbarTitle.setText(R.string.OpenSourceLibs);

        FrameLayout fragmentView = new FrameLayout(activity);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.Color.background()));

        sources.add(new Source("BottomSheet", "https://github.com/michaelbel/bottomsheet","Apache License 2.0"));
        sources.add(new Source("Gson", "https://github.com/google/gson","Apache License 2.0"));
        sources.add(new Source("Retrofit", "https://square.github.io/retrofit","Apache License 2.0"));
        sources.add(new Source("RxJava", "https://github.com/reactivex/rxjava","Apache License 2.0"));
        sources.add(new Source("Picasso", "https://square.github.io/picasso","Apache License 2.0"));
        sources.add(new Source("Realm Java", "https://github.com/realm/realm-java", "Apache License 2.0"));
        sources.add(new Source("ExpandableTextView", "https://github.com/blogcat/android-expandabletextview", "Apache License 2.0"));
        //sources.add(new Source("Moxy", "https://github.com/arello-mobile/moxy", "The MIT License (MIT)"));
        //sources.add(new Source("GestureViews", "https://github.com/alexvasilkov/gestureviews", "Apache License 2.0"));
        //sources.add(new Source("ChipsLayoutManager", "https://github.com/beloos/chipslayoutmanager", "Apache License 2.0"));
        //sources.add(new Source("Android Animated Menu Items", "https://github.com/adonixis/android-animated-menu-items", "Apache License 2.0"));
        //sources.add(new Source("Realm Android Adapters", "https://github.com/realm/realm-android-adapters", "Apache License 2.0"));
        //sources.add(new Source("Dagger 2", "https://github.com/google/dagger", "Apache License v2.0"));
        //sources.add(new Source("CircleIndicator", "https://github.com/ongakuer/circleindicator", "Apache License 2.0"));
        //sources.add(new Source("RxAndroid", "https://github.com/reactivex/rxjava","Apache License 2.0"));

        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(new LibsAdapter());
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        recyclerView.setOnItemClickListener((view, position) -> Browser.openUrl(activity, sources.get(position).url));
        recyclerView.setOnItemLongClickListener((view, position) -> {
            BottomSheet.Builder builder = new BottomSheet.Builder(activity);
            builder.setCellHeight(ScreenUtils.dp(52));
            builder.setTitle(sources.get(position).url).setTitleMultiline(true);
            builder.setDarkTheme(true);
            builder.setTitleTextColorRes(Theme.Color.secondaryText());
            builder.setBackgroundColorRes(Theme.Color.foreground());
            builder.setItemTextColorRes(Theme.Color.primaryText());
            builder.setItems(new int[] {R.string.Open, R.string.CopyLink}, (dialogInterface, i) -> {
                if (i == 0) {
                    Browser.openUrl(activity, sources.get(position).url);
                } else if (i == 1) {
                    Extensions.copyToClipboard(activity, sources.get(position).url);
                    Toast.makeText(activity, R.string.LinkCopied, Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
            return true;
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

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            return new Holder(new TextDetailCell(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Source source = sources.get(position);

            TextDetailCell cell = (TextDetailCell) holder.itemView;
            cell.changeLayoutParams();
            cell.setText(source.name);
            cell.setValue(source.license);
            cell.setDivider(position != sources.size() - 1);
        }

        @Override
        public int getItemCount() {
            return sources != null ? sources.size() : 0;
        }
    }
}