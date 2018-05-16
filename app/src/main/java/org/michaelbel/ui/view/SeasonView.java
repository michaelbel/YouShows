package org.michaelbel.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.app.Theme;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.old.LayoutHelper;
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
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, Theme.Color.foreground()));
        if (Theme.getTheme() == Theme.THEME_NIGHT_BLUE) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, Theme.Color.background()));
        }

        selectIcon = view.findViewById(R.id.select_icon);

        nameText = view.findViewById(R.id.name_text);
        nameText.setLines(1);
        nameText.setMaxLines(1);
        nameText.setSingleLine();
        nameText.setEllipsize(TextUtils.TruncateAt.END);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        nameText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        nameText.setTextColor(ContextCompat.getColor(context, Theme.Color.primaryText()));

        airDateText = view.findViewById(R.id.date_text);
        airDateText.setLines(1);
        airDateText.setMaxLines(1);
        airDateText.setSingleLine();
        airDateText.setEllipsize(TextUtils.TruncateAt.END);
        airDateText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        airDateText.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        airDateText.setTextColor(ContextCompat.getColor(context, Theme.Color.secondaryText()));

        episodesText = view.findViewById(R.id.episodes_text);
        episodesText.setLines(1);
        episodesText.setMaxLines(1);
        episodesText.setSingleLine();
        episodesText.setGravity(Gravity.CENTER_VERTICAL);
        episodesText.setEllipsize(TextUtils.TruncateAt.END);
        episodesText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        episodesText.setTextColor(ContextCompat.getColor(context, Theme.Color.primaryText()));
        episodesText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        episodesText.setCompoundDrawablePadding(Extensions.dp(context, 4));
        episodesText.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_chevron_right), null);
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
        episodesText.setText(String.format(Locale.US, "%d/%d", watched, all));
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