package org.michaelbel.ui.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.Theme;
import org.michaelbel.app.rest.ApiFactory;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.shows.R;
import org.michaelbel.ui.view.circleprogress.CircleProgressView;
import org.michaelbel.ui.view.circleprogress.UnitPosition;

import java.util.Locale;

/**
 * Date: 06 APR 2018
 * Time: 22:28 MSK
 *
 * @author Michael Bel
 */

@SuppressLint({"ClickableViewAccessibility", "InflateParams"})
public class MyShowView extends FrameLayout {

    public static final int TYPE_SHOW = 0;
    public static final int TYPE_SEASONS = 1;
    public static final int TYPE_EPISODES = 2;

    private TextView nameText;
    private TextView datesText;
    private CardView statusCard;
    private ImageView posterImage;
    private TextView progressWatchedText;
    private CircleProgressView circleProgressView;
    private CardView cardView;
    private TextView statusText;
    private View dividerView;
    private ImageView dateIcon;
    private ImageView viewsIcon;

    private Paint paint;
    private boolean divider;
    private Rect rect = new Rect();

    public MyShowView(Context context) {
        super(context);

        setBackgroundColor(ContextCompat.getColor(context, Theme.Color.background()));

        if (paint == null) {
            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(ContextCompat.getColor(context, Theme.Color.background()));
        }

        View view = LayoutInflater.from(context).inflate(R.layout.item_myshow, null);
        view.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, 6, 0, 6, 0));
        addView(view);

        cardView = view.findViewById(R.id.card_item);
        cardView.setForeground(Extensions.selectableItemBackgroundDrawable(context));
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, Theme.Color.foreground()));

        CardView cardPoster = view.findViewById(R.id.card_poster);
        cardPoster.setCardBackgroundColor(ContextCompat.getColor(context, Theme.Color.foreground()));

        posterImage = view.findViewById(R.id.poster_image);

        nameText = view.findViewById(R.id.name_text);
        nameText.setMaxLines(2);
        nameText.setEllipsize(TextUtils.TruncateAt.END);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        nameText.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        nameText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        statusCard = view.findViewById(R.id.status_card);
        statusCard.setCardBackgroundColor(ContextCompat.getColor(context, Theme.Color.background()));

        statusText = view.findViewById(R.id.status_text);
        statusText.setLines(1);
        statusText.setMaxLines(1);
        statusText.setSingleLine();
        statusText.setText(context.getString(R.string.Finished).toUpperCase());
        statusText.setEllipsize(TextUtils.TruncateAt.END);
        statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        statusText.setTextColor(ContextCompat.getColor(context, Theme.Color.primaryText()));
        statusText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        dateIcon = view.findViewById(R.id.date_icon);
        dateIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_event, ContextCompat.getColor(context, Theme.Color.iconActive())));

        datesText = view.findViewById(R.id.dates_text);
        datesText.setLines(1);
        datesText.setMaxLines(1);
        datesText.setSingleLine();
        datesText.setEllipsize(TextUtils.TruncateAt.END);
        datesText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        datesText.setTextColor(ContextCompat.getColor(context, Theme.Color.secondaryText()));
        datesText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        viewsIcon = view.findViewById(R.id.views_icon);
        viewsIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_clipboard_check_outline, ContextCompat.getColor(context, Theme.Color.iconActive())));

        progressWatchedText = view.findViewById(R.id.watched_episodes_text);
        progressWatchedText.setLines(1);
        progressWatchedText.setMaxLines(1);
        progressWatchedText.setSingleLine();
        progressWatchedText.setEllipsize(TextUtils.TruncateAt.END);
        progressWatchedText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        progressWatchedText.setTextColor(ContextCompat.getColor(context, Theme.Color.secondaryText()));
        progressWatchedText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        dividerView = view.findViewById(R.id.divider_view);
        dividerView.setBackgroundColor(ContextCompat.getColor(context, Theme.Color.background()));

        circleProgressView = view.findViewById(R.id.progress_view);
        circleProgressView.setTextSize(Extensions.dp(context, 10));
        circleProgressView.setTextTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        circleProgressView.setTextColor(ContextCompat.getColor(context, Theme.Color.secondaryText()));
        circleProgressView.setUnitColor(ContextCompat.getColor(context, Theme.Color.secondaryText()));
        circleProgressView.setUnitTextTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        circleProgressView.setUnitVisible(true);
        circleProgressView.setUnitSize(Extensions.dp(context, 6));
        circleProgressView.setBarColor(ContextCompat.getColor(context, R.color.green), ContextCompat.getColor(context, R.color.red));
        if (Theme.getTheme() == Theme.THEME_NIGHT_BLUE) {
            circleProgressView.setRimColor(ContextCompat.getColor(context, Theme.Color.iconActive()));
        } else if (Theme.getTheme() == Theme.THEME_NIGHT_BLACK) {
            circleProgressView.setRimColor(ContextCompat.getColor(context, R.color.background));
        }
        circleProgressView.setBarWidth(Extensions.dp(context, 5.2F));
        circleProgressView.setRimWidth(Extensions.dp(context, 5));
        circleProgressView.setUnit("%");
        circleProgressView.setUnitPosition(UnitPosition.RIGHT_BOTTOM);
    }

    public void setPoster(@NonNull String posterPath) {
        Picasso.with(getContext())
               .load(String.format(Locale.US, ApiFactory.TMDB_IMAGE, "w200", posterPath))
               .into(posterImage);
    }

    public void setName(String title) {
        nameText.setText(title);
    }

    public void setDates(String airDate, String lastDate) {
        datesText.setText(lastDate == null ?
            getContext().getString(R.string.FirstAndNoLastDates, AndroidExtensions.formatDate(airDate)) :
            String.format(Locale.US, "%1$s - %2$s", AndroidExtensions.formatDate(airDate), AndroidExtensions.formatDate(lastDate)
        ));
    }

    public void setStatus(boolean status) {
        statusCard.setVisibility(status ? GONE : VISIBLE);
    }

    public void setProgressWatchedText(int choice, int count) {
        if (choice == TYPE_SEASONS) {
            progressWatchedText.setText(getResources().getQuantityString(R.plurals.Seasons, count, count));
        } else if (choice == TYPE_EPISODES) {
            if (count == 0) {
                progressWatchedText.setText(getResources().getString(R.string.NoEpisodesWatched));
            } else {
                progressWatchedText.setText(getResources().getQuantityString(R.plurals.Episodes, count, count));
            }
        } else if (choice == TYPE_SHOW) {
            progressWatchedText.setText(getResources().getString(R.string.ShowIsOverWatched));
        }
    }

    /*public void setProgressWatchedText(int choice, int count, int count2) {
        if (choice == TYPE_SEASONS) {
            SpannableStringBuilder spannable; // First number is green

            String text = getResources().getQuantityString(R.plurals.Seasons2, count, count, count2, count2); // For example: 10/20 Seasons watched
            int pos = (int) Math.ceil(Math.log10(count));

            spannable = new SpannableStringBuilder(text);
            spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.green)), 0, pos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            progressWatchedText.setText(spannable);
        } else if (choice == TYPE_EPISODES) {
            progressWatchedText.setTextColor(ContextCompat.getColor(getContext(), R.color.secondaryText));

            if (count == 0) {
                progressWatchedText.setText(getResources().getString(R.string.NoEpisodesWatched));
            } else {
                progressWatchedText.setText(getResources().getQuantityString(R.plurals.Episodes, count, count2));
            }
        } else if (choice == TYPE_SHOW) {
            progressWatchedText.setTextColor(ContextCompat.getColor(getContext(), R.color.secondaryText));
            progressWatchedText.setText(getResources().getString(R.string.ShowIsOverWatched));
        }
    }*/

    public void setProgress(int progress) {
        SharedPreferences prefs = getContext().getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean animated = prefs.getBoolean("animations", true);

        if (animated) {
            circleProgressView.setValueAnimated(progress);
        } else {
            circleProgressView.setValue(progress);
        }
    }

    public void setDivider(boolean divider) {
        this.divider = divider;
        setWillNotDraw(!divider);
    }

    public void changeTheme() {
        setBackgroundColor(ContextCompat.getColor(getContext(), Theme.Color.background()));
        cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), Theme.Color.foreground()));
        nameText.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
        statusCard.setCardBackgroundColor(ContextCompat.getColor(getContext(), Theme.Color.background()));
        statusText.setTextColor(ContextCompat.getColor(getContext(), Theme.Color.primaryText()));
        datesText.setTextColor(ContextCompat.getColor(getContext(), Theme.Color.secondaryText()));
        progressWatchedText.setTextColor(ContextCompat.getColor(getContext(), Theme.Color.secondaryText()));
        dividerView.setBackgroundColor(ContextCompat.getColor(getContext(), Theme.Color.background()));
        dateIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_event, ContextCompat.getColor(getContext(), Theme.Color.iconActive())));
        viewsIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_clipboard_check_outline, ContextCompat.getColor(getContext(), Theme.Color.iconActive())));
        paint.setColor(ContextCompat.getColor(getContext(), Theme.Color.background()));
        setWillNotDraw(true);

        circleProgressView.setTextColor(ContextCompat.getColor(getContext(), Theme.Color.secondaryText()));
        circleProgressView.setUnitColor(ContextCompat.getColor(getContext(), Theme.Color.secondaryText()));
        circleProgressView.setBarColor(ContextCompat.getColor(getContext(), R.color.green));
        if (Theme.getTheme() == Theme.THEME_NIGHT_BLUE) {
            circleProgressView.setRimColor(ContextCompat.getColor(getContext(), Theme.Color.iconActive()));
        } else if (Theme.getTheme() == Theme.THEME_NIGHT_BLACK) {
            circleProgressView.setRimColor(ContextCompat.getColor(getContext(), R.color.secondaryText));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (divider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }

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