package org.michaelbel.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.michaelbel.rest.model.Series;
import org.michaelbel.seriespicker.AppLoader;
import org.michaelbel.seriespicker.Events;
import org.michaelbel.seriespicker.LayoutHelper;
import org.michaelbel.seriespicker.R;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.sqlite.DatabaseHelper;
import org.michaelbel.ui.MainActivity;
import org.michaelbel.ui.view.CountButton;
import org.michaelbel.ui.view.LayoutContainer;
import org.michaelbel.ui.view.SeriesView;
import org.michaelbel.util.ScreenUtils;

import java.util.Objects;

import me.yokeyword.fragmentation_swipeback.SwipeBackFragment;

@SuppressWarnings("all")
public class SeriesFragment extends SwipeBackFragment implements View.OnClickListener {

    private static final String SERIES_ID = "id";

    private int seriesId;
    private int seasons;
    private int episodes;
    private String title;

    private int oldSeasons;
    private int oldEpisodes;
    private String oldTitle;

    private Series currentSeries;
    private MainActivity activity;

    private EditText editText;
    private CountButton[] buttons;
    private SeriesView currentSeriesView;
    private FloatingActionButton doneButton;

    public static SeriesFragment newInstance(int id) {
        Bundle args = new Bundle();
        args.putInt(SERIES_ID, id);

        SeriesFragment fragment = new SeriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();

        FrameLayout fragmentView = new FrameLayout(activity);
        fragmentView.setBackgroundColor(ContextCompat.getColor(activity, Theme.backgroundColor()));
        setHasOptionsMenu(true);

        activity.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        activity.toolbarTextView.setText(R.string.UpdateSeries);

        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        fragmentView.addView(linearLayout);

        currentSeriesView = new SeriesView(activity);
        currentSeriesView.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, 180,
                Gravity.START | Gravity.TOP, 24, 24, 24, 0));
        linearLayout.addView(currentSeriesView);

        buttons = new CountButton[4];
        for (int i = 0; i < 4; i++) {
            buttons[i] = new CountButton(activity);
            buttons[i].setOnClickListener(this);
        }

        LayoutContainer layout1 = new LayoutContainer(activity);
        layout1.setHeight(ScreenUtils.dp(68));
        layout1.setDivider(true);
        layout1.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                0, 36, 0, 0));
        linearLayout.addView(layout1);

        TextView seasonsTextView = new TextView(activity);
        seasonsTextView.setText(R.string.NumberSeasons);
        seasonsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        seasonsTextView.setTextColor(ContextCompat.getColor(activity, Theme.secondaryTextColor()));
        seasonsTextView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL));
        layout1.addView(seasonsTextView);

        buttons[0].setImageMinus();
        buttons[0].setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.START));
        layout1.addView(buttons[0]);

        buttons[1].setImagePlus();
        buttons[1].setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.END));
        layout1.addView(buttons[1]);

        LayoutContainer layout2 = new LayoutContainer(activity);
        layout2.setHeight(ScreenUtils.dp(68));
        linearLayout.addView(layout2);

        TextView episodesTextView = new TextView(activity);
        episodesTextView.setText(R.string.NumberEpisodes);
        episodesTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        episodesTextView.setTextColor(ContextCompat.getColor(activity, Theme.secondaryTextColor()));
        episodesTextView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL));
        layout2.addView(episodesTextView);

        buttons[2].setImageMinus();
        buttons[2].setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.START));
        layout2.addView(buttons[2]);

        buttons[3].setImagePlus();
        buttons[3].setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, Gravity.END));
        layout2.addView(buttons[3]);

        doneButton = new FloatingActionButton(activity);
        doneButton.setImageResource(R.drawable.ic_done);
        doneButton.setElevation(ScreenUtils.dp(1));
        doneButton.setOnClickListener(this);
        doneButton.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.END | Gravity.BOTTOM, 0, 0, 16, 16));
        fragmentView.addView(doneButton);

        return attachToSwipeBack(fragmentView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            seriesId = getArguments().getInt(SERIES_ID);
            getSeries();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        menu.add(R.string.EditTitle)
                .setIcon(R.drawable.ic_edit)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                .setOnMenuItemClickListener(menuItem -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, Theme.alertTheme());
                    editText = new EditText(activity);
                    editText.setText(title);
                    editText.setLines(1);
                    editText.setMaxLines(1);
                    editText.setSingleLine();
                    editText.setHint(R.string.SeriesTitle);
                    editText.setSelection(editText.getText().length());
                    editText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                            24, 0, 24, 0));

                    LinearLayout layout = new LinearLayout(activity);
                    layout.addView(editText);

                    builder.setTitle(R.string.EditTitle);
                    builder.setView(layout);
                    builder.setNegativeButton(R.string.Cancel, null);
                    builder.setPositiveButton(R.string.Done, (dialogInterface, i) -> {
                        if (!editText.getText().toString().isEmpty()) {
                            title = editText.getText().toString();
                            currentSeriesView.setTitle(title);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, Theme.primaryColor()));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, Theme.primaryColor()));

                    showKeyboard.run();
                    return true;
                });

        menu.add(R.string.RemoveSeries)
                .setIcon(R.drawable.ic_delete)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                .setOnMenuItemClickListener(menuItem -> {
                    DatabaseHelper database = DatabaseHelper.getInstance(activity);
                    database.removeSeries(currentSeries);
                    activity.onBackPressed();
                    ((AppLoader) activity.getApplication()).bus().send(new Events.DeleteSeries(currentSeries.title));
                    return true;
                });
    }

    @Override
    public void onClick(View view) {
        if (view == buttons[0]) {
            int i = seasons;

            if (i == 1) {
                i = 1;
            } else {
                i--;
            }

            currentSeriesView.setSeasons(i);
            seasons = i;
        } else if (view == buttons[1]) {
            int i = seasons;
            i++;

            currentSeriesView.setSeasons(i);
            seasons = i;
        } else if (view == buttons[2]) {
            int i = episodes;
            i--;

            currentSeriesView.setEpisodes(i);
            if (i <= 0) {
                i = 0;
            }

            episodes = i;
        } else if (view == buttons[3]) {
            int i = episodes;
            i++;

            currentSeriesView.setEpisodes(i);
            episodes = i;
        } else if (view == doneButton) {
            updateSeries();
        }
    }

    private void getSeries() {
        DatabaseHelper database = DatabaseHelper.getInstance(activity);
        currentSeries = database.getSeries(seriesId);

        currentSeriesView.setBackdrop(currentSeries.backdropPath);
        currentSeriesView.setTitle(currentSeries.title);
        currentSeriesView.setSeasons(currentSeries.seasonCount);
        currentSeriesView.setEpisodes(currentSeries.episodeCount);

        seasons = currentSeries.seasonCount;
        episodes = currentSeries.episodeCount;
        title = currentSeries.title;

        oldSeasons = seasons;
        oldEpisodes = episodes;
        oldTitle = title;
    }

    private void updateSeries() {
        Series series = currentSeries;

        series.seasonCount = seasons;
        series.episodeCount = episodes;
        series.title = title;

        DatabaseHelper database = DatabaseHelper.getInstance(activity);
        database.updateSeries(series);

        activity.onBackPressed();

        if (seasons != oldSeasons || episodes != oldEpisodes || !Objects.equals(title, oldTitle)) {
            ((AppLoader) activity.getApplication()).bus().send(new Events.UpdateSeries(currentSeries.title));
        }
    }

    private Runnable showKeyboard = new Runnable() {
        @Override
        public void run() {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(editText, 0);
            }
        }
    };
}