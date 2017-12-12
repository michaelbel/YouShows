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

import com.bumptech.glide.Glide;

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
import org.michaelbel.ui.view.NumberPicker;
import org.michaelbel.util.ScreenUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("all")
public class AddFragment extends Fragment implements View.OnClickListener {

    private int seasons = 0;
    private int episodes = 0;
    private String backdropPath = null;

    private MainActivity activity;

    private EditText titleEditText;
    private TextCell seasonsCell;
    private TextCell episodesCell;
    private NumberPicker numberPicker;
    private ImageView backdropImageView;
    private TextView placeHolderTextView;
    private ProgressBar progressBar;
    private FloatingActionButton doneButton;

    private ImageView titleDoneIcon;
    private ImageView seasonsDoneIcon;
    private ImageView episodesDoneIcon;

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
                Gravity.START | Gravity.TOP, 24, 24, 24, 24));
        linearLayout.addView(imageFrameLayout);

        backdropImageView = new ImageView(activity);
        backdropImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        backdropImageView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        imageFrameLayout.addView(backdropImageView);

        placeHolderTextView = new TextView(activity);
        placeHolderTextView.setGravity(Gravity.CENTER);
        placeHolderTextView.setText(R.string.BackdropTextHolder);
        placeHolderTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        placeHolderTextView.setTextColor(ContextCompat.getColor(activity, Theme.secondaryTextColor()));
        placeHolderTextView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL));
        imageFrameLayout.addView(placeHolderTextView);

        progressBar = new ProgressBar(activity);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        imageFrameLayout.addView(progressBar);

        LayoutContainer titleLayout = new LayoutContainer(activity);
        titleLayout.setDivider(true);
        titleLayout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        linearLayout.addView(titleLayout);

        titleEditText = new EditText(activity);
        titleEditText.requestFocus();
        titleEditText.setLines(1);
        titleEditText.setMaxLines(1);
        titleEditText.setSingleLine();
        titleEditText.setBackgroundDrawable(null);
        //AppUtils.clearCursorDrawable(titleEditText);
        titleEditText.setHint(R.string.SeriesTitle);
        titleEditText.setTypeface(Typeface.DEFAULT);
        titleEditText.setEllipsize(TextUtils.TruncateAt.END);
        titleEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        titleEditText.setHintTextColor(ContextCompat.getColor(activity, Theme.hintTextColor()));
        titleEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        titleEditText.setTextColor(ContextCompat.getColor(activity, Theme.primaryTextColor()));
        titleEditText.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.START | Gravity.CENTER_VERTICAL, 13, 0, 53, 0));
        titleEditText.setOnEditorActionListener((textView, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (backdropPath == null) {
                    downloadImage();
                }
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
                    titleDoneIcon.setVisibility(View.INVISIBLE);
                    doneButton.hide();
                } else {
                    titleDoneIcon.setVisibility(View.VISIBLE);
                    if (seasons != 0) {
                        doneButton.show();
                    }
                }

                downloadImage();
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
        titleLayout.addView(titleEditText);

        titleDoneIcon = new ImageView(activity);
        titleDoneIcon.setVisibility(View.INVISIBLE);
        titleDoneIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_done, ContextCompat.getColor(activity, R.color.colorPrimary)));
        titleDoneIcon.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        titleLayout.addView(titleDoneIcon);

        seasonsCell = new TextCell(activity);
        seasonsCell.setText(R.string.NumberSeasons);
        seasonsCell.setTextColor(Theme.hintTextColor());
        seasonsCell.setHeight(ScreenUtils.dp(52));
        seasonsCell.setOnClickListener(this);
        seasonsCell.setDivider(true);
        linearLayout.addView(seasonsCell);

        seasonsDoneIcon = new ImageView(activity);
        seasonsDoneIcon.setVisibility(View.INVISIBLE);
        seasonsDoneIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_done, ContextCompat.getColor(activity, R.color.colorPrimary)));
        seasonsDoneIcon.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        seasonsCell.addView(seasonsDoneIcon);

        episodesCell = new TextCell(activity);
        episodesCell.setText(R.string.NumberEpisodes);
        episodesCell.setTextColor(Theme.hintTextColor());
        episodesCell.setHeight(ScreenUtils.dp(52));
        episodesCell.setOnClickListener(this);
        linearLayout.addView(episodesCell);

        episodesDoneIcon = new ImageView(activity);
        episodesDoneIcon.setVisibility(View.INVISIBLE);
        episodesDoneIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_done, ContextCompat.getColor(activity, R.color.colorPrimary)));
        episodesDoneIcon.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        episodesCell.addView(episodesDoneIcon);

        doneButton = new FloatingActionButton(activity);
        doneButton.setImageResource(R.drawable.ic_done);
        doneButton.setVisibility(View.INVISIBLE);
        doneButton.setOnClickListener(this);
        doneButton.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.END | Gravity.BOTTOM, 0, 0, 16, 16));
        fragmentView.addView(doneButton);

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showKeyboard.run();
    }

    @Override
    public void onClick(View view) {
        if (view == seasonsCell) {
            showPicker(true);
        } else if (view == episodesCell) {
            showPicker(false);
        } else if (view == doneButton) {
            addSeries();
        }
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
                    seasonsCell.setText(activity.getString(R.string.SeasonCount, numberPicker.getValue()));
                } else {
                    seasonsCell.setText(activity.getString(R.string.SeasonsCount, numberPicker.getValue()));
                }

                seasonsDoneIcon.setVisibility(View.VISIBLE);
                seasonsCell.setTextColor(Theme.primaryTextColor());
                seasons = numberPicker.getValue();

                if (titleEditText.getText().toString().isEmpty()) {
                    doneButton.hide();
                } else {
                    doneButton.show();
                }
            } else {
                if (numberPicker.getValue() == 0) {
                    episodesCell.setText(R.string.NoEpisodes);
                } else if (numberPicker.getValue() == 1) {
                    episodesCell.setText(activity.getString(R.string.EpisodeCount, numberPicker.getValue()));
                } else {
                    episodesCell.setText(activity.getString(R.string.EpisodesCount, numberPicker.getValue()));
                }

                episodesDoneIcon.setVisibility(View.VISIBLE);
                episodesCell.setTextColor(Theme.primaryTextColor());
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

        DatabaseHelper database = DatabaseHelper.getInstance(activity);
        database.addSeries(series);
        database.close();

        ((AppLoader) activity.getApplication()).bus().send(new Events.AddSeries());
        activity.finishFragment();
    }

    private void downloadImage() {
        placeHolderTextView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        String finalTitle = titleEditText.getText().toString().trim().replace(" ", "-");

        SEARCH service = ApiFactory.getRetrofit().create(SEARCH.class);
        Call<Results> call = service.searchSeries(Url.TMDB_API_KEY, finalTitle);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.isSuccessful()) {
                    try {
                        List<TVShow> list = response.body().list;

                        if (list.isEmpty()) {
                            loadImage(null);
                        } else {
                            /*if (!Objects.equals(backdropPath, list.get(0).backdropPath)) {
                                loadImage(list.get(0).backdropPath);
                            }*/
                            loadImage(list.get(0).backdropPath);
                        }
                    } catch (Exception e) {
                        // todo something
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
        backdropPath = path;

        if (backdropPath != null) {
            SharedPreferences prefs = activity.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            String quality = prefs.getString("image_quality_backdrop", "w780");

            Glide.with(this)
                    .load("http://image.tmdb.org/t/p/" + quality +"/" + backdropPath)
                    .into(backdropImageView);

            placeHolderTextView.setVisibility(View.INVISIBLE);
        } else {
            backdropImageView.setImageDrawable(null);
            placeHolderTextView.setVisibility(View.VISIBLE);
            if (titleEditText.getText().toString().isEmpty()) {
                placeHolderTextView.setText(R.string.BackdropTextHolder);
            } else {
                placeHolderTextView.setText(R.string.NoImage);
            }
        }

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