package org.michaelbel.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.michaelbel.old.LayoutHelper;
import org.michaelbel.seriespicker.R;
import org.michaelbel.old.ScreenUtils;

/**
 * Date: 21 MAR 2018
 * Time: 20:09 MSK
 *
 * @author Michael Bel
 */

public class FollowButton extends FrameLayout {

    private CardView cardView;
    private TextView textView;

    private Rect rect = new Rect();

    public FollowButton(@NonNull Context context) {
        super(context);

        cardView = new CardView(context);
        cardView.setCardElevation(0);
        cardView.setUseCompatPadding(false);
        cardView.setPreventCornerOverlap(false);
        cardView.setRadius(ScreenUtils.dp(6));
        //cardView.setForeground(Extensions.selectableItemBackgroundBorderlessDrawable(context));
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.accent));
        cardView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        addView(cardView);

        CardView layout = new CardView(context);
        layout.setCardElevation(0);
        layout.setUseCompatPadding(false);
        layout.setPreventCornerOverlap(false);
        layout.setRadius(ScreenUtils.dp(6));
        layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.foreground));
        layout.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 2, 2, 2, 2));
        cardView.addView(layout);

        textView = new TextView(context);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setText("Follow".toUpperCase());
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(ContextCompat.getColor(context, R.color.accent));
        textView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        textView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 10, 3, 10, 3));
        layout.addView(textView);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (cardView.getForeground() != null) {
            if (rect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                cardView.getForeground().setHotspot(event.getX(), event.getY());
            }
        }

        return super.onTouchEvent(event);
    }
}
