package org.michaelbel.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.michaelbel.seriespicker.AppLoader;

import java.io.InputStream;
import java.util.Properties;

public class AppUtils {

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