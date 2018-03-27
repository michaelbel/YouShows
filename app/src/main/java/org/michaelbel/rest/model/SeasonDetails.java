package org.michaelbel.rest.model;

import com.google.gson.annotations.SerializedName;

import org.michaelbel.rest.TmdbObject;

import java.io.Serializable;
import java.util.List;

/**
 * Date: 19 MAR 2018
 * Time: 17:53 MSK
 *
 * @author Michael Bel
 */

public class SeasonDetails extends TmdbObject implements Serializable {

    @SerializedName("season_id")
    public int id;

    @SerializedName("_id")
    public String _id;

    @SerializedName("air_date")
    public String air_date;

    @SerializedName("episodes")
    public List<Episode> episodes;

    @SerializedName("name")
    public String name;

    @SerializedName("overview")
    public String overview;

    @SerializedName("poster_path")
    public String poster_path;

    @SerializedName("season_number")
    public int season_number;
}