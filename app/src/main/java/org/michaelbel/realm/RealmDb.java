package org.michaelbel.realm;

import org.michaelbel.rest.model.Show;

import io.realm.Realm;

/**
 * Date: 27 MAR 2018
 * Time: 19:19 MSK
 *
 * @author Michael Bel
 */

public class RealmDb {

    public static void addShow(Show show) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            ShowRealm showRealm = realm.createObject(ShowRealm.class);
            showRealm.showId = show.id;
            showRealm.name = show.name;
            showRealm.airDate = show.airDate;
            showRealm.overview = show.overview;
            showRealm.posterPath = show.posterPath;
            showRealm.backdropPath = show.backdropPath;
        });
        realmDb.close();
    }

    public static void deleteShow(int showId) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            ShowRealm showRealm = realm.where(ShowRealm.class).equalTo("showId", showId).findFirst();
            if (showRealm != null) {
                showRealm.deleteFromRealm();
            }
        });
        realmDb.close();
    }
}