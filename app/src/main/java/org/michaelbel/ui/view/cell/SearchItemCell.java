package org.michaelbel.ui.view.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.LayoutHelper;
import org.michaelbel.app.ScreenUtils;
import org.michaelbel.app.Theme;
import org.michaelbel.shows.R;

/**
 * Date: 30 MAY 2018
 * Time: 01:09 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("ClickableViewAccessibility")
public class SearchItemCell extends FrameLayout {

    protected TextView textView;
    protected TextView valueText;
    protected ImageView endIconView;

    private Paint paint;
    private boolean divider;
    private Rect rect = new Rect();

    public SearchItemCell(Context context) {
        super(context);

        setElevation(ScreenUtils.dp(1));
        //setForeground(Extensions.selectableItemBackgroundDrawable(context));
        setBackgroundColor(ContextCompat.getColor(context, Theme.Color.foreground()));

        if (paint == null) {
            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(ContextCompat.getColor(context, Theme.Color.divider()));
        }

        textView = new TextView(context);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(ContextCompat.getColor(context, Theme.Color.primaryText()));
        textView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 16, 5, 16, 0));
        addView(textView);

        valueText = new TextView(context);
        valueText.setLines(1);
        valueText.setMaxLines(1);
        valueText.setSingleLine();
        valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5F);
        valueText.setTextColor(ContextCompat.getColor(context, Theme.Color.secondaryText()));
        valueText.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 16, 27, 16, 0));
        addView(valueText);

        endIconView = new ImageView(context);
        endIconView.setFocusable(false);
        endIconView.setScaleType(ImageView.ScaleType.CENTER);
        endIconView.setBackground(AndroidExtensions.selectableItemBackgroundBorderlessDrawable(context));
        endIconView.setImageDrawable(Theme.getIcon(R.drawable.ic_close_circle, ContextCompat.getColor(context, Theme.Color.iconActive())));
        endIconView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        //addView(endIconView);
    }

    public void setQuery(@NonNull String text) {
        textView.setText(text);
    }

    public void setDate(@NonNull String text) {
        if (!TextUtils.isEmpty(text)) {
            valueText.setText(text.replace("-", " at "));
        }
    }

    /*public void setVoiceQuery(boolean voice) {
        if (voice) {
            textView.setCompoundDrawablesWithIntrinsicBounds(Theme.getIcon(R.drawable.ic_mic_mini, ContextCompat.getColor(getContext(), Theme.Color.primaryText())), null, null, null);
        }
    }*/

    public void setDivider(boolean divider) {
        this.divider = divider;
        setWillNotDraw(!divider);
    }

    public void setOnIconClickListener(OnClickListener listener) {
        endIconView.setOnClickListener(listener);
    }

    public void changeLayoutParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (AndroidExtensions.isLandscape()) {
            params.leftMargin = ScreenUtils.dp(56);
            params.rightMargin = ScreenUtils.dp(56);
        }
        setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
        int height = ScreenUtils.dp(48) + (divider ? 1 : 0);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (divider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }

    /*@Override
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
    }*/

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getBackground() != null && endIconView != null) {
            endIconView.getHitRect(rect);
            if (rect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }*/
}