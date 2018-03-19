package org.michaelbel.rest.model;

import com.google.gson.annotations.SerializedName;

import org.michaelbel.rest.TmdbObject;

import java.io.Serializable;

public class Show extends TmdbObject implements Serializable {

    @SerializedName("id")
    public int id;

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

    //@SerializedName("origin_country")

    //@SerializedName("genre_ids")

    @SerializedName("original_language")
    public String originalLanguage;

    @SerializedName("vote_count")
    public int voteCount;

    @SerializedName("name")
    public String name;

    @SerializedName("original_name")
    public String originalName;
}