package org.michaelbel.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ScreenUtils;
import org.michaelbel.seriespicker.R;

/**
 * Date: 25 MAR 2018
 * Time: 18:43 MSK
 *
 * @author Michael Bel
 */

public class BackdropView extends FrameLayout {

    private TextView textView;
    private ImageView backdropImage;

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

//------TextView------------------------------------------------------------------------------------

        CardView cardView = new CardView(context);
        cardView.setCardElevation(0);
        cardView.setUseCompatPadding(false);
        cardView.setPreventCornerOverlap(false);
        cardView.setRadius(ScreenUtils.dp(5));
        cardView.setCardBackgroundColor(0x80000000);
        cardView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.BOTTOM, 6, 0, 16 + 16 + 56, 4));
        addView(cardView);

        textView = new TextView(context);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine();
        textView.setText(R.string.LoadingLabel);
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
               .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
               .into(backdropImage, new Callback() {
                   @Override
                   public void onSuccess() {}

                   @Override
                   public void onError() {}
               });
    }

    public void setLabel(String text) {
        textView.setText(text);
    }
}