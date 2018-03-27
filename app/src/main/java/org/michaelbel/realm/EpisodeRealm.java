package org.michaelbel.realm;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Date: 25 MAR 2018
 * Time: 16:27 MSK
 *
 * @author Michael Bel
 */

public class EpisodeRealm extends RealmObject implements Serializable {

    public int episodeId;
    public int voteCount;
    public int episodeNumber;
    public float voteAverage;
    public String name;
    public String airDate;
    public String overview;
    public String stillPath;
    public String seasonNumber;
    public String productionCode;
}