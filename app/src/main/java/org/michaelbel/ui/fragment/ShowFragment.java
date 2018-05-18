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
import org.michaelbel.app.YouShows;
import org.michaelbel.app.Theme;
import org.michaelbel.app.eventbus.Events;
import org.michaelbel.app.rest.model.Company;
import org.michaelbel.app.rest.model.Genre;
import org.michaelbel.app.rest.model.Season;
import org.michaelbel.app.rest.model.Show;
import org.michaelbel.material.annotation.Beta;
import org.michaelbel.app.LayoutHelper;
import org.michaelbel.shows.R;
import org.michaelbel.ui.ShowActivity;
import org.michaelbel.ui.view.InfoLayout;
import org.michaelbel.ui.view.OverviewLayout;
import org.michaelbel.ui.view.SeasonsLayout;

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
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.Color.appBar()));

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

        if (Theme.getTheme() == Theme.THEME_NIGHT_BLUE) {
            fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.Color.background()));
            overviewLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.Color.foreground()));
            seasonsLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.Color.foreground()));
            infoLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.Color.foreground()));
        }

        return fragmentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((YouShows) activity.getApplication()).bus().toObservable().subscribe(object -> {
            if (object instanceof Events.UpdateSeasonsView) {
                seasonsLayout.updateAdapter(list);
            }
        });
    }

    public void setName(String name) {
        overviewLayout.setName(name);
    }

    public void setOverview(String overview) {
        overviewLayout.setOverview(TextUtils.isEmpty(overview) ? getString(R.string.NoOverview) : overview);
    }

    public void setOriginalName(String name) {
        infoLayout.setOriginalName(name);
    }

    public void setStatus(String status) {
        infoLayout.setStatus(status);
    }

    public void setType(String type) {
        infoLayout.setType(type);
    }

    public void setDates(String firstAirDate, String lastAirDate) {
        infoLayout.setDates(
            AndroidExtensions.formatDate(firstAirDate),
            AndroidExtensions.formatDate(lastAirDate)
        );
    }

    public void setHomepage(String homepage) {
        infoLayout.setHomepage(homepage);
    }

    /*public void setGenres(RealmList<String> genres) {
        infoLayout.setGenres(AndroidExtensions.formatGenres(genres));
    }*/

    public void setGenres(List<Genre> genres) {
        infoLayout.setGenres(AndroidExtensions.formatGenres(genres));
    }

    /*public void setOriginalCountries(RealmList<String> countries) {
        infoLayout.setCountries(AndroidExtensions.formatCountries(countries));
    }*/

    public void setOriginalCountries(List<String> countries) {
        infoLayout.setCountries(AndroidExtensions.formatCountries(countries));
    }

    /*public void setCompanies(RealmList<String> companies) {
        infoLayout.setCompanies(AndroidExtensions.formatCompanies(companies));
    }*/

    public void setCompanies(List<Company> companies) {
        infoLayout.setCompanies(AndroidExtensions.formatCompanies(companies));
    }

    @Beta
    public void setSeasons(Show show) {
        seasonsLayout.setShow(show);
        seasonsLayout.setShowTitle(show);
        seasonsLayout.setSeasons(show.seasons);

        list = show.seasons;
    }
}