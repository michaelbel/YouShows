package org.michaelbel.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.Theme;
import org.michaelbel.app.LayoutHelper;
import org.michaelbel.shows.R;

import at.blogc.android.views.ExpandableTextView;

/**
 * Date: 15 APR 2018
 * Time: 22:45 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("InflateParams")
public class EpisodeOverview extends FrameLayout {

    private TextView dateText;
    private TextView episodesText;
    private ExpandableTextView overviewText;

    public EpisodeOverview(@NonNull Context context) {
        super(context);

        setBackgroundColor(ContextCompat.getColor(context, Theme.backgroundColor()));

        View view = LayoutInflater.from(context).inflate(R.layout.item_season_overview, null);
        view.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        addView(view);

        View dividerView = view.findViewById(R.id.divider_view);
        dividerView.setBackground(AndroidExtensions.getIcon(context, R.drawable.dot_divider, ContextCompat.getColor(context, Theme.primaryTextColor())));

        episodesText = view.findViewById(R.id.episodes_text);
        episodesText.setTextColor(ContextCompat.getColor(context, Theme.primaryTextColor()));
        episodesText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        dateText = view.findViewById(R.id.date_text);
        dateText.setTextColor(ContextCompat.getColor(context, Theme.primaryTextColor()));
        dateText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        overviewText = view.findViewById(R.id.expandable_text);
        overviewText.setMaxLines(5);
        overviewText.setAnimationDuration(350L);
        overviewText.setTextIsSelectable(false);
        overviewText.setEllipsize(TextUtils.TruncateAt.END);
        overviewText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        overviewText.setTextColor(ContextCompat.getColor(context, Theme.secondaryTextColor()));
        overviewText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        overviewText.setOnClickListener(v -> overviewText.toggle());
    }

    public void setEpisodes(int episodes) {
        if (episodesText != null) {
            episodesText.setText(getResources().getString(R.string.EpisodesCount, episodes));
        }
    }

    public void setDate(String date) {
        if (dateText != null) {
            if (TextUtils.isEmpty(date)) {
                dateText.setText(R.string.UnknownDate);
            } else {
                dateText.setText(date);
            }
        }
    }

    public void setOverview(String overview) {
        if (overviewText != null) {
            if (TextUtils.isEmpty(overview)) {
                overviewText.setText(R.string.NoOverview);
            } else {
                overviewText.setText(overview);
            }
        }
    }
}