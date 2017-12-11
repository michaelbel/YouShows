package org.michaelbel.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Results {

    @SerializedName("results")
    public List<TVShow> list;
}