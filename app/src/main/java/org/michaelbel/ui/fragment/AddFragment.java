package org.michaelbel.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.michaelbel.ui.view.NumberPicker;
import org.michaelbel.api.SEARCH;
import org.michaelbel.database.DatabaseHelper;
import org.michaelbel.model.Results;
import org.michaelbel.model.Series;
import org.michaelbel.model.TVShow;
import org.michaelbel.seriespicker.ApiFactory;
import org.michaelbel.seriespicker.AppLoader;
import org.michaelbel.seriespicker.Events;
import org.michaelbel.seriespicker.LayoutHelper;
import org.michaelbel.seriespicker.R;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.seriespicker.Url;
import org.michaelbel.ui.MainActivity;
import org.michaelbel.ui.cell.TextCell;
import org.michaelbel.ui.view.LayoutContainer;
import org.michaelbel.util.AppUtils;
import org.michaelbel.util.ScreenUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFragment extends Fragment {

    private int seasons = 0;
    private int episodes = 0;
    private String backdropPath;

    private MainActivity activity;

    private EditText titleEditText;
    private TextCell seasonsTextView;
    private TextCell episodesTextView;
    private NumberPicker numberPicker;
    private ImageView backdropImageView;
    private TextView placeHolderTextView;
    private ProgressBar progressBar;
    private FloatingActionButton doneButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();

        FrameLayout fragmentView = new FrameLayout(activity);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));
        setHasOptionsMenu(true);

        activity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        activity.toolbar.setNavigationOnClickListener(view -> activity.finishFragment());
        activity.toolbarTextView.setText(R.string.AddNewSeries);

        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        fragmentView.addView(linearLayout);

        FrameLayout imageFrameLayout = new FrameLayout(activity);
        imageFrameLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, 180,
                Gravity.START | Gravity.TOP, 24, 24, 24, 0));
        linearLayout.addView(imageFrameLayout);

        backdropImageView = new ImageView(activity);
        backdropImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        backdropImageView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        imageFrameLayout.addView(backdropImageView);

        placeHolderTextView = new TextView(activity);
        placeHolderTextView.setGravity(Gravity.CENTER);
        placeHolderTextView.setText(R.string.BackdropTextHolder);
        placeHolderTextView.setTextColor(ContextCompat.getColor(activity, Theme.secondaryTextColor()));
        placeHolderTextView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 12, 0, 12, 0));
        imageFrameLayout.addView(placeHolderTextView);

        progressBar = new ProgressBar(activity);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0, 0, 16));
        imageFrameLayout.addView(progressBar);

        LayoutContainer frameLayout = new LayoutContainer(activity);
        frameLayout.setDivider(true);
        frameLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                0, 24, 0, 0));
        linearLayout.addView(frameLayout);

        titleEditText = new EditText(activity);
        titleEditText.setLines(1);
        titleEditText.setMaxLines(1);
        titleEditText.requestFocus();
        titleEditText.setBackgroundDrawable(null);
        titleEditText.setHint(R.string.SeriesTitle);
        titleEditText.setTypeface(Typeface.DEFAULT);
        titleEditText.setEllipsize(TextUtils.TruncateAt.END);
        titleEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        titleEditText.setHintTextColor(ContextCompat.getColor(activity, Theme.hintTextColor()));
        titleEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        titleEditText.setTextColor(ContextCompat.getColor(activity, Theme.primaryTextColor()));
        titleEditText.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.START | Gravity.CENTER_VERTICAL, 13, 0, 13, 0));
        titleEditText.setOnEditorActionListener((textView, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                downloadImage();
            }
            return false;
        });
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) {
                    if (seasons != 0) {
                        doneButton.show();
                    }
                } else {
                    if (seasons != 0) {
                        doneButton.show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                /*if (TextUtils.isEmpty(editable.toString().trim())) {
                    doneButton.hide();
                }*/
            }
        });
        titleEditText.setOnFocusChangeListener((view, hasFocus) ->
                setImeVisibility(hasFocus)
        );
        frameLayout.addView(titleEditText);

        AppUtils.clearCursorDrawable(titleEditText);

        seasonsTextView = new TextCell(activity);
        seasonsTextView.setMode(TextCell.MODE_DEFAULT);
        seasonsTextView.setText(R.string.NumberSeasons);
        seasonsTextView.setTextColor(Theme.hintTextColor());
        seasonsTextView.setHeight(ScreenUtils.dp(52));
        seasonsTextView.setDivider(true);
        seasonsTextView.setOnClickListener(view -> {
            showPicker(true);
        });
        linearLayout.addView(seasonsTextView);

        episodesTextView = new TextCell(activity);
        episodesTextView.setMode(TextCell.MODE_DEFAULT);
        episodesTextView.setText(R.string.NumberEpisodes);
        episodesTextView.setTextColor(Theme.hintTextColor());
        episodesTextView.setHeight(ScreenUtils.dp(52));
        episodesTextView.setOnClickListener(view -> {
            showPicker(false);
        });
        linearLayout.addView(episodesTextView);

        doneButton = new FloatingActionButton(activity);
        doneButton.setImageResource(R.drawable.ic_done);
        doneButton.setVisibility(View.INVISIBLE);
        doneButton.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.END | Gravity.BOTTOM, 0, 0, 16, 16));
        doneButton.setOnClickListener(view ->
                addSeries()
        );
        fragmentView.addView(doneButton);

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showKeyboard.run();
    }

    private void showPicker(boolean value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, Theme.alertTheme());
        numberPicker = new NumberPicker(activity);
        numberPicker.setMinValue(value ? 1 : 0);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(value ? (seasons != 0 ? seasons : 1) : episodes);
        builder.setView(numberPicker);
        builder.setTitle(value ? R.string.Seasons : R.string.Episodes);
        builder.setNegativeButton(R.string.Cancel, null);
        builder.setPositiveButton(R.string.Done, (dialog, which) -> {
            if (value) {
                if (numberPicker.getValue() == 1) {
                    seasonsTextView.setText(activity.getString(R.string.SeasonCount, numberPicker.getValue()));
                } else {
                    seasonsTextView.setText(activity.getString(R.string.SeasonsCount, numberPicker.getValue()));
                }

                seasonsTextView.setTextColor(Theme.primaryTextColor());
                seasons = numberPicker.getValue();

                if (!titleEditText.getText().toString().isEmpty()) {
                    doneButton.show();
                }
            } else {
                if (numberPicker.getValue() == 0) {
                    episodesTextView.setText(R.string.NoEpisodes);
                } else if (numberPicker.getValue() == 1) {
                    episodesTextView.setText(activity.getString(R.string.EpisodeCount, numberPicker.getValue()));
                } else {
                    episodesTextView.setText(activity.getString(R.string.EpisodesCount, numberPicker.getValue()));
                }

                episodesTextView.setTextColor(Theme.primaryTextColor());
                episodes = numberPicker.getValue();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, Theme.primaryColor()));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, Theme.primaryColor()));
    }

    private void addSeries() {
        Series series = new Series();
        series.title = titleEditText.getText().toString();
        series.seasonCount = seasons;
        series.episodeCount = episodes;
        series.backdropPath = backdropPath;

        if (series.title.isEmpty()) {
            Toast.makeText(activity, R.string.SeriesTitleEmpty, Toast.LENGTH_SHORT).show();
        } else if (series.seasonCount == 0) {
            Toast.makeText(activity, R.string.NumberSeasonsEmpty, Toast.LENGTH_SHORT).show();
        } else {
            DatabaseHelper database = DatabaseHelper.getInstance(activity);
            database.addSeries(series);

            activity.finishFragment();
            ((AppLoader) activity.getApplication()).bus().send(new Events.AddSeries());
        }
    }

    private void downloadImage() {
        progressBar.setVisibility(View.VISIBLE);

        String finalTitle = titleEditText.getText().toString().trim().replace(" ", "-");

        SEARCH service = ApiFactory.getRetrofit().create(SEARCH.class);
        Call<Results> call = service.searchSeries(Url.TMDB_API_KEY, finalTitle);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.isSuccessful()) {
                    if (response.body().list.isEmpty()) {
                        loadImage(null);
                    } else {
                        TVShow show = response.body().list.get(0);
                        if (show != null) {
                            loadImage(show.backdropPath);
                        }
                    }
                } else {
                    loadImage(null);
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                loadImage(null);
            }
        });
    }

    private void loadImage(String path) {
        if (path != null) {
            SharedPreferences prefs = getContext().getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            String imageQuality = prefs.getString("image_quality_backdrop", "w780");

            Glide.with(this)
                    .load("http://image.tmdb.org/t/p/" + imageQuality +"/" + path)
                    .into(backdropImageView);
            backdropPath = path;
        } else {
            backdropImageView.setImageResource(R.drawable.place_holder);
            backdropPath = null;
        }

        placeHolderTextView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private Runnable showKeyboard = new Runnable() {
        @Override
        public void run() {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(titleEditText, 0);
            }
        }
    };

    private void setImeVisibility(boolean visible) {
        if (visible) {
            showKeyboard.run();
        } else {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
            }
        }
    }
}