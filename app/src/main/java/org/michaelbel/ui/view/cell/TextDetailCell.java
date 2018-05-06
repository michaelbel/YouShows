package org.michaelbel.ui.view.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.michaelbel.app.AndroidExtensions;
import org.michaelbel.old.LayoutHelper;
import org.michaelbel.old.ScreenUtils;
import org.michaelbel.old.Theme;
import org.michaelbel.shows.R;

/**
 * Date: 02 MAR 2018
 * Time: 00:21 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("ClickableViewAccessibility")
public class TextDetailCell extends FrameLayout {

    public static final int MODE_DEFAULT = 100;
    public static final int MODE_SWITCH = 200;
    public static final int MODE_CHECKBOX = 300;
    public static final int MODE_ICONS = 400;

    @IntDef({
        MODE_DEFAULT,
        MODE_SWITCH,
        MODE_CHECKBOX,
        MODE_ICONS
    })
    private @interface Mode {}

    protected TextView textView;
    protected TextView valueText;
    protected SwitchCompat switchCompat;
    protected AppCompatCheckBox checkBox;
    protected ImageView startIconView;
    protected ImageView endIconView;

    private Paint paint;
    private boolean divider;
    private boolean multiline;
    private Rect rect = new Rect();
    private int currentMode = MODE_DEFAULT;

    public TextDetailCell(Context context) {
        super(context);

        setElevation(ScreenUtils.dp(1));
        setForeground(Theme.selectableItemBackgroundDrawable());
        setBackgroundColor(ContextCompat.getColor(context, R.color.foreground));

        if (paint == null) {
            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(ContextCompat.getColor(context, Theme.dividerColor()));
        }

        startIconView = new ImageView(context);
        startIconView.setVisibility(INVISIBLE);
        startIconView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL, 16, 0, 0, 0));
        addView(startIconView);

        textView = new TextView(context);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        textView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 16, 10, 16, 0));
        addView(textView);

        valueText = new TextView(context);
        valueText.setLines(1);
        valueText.setMaxLines(1);
        valueText.setSingleLine();
        valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        valueText.setTextColor(ContextCompat.getColor(context, R.color.secondaryText));
        valueText.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 16, 35, 16, 0));
        addView(valueText);

        switchCompat = new SwitchCompat(context);
        switchCompat.setClickable(false);
        switchCompat.setFocusable(false);
        switchCompat.setVisibility(INVISIBLE);
        switchCompat.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        addView(switchCompat);

        checkBox = new AppCompatCheckBox(context);
        checkBox.setClickable(false);
        checkBox.setFocusable(false);
        checkBox.setVisibility(INVISIBLE);
        checkBox.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        addView(checkBox);

        endIconView = new ImageView(context);
        endIconView.setClickable(false);
        endIconView.setFocusable(false);
        endIconView.setVisibility(INVISIBLE);
        endIconView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        addView(endIconView);

        setMode(currentMode);
    }

    public void setStartIcon(@DrawableRes int icon) {
        startIconView.setVisibility(icon == 0 ? INVISIBLE : VISIBLE);
        if (icon != 0) {
            startIconView.setImageDrawable(Theme.getIcon(icon, ContextCompat.getColor(getContext(), R.color.iconActive)));
        }

        if (currentMode == MODE_ICONS) {
            textView.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 56, 10, 16, 0));
            valueText.setLayoutParams(LayoutHelper.makeFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.TOP, 56, 35, 16, 0));
        }
    }

    public void setText(@NonNull String text) {
        textView.setText(text);
    }

    public void setText(@StringRes int textId) {
        textView.setText(getContext().getText(textId));
    }

    public void setValue(@NonNull String text) {
        valueText.setText(text);
    }

    public void setValue(@StringRes int textId) {
        valueText.setText(getContext().getText(textId));
    }

    public void setChecked(boolean value) {
        if (currentMode == MODE_SWITCH) {
            switchCompat.setChecked(value);
        } else if (currentMode == MODE_CHECKBOX) {
            checkBox.setChecked(value);
        }
    }

    public void setEndIcon(Drawable icon) {
        endIconView.setImageDrawable(icon);
    }

    public void setMode(@Mode int mode) {
        currentMode = mode;

        if (currentMode == MODE_DEFAULT) {
            valueText.setVisibility(VISIBLE);
            switchCompat.setVisibility(INVISIBLE);
            checkBox.setVisibility(INVISIBLE);
            endIconView.setVisibility(INVISIBLE);
            startIconView.setVisibility(INVISIBLE);
        } else if (currentMode == MODE_SWITCH) {
            valueText.setVisibility(VISIBLE);
            switchCompat.setVisibility(VISIBLE);
            checkBox.setVisibility(INVISIBLE);
            endIconView.setVisibility(INVISIBLE);
            startIconView.setVisibility(INVISIBLE);
        } else if (currentMode == MODE_CHECKBOX) {
            valueText.setVisibility(VISIBLE);
            checkBox.setVisibility(VISIBLE);
            switchCompat.setVisibility(INVISIBLE);
            endIconView.setVisibility(INVISIBLE);
            startIconView.setVisibility(INVISIBLE);
        } else if (currentMode == MODE_ICONS) {
            valueText.setVisibility(VISIBLE);
            endIconView.setVisibility(VISIBLE);
            startIconView.setVisibility(VISIBLE);
            checkBox.setVisibility(INVISIBLE);
            switchCompat.setVisibility(INVISIBLE);
        }
    }

    public void setDivider(boolean divider) {
        this.divider = divider;
        setWillNotDraw(!divider);
    }

    /*public void setMultiline(boolean value) {
        multiline = value;

        if (value) {
            valueText.setLines(0);
            valueText.setMaxLines(0);
            valueText.setSingleLine(false);
            valueText.setPadding(0, 0, 0, ScreenUtils.dp(12));
        } else {
            valueText.setLines(1);
            valueText.setMaxLines(1);
            valueText.setSingleLine();
            valueText.setPadding(0, 0, 0, 0);
        }
    }*/

    public void changeLayoutParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (AndroidExtensions.isLandscape()) {
            params.leftMargin = ScreenUtils.dp(56);
            params.rightMargin = ScreenUtils.dp(56);
        }
        setLayoutParams(params);
    }

    public void changeSwitchTheme() {
        int thumbOn = ContextCompat.getColor(getContext(), Theme.thumbOnColor());
        int thumbOff = ContextCompat.getColor(getContext(), Theme.thumbOffColor());

        int trackOn = ContextCompat.getColor(getContext(), Theme.trackOnColor());
        int trackOff = ContextCompat.getColor(getContext(), Theme.trackOffColor());

        DrawableCompat.setTintList(switchCompat.getThumbDrawable(), new ColorStateList(
                new int[][]{
                        new int[]{ android.R.attr.state_checked },
                        new int[]{}
                },
                new int[]{
                        thumbOn,
                        thumbOff
                }));

        DrawableCompat.setTintList(switchCompat.getTrackDrawable(), new ColorStateList(
                new int[][]{
                        new int[]{ android.R.attr.state_checked  },
                        new int[]{}
                },
                new int[]{
                        trackOn,
                        trackOff
                }));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
        int height = multiline ? getMeasuredHeight() : ScreenUtils.dp(64) + (divider ? 1 : 0);
        /*if (multiline) {
            height = getMeasuredHeight();
        } else {
            height = ScreenUtils.dp(64) + (divider ? 1 : 0);
        }*/
        setMeasuredDimension(width, height);
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