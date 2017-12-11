package org.michaelbel.seriespicker;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

@SuppressWarnings("all")
public class AppLoader extends Application {

    private RxBus rxBus;
    public static volatile Context AppContext;
    public static volatile Handler AppHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        rxBus = new RxBus();
        AppContext = getApplicationContext();
        AppHandler = new Handler(getApplicationContext().getMainLooper());
    }

    public RxBus bus() {
        return rxBus;
    }
}