package org.michaelbel.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.DrawableRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import org.michaelbel.app.rest.model.Company;
import org.michaelbel.app.rest.model.Genre;
import org.michaelbel.material.extensions.Extensions;
import org.michaelbel.shows.R;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Date: 19 MAR 2018
 * Time: 16:43 MSK
 *
 * @author Michael Bel
 */

@SuppressWarnings("all")
public class AndroidExtensions extends Extensions {

    private static final int TYPE_WIFI = 1900;
    private static final int TYPE_MOBILE = 1901;
    private static final int TYPE_VPN = 1902;
    private static final int TYPE_BLUETOOTH = 1903;
    private static final int TYPE_NOT_CONNECTED = 1904;

    public static AppBarLayout.LayoutParams setScrollFlags(View view) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) view.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
        return params;
    }

    private static int getNetworkStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) YouShows.AppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_VPN) {
                return TYPE_VPN;
            }
        }

        return TYPE_NOT_CONNECTED;
    }

    public static boolean typeNotConnected() {
        return getNetworkStatus() == TYPE_NOT_CONNECTED;
    }

    public static int dp(float value) {
        return (int) Math.ceil(YouShows.AppContext.getResources().getDisplayMetrics().density * value);
    }

    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) YouShows.AppContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        WindowManager windowManager = (WindowManager) YouShows.AppContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static boolean isPortrait() {
        return YouShows.AppContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean isLandscape() {
        return YouShows.AppContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isUndefined() {
        return YouShows.AppContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_UNDEFINED;
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = YouShows.AppContext.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = YouShows.AppContext.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public static boolean isScreenLock() {
        KeyguardManager keyguardManager = (KeyguardManager) YouShows.AppContext.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }

    public static String formatDate(String releaseDate) {
        SharedPreferences prefs = YouShows.AppContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);

        if (releaseDate == null || releaseDate.isEmpty()) {
            return "";
        }

        String pattern = "yyyy-MM-dd";
        String newPattern = prefs.getString("date_format", "d MMM yyyy");

        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        SimpleDateFormat newFormat = new SimpleDateFormat(newPattern, Locale.US);

        Date date = null;
        try {
            date = format.parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newFormat.format(date);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRTL() {
        return YouShows.AppContext.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isLTR() {
        return YouShows.AppContext.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;
    }

    public static String getProperty(String key) {
        try {
            Properties properties = new Properties();
            AssetManager assetManager = YouShows.AppContext.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);
            return properties.getProperty(key);
        } catch (Exception e) {
            //FirebaseCrash.logcat(Log.ERROR, "e_message", "Error retrieving file asset");
            //FirebaseCrash.report(e);
        }

        return null;
    }

    public static Point displaySize = new Point();
    public static boolean usingHardwareInput;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();

    public static float getPixelsInCM(float cm, boolean isX) {
        return (cm / 2.54f) * (isX ? displayMetrics.xdpi : displayMetrics.ydpi);
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            YouShows.AppHandler.post(runnable);
        } else {
            YouShows.AppHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        YouShows.AppHandler.removeCallbacks(runnable);
    }

    static {
        checkDisplaySize();
    }

    public static void checkDisplaySize() {
        try {
            Configuration configuration = YouShows.AppContext.getResources().getConfiguration();
            usingHardwareInput = configuration.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
            WindowManager manager = (WindowManager) YouShows.AppContext.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                    //FileLog.e("tmessages", "display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
                }
            }
        } catch (Exception e) {
            //FirebaseCrash.logcat(Log.ERROR, "e_message", "Error check display size");
            //FirebaseCrash.report(e);
        }
    }

    public static String formatCountries(List<String> countries) {
        if (countries == null || countries.isEmpty()) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        for (String country : countries) {
            text.append(country).append(", ");
        }

        text.delete(text.toString().length() - 2, text.toString().length());
        return text.toString();
    }

    public static String formatGenres(List<Genre> genres) {
        if (genres == null) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        for (Genre genre : genres) {
            text.append(genre.name);
            if (genre != genres.get(genres.size() - 1)) {
                text.append(", ");
            }
        }

        return text.toString();
    }

    /*public static String formatGenres(RealmList<String> genres) {
        if (genres == null) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        for (String genre : genres) {
            text.append(genre).append(", ");
        }

        text.delete(text.toString().length() - 2, text.toString().length());
        return text.toString();
    }*/

    public static String formatCompanies(List<Company> companies) {
        if (companies == null) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        for (Company company : companies) {
            text.append(company.name);
            if (company != companies.get(companies.size() - 1)) {
                text.append(", ");
            }
        }

        return text.toString();
    }

    /*public static String formatCompanies(List<String> companies) {
        if (companies == null) {
            return "";
        }

        StringBuilder text = new StringBuilder();
        for (String company : companies) {
            text.append(company).append(", ");
        }

        text.delete(text.toString().length() - 2, text.toString().length());
        return text.toString();
    }*/

    public static int getLanguage() {
        SharedPreferences prefs = YouShows.AppContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        return prefs.getInt("language", 0);
    }

    public static void vibrate(int milliseconds) {
        Vibrator vibrator = (Vibrator) YouShows.AppContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Deprecated in API 26
                vibrator.vibrate(milliseconds);
            }
        }
    }

    public static LayoutAnimationController layoutAnimationController() {
        SharedPreferences prefs = YouShows.AppContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
        boolean animations = prefs.getBoolean("animations", true);
        if (animations) {
            return AnimationUtils.loadLayoutAnimation(YouShows.AppContext, R.anim.layout_animation_from_bottom);
        } else {
            return null;
        }
    }

    private static String formatDate(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.format(date != null ? date : new Date());
    }

    public static String getCurrentDateAndTime() {
        String pattern = "dd.MM.yy-HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String currentDateAndTime = simpleDateFormat.format(new Date());
        return currentDateAndTime;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static Drawable getIcon(Context context, @DrawableRes int resource, int colorFilter) {
        return getIcon(context, resource, colorFilter, PorterDuff.Mode.MULTIPLY);
    }

    public static Drawable getIcon(Context context, @DrawableRes int resource, int colorFilter, PorterDuff.Mode mode) {
        Drawable iconDrawable = ContextCompat.getDrawable(context, resource);

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

    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_ALL = FLAG_TAG_BR | FLAG_TAG_BOLD;

    public static SpannableStringBuilder replaceTags(String str) {
        return replaceTags(str, FLAG_TAG_ALL);
    }

    public static SpannableStringBuilder replaceTags(String str, int flag) {
        try {
            int start;
            int end;
            StringBuilder stringBuilder = new StringBuilder(str);
            if ((flag & FLAG_TAG_BR) != 0) {
                while ((start = stringBuilder.indexOf("<br>")) != -1) {
                    stringBuilder.replace(start, start + 4, "\n");
                }
                while ((start = stringBuilder.indexOf("<br/>")) != -1) {
                    stringBuilder.replace(start, start + 5, "\n");
                }
            }
            ArrayList<Integer> bolds = new ArrayList<>();
            if ((flag & FLAG_TAG_BOLD) != 0) {
                while ((start = stringBuilder.indexOf("<b>")) != -1) {
                    stringBuilder.replace(start, start + 3, "");
                    end = stringBuilder.indexOf("</b>");
                    if (end == -1) {
                        end = stringBuilder.indexOf("<b>");
                    }
                    stringBuilder.replace(end, end + 4, "");
                    bolds.add(start);
                    bolds.add(end);
                }
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringBuilder);
            for (int a = 0; a < bolds.size() / 2; a++) {
                spannableStringBuilder.setSpan(new TypefaceSpan("sans-serif-medium"), bolds.get(a * 2), bolds.get(a * 2 + 1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return spannableStringBuilder;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new SpannableStringBuilder(str);
    }
}