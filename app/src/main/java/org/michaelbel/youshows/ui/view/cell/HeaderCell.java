package org.michaelbel.youshows.ui.view.cell;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.youshows.Theme;
import org.michaelbel.material.extensions.Extensions;

/**
 * Date: 02 MAR 2018
 * Time: 00:21 MSK
 *
 * @author Michael Bel
 */

public class HeaderCell extends FrameLayout {

    protected TextView textView;

    public HeaderCell(Context context) {
        super(context);

        setElevation(Extensions.dp(context,1));
        setBackgroundColor(ContextCompat.getColor(context, Theme.foregroundColor()));

        textView = new AppCompatTextView(context);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        textView.setTextColor(ContextCompat.getColor(context, Theme.changelogVersionText()));
        textView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.BOTTOM, 16, 16, 16, 8));
        addView(textView);
    }

    public void setText(@NonNull String text) {
        textView.setText(text);
    }

    public void setText(@StringRes int textId) {
        textView.setText(getContext().getText(textId));
    }

    public void setTextColor(@ColorRes int color) {
        textView.setTextColor(ContextCompat.getColor(getContext(), color));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        int height = getMeasuredHeight();
        setMeasuredDimension(width, height);
    }
}