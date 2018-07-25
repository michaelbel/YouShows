package org.michaelbel.youshows.ui.view.cell;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.youshows.Theme;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.shows.R;

/**
 * Date: 08 MAY 2018
 * Time: 18:29 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("ViewConstructor")
public class ThemeCell extends FrameLayout {

    protected TextView textView;
    protected ImageView iconView;
    private ImageView iconCheckView;

    private int cellHeight;
    protected Paint paint;
    private boolean divider;

    public ThemeCell(Context context, int theme) {
        super(context);

        setElevation(Extensions.dp(context,1));
        setBackgroundColor(ContextCompat.getColor(context, Theme.foregroundColor()));
        setForeground(Extensions.selectableItemBackgroundBorderlessDrawable(context));

        setHeight(Extensions.dp(context, 56));

        if (paint == null) {
            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(ContextCompat.getColor(context, Theme.dividerColor()));
        }

        iconView = new ImageView(context);
        iconView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL, 16, 0, 0, 0));
        addView(iconView);

        textView = new AppCompatTextView(context);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(ContextCompat.getColor(context, Theme.primaryTextColor()));
        textView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL, 56, 0, 16, 0));
        addView(textView);

        iconCheckView = new ImageView(context);
        iconCheckView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        addView(iconCheckView);

        if (theme == Theme.THEME_LIGHT) {
            iconCheckView.setImageDrawable(AndroidExtensions.getIcon(context, R.drawable.ic_done, ContextCompat.getColor(context, R.color.iconActive)));
        } else if (theme == Theme.THEME_NIGHT) {
            iconCheckView.setImageDrawable(AndroidExtensions.getIcon(context, R.drawable.ic_done, ContextCompat.getColor(context, R.color.n_iconActive)));
        }
    }

    public void setText(@NonNull String text) {
        textView.setText(text);
    }

    public void setHeight(int height) {
        cellHeight = height;
    }

    public void setDivider(boolean divider) {
        this.divider = divider;
        setWillNotDraw(!divider);
    }

    public void setIcon(Drawable icon) {
        iconView.setImageDrawable(icon);
    }

    public void setIconChecked(boolean value) {
        iconCheckView.setVisibility(value ? VISIBLE : INVISIBLE);
    }

    public void changeTheme() {
        ArgbEvaluator evaluator = new ArgbEvaluator();

        ObjectAnimator foregroundAnimator = ObjectAnimator.ofObject(this, "backgroundColor", evaluator, 0, 0);
        ObjectAnimator textColorAnimator = ObjectAnimator.ofObject(textView, "textColor", evaluator, 0, 0);
        ObjectAnimator dividerColorAnimator = ObjectAnimator.ofObject(paint, "color", evaluator, 0, 0);

        if (Theme.getTheme() == Theme.THEME_LIGHT) {
            foregroundAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.n_foreground), ContextCompat.getColor(getContext(), R.color.foreground));
            textColorAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.n_primaryText), ContextCompat.getColor(getContext(), R.color.primaryText));
            dividerColorAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.n_divider), ContextCompat.getColor(getContext(), R.color.divider));
        } else if (Theme.getTheme() == Theme.THEME_NIGHT) {
            foregroundAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.foreground), ContextCompat.getColor(getContext(), R.color.n_foreground));
            textColorAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.primaryText), ContextCompat.getColor(getContext(), R.color.n_primaryText));
            dividerColorAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.divider), ContextCompat.getColor(getContext(), R.color.n_divider));
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator(2));
        animatorSet.playTogether(foregroundAnimator, textColorAnimator, dividerColorAnimator);
        animatorSet.setDuration(300);
        AndroidExtensions.runOnUIThread(animatorSet:: start);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        int height = cellHeight + (divider ? 1 : 0);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (divider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }
}