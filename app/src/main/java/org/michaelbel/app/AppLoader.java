package org.michaelbel.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import org.michaelbel.app.eventbus.RxBus;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppLoader extends Application {

    private RxBus rxBus;
    public static volatile Context AppContext;
    public static volatile Handler AppHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        AppContext = getApplicationContext();
        AppHandler = new Handler(getApplicationContext().getMainLooper());

        rxBus = new RxBus();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                //.schemaVersion(1)
                //.migration()
                .name("RealmDb-v11.realm")
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public RxBus bus() {
        return rxBus;
    }
}