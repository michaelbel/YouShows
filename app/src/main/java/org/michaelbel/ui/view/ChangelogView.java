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

import org.michaelbel.app.Theme;
import org.michaelbel.app.LayoutHelper;
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

    private TextView dateText;
    private TextView versionText;
    private LinearLayout changesLayout;

    public ChangelogView(Context context) {
        super(context);

        setBackgroundColor(ContextCompat.getColor(context, Theme.Color.background()));

        FrameLayout layout = new FrameLayout(context);
        layout.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 16, 16, 16, 0));
        addView(layout);

        versionText = new TextView(context);
        versionText.setLines(1);
        versionText.setMaxLines(1);
        versionText.setSingleLine();
        versionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        versionText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        versionText.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL));
        layout.addView(versionText);

        dateText = new TextView(context);
        dateText.setLines(1);
        dateText.setMaxLines(1);
        dateText.setSingleLine();
        dateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        dateText.setTextColor(ContextCompat.getColor(context, Theme.Color.primaryText()));
        dateText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        dateText.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL));
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
        boolean dates = prefs.getBoolean("changelog_dates", false);

        dateText.setText(date);
        dateText.setVisibility(dates ? VISIBLE : GONE);
    }

    public void setChanges(List<String> changes) {
        changesLayout.removeAllViews();

        for (String change : changes) {
            ChangeView view = new ChangeView(getContext());
            view.addChange(change);
            view.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            changesLayout.addView(view);
        }
    }

    public void setVersionColor(int color) {
        versionText.setTextColor(color);
    }

    private class ChangeView extends FrameLayout {

        private TextView changeText;

        public ChangeView(@NonNull Context context) {
            super(context);

            View view = new View(context);
            view.setBackground(Theme.getIcon(R.drawable.dot_divider, ContextCompat.getColor(context, Theme.Color.secondaryText())));
            view.setLayoutParams(LayoutHelper.makeFrame(5, 5, Gravity.START | Gravity.TOP, 0, 8, 0, 0));
            addView(view);

            changeText = new TextView(context);
            changeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            changeText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
            changeText.setTextColor(ContextCompat.getColor(context, Theme.Color.secondaryText()));
            changeText.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 12, 0, 0, 0));
            addView(changeText);
        }

        public void addChange(String text) {
            changeText.setText(text);
        }
    }
}