package org.michaelbel.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.michaelbel.app.AppLoader;
import org.michaelbel.app.eventbus.Events;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.rest.model.Season;
import org.michaelbel.rest.model.Show;
import org.michaelbel.seriespicker.R;
import org.michaelbel.ui.ShowActivity;
import org.michaelbel.ui.view.SeasonsLayout;
import org.michaelbel.ui.view.ShowLayout;

import java.util.List;

/**
 * Date: 19 MAR 2018
 * Time: 12:12 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("CheckResult")
public class ShowFragment extends Fragment {

    private ShowActivity activity;
    private List<Season> list;

    private ShowLayout showLayout;
    private SeasonsLayout seasonsLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (ShowActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout fragmentLayout = new LinearLayout(activity);
        fragmentLayout.setOrientation(LinearLayout.VERTICAL);
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.appBar));

        showLayout = new ShowLayout(activity);
        showLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 6, 0, 0));
        fragmentLayout.addView(showLayout);

        seasonsLayout = new SeasonsLayout(activity);
        seasonsLayout.recyclerView.setOnItemClickListener((view, position) -> {
            Season season = seasonsLayout.getSeasons().get(position);
            activity.startSeason(season);
        });
        seasonsLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 6, 0, 6));
        fragmentLayout.addView(seasonsLayout);
        return fragmentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppLoader) activity.getApplication()).bus().toObservable().subscribe(object -> {
            if (object instanceof Events.UpdateSeasonView) {
                seasonsLayout.updateAdapter(list);
            }
        });
    }

    public void setName(String name) {
        showLayout.setName(name);
    }

    public void setOverview(String overview) {
        if (TextUtils.isEmpty(overview)) {
            showLayout.setOverview(getString(R.string.NoOverview));
        } else {
            showLayout.setOverview(overview);
        }
    }

    public void setSeasons(Show show) {
        seasonsLayout.setShow(show);
        seasonsLayout.setShowTitle(show);
        seasonsLayout.setSeasons(show.seasons);

        list = show.seasons;
    }
}