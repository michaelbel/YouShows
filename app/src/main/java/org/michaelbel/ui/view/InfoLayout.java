package org.michaelbel.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.michaelbel.old.LayoutHelper;
import org.michaelbel.seriespicker.R;

/**
 * Date: 19 MAR 2018
 * Time: 17:40 MSK
 *
 * @author Michael Bel
 */

public class InfoLayout extends FrameLayout {

    public InfoLayout(@NonNull Context context) {
        super(context);

        setBackgroundColor(ContextCompat.getColor(context, R.color.background));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        addView(linearLayout);

        TextView seasonsTitle = new TextView(context);
        seasonsTitle.setLines(1);
        seasonsTitle.setMaxLines(1);
        seasonsTitle.setSingleLine();
        seasonsTitle.setText("Info");
        seasonsTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        seasonsTitle.setTextColor(ContextCompat.getColor(context, R.color.accent));
        seasonsTitle.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        seasonsTitle.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 16, 8, 16, 8));
        linearLayout.addView(seasonsTitle);
    }
}