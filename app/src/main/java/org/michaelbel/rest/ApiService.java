package org.michaelbel.rest;

import org.michaelbel.rest.model.SeasonDetails;
import org.michaelbel.rest.model.ShowDetails;
import org.michaelbel.rest.response.ShowsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Date: 17 MAR 2018
 * Time: 15:51 MSK
 *
 * @author Michael Bel
 */

public interface ApiService {

    @GET("search/tv?")
    Call<ShowsResponse> search(
        @Query("api_key") String apiKey,
        @Query("language") String lang,
        @Query("query") String query,
        @Query("page") int page
        /*@Query("first_air_date_year") int year*/
    );

    @GET("tv/on_the_air?")
    Call<ShowsResponse> trending(
        @Query("api_key") String apiKey,
        @Query("language") String lang,
        @Query("page") int page
    );

    @GET("tv/airing_today?")
    Call<ShowsResponse> nowPlaying(
        @Query("api_key") String apiKey,
        @Query("language") String lang,
        @Query("page") int page
    );

    @GET("tv/popular?")
    Call<ShowsResponse> popular(
        @Query("api_key") String apiKey,
        @Query("language") String lang,
        @Query("page") int page
    );

    @GET("tv/top_rated?")
    Call<ShowsResponse> topRated(
        @Query("api_key") String apiKey,
        @Query("language") String lang,
        @Query("page") int page
    );

    @GET("tv/{tv_id}?")
    Call<ShowDetails> details(
        @Path("tv_id") int id,
        @Query("api_key") String apiKey,
        @Query("language") String lang
        //@Query("append_to_response") String append_to_response
    );

    @GET("tv/{tv_id}/season/{seasonNumber}?")
    Call<SeasonDetails> season(
        @Path("tv_id") int id,
        @Path("seasonNumber") int number,
        @Query("api_key") String apiKey,
        @Query("language") String lang
        //@Query("append_to_response") String append_to_response
    );

    /*@GET("search/show?")
    Call<List<Show>> search(
        @Header("trakt-api-key") String apiKey,
        @Header("trakt-api-version") int apiVersion,
        @Query("query") String query
    );*/

    /*@GET("shows/trending?")
    Call<ShowResponse> trending(
        @Header("trakt-api-version") int apiVersion,
        @Header("trakt-api-key") String apiKey
    );*/

    @GET("show/{show_id}/next_episode?")
    Call<?> nextEpisode(
        @Header("trakt-api-key") String apiKey,
        @Header("trakt-api-version") int apiVersion,
        @Path("show_id") int id
    );
}