package org.michaelbel.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

import org.michaelbel.shows.R;

/**
 * Date: 10 MAY 2018
 * Time: 13:09 MSK
 *
 * @author Michael Bel
 */

public class Theme {

    public static final int THEME_NIGHT_BLUE = 0;
    public static final int THEME_NIGHT_BLACK = 1;
    public static final int THEME_NIGHT_RED = 2;

    private static Context getContext() {
        return YouShows.AppContext;
    }

    public static int getTheme() {
        SharedPreferences prefs = getContext().getSharedPreferences("mainconfig", Context.MODE_PRIVATE);
        return prefs.getInt("theme", 1);
    }

    public static class Color {

        public static int primary() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_primary;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.primary;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_primary;
            }

            return 0;
        }

        public static int primaryDark() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_primaryDark;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.primaryDark;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_primaryDark;
            }

            return 0;
        }

        public static int accent() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_accent;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.accent;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_accent;
            }

            return 0;
        }

        public static int primaryText() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_primaryText;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.primaryText;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_primaryText;
            }

            return 0;
        }

        public static int secondaryText() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_secondaryText;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.secondaryText;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_secondaryText;
            }

            return 0;
        }

        public static int disabledHintText() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_disabledHintText;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.disabledHintText;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_disabledHintText;
            }

            return 0;
        }

        public static int divider() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_divider;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.divider;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_divider;
            }

            return 0;
        }

        public static int iconActive() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_iconActive;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.iconActive;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_iconActive;
            }

            return 0;
        }

        public static int iconInactive() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_iconInactive;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.iconInactive;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_iconInactive;
            }

            return 0;
        }

        public static int statusBar() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_statusBar;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.statusBar;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_statusBar;
            }

            return 0;
        }

        public static int appBar() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_appBar;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.appBar;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_appBar;
            }

            return 0;
        }

        public static int background() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_background;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.background;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_background;
            }

            return 0;
        }

        public static int foreground() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_foreground;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.foreground;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_foreground;
            }

            return 0;
        }

        public static int thumbOn() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_switch_thumbOn;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.switch_thumbOn;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_switch_thumbOn;
            }

            return 0;
        }

        public static int thumbOff() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_switch_thumbOff;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.switch_thumbOff;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_switch_thumbOff;
            }

            return 0;
        }

        public static int trackOn() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_switch_trackOn;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.switch_trackOn;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_switch_trackOn;
            }

            return 0;
        }

        public static int trackOff() {
            if (Theme.getTheme() == THEME_NIGHT_BLUE) {
                return R.color.nb_switch_trackOff;
            } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
                return R.color.switch_trackOff;
            } else if (Theme.getTheme() == THEME_NIGHT_RED) {
                return R.color.nr_switch_trackOff;
            }

            return 0;
        }
    }

    public static int alertTheme() {
        if (Theme.getTheme() == THEME_NIGHT_BLUE) {
            return R.style.AlertNightBlue;
        } else if (Theme.getTheme() == THEME_NIGHT_BLACK) {
            return R.style.AlertNightBlack;
        } else if (Theme.getTheme() == THEME_NIGHT_RED) {
            return R.style.AlertNightRed;
        }

        return 0;
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

    /*
    public static int getAttrColor(@NonNull Context context, @AttrRes int colorAttr) {
        int color = 0;
        int[] attrs = new int[] {
                colorAttr
        };

        try {
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            color = typedArray.getColor(0, 0);
            typedArray.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return color;
    }

    public static int[] getColorArray(@NonNull Context context, @ArrayRes int arrayRes) {
        if (arrayRes == 0) {
            return null;
        }

        TypedArray ta = context.getResources().obtainTypedArray(arrayRes);
        int[] colors = new int[ta.length()];

        for (int i = 0; i < ta.length(); i++) {
            colors[i] = ta.getColor(i, 0);
        }

        ta.recycle();
        return colors;
    }*/
}