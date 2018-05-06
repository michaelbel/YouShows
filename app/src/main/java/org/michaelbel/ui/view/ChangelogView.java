package org.michaelbel.ui.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.michaelbel.old.LayoutHelper;
import org.michaelbel.shows.R;

import java.util.List;

/**
 * Date: 26 APR 2018
 * Time: 16:54 MSK
 *
 * @author Michael Bel
 */

@SuppressLint({"ClickableViewAccessibility", "InflateParams"})
public class ChangelogView extends FrameLayout {

    private TextView versionText;
    private TextView dateText;
    private LinearLayout changesLayout;

    public ChangelogView(Context context) {
        super(context);

        setBackgroundColor(ContextCompat.getColor(context, R.color.background));

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 16, 16, 16, 0));
        addView(layout);

        versionText = new TextView(context);
        versionText.setLines(1);
        versionText.setMaxLines(1);
        versionText.setSingleLine();
        versionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        versionText.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        versionText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        versionText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL));
        layout.addView(versionText);

        dateText = new TextView(context);
        dateText.setLines(1);
        dateText.setMaxLines(1);
        dateText.setSingleLine();
        dateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        dateText.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        dateText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        dateText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL, 6, 0, 0, 0));
        layout.addView(dateText);

        changesLayout = new LinearLayout(context);
        changesLayout.setOrientation(LinearLayout.VERTICAL);
        changesLayout.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP, 32, 48, 16, 0));
        addView(changesLayout);
    }

    public void setVersion(String version) {
        versionText.setText(getContext().getString(R.string.Version, version));
    }

    public void setDate(String date) {
        SharedPreferences prefs = getContext().getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean dates = prefs.getBoolean("changelogDates", false);

        dateText.setText(String.format("(%s)", date));
        dateText.setVisibility(dates ? VISIBLE : GONE);
    }

    public void setChanges(List<String> changes) {
        for (String log : changes) {
            ChangeView view = new ChangeView(getContext());
            view.addChange(log);
            view.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            changesLayout.addView(view);
        }
    }

    private class ChangeView extends FrameLayout {

        private TextView changeText;

        public ChangeView(@NonNull Context context) {
            super(context);

            View view = new View(context);
            view.setBackgroundResource(R.drawable.dot_divider);
            view.setLayoutParams(LayoutHelper.makeFrame(5, 5, Gravity.START | Gravity.TOP, 0, 8, 0, 0));
            addView(view);

            changeText = new TextView(context);
            changeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            changeText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
            changeText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            changeText.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 12, 0, 0, 0));
            addView(changeText);
        }

        public void addChange(String text) {
            changeText.setText(text);
        }
    }
}