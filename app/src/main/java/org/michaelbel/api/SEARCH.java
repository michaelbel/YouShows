package org.michaelbel.api;

import org.michaelbel.model.Results;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

@SuppressWarnings("all")
public interface SEARCH {

    @GET("search/tv?")
    Call<Results> searchSeries(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );
}