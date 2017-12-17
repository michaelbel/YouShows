package org.michaelbel.rest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("all")
public class TVShow implements Serializable {

    @SerializedName("id")
    public int id;

    @SerializedName("backdrop_path")
    public String backdropPath;

    @SerializedName("poster_path")
    public String posterPath;
}