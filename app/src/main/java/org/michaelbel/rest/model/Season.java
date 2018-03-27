package org.michaelbel.rest.model;

import com.google.gson.annotations.SerializedName;

import org.michaelbel.rest.TmdbObject;

import java.io.Serializable;

/**
 * Date: 19 MAR 2018
 * Time: 17:53 MSK
 *
 * @author Michael Bel
 */

public class Season extends TmdbObject implements Serializable {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("overview")
    public String overview;

    @SerializedName("air_date")
    public String airDate;

    @SerializedName("poster_path")
    public String posterPath;

    @SerializedName("episode_count")
    public int episodeCount;

    @SerializedName("season_number")
    public int seasonNumber;
}
