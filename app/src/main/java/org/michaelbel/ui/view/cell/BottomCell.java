package org.michaelbel.ui.view.cell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.michaelbel.seriespicker.LayoutHelper;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.util.ScreenUtils;

public class BottomCell extends FrameLayout {

    private TextView textView;

    public BottomCell(Context context) {
        super(context);

        textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(ContextCompat.getColor(context, Theme.secondaryTextColor()));
        textView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 16, 0, 16, 0));
        addView(textView);
    }

    public void setText(@NonNull String text) {
        textView.setText(text.toLowerCase());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        int height = ScreenUtils.dp(32);
        setMeasuredDimension(width, height);
    }
}