package org.michaelbel.rest.model;

import com.google.gson.annotations.SerializedName;

import org.michaelbel.rest.Response;
import org.michaelbel.rest.TmdbObject;

import java.io.Serializable;

/**
 * Date: 19 MAR 2018
 * Time: 17:53 MSK
 *
 * @author Michael Bel
 */

public class Season extends TmdbObject implements Serializable {

    @Response
    @SerializedName("id")
    public int id;

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

}
