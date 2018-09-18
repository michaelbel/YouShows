package org.michaelbel.youshows.ui.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.material.widget.RecyclerListView;
import org.michaelbel.youshows.Theme;
import org.michaelbel.youshows.rest.ApiFactory;
import org.michaelbel.youshows.rest.ApiService;
import org.michaelbel.youshows.rest.model.Show;
import org.michaelbel.youshows.rest.response.ShowsResponse;
import org.michaelbel.youshows.ui.ExploreActivity;
import org.michaelbel.youshows.ui.adapter.PaginationShowsAdapter;
import org.michaelbel.youshows.ui.view.EmptyView;
import org.michaelbel.youshows.ui.view.EmptyViewMode;
import org.michaelbel.youshows.ui.view.ShowView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Date: 19 MAR 2018
 * Time: 12:12 MSK
 *
 * @author Michael Bel
 */

public class NowPlayingShowsFragment extends Fragment {

    public int page = 1;
    public int totalPages;
    public int totalResults;
    public boolean isLoading = false;
    public boolean isLastPage = false;

    private ExploreActivity activity;
    private PaginationShowsAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private EmptyView emptyView;
    private ProgressBar progressBar;
    private RecyclerListView recyclerView;
    private SwipeRefreshLayout fragmentLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (ExploreActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {}

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (isAdapterEmpty()) {
                    loadFirstPage();
                } else {
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });

        fragmentLayout = new SwipeRefreshLayout(activity);
        fragmentLayout.setRefreshing(false);
        fragmentLayout.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));
        fragmentLayout.setColorSchemeColors(ContextCompat.getColor(activity, Theme.swipeRefreshProgressColor()));
        fragmentLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(activity, Theme.primaryColor()));
        fragmentLayout.setOnRefreshListener(() -> {
            if (isAdapterEmpty()) {
                loadFirstPage();
            } else {
                fragmentLayout.setRefreshing(false);
            }
        });

        FrameLayout contentLayout = new FrameLayout(activity);
        contentLayout.setLayoutParams(LayoutHelper.makeSwipeRefresh(activity, LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        fragmentLayout.addView(contentLayout);

        progressBar = new ProgressBar(activity);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, Theme.accentColor()), PorterDuff.Mode.MULTIPLY);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(activity, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        contentLayout.addView(progressBar);

        emptyView = new EmptyView(activity);
        emptyView.setVisibility(View.GONE);
        emptyView.setOnClickListener(view -> loadFirstPage());
        emptyView.setLayoutParams(LayoutHelper.makeFrame(activity, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 0, 24, 0));
        contentLayout.addView(emptyView);

        adapter = new PaginationShowsAdapter();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(activity, LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        recyclerView.setOnItemClickListener((view, position) -> {
            if (view instanceof ShowView) {
                Show show = adapter.getList().get(position);
                activity.startShow(show);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = linearLayoutManager.getItemCount();
                int visibleItemCount = linearLayoutManager.getChildCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        isLoading = true;
                        page++;
                        loadNextPage();
                    }
                }
            }
        });
        contentLayout.addView(recyclerView);
        return fragmentLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFirstPage();
    }

    public void loadFirstPage() {
        emptyView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        ApiService service = ApiFactory.createService(ApiService.class, ApiFactory.TMDB_API_ENDPOINT);
        service.nowPlaying(ApiFactory.TMDB_API_KEY, ApiFactory.getLanguage(), page).enqueue(new Callback<ShowsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ShowsResponse> call, @NonNull Response<ShowsResponse> response) {
                if (response.isSuccessful()) {
                    ShowsResponse showsResponse = response.body();
                    if (showsResponse != null) {
                        totalPages = showsResponse.totalPages;
                        totalResults = showsResponse.totalResults;

                        List<Show> shows = new ArrayList<>(showsResponse.shows);
                        if (shows.isEmpty()) {
                            showError(EmptyViewMode.MODE_NO_SHOWS);
                            return;
                        }
                        showResults(shows, true);
                    }
                } else {
                    showError(EmptyViewMode.MODE_NO_CONNECTION);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ShowsResponse> call, @NonNull Throwable t) {
                showError(EmptyViewMode.MODE_NO_CONNECTION);
            }
        });
    }

    public void loadNextPage() {
        ApiService service = ApiFactory.createService(ApiService.class, ApiFactory.TMDB_API_ENDPOINT);
        service.nowPlaying(ApiFactory.TMDB_API_KEY, ApiFactory.getLanguage(), page).enqueue(new Callback<ShowsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ShowsResponse> call, @NonNull Response<ShowsResponse> response) {
                if (response.isSuccessful()) {
                    ShowsResponse showsResponse = response.body();
                    if (showsResponse != null) {
                        List<Show> results = new ArrayList<>(showsResponse.shows);
                        showResults(results, false);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ShowsResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void showResults(List<Show> results, boolean firstPage) {
        if (firstPage) {
            fragmentLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);

            adapter.addAll(results);

            if (page < totalPages) {
                adapter.addLoadingFooter();
            } else {
                isLastPage = true;
            }
        } else {
            adapter.removeLoadingFooter();
            isLoading = false;
            adapter.addAll(results);

            if (page != totalPages) {
                adapter.addLoadingFooter();
            } else {
                isLastPage = true;
            }
        }
    }

    public void showError(int mode) {
        fragmentLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setMode(mode);
    }

    public boolean isAdapterEmpty() {
        return adapter.getList().isEmpty();
    }
}