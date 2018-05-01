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

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.ShowsApp;
import org.michaelbel.app.eventbus.Events;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.app.rest.model.Season;
import org.michaelbel.app.rest.model.Show;
import org.michaelbel.shows.R;
import org.michaelbel.ui.ShowActivity;
import org.michaelbel.ui.view.InfoLayout;
import org.michaelbel.ui.view.SeasonsLayout;
import org.michaelbel.ui.view.OverviewLayout;

import java.util.List;

/**
 * Date: 19 MAR 2018
 * Time: 12:12 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("CheckResult")
public class ShowFragment extends Fragment {

    private List<Season> list;
    private ShowActivity activity;

    private OverviewLayout overviewLayout;
    private SeasonsLayout seasonsLayout;
    private InfoLayout infoLayout;

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

        overviewLayout = new OverviewLayout(activity);
        overviewLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 0));
        fragmentLayout.addView(overviewLayout);

        seasonsLayout = new SeasonsLayout(activity);
        seasonsLayout.recyclerView.setOnItemClickListener((view, position) -> {
            Season season = seasonsLayout.getSeasons().get(position);
            activity.startSeason(season);
        });
        seasonsLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 6, 0, 0));
        fragmentLayout.addView(seasonsLayout);

        infoLayout = new InfoLayout(activity);
        infoLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 6, 0, 0));
        fragmentLayout.addView(infoLayout);

        return fragmentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((ShowsApp) activity.getApplication()).bus().toObservable().subscribe(object -> {
            if (object instanceof Events.UpdateSeasonView) {
                seasonsLayout.updateAdapter(list);
            }
        });
    }

    public void setName(String name) {
        overviewLayout.setName(name);
    }

    public void setOverview(String overview) {
        if (TextUtils.isEmpty(overview)) {
            overviewLayout.setOverview(getString(R.string.NoOverview));
        } else {
            overviewLayout.setOverview(overview);
        }
    }

    public void setSeasons(Show show) {
        seasonsLayout.setShow(show);
        seasonsLayout.setShowTitle(show);
        seasonsLayout.setSeasons(show.seasons);

        list = show.seasons;
    }

    public void setInfo(Show show) {
        infoLayout.setGenres(AndroidExtensions.formatGenres(show.genres));

        infoLayout.setOriginalName(show.originalName);
        infoLayout.setCountries(AndroidExtensions.formatCountries(show.countries));

        infoLayout.setStatus(show.status);
        infoLayout.setType(show.type);

        infoLayout.setDates(AndroidExtensions.formatDate(show.firstAirDate), AndroidExtensions.formatDate(show.lastAirDate));

        infoLayout.setCompanies(AndroidExtensions.formatCompanies(show.companies));
        infoLayout.setHomepage(show.homepage);
    }
}