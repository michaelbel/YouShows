package org.michaelbel.youshows.rest;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.michaelbel.youshows.AndroidExtensions;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Date: 27 MAR 2018
 * Time: 21:03 MSK
 *
 * @author Michael Bel
 */

public class ApiFactory {

    public static final String TMDB_API_ENDPOINT = "https://api.themoviedb.org/3/";
    public static final String TMDB_IMAGE = "http://image.tmdb.org/t/p/%s/%s";
    public static final String TMDB_MOVIE = "https://themoviedb.org/movie/%d";

    private static final String TMDB_EN_US = "en-US";
    private static final String TMDB_RU_RU = "ru-RU";

    public static final int TRAKT_API_VERSION = 2;
    public static final String TRAKT_API_ENDPOINT = "https://api-v2launch.trakt.tv/";

    private static Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    @NonNull
    private static Retrofit getRetrofit(String baseUrl) {
        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GSON))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    }

    public static <S> S createService(Class<S> serviceClass, String baseUrl) {
        return getRetrofit(baseUrl).create(serviceClass);
    }

    public static String getLanguage() {
        if (AndroidExtensions.getLanguage() == 0) {
            return TMDB_EN_US;
        } else if (AndroidExtensions.getLanguage() == 1) {
            return TMDB_RU_RU;
        } else {
            return TMDB_EN_US;
        }
    }
}