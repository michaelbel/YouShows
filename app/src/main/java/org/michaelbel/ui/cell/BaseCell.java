package org.michaelbel.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;

import org.michaelbel.material.util.Utils;
import org.michaelbel.seriespicker.Theme;

public class BaseCell extends FrameLayout {

    private boolean divider;
    private int height = 48;
    private Paint paint;

    public BaseCell(Context context) {
        super(context);

        if (paint == null) {
            paint = new Paint();
            paint.setColor(ContextCompat.getColor(context, Theme.dividerColor()));
        }
    }

    public BaseCell setDivider(boolean d) {
        divider = d;
        return this;
    }

    public BaseCell setHeight(int h) {
        height = h;
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(Utils.dp(getContext(), height + (divider ? 1 : 0)), MeasureSpec.EXACTLY)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (divider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(),
                    getHeight() - 1, paint);
        }
    }
}