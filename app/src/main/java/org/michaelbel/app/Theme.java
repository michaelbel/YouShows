package org.michaelbel.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.michaelbel.shows.R;

/**
 * Date: 10 MAY 2018
 * Time: 13:09 MSK
 *
 * @author Michael Bel
 */

public class Theme {

    public static final int THEME_LIGHT = 0;
    public static final int THEME_NIGHT = 1;

    public static int getTheme() {
        SharedPreferences prefs = YouShows.AppContext.getSharedPreferences("mainconfig", Context.MODE_PRIVATE);
        return prefs.getInt("theme", THEME_LIGHT);
    }

    public static boolean themeLight() {
        return getTheme() == THEME_LIGHT;
    }

//--Styles------------------------------------------------------------------------------------------

    public static int alertDialogStyle() {
        return themeLight() ? R.style.AlertLight : R.style.AlertNight;
    }

//--Colors------------------------------------------------------------------------------------------

    public static int primaryColor() {
        return themeLight() ? R.color.primary : R.color.n_primary;
    }

    public static int primaryDarkColor() {
        return themeLight() ? R.color.primaryDark : R.color.n_primaryDark;
    }

    public static int accentColor() {
        return themeLight() ? R.color.accent : R.color.n_accent;
    }

    public static int backgroundColor() {
        return themeLight() ? R.color.background : R.color.n_background;
    }

    public static int foregroundColor() {
        return themeLight() ? R.color.foreground : R.color.n_foreground;
    }

    public static int primaryTextColor() {
        return themeLight() ? R.color.primaryText : R.color.n_primaryText;
    }

    public static int secondaryTextColor() {
        return themeLight() ? R.color.secondaryText : R.color.n_secondaryText;
    }

    public static int iconActiveColor() {
        return themeLight() ? R.color.iconActive : R.color.n_iconActive;
    }

    public static int iconInactiveColor() {
        return themeLight() ? R.color.iconInactive : R.color.n_iconInactive;
    }

    public static int statusBarColor() {
        return themeLight() ? R.color.statusBar : R.color.n_statusBar;
    }

    public static int appBarColor() {
        return themeLight() ? R.color.appBar : R.color.n_appBar;
    }

    public static int dividerColor() {
        return themeLight() ? R.color.divider : R.color.n_divider;
    }

    public static int thumbOnColor() {
        return themeLight() ? R.color.switch_thumbOn : R.color.n_switch_thumbOn;
    }

    public static int thumbOffColor() {
        return themeLight() ? R.color.switch_thumbOff : R.color.n_switch_thumbOff;
    }

    public static int trackOnColor() {
        return themeLight() ? R.color.switch_trackOn : R.color.n_switch_trackOn;
    }

    public static int trackOffColor() {
        return themeLight() ? R.color.switch_trackOff : R.color.n_switch_trackOff;
    }

//--Other colors------------------------------------------------------------------------------------

    public static int fabShowFollowColor() {
        return themeLight() ? R.color.white : R.color.n_accent;
    }

    public static int fabShowFollowIconColor() {
        return themeLight() ? R.color.iconActive : R.color.white;
    }

    public static int showNameColor() {
        return themeLight() ? R.color.primaryDark : R.color.n_accent;
    }

    public static int swipeRefreshProgressColor() {
        return themeLight() ? R.color.white : R.color.n_iconActive;
    }

    public static int tabSelectColor() {
        return themeLight() ? R.color.white : R.color.accent;
    }

    public static int tabUnselectColor() {
        return themeLight() ? R.color.tabUnselected : R.color.n_tabUnselected;
    }

    public static int changelogCurrentVersionText() {
        return themeLight() ? R.color.green_dark : R.color.green;
    }

    public static int seasonViewBackground() {
        return themeLight() ? R.color.background : R.color.n_background;
    }

    public static int background() {
        return themeLight() ? R.color.background : R.color.n_background;
    }

    public static int dialogButtonText() {
        return themeLight() ? R.color.n_accent : R.color.accent;
    }

    public static int changelogVersionText() {
        return themeLight() ? R.color.blue : R.color.yellow;
    }

    public static int sortDivider() {
        return themeLight() ? R.color.sortDivider : R.color.n_background;
    }

//--Resources---------------------------------------------------------------------------------------

    public static int fabProgressBar() {
        return themeLight() ? R.drawable.ic_anim_progressbar2 : R.drawable.ic_anim_progressbar;
    }
}