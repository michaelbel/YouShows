package org.michaelbel.app;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import org.michaelbel.material.extensions.Extensions;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        ConnectivityManager connectivityManager = (ConnectivityManager) AppLoader.AppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        return (int) Math.ceil(AppLoader.AppContext.getResources().getDisplayMetrics().density * value);
    }

    public static int getScreenWidth() {
        WindowManager windowManager = (WindowManager) AppLoader.AppContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight() {
        WindowManager windowManager = (WindowManager) AppLoader.AppContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static boolean isPortrait() {
        return AppLoader.AppContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean isLandscape() {
        return AppLoader.AppContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isUndefined() {
        return AppLoader.AppContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_UNDEFINED;
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = AppLoader.AppContext.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = AppLoader.AppContext.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public static boolean isScreenLock() {
        KeyguardManager keyguardManager = (KeyguardManager) AppLoader.AppContext.getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }

    public static String formatDate(String releaseDate) {
        if (releaseDate == null || releaseDate.isEmpty()) {
            return "";
        }

        String pattern = "yyyy-MM-dd";
        String newPattern = "d MMM yyyy";

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
        return AppLoader.AppContext.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isLTR() {
        return AppLoader.AppContext.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;
    }

    public static String getProperty(String key) {
        try {
            Properties properties = new Properties();
            AssetManager assetManager = AppLoader.AppContext.getAssets();
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
            AppLoader.AppHandler.post(runnable);
        } else {
            AppLoader.AppHandler.postDelayed(runnable, delay);
        }
    }

    public static void cancelRunOnUIThread(Runnable runnable) {
        AppLoader.AppHandler.removeCallbacks(runnable);
    }

    static {
        checkDisplaySize();
    }

    public static void checkDisplaySize() {
        try {
            Configuration configuration = AppLoader.AppContext.getResources().getConfiguration();
            usingHardwareInput = configuration.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;
            WindowManager manager = (WindowManager) AppLoader.AppContext.getSystemService(Context.WINDOW_SERVICE);
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
}