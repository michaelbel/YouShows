package org.michaelbel.app;

public class ScreenUtils {

    public static int dp(float value) {
        return (int) Math.ceil(YouShows.AppContext.getResources().getDisplayMetrics().density * value);
    }
}