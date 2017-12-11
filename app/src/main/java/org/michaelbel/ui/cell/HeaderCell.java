package org.michaelbel.ui.cell;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.michaelbel.seriespicker.LayoutHelper;
import org.michaelbel.seriespicker.R;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.util.ScreenUtils;

public class HeaderCell extends FrameLayout {

    private TextView textView;

    public HeaderCell(Context context) {
        super(context);

        setElevation(ScreenUtils.dp(1));
        setBackgroundColor(ContextCompat.getColor(context, Theme.foregroundColor()));

        textView = new TextView(context);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5F);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        textView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        textView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT,
                LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.BOTTOM, 16, 0, 16, 2));
        addView(textView);
    }

    public void setText(@NonNull String text) {
        textView.setText(text);
    }

    public void changeLayoutParams() {
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (ScreenUtils.isLandscape()) {
            params.leftMargin = ScreenUtils.dp(56);
            params.rightMargin = ScreenUtils.dp(56);
        }

        setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
        int height = ScreenUtils.dp(36);

        setMeasuredDimension(width, height);
    }
}