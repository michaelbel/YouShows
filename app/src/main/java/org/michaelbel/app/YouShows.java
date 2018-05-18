package org.michaelbel.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import org.michaelbel.app.eventbus.RxBus;
import org.michaelbel.app.realm.MyRealmMigration;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Date: 02 MAY 2018
 * Time: 00:11 MSK
 *
 * @author Michael Bel
 */

public class YouShows extends Application {

    public static final String EMAIL = "mchlb@ya.ru";
    public static final String TELEGRAM_URL = "https://t.me/michaelbel";
    public static final String TELEGRAM_FDL = "t.me/michaelbel";
    public static final String PAYPAL_ME = "https://paypal.me/michaelbel";
    public static final String GITHUB_URL = "https://github.com/michaelbel/shows";
    public static final String APP_WEB = "https://play.google.com/store/apps/details?id=org.michaelbel.shows";
    public static final String APP_MARKET = "market://details?id=org.michaelbel.shows";
    //public static final String ACCOUNT_WEB = "https://play.google.com/store/apps/developer?id=Michael+Bel";
    //public static final String ACCOUNT_MARKET = "market://developer?id=Michael+Bel";

    public static volatile Context AppContext;
    public static volatile Handler AppHandler;

    private RxBus rxBus;
    private static final String REALM_NAME = "realmDb.realm";

    @Override
    public void onCreate() {
        super.onCreate();

        AppContext = getApplicationContext();
        AppHandler = new Handler(getApplicationContext().getMainLooper());

        rxBus = new RxBus();

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
            .name(REALM_NAME)
            .schemaVersion(6)
            .migration(new MyRealmMigration())
            .build();
        Realm.setDefaultConfiguration(config);
    }

    public RxBus bus() {
        return rxBus;
    }
}