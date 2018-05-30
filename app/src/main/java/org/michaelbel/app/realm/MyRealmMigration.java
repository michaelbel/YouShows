package org.michaelbel.app.realm;

import android.support.annotation.NonNull;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Date: 22 APR 2018
 * Time: 09:56 MSK
 *
 * @author Michael Bel
 */

@SuppressWarnings("all")
public class MyRealmMigration implements RealmMigration {

    @Override
    public void migrate(@NonNull DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema realmSchema = realm.getSchema();

        if (oldVersion == 0) {
            RealmObjectSchema schema = realmSchema.get("Season");
            schema.addField("lastWatchDate", String.class);
            oldVersion++;
        }

        if (oldVersion == 1) {
            RealmObjectSchema schema = realmSchema.get("Show");
            schema.addField("progress", int.class);
            oldVersion++;
        }

        if (oldVersion == 2) {
            RealmObjectSchema schema = realmSchema.get("Show");
            schema.addRealmListField("genresString", String.class);
            oldVersion++;
        }

        if (oldVersion == 3) {
            RealmObjectSchema schema = realmSchema.get("Show");
            schema.addRealmListField("countriesString", String.class);
            schema.addRealmListField("companiesString", String.class);
            oldVersion++;
        }

        if (oldVersion == 4) {
            RealmObjectSchema schema = realmSchema.get("Show");
            schema.addField("showProgress", float.class);
            oldVersion++;
        }

        if (oldVersion == 5) {
            RealmObjectSchema schema = realmSchema.create("SearchItem");
            schema.addField("query", String.class);
            schema.addField("date", String.class);
            oldVersion++;
        }

        if (oldVersion == 6) {
            RealmObjectSchema schema = realmSchema.get("SearchItem");
            schema.addField("voice", boolean.class);
            oldVersion++;
        }

        if (oldVersion == 7) {
            realmSchema.get("Show").addField("lastChangesDate", String.class);
            realmSchema.get("Show").addField("startFollowingDate", String.class);
            realmSchema.get("Episode").addField("watchDate", String.class);
            oldVersion++;
        }
    }
}