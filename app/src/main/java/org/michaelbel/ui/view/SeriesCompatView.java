package org.michaelbel.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.michaelbel.seriespicker.LayoutHelper;
import org.michaelbel.seriespicker.R;
import org.michaelbel.seriespicker.Theme;
import org.michaelbel.util.ScreenUtils;
import org.michaelbel.util.ThemeUtils;

public class SeriesCompatView extends FrameLayout {

    private CardView cardView;
    private ImageView backdropImageView;
    private TextView titleTextView;
    private TextView seasonsCountTextView;
    private TextView episodeCountTextView;
    /*private ImageView menuImageView;*/

    private int seasonsCounter;
    private int episodesCounter;
    private Rect rect = new Rect();

    public SeriesCompatView(Context context) {
        super(context);

        cardView = new CardView(context);
        cardView.setRadius(ScreenUtils.dp(4));
        cardView.setUseCompatPadding(true);
        cardView.setPreventCornerOverlap(false);
        cardView.setForeground(ThemeUtils.selectableItemBackgroundBorderlessDrawable());
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, Theme.foregroundColor()));
        cardView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT));
        addView(cardView);

        backdropImageView = new ImageView(context);
        backdropImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        backdropImageView.setImageResource(R.drawable.place_holder);
        backdropImageView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, 200, Gravity.TOP));
        cardView.addView(backdropImageView);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setBackgroundColor(0x8A000000);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.MATCH_PARENT, 32 + 32, Gravity.BOTTOM,
                0, 0, 0, 0));
        cardView.addView(linearLayout);

        LinearLayout linearLayout1 = new LinearLayout(context);
        linearLayout1.setOrientation(LinearLayout.VERTICAL);
        linearLayout1.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, 32));
        linearLayout.addView(linearLayout1);

        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout2.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, 32));
        linearLayout.addView(linearLayout2);

        titleTextView = new TextView(context);
        titleTextView.setLines(1);
        titleTextView.setMaxLines(1);
        titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        titleTextView.setTextColor(ContextCompat.getColor(context, R.color.md_white));
        titleTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        titleTextView.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                Gravity.START | Gravity.CENTER_VERTICAL, 12, 3, 12, 0));
        linearLayout1.addView(titleTextView);

        seasonsCountTextView = new TextView(context);
        seasonsCountTextView.setLines(1);
        seasonsCountTextView.setMaxLines(1);
        seasonsCountTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        seasonsCountTextView.setTextColor(ContextCompat.getColor(context, R.color.md_white));
        seasonsCountTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        seasonsCountTextView.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.START | Gravity.CENTER_VERTICAL, 12, 0, 0, 2));
        linearLayout2.addView(seasonsCountTextView);

        episodeCountTextView = new TextView(context);
        episodeCountTextView.setLines(1);
        episodeCountTextView.setMaxLines(1);
        episodeCountTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        episodeCountTextView.setTextColor(ContextCompat.getColor(context, R.color.md_white));
        episodeCountTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        episodeCountTextView.setLayoutParams(LayoutHelper.makeLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.START | Gravity.CENTER_VERTICAL, 12, 0, 12, 2));
        linearLayout2.addView(episodeCountTextView);

        /*menuImageView = new ImageView(context);
        menuImageView.setFocusable(false);
        menuImageView.setScaleType(ImageView.ScaleType.CENTER);
        menuImageView.setImageResource(R.drawable.ic_dots_menu);
        menuImageView.setBackground(ThemeUtils.selectableItemBackgroundDrawable());
        menuImageView.setLayoutParams(LayoutHelper.makeFrame(40, 40, Gravity.END | Gravity.TOP));
        cardView.addView(menuImageView);*/
    }

    public void setBackdrop(@NonNull String path) {
        if (path == null) {
            backdropImageView.setImageResource(R.drawable.place_holder);
        } else {
            SharedPreferences prefs = getContext().getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            String imageQuality = prefs.getString("image_quality_backdrop", "w780");

            Glide.with(getContext())
                 .load("http://image.tmdb.org/t/p/" + imageQuality +"/" + path)
                 .into(backdropImageView);
        }
    }

    public void setTitle(@NonNull String text) {
        titleTextView.setText(text);
    }

    public void setSeasons(int count) {
        seasonsCounter = count;

        SharedPreferences prefs = getContext().getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean style = prefs.getBoolean("short_names", false);

        if (style) {
            if (seasonsCounter <= 9) {
                seasonsCountTextView.setText("S0" + seasonsCounter);
            } else {
                seasonsCountTextView.setText("S" + seasonsCounter);
            }
        } else {
            if (seasonsCounter == 1) {
                seasonsCountTextView.setText(getContext().getString(R.string.SeasonCount, seasonsCounter));
            } else {
                if (episodesCounter <= 0) {
                    seasonsCountTextView.setText(getContext().getString(R.string.SeasonsCount, seasonsCounter));
                } else {
                    seasonsCountTextView.setText(getContext().getString(R.string.SeasonCount, seasonsCounter));
                }
            }
        }
    }

    public void setEpisodes(int count) {
        episodesCounter = count;

        SharedPreferences prefs = getContext().getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean style = prefs.getBoolean("short_names", false);

        if (style) {
            if (count <= 0) {
                episodeCountTextView.setText("");
            } else {
                if (episodesCounter <= 9) {
                    episodeCountTextView.setText("E0" + episodesCounter);
                } else {
                    episodeCountTextView.setText("E" + episodesCounter);
                }
            }
        } else {
            if (count <= 0) {
                episodeCountTextView.setText("");
                if (seasonsCounter == 1) {
                    seasonsCountTextView.setText(getContext().getString(R.string.SeasonCount, seasonsCounter));
                } else {
                    seasonsCountTextView.setText(getContext().getString(R.string.SeasonsCount, seasonsCounter));
                }
            } else if (count == 1) {
                episodeCountTextView.setText(getContext().getString(R.string.EpisodeCount, count));
                seasonsCountTextView.setText(getContext().getString(R.string.SeasonCount, seasonsCounter));
            } else {
                episodeCountTextView.setText(getContext().getString(R.string.EpisodesCount, count));
                seasonsCountTextView.setText(getContext().getString(R.string.SeasonCount, seasonsCounter));
            }
        }
    }

    /*public void setOnMenuClick(OnClickListener listener) {
        menuImageView.setOnClickListener(listener);
    }*/

    /*public void hideMenu() {
        menuImageView.setVisibility(INVISIBLE);
    }*/

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

        /*if (Build.VERSION.SDK_INT >= 21 && getBackground() != null) {
            menuImageView.getHitRect(rect);
            if (rect.contains((int) event.getX(), (int) event.getY())) {
                return true;
            }
        }*/

        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        setMeasuredDimension(width, height);
    }
}