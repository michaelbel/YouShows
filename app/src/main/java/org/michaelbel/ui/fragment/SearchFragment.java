package org.michaelbel.ui.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ui_old.view.RecyclerListView;
import org.michaelbel.app.rest.ApiFactory;
import org.michaelbel.app.rest.ApiService;
import org.michaelbel.app.rest.model.Show;
import org.michaelbel.app.rest.response.ShowsResponse;
import org.michaelbel.shows.R;
import org.michaelbel.ui.SearchActivity;
import org.michaelbel.ui.adapter.PaginationShowsAdapter;
import org.michaelbel.ui.view.EmptyView;
import org.michaelbel.ui.view.EmptyViewMode;
import org.michaelbel.ui.view.ShowView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Date: 17 MAR 2018
 * Time: 13:40 MSK
 *
 * @author Michael Bel
 */

public class SearchFragment extends Fragment {

    public int page = 1;
    public int totalPages;
    public int totalResults;
    public boolean isLoading = false;
    public boolean isLastPage = false;

    private String searchQuery;
    private SearchActivity activity;
    private PaginationShowsAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private EmptyView emptyView;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (SearchActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout fragmentView = new FrameLayout(activity);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, R.color.background));

        emptyView = new EmptyView(activity);
        emptyView.setMode(EmptyViewMode.MODE_NO_RESULTS);
        emptyView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 0, 24, 0));
        fragmentView.addView(emptyView);

        progressBar = new ProgressBar(activity);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.accent), PorterDuff.Mode.MULTIPLY);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        fragmentView.addView(progressBar);

        adapter = new PaginationShowsAdapter();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        RecyclerListView recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
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
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0/* && totalItemCount >= totalPages*/) {
                        isLoading = true;
                        page++;
                        loadNext();
                    }
                }
            }
        });
        fragmentView.addView(recyclerView);
        return fragmentView;
    }

    public void search(String query) {
        searchQuery = query;
        searchStart();

        if (AndroidExtensions.typeNotConnected()) {
            showError(EmptyViewMode.MODE_NO_CONNECTION);
            return;
        }

        ApiService service = ApiFactory.createService(ApiService.class, ApiFactory.TMDB_API_ENDPOINT);
        service.search(ApiFactory.TMDB_API_KEY, ApiFactory.getLanguage(), searchQuery, page).enqueue(new Callback<ShowsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ShowsResponse> call, @NonNull Response<ShowsResponse> response) {
                if (response.isSuccessful()) {
                    ShowsResponse showsResponse = response.body();
                    if (showsResponse != null) {
                        totalPages = showsResponse.totalPages;
                        totalResults = showsResponse.totalResults;
                        List<Show> results = new ArrayList<>(showsResponse.shows);
                        if (results.isEmpty()) {
                            showError(EmptyViewMode.MODE_NO_RESULTS);
                            return;
                        }
                        showResults(results, true);
                    }
                } else {
                    showError(EmptyViewMode.MODE_NO_RESULTS);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ShowsResponse> call, @NonNull Throwable t) {
                showError(EmptyViewMode.MODE_NO_CONNECTION);
            }
        });
    }

    private void loadNext() {
        ApiService service = ApiFactory.createService(ApiService.class, ApiFactory.TMDB_API_ENDPOINT);
        service.search(ApiFactory.TMDB_API_KEY, ApiFactory.getLanguage(), searchQuery, page).enqueue(new Callback<ShowsResponse>() {
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

    public void searchStart() {
        adapter.getList().clear();
        adapter.notifyDataSetChanged();

        emptyView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void showResults(List<Show> results, boolean firstPage) {
        if (firstPage) {
            progressBar.setVisibility(View.GONE);

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
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyView.setMode(mode);
    }
}