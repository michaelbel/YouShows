package org.michaelbel.youshows.rest.model;

import com.google.gson.annotations.SerializedName;

import org.michaelbel.youshows.realm.Realmed;
import org.michaelbel.youshows.rest.Response;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.RealmClass;

/**
 * Date: 19 MAR 2018
 * Time: 17:53 MSK
 *
 * @author Michael Bel
 */

@RealmClass
public class Season extends RealmObject implements Serializable {

    @Response
    @SerializedName("id")
    public int seasonId;

    @Response
    @SerializedName("air_date")
    public String airDate;

    @Response
    @SerializedName("poster_path")
    public String posterPath;

    @Response
    @SerializedName("episode_count")
    public int episodeCount;

    @Response
    @SerializedName("season_number")
    public int seasonNumber;

    @SerializedName("name")
    public String name;

    @SerializedName("overview")
    public String overview;

    @SerializedName("_id")
    public String _id;

    @Ignore
    @SerializedName("episodes")
    public List<Episode> episodes;

    @Realmed
    public int showId;

    @Realmed
    public int episodeWatchedCount;

    @Realmed
    public RealmList<Episode> episodesList;

    @Realmed
    public int scrollPosition;

//--Deprecated (added to realm, can not be removed)-------------------------------------------------

    @Deprecated
    public boolean isWatched;

    @Deprecated
    public String lastWatchDate;
}