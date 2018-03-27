package org.michaelbel.rest.model;

import com.google.gson.annotations.SerializedName;

import org.michaelbel.rest.TmdbObject;

import java.io.Serializable;
import java.util.List;

/**
 * Date: 27 MAR 2018
 * Time: 20:53 MSK
 *
 * @author Michael Bel
 */

public class ShowDetails extends TmdbObject implements Serializable {

    @SerializedName("season_id")
    public int id;

    @SerializedName("name")
    public String name;

    //@SerializedName("created_by")

    //@SerializedName("episode_run_time")

    //@SerializedName("languages")

    @SerializedName("homepage")
    public String homepage;

    @SerializedName("in_production")
    public boolean inProduction;

    @SerializedName("backdrop_path")
    public String backdropPath;

    @SerializedName("poster_path")
    public String posterPath;

    @SerializedName("popularity")
    public float popularity;

    @SerializedName("vote_average")
    public float voteAverage;

    @SerializedName("overview")
    public String overview;

    @SerializedName("first_air_date")
    public String first_air_date;

    @SerializedName("last_air_date")
    public String last_air_date;

    //@SerializedName("networks")

    //@SerializedName("origin_country")

    //@SerializedName("genre_ids")

    @SerializedName("original_language")
    public String originalLanguage;

    @SerializedName("vote_count")
    public int voteCount;

    @SerializedName("original_name")
    public String originalName;

    @SerializedName("number_of_episodes")
    public int number_of_episodes;

    @SerializedName("number_of_seasons")
    public int number_of_seasons;

    //@SerializedName("production_companies")

    @SerializedName("seasons")
    public List<Season> seasons;

    @SerializedName("status")
    public String status;

    @SerializedName("type")
    public String type;
}