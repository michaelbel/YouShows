package org.michaelbel.youshows.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.youshows.Theme;

/**
 * Date: 01 APR 2018
 * Time: 20:07 MSK
 *
 * @author Michael Bel
 */

public class LoadingView extends FrameLayout {

    public LoadingView(Context context) {
        super(context);

        setBackgroundColor(ContextCompat.getColor(context, Theme.backgroundColor()));

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, Theme.accentColor()), PorterDuff.Mode.MULTIPLY);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(context, 24, 24, Gravity.CENTER, 0, 12, 0, 12));
        addView(progressBar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        int height = getMeasuredHeight();
        setMeasuredDimension(width, height);
    }
}