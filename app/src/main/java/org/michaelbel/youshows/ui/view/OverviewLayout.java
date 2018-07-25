package org.michaelbel.youshows.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.michaelbel.youshows.Theme;
import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.shows.R;

import at.blogc.android.views.ExpandableTextView;

/**
 * Date: 23 MAR 2018
 * Time: 18:25 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("InflateParams")
public class OverviewLayout extends LinearLayout {

    private TextView titleText;
    public ExpandableTextView overviewText;

    public OverviewLayout(@NonNull Context context) {
        super(context);

        setOrientation(VERTICAL);
        setElevation(Extensions.dp(context, 1));
        setBackgroundColor(ContextCompat.getColor(context, Theme.foregroundColor()));

        titleText = new TextView(context);
        titleText.setTextIsSelectable(true);
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
        titleText.setTextColor(ContextCompat.getColor(context, Theme.primaryTextColor()));
        titleText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        titleText.setLayoutParams(LayoutHelper.makeLinear(context, LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 12, 12, 16 + 56 + 8, 0));
        addView(titleText);

        overviewText = (ExpandableTextView) LayoutInflater.from(context).inflate(R.layout.item_overview, null);
        overviewText.setMaxLines(5);
        overviewText.setTextIsSelectable(false);
        overviewText.setAnimationDuration(350L);
        overviewText.setEllipsize(TextUtils.TruncateAt.END);
        overviewText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        overviewText.setInterpolator(new OvershootInterpolator(0));
        overviewText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        overviewText.setTextColor(ContextCompat.getColor(context, Theme.secondaryTextColor()));
        overviewText.setLayoutParams(LayoutHelper.makeLinear(context, LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 12, 2, 12, 12));
        overviewText.setOnClickListener(view -> overviewText.toggle());
        addView(overviewText);
    }

    public void setName(String name) {
        titleText.setText(name);
    }

    public void setOverview(String overview) {
        overviewText.setText(overview);
    }
}