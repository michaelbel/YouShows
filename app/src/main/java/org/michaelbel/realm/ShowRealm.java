package org.michaelbel.realm;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Date: 25 MAR 2018
 * Time: 16:26 MSK
 *
 * @author Michael Bel
 */

public class ShowRealm extends RealmObject implements Serializable {

    public int showId;
    public String name;
    public String airDate;
    public String overview;
    public String posterPath;
    public String backdropPath;

    public RealmList<SeasonRealm> seasons;
}