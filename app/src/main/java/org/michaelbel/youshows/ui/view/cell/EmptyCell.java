package org.michaelbel.youshows.ui.view.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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

@SuppressLint("ClickableViewAccessibility")
public class EmptyCell extends FrameLayout {

    public static final int MODE_DEFAULT = 10;
    public static final int MODE_TEXT = 11;
    public static final int MODE_LOADING = 12;

    @IntDef({ MODE_DEFAULT, MODE_TEXT, MODE_LOADING })
    private @interface Mode {}

    private int mHeight = 8;
    private int currentMode = MODE_DEFAULT;

    private Paint paint;
    private boolean divider;
    private Rect rect = new Rect();

    public TextView textView;
    private ProgressBar progressBar;

    public EmptyCell(Context context) {
        super(context);

        if (paint == null) {
            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(ContextCompat.getColor(context, Theme.dividerColor()));
        }

        textView = new TextView(context);
        textView.setVisibility(GONE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(ContextCompat.getColor(context, Theme.secondaryTextColor()));
        textView.setPadding(Extensions.dp(context,16), Extensions.dp(context,12), Extensions.dp(context,16), Extensions.dp(context,4));
        textView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        addView(textView);

        progressBar = new ProgressBar(context);
        progressBar.setVisibility(INVISIBLE);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
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

    public void setDivider(boolean divider) {
        this.divider = divider;
        setWillNotDraw(!divider);
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

    @Override
    protected void onDraw(Canvas canvas) {
        if (divider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getForeground() != null) {
            if (rect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                getForeground().setHotspot(event.getX(), event.getY());
            }
        }

        return super.onTouchEvent(event);
    }
}