package org.michaelbel.youshows;

import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.FrameLayout;

@Deprecated
public class LayoutHelper {

    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;

    private static int getSize(float size) {
        return (int) (size < 0 ? size : dp(size));
    }

    public static int dp(float value) {
        return (int) Math.ceil(YouShows.AppContext.getResources().getDisplayMetrics().density * value);
    }

    public static FrameLayout.LayoutParams makeFrame(int width, int height) {
        return new FrameLayout.LayoutParams(getSize(width), getSize(height));
    }

    public static FrameLayout.LayoutParams makeFrame(int width, int height, int gravity) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getSize(width), getSize(height));
        params.gravity = gravity;
        return params;
    }

    public static FrameLayout.LayoutParams makeFrame(int width, int height, int gravity, float start, float top, float end, float bottom) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getSize(width), getSize(height));
        params.gravity = gravity;
        params.leftMargin = getSize(start);
        params.topMargin = getSize(top);
        params.rightMargin = getSize(end);
        params.bottomMargin = getSize(bottom);
        return params;
    }

    public static SwipeRefreshLayout.LayoutParams makeSwipeRefresh(int width, int height) {
        return new SwipeRefreshLayout.LayoutParams(getSize(width), getSize(height));
    }
}