package org.michaelbel.rest.model;

import com.google.gson.annotations.SerializedName;

import org.michaelbel.rest.TmdbObject;

import java.io.Serializable;

/**
 * Date: 19 MAR 2018
 * Time: 21:37 MSK
 *
 * @author Michael Bel
 */

public class Episode extends TmdbObject implements Serializable {

    @SerializedName("air_date")
    public String airDate;

    //@SerializedName("crew")

    @SerializedName("episode_number")
    public int episodeNumber;

    //@SerializedName("guest_stars")

    @SerializedName("name")
    public String name;

    @SerializedName("overview")
    public String overview;

    @SerializedName("season_id")
    public int id;

    @SerializedName("production_code")
    public String production_code;

    @SerializedName("season_number")
    public String season_number;

    @SerializedName("still_path")
    public String still_path;

    @SerializedName("vote_average")
    public float vote_average;

    @SerializedName("vote_count")
    public int vote_count;
}