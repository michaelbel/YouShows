package org.michaelbel.app.rest.model;

import com.google.gson.annotations.SerializedName;

import org.michaelbel.app.realm.Realmed;
import org.michaelbel.app.rest.Response;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Date: 19 MAR 2018
 * Time: 21:37 MSK
 *
 * @author Michael Bel
 */

@RealmClass
public class Episode extends RealmObject implements Serializable {

    @Response
    @SerializedName("air_date")
    public String airDate;

    //@SerializedName("crew")

    @Response
    @SerializedName("episode_number")
    public int episodeNumber;

    //@SerializedName("guest_stars")

    @Response
    @SerializedName("name")
    public String name;

    @Response
    @SerializedName("overview")
    public String overview;

    @SerializedName("id")
    public int episodeId;

    @Response
    @SerializedName("production_code")
    public String production_code;

    @Response
    @SerializedName("season_number")
    public String season_number;

    @Response
    @SerializedName("still_path")
    public String still_path;

    @Response
    @SerializedName("vote_average")
    public float vote_average;

    @Response
    @SerializedName("vote_count")
    public int vote_count;

    @Realmed
    public int showId;

    @Realmed
    public int seasonId;

    @Realmed
    public boolean isWatched;
}