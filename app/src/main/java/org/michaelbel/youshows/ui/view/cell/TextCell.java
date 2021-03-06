package org.michaelbel.youshows.ui.view.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.michaelbel.youshows.AndroidExtensions;
import org.michaelbel.material.widget.LayoutHelper;
import org.michaelbel.youshows.Theme;
import org.michaelbel.material.extensions.Extensions;

/**
 * Date: 02 MAR 2018
 * Time: 00:21 MSK
 *
 * @author Michael Bel
 */

@SuppressLint("ClickableViewAccessibility")
public class TextCell extends FrameLayout {

    public static final int MODE_DEFAULT = 100;
    public static final int MODE_VALUE_TEXT = 200;
    public static final int MODE_SWITCH = 300;
    public static final int MODE_CHECKBOX = 400;
    public static final int MODE_COLOR = 500;
    public static final int MODE_ICON = 600;

    @IntDef({
        MODE_DEFAULT,
        MODE_VALUE_TEXT,
        MODE_SWITCH,
        MODE_CHECKBOX,
        MODE_COLOR,
        MODE_ICON
    })
    private @interface Mode {}

    protected TextView textView;
    protected TextView valueText;
    protected ImageView iconView;
    protected SwitchCompat switchCompat;
    protected AppCompatCheckBox checkBox;

    protected Paint paint;
    private boolean divider;
    private Rect rect = new Rect();
    private int cellHeight;
    private int currentMode = MODE_DEFAULT;

    public TextCell(Context context) {
        super(context);

        cellHeight = Extensions.dp(context, 52);

        setElevation(Extensions.dp(context,1));
        setForeground(Extensions.selectableItemBackgroundDrawable(context));
        setBackgroundColor(ContextCompat.getColor(context, Theme.foregroundColor()));

        if (paint == null) {
            paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setColor(ContextCompat.getColor(context, Theme.dividerColor()));
        }

        iconView = new ImageView(context);
        iconView.setVisibility(INVISIBLE);
        iconView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL, 16, 0, 0, 0));
        addView(iconView);

        textView = new AppCompatTextView(context);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(ContextCompat.getColor(context, Theme.primaryTextColor()));
        textView.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL, 16, 0, 16, 0));
        addView(textView);

        valueText = new AppCompatTextView(context);
        valueText.setLines(1);
        valueText.setMaxLines(1);
        valueText.setSingleLine();
        valueText.setVisibility(INVISIBLE);
        valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        valueText.setTextColor(ContextCompat.getColor(context, Theme.accentColor()));
        valueText.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        addView(valueText);

        switchCompat = new SwitchCompat(context);
        switchCompat.setClickable(false);
        switchCompat.setFocusable(false);
        switchCompat.setVisibility(INVISIBLE);
        switchCompat.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        addView(switchCompat);

        checkBox = new AppCompatCheckBox(context);
        checkBox.setClickable(false);
        checkBox.setFocusable(false);
        checkBox.setVisibility(INVISIBLE);
        checkBox.setLayoutParams(LayoutHelper.makeFrame(context, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 16, 0));
        addView(checkBox);

        setMode(currentMode);
    }

    public void setIcon(@DrawableRes int icon) {
        iconView.setVisibility(icon == 0 ? INVISIBLE : VISIBLE);
        if (icon != 0) {
            iconView.setImageDrawable(AndroidExtensions.getIcon(getContext(), icon, ContextCompat.getColor(getContext(), Theme.iconActiveColor())));
        }

        if (currentMode == MODE_ICON) {
            textView.setLayoutParams(LayoutHelper.makeFrame(getContext(), LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.CENTER_VERTICAL, 56, 0, 16, 0));
        }
    }

    public void setText(@NonNull String text) {
        textView.setText(text);
    }

    public void setText(@StringRes int textId) {
        textView.setText(getContext().getText(textId));
    }

    public void setChecked(boolean value) {
        if (currentMode == MODE_SWITCH) {
            switchCompat.setChecked(value);
        }

        if (currentMode == MODE_CHECKBOX) {
            checkBox.setChecked(value);
        }
    }

    public void setValue(@NonNull String value) {
        valueText.setText(value);
    }

    public void setValue(@StringRes int textId) {
        valueText.setText(getContext().getText(textId));
    }

    public void setMode(@Mode int mode) {
        currentMode = mode;

        if (currentMode == MODE_DEFAULT) {
            valueText.setVisibility(INVISIBLE);
            switchCompat.setVisibility(INVISIBLE);
            checkBox.setVisibility(INVISIBLE);
            iconView.setVisibility(INVISIBLE);
        } else if (currentMode == MODE_VALUE_TEXT) {
            valueText.setVisibility(VISIBLE);
            switchCompat.setVisibility(INVISIBLE);
            checkBox.setVisibility(INVISIBLE);
            iconView.setVisibility(INVISIBLE);
        } else if (currentMode == MODE_SWITCH) {
            switchCompat.setVisibility(VISIBLE);
            valueText.setVisibility(INVISIBLE);
            checkBox.setVisibility(INVISIBLE);
            iconView.setVisibility(INVISIBLE);
        } else if (currentMode == MODE_CHECKBOX) {
            checkBox.setVisibility(VISIBLE);
            valueText.setVisibility(INVISIBLE);
            switchCompat.setVisibility(INVISIBLE);
            iconView.setVisibility(INVISIBLE);
        } else if (currentMode == MODE_COLOR) {
            valueText.setVisibility(INVISIBLE);
            switchCompat.setVisibility(INVISIBLE);
            checkBox.setVisibility(INVISIBLE);
            iconView.setVisibility(INVISIBLE);
        } else if (currentMode == MODE_ICON) {
            iconView.setVisibility(VISIBLE);
            valueText.setVisibility(INVISIBLE);
            switchCompat.setVisibility(INVISIBLE);
            checkBox.setVisibility(INVISIBLE);
        }
    }

    public void setDivider(boolean divider) {
        this.divider = divider;
        setWillNotDraw(!divider);
    }

    public void changeLayoutParams() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (AndroidExtensions.isLandscape()) {
            params.leftMargin = Extensions.dp(getContext(),56);
            params.rightMargin = Extensions.dp(getContext(),56);
        }
        setLayoutParams(params);
    }

    public void changeSwitchTheme() {
        int thumbOn = ContextCompat.getColor(getContext(), Theme.thumbOnColor());
        int thumbOff = ContextCompat.getColor(getContext(), Theme.thumbOffColor());

        int trackOn = ContextCompat.getColor(getContext(), Theme.trackOnColor());
        int trackOff = ContextCompat.getColor(getContext(), Theme.trackOffColor());

        DrawableCompat.setTintList(switchCompat.getThumbDrawable(), new ColorStateList(
            new int[][] {
                new int[]{android.R.attr.state_checked},
                new int[]{}
            },
            new int[] {
                thumbOn,
                thumbOff
            }));

        DrawableCompat.setTintList(switchCompat.getTrackDrawable(), new ColorStateList(
            new int[][] {
                new int[]{android.R.attr.state_checked},
                new int[]{}
            },
            new int[] {
                trackOn,
                trackOff
            }));
    }

    public void setTextColor(@ColorRes int color) {
        textView.setTextColor(ContextCompat.getColor(getContext(), color));
    }

    public TextCell setHeight(int height) {
        cellHeight = height;
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        int height = cellHeight + (divider ? 1 : 0);
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