package org.michaelbel.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;

import org.michaelbel.seriespicker.Theme;
import org.michaelbel.util.ScreenUtils;

public class LayoutContainer extends FrameLayout {

    private float mHeight;
    private Paint paint;
    private boolean divider;

    public LayoutContainer(@NonNull Context context) {
        super(context);

        mHeight = ScreenUtils.dp(52);
        setElevation(ScreenUtils.dp(1));
        setBackgroundColor(ContextCompat.getColor(context, Theme.foregroundColor()));

        if (paint == null) {
            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(ContextCompat.getColor(context, Theme.dividerColor()));
        }
    }

    public void setDivider(boolean divider) {
        this.divider = divider;
        setWillNotDraw(!divider);
    }

    public void setHeight(float height) {
        mHeight = height;
    }

    public void updateDividerColor() {
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
}