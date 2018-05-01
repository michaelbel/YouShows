package org.michaelbel.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ScreenUtils;
import org.michaelbel.shows.R;

/**
 * Date: 25 MAR 2018
 * Time: 18:43 MSK
 *
 * @author Michael Bel
 */

public class BackdropView extends FrameLayout {

    private TextView textView;
    private ImageView backdropImage;
    private TextView followHintTextView;

    private Runnable followHintHideRunnable;
    private AnimatorSet followHintAnimation;

    public BackdropView(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    public BackdropView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(@NonNull Context context) {
        backdropImage = new ImageView(context);
        backdropImage.setScaleType(ImageView.ScaleType.FIT_XY);
        backdropImage.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        addView(backdropImage);

//------Show Status Label---------------------------------------------------------------------------

        CardView cardView = new CardView(context);
        cardView.setCardElevation(0);
        cardView.setUseCompatPadding(false);
        cardView.setPreventCornerOverlap(false);
        cardView.setCardBackgroundColor(0x80000000);
        cardView.setRadius(ScreenUtils.dp(5));
        cardView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.BOTTOM, 6, 0, 16 + 16 + 56, 4));
        addView(cardView);

        textView = new TextView(context);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine();
        textView.setText(R.string.Loading);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        textView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        textView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 5, 2.5F, 5, 2.5F));
        cardView.addView(textView);
    }

    public void setImage(String path) {
        Picasso.with(getContext())
               .load(path)
               .into(backdropImage);
    }

    public void setLabel(String text) {
        textView.setText(text);
    }

    public void showFollowHint(boolean hide, boolean follow) {
        if (hide && followHintTextView == null) {
            return;
        }
        if (followHintTextView == null) {
            followHintTextView = new TextView(getContext());
            followHintTextView.setAlpha(0.0F);
            followHintTextView.setGravity(Gravity.CENTER_VERTICAL);
            followHintTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            followHintTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryText));
            followHintTextView.setBackgroundDrawable(createRoundRectDrawable(ScreenUtils.dp(3), 0xCC111111));
            followHintTextView.setPadding(ScreenUtils.dp(8), ScreenUtils.dp(7), ScreenUtils.dp(8), ScreenUtils.dp(7));
            addView(followHintTextView, 1, LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.BOTTOM, 5, 0, 5, 5 + 28));
        }
        if (hide) {
            if (followHintAnimation != null) {
                followHintAnimation.cancel();
                followHintAnimation = null;
            }
            AndroidExtensions.cancelRunOnUIThread(followHintHideRunnable);
            followHintHideRunnable = null;
            hideFollowHint();
            return;
        }

        followHintTextView.setText(follow ? R.string.TapToFollow : R.string.TapToUnFollow);

        if (followHintHideRunnable != null) {
            if (followHintAnimation != null) {
                followHintAnimation.cancel();
                followHintAnimation = null;
            } else {
                AndroidExtensions.cancelRunOnUIThread(followHintHideRunnable);
                AndroidExtensions.runOnUIThread(followHintHideRunnable = this :: hideFollowHint, 2000);
                return;
            }
        } else if (followHintAnimation != null) {
            return;
        }

        followHintTextView.setVisibility(View.VISIBLE);
        followHintAnimation = new AnimatorSet();
        followHintAnimation.playTogether(ObjectAnimator.ofFloat(followHintTextView, "alpha", 1.0f));
        followHintAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(followHintAnimation)) {
                    followHintAnimation = null;
                    AndroidExtensions.runOnUIThread(followHintHideRunnable = () -> hideFollowHint(), 2000);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (animation.equals(followHintAnimation)) {
                    followHintAnimation = null;
                }
            }
        });
        followHintAnimation.setDuration(300);
        followHintAnimation.start();
    }

    private void hideFollowHint() {
        followHintAnimation = new AnimatorSet();
        followHintAnimation.playTogether(
                ObjectAnimator.ofFloat(followHintTextView, "alpha", 0.0F)
        );
        followHintAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation.equals(followHintAnimation)) {
                    followHintAnimation = null;
                    followHintHideRunnable = null;
                    if (followHintTextView != null) {
                        followHintTextView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (animation.equals(followHintAnimation)) {
                    followHintHideRunnable = null;
                    followHintHideRunnable = null;
                }
            }
        });
        followHintAnimation.setDuration(300);
        followHintAnimation.start();
    }

    public static Drawable createRoundRectDrawable(int rad, int defaultColor) {
        ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(new float[]{rad, rad, rad, rad, rad, rad, rad, rad}, null, null));
        defaultDrawable.getPaint().setColor(defaultColor);
        return defaultDrawable;
    }
}