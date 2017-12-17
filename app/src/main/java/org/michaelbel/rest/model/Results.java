package org.michaelbel.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Results {

    @SerializedName("results")
    public List<TVShow> list;
}