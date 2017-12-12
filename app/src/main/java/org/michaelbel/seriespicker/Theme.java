package org.michaelbel.seriespicker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

@SuppressWarnings("all")
public class Theme {

    private static Context getContext() {
        return AppLoader.AppContext;
    }

    public static boolean getTheme() {
        SharedPreferences prefs = getContext().getSharedPreferences("mainconfig", Context.MODE_PRIVATE);
        return prefs.getBoolean("theme", true);
    }

    public static int primaryColor() {
        return getTheme() ? R.color.colorPrimary : R.color.colorPrimary;
    }

    public static int backgroundColor() {
        return getTheme() ? R.color.backgroundColor : R.color.night_backgroundColor;
    }

    public static int foregroundColor() {
        return getTheme() ? R.color.foregroundColor : R.color.night_foregroundColor;
    }

    public static int primaryTextColor() {
        return getTheme() ? R.color.primaryTextColor: R.color.night_primaryTextColor;
    }

    public static int secondaryTextColor() {
        return getTheme() ? R.color.secondaryTextColor : R.color.night_secondaryTextColor;
    }

    public static int dividerColor() {
        return getTheme() ? R.color.dividerColor : R.color.night_dividerColor;
    }

    public static int hintTextColor() {
        return getTheme() ? R.color.disabledHintTextColor : R.color.night_disabledHintTextColor;
    }

    public static int popupTheme() {
        return getTheme() ? R.style.ThemeOverlay_AppCompat_Light : R.style.ThemeOverlay_AppCompat;
    }

    public static int alertTheme() {
        return getTheme() ? R.style.AlertThemeLight : R.style.AlertThemeNight;
    }

    public static int selectableItemBackground() {
        int[] attrs = new int[] {
                R.attr.selectableItemBackground
        };

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        return backgroundResource;
    }

    public static int selectableItemBackgroundBorderless() {
        int[] attrs = new int[] {
                R.attr.selectableItemBackgroundBorderless
        };

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        return backgroundResource;
    }

    public static Drawable selectableItemBackgroundDrawable() {
        int[] attrs = new int[] {
                android.R.attr.selectableItemBackground
        };

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = typedArray.getDrawable(0);
        typedArray.recycle();

        return drawableFromTheme;
    }

    public static Drawable selectableItemBackgroundBorderlessDrawable() {
        int[] attrs = new int[] {
                android.R.attr.selectableItemBackgroundBorderless
        };

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = typedArray.getDrawable(0);
        typedArray.recycle();

        return drawableFromTheme;
    }

    public static Drawable getIcon(@DrawableRes int resource, int colorFilter) {
        return getIcon(resource, colorFilter, PorterDuff.Mode.MULTIPLY);
    }

    public static Drawable getIcon(@DrawableRes int resource, int colorFilter, PorterDuff.Mode mode) {
        Drawable iconDrawable = getContext().getResources().getDrawable(resource, null);

        if (iconDrawable != null) {
            iconDrawable.clearColorFilter();
            iconDrawable.mutate().setColorFilter(colorFilter, mode);
        }

        return iconDrawable;
    }

    public static void clearCursorDrawable(EditText editText) {
        if (editText == null) {
            return;
        }
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.setInt(editText, 0);
        } catch (Exception e) {

        }
    }
}