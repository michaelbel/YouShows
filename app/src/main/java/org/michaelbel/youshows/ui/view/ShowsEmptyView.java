package org.michaelbel.youshows.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.youshows.Theme;
import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.shows.R;

/**
 * Date: 01 APR 2018
 * Time: 20:11 MSK
 *
 * @author Michael Bel
 */

public class ShowsEmptyView extends LinearLayout {

    public static final int MY_SHOWS_MODE = 1;
    public static final int FOLLOWING_MODE = 2;

    private int mode;

    private ImageView emptyIcon;
    private TextView titleText;
    private ImageView plusIcon;
    private TextView descText1;
    private TextView descText2;

    public ShowsEmptyView(Context context) {
        super(context);
        initialize();
    }

    public ShowsEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        emptyIcon = new ImageView(getContext());
        emptyIcon.setImageDrawable(AndroidExtensions.getIcon(getContext(), R.drawable.ic_show, ContextCompat.getColor(getContext(), Theme.iconActiveColor())));
        emptyIcon.setLayoutParams(LayoutHelper.makeLinear(getContext(), 62, 62));
        addView(emptyIcon);

        titleText = new TextView(getContext());
        titleText.setGravity(Gravity.CENTER);
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        titleText.setTextColor(ContextCompat.getColor(getContext(), Theme.primaryTextColor()));
        titleText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        titleText.setLayoutParams(LayoutHelper.makeLinear(getContext(), LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 8, 24, 0));
        addView(titleText);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(HORIZONTAL);
        layout.setLayoutParams(LayoutHelper.makeLinear(getContext(), LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 2, 24, 0));
        addView(layout);

        descText1 = new TextView(getContext());
        descText1.setText(R.string.ClickOn);
        descText1.setGravity(Gravity.CENTER);
        descText1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        descText1.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        descText1.setTextColor(ContextCompat.getColor(getContext(), Theme.secondaryTextColor()));
        descText1.setLayoutParams(LayoutHelper.makeLinear(getContext(), LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
        layout.addView(descText1);

        plusIcon = new ImageView(getContext());
        plusIcon.setLayoutParams(LayoutHelper.makeLinear(getContext(), 17, 17, Gravity.BOTTOM, 3, 0, 3, 1));
        layout.addView(plusIcon);

        descText2 = new TextView(getContext());
        descText2.setGravity(Gravity.CENTER);
        descText2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        descText2.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        descText2.setTextColor(ContextCompat.getColor(getContext(), Theme.secondaryTextColor()));
        descText2.setLayoutParams(LayoutHelper.makeLinear(getContext(), LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
        layout.addView(descText2);
    }

    public void setMode(int mode) {
        this.mode = mode;

        if (mode == MY_SHOWS_MODE) {
            titleText.setText(R.string.MyShowsEmpty);
            plusIcon.setImageDrawable(AndroidExtensions.getIcon(getContext(), R.drawable.ic_plus, ContextCompat.getColor(getContext(), Theme.accentColor())));
            descText2.setText(R.string.ClickOn2);
        } else if (mode == FOLLOWING_MODE) {
            titleText.setText(R.string.NoFollowingShows);
            plusIcon.setImageDrawable(AndroidExtensions.getIcon(getContext(), R.drawable.ic_eye_plus, ContextCompat.getColor(getContext(), Theme.accentColor())));
            descText2.setText(R.string.ClickOn3);
        }
    }

    public void changeTheme() {
        emptyIcon.setImageDrawable(AndroidExtensions.getIcon(getContext(), R.drawable.ic_show, ContextCompat.getColor(getContext(), Theme.iconActiveColor())));
        titleText.setTextColor(ContextCompat.getColor(getContext(), Theme.primaryTextColor()));
        descText1.setTextColor(ContextCompat.getColor(getContext(), Theme.secondaryTextColor()));
        descText2.setTextColor(ContextCompat.getColor(getContext(), Theme.secondaryTextColor()));

        if (mode == MY_SHOWS_MODE) {
            plusIcon.setImageDrawable(AndroidExtensions.getIcon(getContext(), R.drawable.ic_plus, ContextCompat.getColor(getContext(), Theme.accentColor())));
        } else if (mode == FOLLOWING_MODE) {
            plusIcon.setImageDrawable(AndroidExtensions.getIcon(getContext(), R.drawable.ic_eye_plus, ContextCompat.getColor(getContext(), Theme.accentColor())));
        }
    }
}