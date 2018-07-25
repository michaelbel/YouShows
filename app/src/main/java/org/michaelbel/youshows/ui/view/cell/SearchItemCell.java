package org.michaelbel.youshows.ui.view.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.youshows.Theme;
import org.michaelbel.material.extensions.Extensions;
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

    public SearchItemCell(Context context) {
        super(context);

        setElevation(Extensions.dp(context,1));
        setBackgroundColor(ContextCompat.getColor(context, Theme.foregroundColor()));

        if (paint == null) {
            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(ContextCompat.getColor(context, Theme.dividerColor()));
        }

        textView = new TextView(context);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(ContextCompat.getColor(context, Theme.primaryTextColor()));
        textView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 16, 5, 16, 0));
        addView(textView);

        valueText = new TextView(context);
        valueText.setLines(1);
        valueText.setMaxLines(1);
        valueText.setSingleLine();
        valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.5F);
        valueText.setTextColor(ContextCompat.getColor(context, Theme.secondaryTextColor()));
        valueText.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 16, 27, 16, 0));
        addView(valueText);

        endIconView = new ImageView(context);
        endIconView.setFocusable(false);
        endIconView.setScaleType(ImageView.ScaleType.CENTER);
        endIconView.setBackground(AndroidExtensions.selectableItemBackgroundBorderlessDrawable(context));
        endIconView.setImageDrawable(AndroidExtensions.getIcon(context, R.drawable.ic_close_circle, ContextCompat.getColor(context, Theme.iconActiveColor())));
        endIconView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
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
            textView.setCompoundDrawablesWithIntrinsicBounds(Theme.getIcon(R.drawable.ic_mic_mini, ContextCompat.getColor(getContext(), Theme.Color.primaryTextColor())), null, null, null);
        }
    }*/

    public void setDivider(boolean divider) {
        this.divider = divider;
        setWillNotDraw(!divider);
    }

    /*public void setOnIconClickListener(OnClickListener listener) {
        endIconView.setOnClickListener(listener);
    }*/

    public void changeLayoutParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (AndroidExtensions.isLandscape()) {
            params.leftMargin = Extensions.dp(getContext(),56);
            params.rightMargin = Extensions.dp(getContext(),56);
        }
        setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
        int height = Extensions.dp(getContext(),48) + (divider ? 1 : 0);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (divider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}