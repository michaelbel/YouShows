package org.michaelbel.youshows.ui.view;

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

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.youshows.Theme;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.shows.R;

/**
 * Date: 25 MAR 2018
 * Time: 18:43 MSK
 *
 * @author Michael Bel
 */

public class BackdropView extends FrameLayout {

    public CardView labelView;

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
        setBackgroundColor(ContextCompat.getColor(getContext(), Theme.primaryColor()));

        backdropImage = new ImageView(context);
        backdropImage.setScaleType(ImageView.ScaleType.FIT_XY);
        backdropImage.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        addView(backdropImage);

        /*ImageView playIcon = new ImageView(context);
        playIcon.setScaleType(ImageView.ScaleType.FIT_XY);
        playIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_play_circle, 0x4DFFFFFF));
        playIcon.setLayoutParams(LayoutHelper.makeFrame(42, 42, Gravity.CENTER));
        addView(playIcon);*/

//------Show Status Label---------------------------------------------------------------------------

        labelView = new CardView(context);
        labelView.setCardElevation(0);
        labelView.setUseCompatPadding(false);
        labelView.setPreventCornerOverlap(false);
        labelView.setRadius(Extensions.dp(context, 5));
        labelView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.transparent50));
        labelView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.BOTTOM, 4, 0, 16 + 16 + 56, 4));
        addView(labelView);

        textView = new TextView(context);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine();
        textView.setText(R.string.Loading);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
        textView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        textView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 5, 2.5F, 5, 2.5F));
        labelView.addView(textView);
    }

    public void setImage(String path) {
        Picasso.with(getContext())
               .load("http://image.tmdb.org/t/p/original/" + path)
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
            followHintTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            followHintTextView.setBackgroundDrawable(createRoundRectDrawable(Extensions.dp(getContext(),3), 0xCC111111));
            followHintTextView.setPadding(Extensions.dp(getContext(),8), Extensions.dp(getContext(),7), Extensions.dp(getContext(),8), Extensions.dp(getContext(),7));
            addView(followHintTextView, 1, LayoutHelper.makeFrame(getContext(), LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.BOTTOM, 5, 0, 5, 5 + 28));
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
        followHintAnimation.playTogether(ObjectAnimator.ofFloat(followHintTextView, "alpha", 1));
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
            ObjectAnimator.ofFloat(followHintTextView, "alpha", 0)
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