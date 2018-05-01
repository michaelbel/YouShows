package org.michaelbel.ui.view;

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
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ScreenUtils;
import org.michaelbel.old.Theme;
import org.michaelbel.shows.R;

/**
 * Date: 06 APR 2018
 * Time: 22:19 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("ClickableViewAccessibility")
public class EpisodeView extends FrameLayout {

    protected TextView nameText;
    protected TextView valueText;
    protected TextView airDateText;
    protected CheckBox checkBox;
    protected ImageView selectIcon;

    private Paint paint;
    private boolean divider;
    private Rect rect = new Rect();

    public EpisodeView(Context context) {
        super(context);

        setElevation(ScreenUtils.dp(1));
        setForeground(Theme.selectableItemBackgroundDrawable());
        setBackgroundColor(ContextCompat.getColor(context, R.color.foreground));

        if (paint == null) {
            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(ContextCompat.getColor(context, R.color.divider));
        }

        nameText = new TextView(context);
        nameText.setLines(1);
        nameText.setMaxLines(1);
        nameText.setSingleLine();
        nameText.setEllipsize(TextUtils.TruncateAt.END);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        nameText.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        nameText.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 16, 10, 56, 0));
        addView(nameText);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 16, 35, 16, 0));
        addView(layout);

        valueText = new TextView(context);
        valueText.setLines(1);
        valueText.setMaxLines(1);
        valueText.setSingleLine();
        valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        valueText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        valueText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
        layout.addView(valueText);

        View dotDivider = new View(context);
        dotDivider.setBackgroundResource(R.drawable.dot_divider);
        dotDivider.setLayoutParams(LayoutHelper.makeLinear(4, 4, Gravity.CENTER_VERTICAL, 6, 1, 6, 0));
        layout.addView(dotDivider);

        airDateText = new TextView(context);
        airDateText.setLines(1);
        airDateText.setMaxLines(1);
        airDateText.setSingleLine();
        airDateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        airDateText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        airDateText.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
        layout.addView(airDateText);

        selectIcon = new ImageView(context);
        selectIcon.setVisibility(VISIBLE);
        selectIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_check_outline, ContextCompat.getColor(context, R.color.green)));
        selectIcon.setLayoutParams(LayoutHelper.makeFrame(29, 29, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 14, 0));
        addView(selectIcon);

        checkBox = new CheckBox(context, R.drawable.round_check2);
        checkBox.setVisibility(VISIBLE);
        checkBox.setColor(ContextCompat.getColor(context, R.color.green), ContextCompat.getColor(context, R.color.foreground));
        checkBox.setLayoutParams(LayoutHelper.makeFrame(24, 24, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        addView(checkBox);
    }

    public void setName(@NonNull String text) {
        nameText.setText(text);
    }

    public void setNumber(@NonNull String text) {
        valueText.setText(text);
    }

    public void setDate(@NonNull String text) {
        airDateText.setText(text);
    }

    public void setChecked(boolean value, boolean animated) {
        checkBox.setChecked(value, animated);
    }

    public void setDivider(boolean divider) {
        this.divider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
        int height = ScreenUtils.dp(64) + (divider ? 1 : 0);
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