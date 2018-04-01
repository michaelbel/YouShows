package org.michaelbel.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.Theme;
import org.michaelbel.seriespicker.R;

/**
 * Date: 01 APR 2018
 * Time: 20:11 MSK
 *
 * @author Michael Bel
 */

public class MyShowsEmptyView extends LinearLayout {

    public MyShowsEmptyView(Context context) {
        super(context);
        initialize();
    }

    public MyShowsEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        ImageView emptyIcon = new ImageView(getContext());
        emptyIcon.setImageResource(R.drawable.ic_show);
        emptyIcon.setLayoutParams(LayoutHelper.makeLinear(62, 62));
        addView(emptyIcon);

        TextView titleText = new TextView(getContext());
        titleText.setGravity(Gravity.CENTER);
        titleText.setText(R.string.MyShowsEmpty);
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        titleText.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryText));
        titleText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        titleText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 8, 24, 0));
        addView(titleText);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(HORIZONTAL);
        layout.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 24, 2, 24, 0));
        addView(layout);

        TextView descText1 = new TextView(getContext());
        descText1.setText(R.string.ClickOn);
        descText1.setGravity(Gravity.CENTER);
        descText1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        descText1.setTextColor(ContextCompat.getColor(getContext(), R.color.secondaryText));
        descText1.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        descText1.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
        layout.addView(descText1);

        ImageView plusIcon = new ImageView(getContext());
        plusIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_plus, ContextCompat.getColor(getContext(), R.color.accent)));
        plusIcon.setLayoutParams(LayoutHelper.makeLinear(17, 17, Gravity.BOTTOM, 0, 0, 0, 1));
        layout.addView(plusIcon);

        TextView descText2 = new TextView(getContext());
        descText2.setGravity(Gravity.CENTER);
        descText2.setText(R.string.ClickOn2);
        descText2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        descText2.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        descText2.setTextColor(ContextCompat.getColor(getContext(), R.color.secondaryText));
        descText2.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));
        layout.addView(descText2);
    }
}