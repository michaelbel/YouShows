package org.michaelbel.realm;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Date: 25 MAR 2018
 * Time: 16:27 MSK
 *
 * @author Michael Bel
 */

public class SeasonRealm extends RealmObject implements Serializable {

    public int seasonId;
    public int episodeCount;
    public int episodeWatchedCount;
    public int seasonNumber;
    public String name;
    public String airDate;
    public String overview;
    public String posterPath;

    public RealmList<EpisodeRealm> episodes;
}