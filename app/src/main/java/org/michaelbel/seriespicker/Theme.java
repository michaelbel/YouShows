package org.michaelbel.seriespicker;

import android.content.Context;
import android.content.SharedPreferences;

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
}