package org.michaelbel.youshows.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.youshows.Theme;
import org.michaelbel.youshows.rest.ApiFactory;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.shows.R;

import java.util.Locale;

/**
 * Date: 03 JUN 2018
 * Time: 17:40 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("ClickableViewAccessibility")
public class EpisodePeekView extends FrameLayout {

    private TextView nameText;
    private TextView overviewText;
    private ImageView stillImage;
    private ProgressBar progressBar;
    private TextView numberText;
    private TextView airDateText;
    private TextView emptyImageText;

    private ImageView noIconImage;
    private ImageView yesIconImage;

    public FrameLayout noButtonLayout;
    public FrameLayout yesButtonLayout;

    public EpisodePeekView(Context context) {
        super(context);

        CardView cardView = new CardView(context);
        cardView.setUseCompatPadding(false);
        cardView.setPreventCornerOverlap(false);
        cardView.setRadius(Extensions.dp(context,4));
        cardView.setCardElevation(Extensions.dp(context, 4));
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, Theme.backgroundColor()));
        cardView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        addView(cardView);

//------ContentLayout-------------------------------------------------------------------------------

        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, /*48*/0));
        cardView.addView(contentLayout);

//------EpisodePosterLayout-------------------------------------------------------------------------

        FrameLayout posterLayout = new FrameLayout(context);
        posterLayout.setLayoutParams(LayoutHelper.makeLinear(context, LayoutHelper.MATCH_PARENT, 165));
        contentLayout.addView(posterLayout);

        stillImage = new ImageView(context);
        stillImage.setVisibility(GONE);
        stillImage.setScaleType(ImageView.ScaleType.FIT_XY);
        stillImage.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        posterLayout.addView(stillImage);

        progressBar = new ProgressBar(context);
        progressBar.setVisibility(VISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, Theme.accentColor()), PorterDuff.Mode.MULTIPLY);
        progressBar.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        posterLayout.addView(progressBar);

        emptyImageText = new TextView(context);
        emptyImageText.setLines(1);
        emptyImageText.setMaxLines(1);
        emptyImageText.setSingleLine();
        emptyImageText.setVisibility(GONE);
        emptyImageText.setText(R.string.NoStillImage);
        emptyImageText.setEllipsize(TextUtils.TruncateAt.END);
        emptyImageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        emptyImageText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        emptyImageText.setTextColor(ContextCompat.getColor(context, Theme.secondaryTextColor()));
        emptyImageText.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        posterLayout.addView(emptyImageText);

//------Name----------------------------------------------------------------------------------------

        nameText = new TextView(context);
        nameText.setLines(1);
        nameText.setMaxLines(2);
        nameText.setEllipsize(TextUtils.TruncateAt.END);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        nameText.setTextColor(ContextCompat.getColor(context, Theme.dialogButtonText()));
        nameText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        nameText.setLayoutParams(LayoutHelper.makeLinear(context, LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 8, 8, 8, 0));
        contentLayout.addView(nameText);

//------Number and AirDate--------------------------------------------------------------------------

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(LayoutHelper.makeLinear(context, LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 8, 2, 8, 0));
        contentLayout.addView(layout);

        numberText = new TextView(context);
        numberText.setLines(1);
        numberText.setMaxLines(1);
        numberText.setSingleLine();
        numberText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        numberText.setTextColor(ContextCompat.getColor(context, Theme.primaryTextColor()));
        numberText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        numberText.setLayoutParams(LayoutHelper.makeLinear(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
        layout.addView(numberText);

        View dotDivider = new View(context);
        dotDivider.setBackground(AndroidExtensions.getIcon(context, R.drawable.dot_divider, ContextCompat.getColor(context, Theme.primaryTextColor())));
        dotDivider.setLayoutParams(LayoutHelper.makeLinear(context, 4, 4, Gravity.CENTER_VERTICAL, 6, 1, 6, 0));
        layout.addView(dotDivider);

        airDateText = new TextView(context);
        airDateText.setLines(1);
        airDateText.setMaxLines(1);
        airDateText.setSingleLine();
        airDateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        airDateText.setTextColor(ContextCompat.getColor(context, Theme.primaryTextColor()));
        airDateText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        airDateText.setLayoutParams(LayoutHelper.makeLinear(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
        layout.addView(airDateText);

//------Overview------------------------------------------------------------------------------------

        overviewText = new TextView(context);
        overviewText.setEllipsize(TextUtils.TruncateAt.END);
        overviewText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        overviewText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        overviewText.setTextColor(ContextCompat.getColor(context, Theme.secondaryTextColor()));
        overviewText.setLayoutParams(LayoutHelper.makeLinear(context, LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 8, 2, 8, 12));
        contentLayout.addView(overviewText);

//------ActionButtonsLayout-------------------------------------------------------------------------

        LinearLayout buttonsLayout = new LinearLayout(context);
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonsLayout.setElevation(Extensions.dp(context, 2));
        buttonsLayout.setBackgroundColor(ContextCompat.getColor(context, Theme.foregroundColor()));
        buttonsLayout.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.MATCH_PARENT, 48, Gravity.BOTTOM));
        //cardView.addView(buttonsLayout);

        // No button

        noButtonLayout = new FrameLayout(context);
        noButtonLayout.setLayoutParams(LayoutHelper.makeLinear(context, LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 1.0F));
        buttonsLayout.addView(noButtonLayout);

        noIconImage = new ImageView(context);
        noIconImage.setImageDrawable(AndroidExtensions.getIcon(context, R.drawable.ic_clear, ContextCompat.getColor(context, R.color.red)));
        noIconImage.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        noButtonLayout.addView(noIconImage);

        // Yes button

        yesButtonLayout = new FrameLayout(context);
        yesButtonLayout.setLayoutParams(LayoutHelper.makeLinear(context, LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 1.0F));
        buttonsLayout.addView(yesButtonLayout);

        yesIconImage = new ImageView(context);
        yesIconImage.setImageDrawable(AndroidExtensions.getIcon(context, R.drawable.ic_done, ContextCompat.getColor(context, R.color.green)));
        yesIconImage.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
        yesButtonLayout.addView(yesIconImage);
    }

    public void setStillImage(String path) {
        progressBar.setVisibility(VISIBLE);
        emptyImageText.setVisibility(GONE);

        Picasso.with(getContext())
               .load(String.format(Locale.US, ApiFactory.TMDB_IMAGE, "original", path))
               .into(stillImage, new Callback() {
                   @Override
                   public void onSuccess() {
                       emptyImageText.setVisibility(GONE);
                       stillImage.setVisibility(VISIBLE);
                       progressBar.setVisibility(GONE);
                   }

                   @Override
                   public void onError() {
                       emptyImageText.setVisibility(VISIBLE);
                       progressBar.setVisibility(GONE);
                   }
               });
    }

    public void setTitle(String title) {
        if (title != null) {
            nameText.setText(title);
        }
    }

    public void setNumber(int episode) {
        numberText.setText(getContext().getString(R.string.EpisodeNumber, episode));
    }

    public void setAirDate(String date) {
        airDateText.setText(date);
    }

    public void setOverview(String overview) {
        if (TextUtils.isEmpty(overview)) {
            overviewText.setText(getResources().getString(R.string.NoOverview));
        } else {
            overviewText.setText(overview);
        }
    }
}