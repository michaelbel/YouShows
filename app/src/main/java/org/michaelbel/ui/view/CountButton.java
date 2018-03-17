package org.michaelbel.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.michaelbel.material.util2.Utils;
import org.michaelbel.seriespicker.LayoutHelper;
import org.michaelbel.seriespicker.R;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.util.ScreenUtils;

public class CountButton extends FrameLayout {

    private ImageView numberImage;
    private Rect rect = new Rect();

    public CountButton(@NonNull Context context) {
        super(context);

        setForeground(Theme.selectableItemBackgroundDrawable());

        numberImage = new ImageView(context);
        numberImage.setScaleType(ImageView.ScaleType.CENTER);
        numberImage.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        addView(numberImage);
    }

    public void setImagePlus() {
        Utils.bind(getContext());
        numberImage.setImageDrawable(
                Utils.getIcon(R.drawable.ic_exposure_plus_1,
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark))
        );
    }

    public void setImageMinus() {
        Utils.bind(getContext());
        numberImage.setImageDrawable(
                Utils.getIcon(R.drawable.ic_exposure_neg_1,
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark))
        );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = ScreenUtils.dp(84);
        int height = ScreenUtils.dp(68);

        setMeasuredDimension(width, height);
    }

    @SuppressLint("ClickableViewAccessibility")
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