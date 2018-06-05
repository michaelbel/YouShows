package org.michaelbel.app;

@Deprecated
public class ScreenUtils {

    @Deprecated
    public static int dp(float value) {
        return (int) Math.ceil(YouShows.AppContext.getResources().getDisplayMetrics().density * value);
    }
}