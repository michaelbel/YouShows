package org.michaelbel.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.LayoutHelper;
import org.michaelbel.app.Theme;
import org.michaelbel.app.model.SearchItem;
import org.michaelbel.app.realm.RealmDb;
import org.michaelbel.app.rest.ApiFactory;
import org.michaelbel.app.rest.ApiService;
import org.michaelbel.app.rest.model.Show;
import org.michaelbel.app.rest.response.ShowsResponse;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.material.widget.Holder;
import org.michaelbel.material.widget.RecyclerListView;
import org.michaelbel.shows.R;
import org.michaelbel.ui.SearchActivity;
import org.michaelbel.ui.adapter.PaginationShowsAdapter;
import org.michaelbel.ui.view.EmptyView;
import org.michaelbel.ui.view.EmptyViewMode;
import org.michaelbel.ui.view.ShowView;
import org.michaelbel.ui.view.cell.TextCell;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;
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

    private SuggestionsAdapter suggestionsAdapter;
    private RecyclerListView suggestionsRecyclerView;

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
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));

        emptyView = new EmptyView(activity);
        emptyView.setMode(EmptyViewMode.MODE_NO_RESULTS);
        emptyView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 0, 24, 0));
        fragmentView.addView(emptyView);

        progressBar = new ProgressBar(activity);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, Theme.accentColor()), PorterDuff.Mode.MULTIPLY);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        fragmentView.addView(progressBar);

        adapter = new PaginationShowsAdapter();
        linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);

        RecyclerListView recyclerView = new RecyclerListView(activity);
        recyclerView.setAdapter(adapter);
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

        suggestionsAdapter = new SuggestionsAdapter();

        suggestionsRecyclerView = new RecyclerListView(activity);
        suggestionsRecyclerView.setVisibility(View.GONE);
        suggestionsRecyclerView.setAdapter(suggestionsAdapter);
        suggestionsRecyclerView.setElevation(Extensions.dp(activity, 2));
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        suggestionsRecyclerView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP));
        suggestionsRecyclerView.setOnItemClickListener((view, position) -> {
            SearchItem item = suggestionsAdapter.suggestions.get(position);
            searchFromSuggestion(item.query);
        });
        suggestionsRecyclerView.setOnItemLongClickListener((view, position) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, Theme.alertDialogStyle());
            builder.setTitle(R.string.AppName);
            builder.setMessage(R.string.ClearHistoryMessage);
            builder.setNegativeButton(R.string.Cancel, null);
            builder.setPositiveButton(R.string.Ok, (dialog, which) -> {
                RealmDb.clearSearchHistory();
                emptyView.setVisibility(View.VISIBLE);
                hideSuggestionsList();
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, Theme.Color.dialogButtonText()));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, Theme.Color.dialogButtonText()));
            return true;
        });
        fragmentView.addView(suggestionsRecyclerView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showSuggestions();
    }

    public void search(String query) {
        if (suggestionsRecyclerView.getVisibility() == View.VISIBLE) {
            hideSuggestionsList();
        }

        searchQuery = query.trim();
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

    public void addToSearchHistory(String query, boolean voice) {
        SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean enable = prefs.getBoolean("search_history", true);
        if (enable) {
            SearchItem item = new SearchItem();
            item.query = query;
            item.date = AndroidExtensions.getCurrentDateAndTime();
            item.voice = voice;
            RealmDb.insertSearchItem(item);
        }
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

    private void showSuggestions() {
        SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean suggestions = prefs.getBoolean("enable_suggestions", true);
        if (suggestions) {
            RealmResults<SearchItem> results = RealmDb.getSearchItems();
            if (results != null) {
                if (results.isLoaded()) {
                    suggestionsAdapter.addSuggestions(results);
                    suggestionsRecyclerView.setVisibility(View.VISIBLE);
                    if (!suggestionsAdapter.suggestions.isEmpty()) {
                        emptyView.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void searchFromSuggestion(String text) {
        activity.searchEditText.setText(text);
        activity.searchEditText.setSelection(activity.searchEditText.getText().length());
        activity.hideKeyboard(activity.searchEditText);
        hideSuggestionsList();
        search(text);
    }

    private void hideSuggestionsList() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(suggestionsRecyclerView, "translationY", 0, -suggestionsRecyclerView.getMeasuredWidth());
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                suggestionsRecyclerView.setVisibility(View.GONE);
            }
        });
        AndroidExtensions.runOnUIThread(animator:: start);
    }

    private class SuggestionsAdapter extends RecyclerView.Adapter {

        private List<SearchItem> suggestions = new ArrayList<>();

        private void addSuggestions(List<SearchItem> results) {
            for (SearchItem item : results) {
                if (suggestions.size() < 5){
                    suggestions.add(item);
                }
            }

            notifyItemRangeInserted(suggestions.size() + 1, results.size());
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            return new Holder(new TextCell(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            SearchItem item = suggestions.get(position);

            TextCell cell = (TextCell) holder.itemView;
            cell.setMode(TextCell.MODE_ICON);
            cell.setHeight(Extensions.dp(activity, 48));
            cell.setText(item.query);
            cell.setIcon(R.drawable.ic_history);
        }

        @Override
        public int getItemCount() {
            return suggestions != null ? suggestions.size() : 0;
        }
    }
}