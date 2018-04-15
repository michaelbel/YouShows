package org.michaelbel.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.Theme;
import org.michaelbel.rest.ApiFactory;
import org.michaelbel.seriespicker.R;

import java.util.Locale;

/**
 * Date: 06 APR 2018
 * Time: 22:28 MSK
 *
 * @author Michael Bel
 */

@SuppressLint({"ClickableViewAccessibility", "InflateParams"})
public class MyShowView extends FrameLayout {

    private TextView nameText;
    private TextView datesText;
    private CardView statusCard;
    private ImageView posterImage;
    private CardView posterPlaceholder;
    private TextView episodeWatchedText;
    //private CircleProgressView circleProgressView;

    private Paint paint;
    private boolean divider;
    private Rect rect = new Rect();

    public MyShowView(Context context) {
        super(context);

        setForeground(Extensions.selectableItemBackgroundDrawable(context));
        setBackgroundColor(ContextCompat.getColor(context, R.color.foreground));

        if (paint == null) {
            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(ContextCompat.getColor(context, R.color.background));
        }

        View view = LayoutInflater.from(context).inflate(R.layout.item_myshow, null);
        view.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        addView(view);

        posterImage = view.findViewById(R.id.poster_image);
        posterPlaceholder = view.findViewById(R.id.place_holder);

        nameText = view.findViewById(R.id.name_text);
        nameText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        statusCard = view.findViewById(R.id.status_card);

        TextView statusText = view.findViewById(R.id.status_text);
        statusText.setText(context.getString(R.string.Finished).toUpperCase());
        statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        statusText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        ImageView dateIcon = view.findViewById(R.id.date_icon);
        dateIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_event, ContextCompat.getColor(context, R.color.iconActive)));

        datesText = view.findViewById(R.id.dates_text);
        datesText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        ImageView viewsIcon = view.findViewById(R.id.views_icon);
        viewsIcon.setImageDrawable(Theme.getIcon(R.drawable.ic_eye, ContextCompat.getColor(context, R.color.iconActive)));

        episodeWatchedText = view.findViewById(R.id.watched_episodes_text);
        episodeWatchedText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        //circleProgressView = view.findViewById(R.id.circle_progress);
        //circleProgressView.setVisibility(GONE);
        //circleProgressView.setTextSize(14);
        //circleProgressView.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
    }

    public void setPoster(@NonNull String posterPath) {
        Picasso.with(getContext())
               .load(String.format(Locale.US, ApiFactory.TMDB_IMAGE, "original", posterPath))
               .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
               .into(posterImage, new Callback() {
                   @Override
                   public void onSuccess() {
                       posterImage.setVisibility(VISIBLE);
                       posterPlaceholder.setVisibility(INVISIBLE);
                   }

                   @Override
                   public void onError() {
                       posterImage.setVisibility(GONE);
                       posterPlaceholder.setVisibility(VISIBLE);
                   }
               });
    }

    public void setName(String title) {
        if (title != null) {
            nameText.setText(title);
        }
    }

    public void setDates(String airDate, String lastDate) {
        if (lastDate == null) {
            datesText.setText(AndroidExtensions.formatDate(airDate) + " - Present");
        } else {
            datesText.setText(AndroidExtensions.formatDate(airDate) + " - " + AndroidExtensions.formatDate(lastDate));
        }
    }

    public void setStatus(boolean status) {
        statusCard.setVisibility(status ? GONE : VISIBLE);
    }

    public void setWatchedEpisodes(int episodes) {
        episodeWatchedText.setText(episodes != 0 ? getContext().getString(R.string.EpisodesWatched, episodes) : getContext().getString(R.string.NoEpisodesWatched));
    }

    /*public void setProgress(int watchedEpisodes, int allEpisodes) {
        float percent = (watchedEpisodes * 100F) / allEpisodes;
        circleProgressView.setValue(percent);
    }*/

    public void setDivider(boolean divider) {
        this.divider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (divider) {
            canvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, paint);
        }
    }

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