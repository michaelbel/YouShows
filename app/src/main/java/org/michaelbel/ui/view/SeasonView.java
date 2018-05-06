package org.michaelbel.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.Theme;
import org.michaelbel.shows.R;

import java.util.Locale;

/**
 * Date: 23 MAR 2018
 * Time: 18:15 MSK
 *
 * @author Michael Bel
 */

@SuppressLint({"ClickableViewAccessibility", "InflateParams"})
public class SeasonView extends FrameLayout {

    private int allEpisodeCount;
    private int watchedEpisodeCount;

    private CardView cardView;
    private ImageView selectIcon;

    private TextView nameText;
    private TextView airDateText;

    private TextView episodesText;

    private Rect rect = new Rect();

    public SeasonView(@NonNull Context context) {
        super(context);

        View view = LayoutInflater.from(context).inflate(R.layout.item_season, null);
        view.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 12, 0, 12, 6));
        addView(view);

        cardView = view.findViewById(R.id.card_view);
        cardView.setForeground(Extensions.selectableItemBackgroundDrawable(context));

        selectIcon = view.findViewById(R.id.select_icon);

        nameText = view.findViewById(R.id.name_text);
        nameText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        airDateText = view.findViewById(R.id.date_text);
        airDateText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));

        episodesText = view.findViewById(R.id.episodes_text);
        episodesText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
    }

    public void setName(String name) {
        nameText.setText(name);
    }

    public void setAirDate(String date) {
        if (TextUtils.isEmpty(date)) {
            airDateText.setText(R.string.UnknownDate);
        } else {
            airDateText.setText(AndroidExtensions.formatDate(date));
        }
    }

    public void setSelected(boolean selected) {
        selectIcon.setImageDrawable(selected ?
            Theme.getIcon(R.drawable.ic_check_circle, ContextCompat.getColor(getContext(), R.color.green)) :
            Theme.getIcon(R.drawable.ic_check_outline, ContextCompat.getColor(getContext(), R.color.iconActive))
        );
    }

    public void setEpisodeCount(int watched, int all) {
        watchedEpisodeCount = watched;
        allEpisodeCount = all;

        episodesText.setText(String.format(Locale.US, "%d/%d", watchedEpisodeCount, allEpisodeCount));
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