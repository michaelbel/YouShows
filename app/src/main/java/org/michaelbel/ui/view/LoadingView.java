package org.michaelbel.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.michaelbel.app.Theme;
import org.michaelbel.old.LayoutHelper;

/**
 * Date: 01 APR 2018
 * Time: 20:07 MSK
 *
 * @author Michael Bel
 */

public class LoadingView extends FrameLayout {

    public static final int MODE_DEFAULT = 0;
    public static final int MODE_BIG_POSTER = 1;

    @IntDef({
        MODE_DEFAULT,
        MODE_BIG_POSTER
    })
    private @interface Mode {}

    private int mode = MODE_DEFAULT;

    public LoadingView(Context context) {
        super(context);

        setBackgroundColor(ContextCompat.getColor(context, Theme.Color.background()));

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, Theme.Color.accent()), PorterDuff.Mode.MULTIPLY);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(24, 24, Gravity.CENTER, 0, 12, 0, 12));
        addView(progressBar);
    }

    public LoadingView setMode(@Mode int mode) {
        this.mode = mode;
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mode == MODE_DEFAULT) {
            int width = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
            int height = getMeasuredHeight();
            setMeasuredDimension(width, height);
        } else if (mode == MODE_BIG_POSTER) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            setMeasuredDimension(width, height);
        }
    }
}