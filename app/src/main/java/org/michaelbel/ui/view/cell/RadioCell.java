package org.michaelbel.ui.view.cell;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.michaelbel.seriespicker.LayoutHelper;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.util.ScreenUtils;

public class RadioCell extends FrameLayout {

    private TextView textView;
    private AppCompatRadioButton radioButton;

    private float mHeight;
    private boolean divider;
    private Paint paint;
    private Rect rect = new Rect();

    public RadioCell(Context context) {
        super(context);

        mHeight = ScreenUtils.dp(52);

        if (paint == null) {
            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(ContextCompat.getColor(context, Theme.dividerColor()));
        }

        setElevation(ScreenUtils.dp(1));
        setForeground(Theme.selectableItemBackgroundDrawable());
        setBackgroundColor(ContextCompat.getColor(context, Theme.foregroundColor()));

        textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setTextColor(ContextCompat.getColor(context, Theme.primaryTextColor()));
        textView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL, 16, 0, 0, 0));
        addView(textView);

        radioButton = new AppCompatRadioButton(context);
        radioButton.setFocusable(false);
        radioButton.setClickable(false);
        radioButton.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, Theme.secondaryTextColor())));
        radioButton.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        addView(radioButton);
    }

    public RadioCell setText(@NonNull String text) {
        textView.setText(text);
        return this;
    }

    public RadioCell setText(@StringRes int stringId) {
        textView.setText(getContext().getText(stringId));
        return this;
    }

    public void setChecked(boolean value) {
        radioButton.setChecked(value);
    }

    public RadioCell setDivider(boolean divider) {
        this.divider = divider;
        setWillNotDraw(!divider);
        return this;
    }

    public RadioCell setHeight(float height) {
        mHeight = height;
        return this;
    }

    public void changeTheme() {
        setBackgroundColor(ContextCompat.getColor(getContext(), Theme.foregroundColor()));
        textView.setTextColor(ContextCompat.getColor(getContext(), Theme.primaryTextColor()));
        radioButton.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), Theme.secondaryTextColor())));
        paint.setColor(ContextCompat.getColor(getContext(), Theme.dividerColor()));
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        int height = (int) mHeight + (divider ? 1 : 0);
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