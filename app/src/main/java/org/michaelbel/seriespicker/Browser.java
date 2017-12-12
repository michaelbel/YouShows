package org.michaelbel.seriespicker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import org.michaelbel.material.util.Utils;

@SuppressWarnings("all")
public class Browser {

    private static final String TAG = Browser.class.getSimpleName();

    public static void openUrl(Context context, @NonNull String url) {
        SharedPreferences prefs = context.getSharedPreferences("mainconfig", Context.MODE_PRIVATE);

        if (prefs.getBoolean("in_app_browser", true)) {
            openUrl2(context, url);
        } else {
            openBrowserUrl(context, url);
        }
    }

    private static void openUrl2(@NonNull Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.putExtra("android.support.customtabs.extra.SESSION", (Parcelable) null);
            intent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", Utils.getAttrColor(context, R.attr.colorPrimary));
            intent.putExtra("android.support.customtabs.extra.TITLE_VISIBILITY", 1);
            Intent actionIntent = new Intent(Intent.ACTION_SEND);
            actionIntent.setType("text/plain");
            actionIntent.putExtra(Intent.EXTRA_TEXT, Uri.parse(url).toString());
            actionIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, actionIntent, PendingIntent.FLAG_ONE_SHOT);
            Bundle bundle = new Bundle();
            bundle.putInt("android.support.customtabs.customaction.ID", 0);
            bundle.putParcelable("android.support.customtabs.customaction.ICON", BitmapFactory.decodeResource(context.getResources(), org.michaelbel.material.R.drawable.abc_ic_menu_share_mtrl_alpha));
            bundle.putString("android.support.customtabs.customaction.DESCRIPTION", "Share link");
            bundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", pendingIntent);
            intent.putExtra("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", bundle);
            intent.putExtra("android.support.customtabs.extra.TINT_ACTION_BUTTON", false);
            intent.putExtra(android.provider.Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private static void openBrowserUrl(@NonNull Context context, String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /*public static void openUrl(@NonNull Context context, @StringRes int stringId) {
        openUrl(context, context.getString(stringId));
    }*/

    /*public static void openBrowserUrl(@NonNull Context context, @StringRes int stringId) {
        openBrowserUrl(context, context.getString(stringId));
    }*/

    /*public static void openAppInGooglePlay(@NonNull Context context) {
        openAppInGooglePlay(context, context.getPackageName());
    }*/

    /*public static void openAppInGooglePlay(@NonNull Context context, String packageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            Log.e(TAG, e.getMessage());
        }
    }*/
}