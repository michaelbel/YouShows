package org.michaelbel.ui.view.cell;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.michaelbel.app.LayoutHelper;
import org.michaelbel.app.Theme;
import org.michaelbel.material.extensions.Extensions;

/**
 * Date: 02 MAR 2018
 * Time: 00:21 MSK
 *
 * @author Michael Bel
 */

public class EmptyCell extends FrameLayout {

    public static final int MODE_DEFAULT = 10;
    public static final int MODE_TEXT = 11;
    public static final int MODE_LOADING = 12;

    @IntDef({ MODE_DEFAULT, MODE_TEXT, MODE_LOADING })
    private @interface Mode {}

    private int mHeight = 8;
    private int currentMode = MODE_DEFAULT;

    private ProgressBar progressBar;
    private TextView textView;

    public EmptyCell(Context context) {
        super(context);

        textView = new TextView(context);
        textView.setVisibility(GONE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(ContextCompat.getColor(context, Theme.secondaryTextColor()));
        textView.setPadding(Extensions.dp(context,16), Extensions.dp(context,12), Extensions.dp(context,16), Extensions.dp(context,4));
        textView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        addView(textView);

        progressBar = new ProgressBar(context);
        progressBar.setVisibility(INVISIBLE);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        addView(progressBar);
    }

    public void setHeight(int height) {
        if (currentMode == MODE_DEFAULT) {
            mHeight = height;
            requestLayout();
        }
    }

    public void setText(@NonNull CharSequence text) {
        if (currentMode == MODE_TEXT) {
            textView.setText(text);
        }
    }

    public void setText(@StringRes int textId) {
        setText(getContext().getText(textId));
    }

    public void setMode(@Mode int mode) {
        currentMode = mode;

        if (currentMode == MODE_DEFAULT) {
            textView.setVisibility(GONE);
            progressBar.setVisibility(GONE);
        } else if (currentMode == MODE_TEXT) {
            textView.setVisibility(VISIBLE);
            progressBar.setVisibility(GONE);
        } else if (currentMode == MODE_LOADING) {
            progressBar.setVisibility(VISIBLE);
            textView.setVisibility(GONE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
        int height = mHeight;

        if (currentMode == MODE_TEXT) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        } else if (currentMode == MODE_LOADING) {
            height = MeasureSpec.makeMeasureSpec(Extensions.dp(getContext(),54), MeasureSpec.EXACTLY);
        } else if (currentMode == MODE_DEFAULT) {
            height = mHeight;
        }

        setMeasuredDimension(width, height);
    }
}