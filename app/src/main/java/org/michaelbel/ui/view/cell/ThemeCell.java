package org.michaelbel.ui.view.cell;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.ImageView;

import org.michaelbel.app.Theme;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ScreenUtils;
import org.michaelbel.shows.R;

/**
 * Date: 08 MAY 2018
 * Time: 18:29 MSK
 *
 * @author Michael Bel
 */

public class ThemeCell extends TextCell {

    private ImageView iconCheckView;

    public ThemeCell(Context context) {
        super(context);

        setHeight(ScreenUtils.dp(52));

        iconCheckView = new ImageView(context);
        iconCheckView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        addView(iconCheckView);
    }

    public ThemeCell(Context context, int theme) {
        super(context);

        setHeight(ScreenUtils.dp(52));

        iconCheckView = new ImageView(context);
        iconCheckView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        addView(iconCheckView);

        if (theme == Theme.THEME_NIGHT_BLUE) {
            iconCheckView.setImageDrawable(Theme.getIcon(R.drawable.ic_done, ContextCompat.getColor(context, R.color.nb_iconActive)));
        } else if (theme == Theme.THEME_NIGHT_BLACK) {
            iconCheckView.setImageDrawable(Theme.getIcon(R.drawable.ic_done, ContextCompat.getColor(context, R.color.iconActive)));
        }
    }

    public ThemeCell setIcon(boolean value) {
        iconCheckView.setImageDrawable(value ? Theme.getIcon(R.drawable.ic_done, ContextCompat.getColor(getContext(), R.color.iconActive)) : null);
        return this;
    }

    public void setIconChecked(boolean value) {
        iconCheckView.setVisibility(value ? VISIBLE : INVISIBLE);
    }

    public void changeTheme() {
        ArgbEvaluator evaluator = new ArgbEvaluator();

        ObjectAnimator foregroundAnimator = ObjectAnimator.ofObject(this, "backgroundColor", evaluator, 0, 0);
        ObjectAnimator textColorAnimator = ObjectAnimator.ofObject(textView, "textColor", evaluator, 0, 0);
        ObjectAnimator dividerColorAnimator = ObjectAnimator.ofObject(paint, "color", evaluator, 0, 0);

        if (Theme.getTheme() == Theme.THEME_NIGHT_BLACK) {
            foregroundAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.nb_foreground), ContextCompat.getColor(getContext(), R.color.foreground));
            textColorAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.nb_primaryText), ContextCompat.getColor(getContext(), R.color.primaryText));
            dividerColorAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.nb_divider), ContextCompat.getColor(getContext(), R.color.divider));
        } else if (Theme.getTheme() == Theme.THEME_NIGHT_BLUE) {
            foregroundAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.foreground), ContextCompat.getColor(getContext(), R.color.nb_foreground));
            textColorAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.primaryText), ContextCompat.getColor(getContext(), R.color.nb_primaryText));
            dividerColorAnimator.setObjectValues(ContextCompat.getColor(getContext(), R.color.divider), ContextCompat.getColor(getContext(), R.color.nb_divider));
        }

        AnimatorSet set = new AnimatorSet();
        set.playTogether(foregroundAnimator, textColorAnimator, dividerColorAnimator);
        set.setDuration(300);
        set.start();
    }
}