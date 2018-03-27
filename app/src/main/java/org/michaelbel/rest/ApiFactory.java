package org.michaelbel.rest;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.michaelbel.rest.del.Url;
import org.michaelbel.util.AppUtils;

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

    public static final int TRAKT_API_VERSION = 2;
    public static final String TRAKT_API_ENDPOINT = "https://api-v2launch.trakt.tv/";
    public static final String TRAKT_CLIENT_ID = AppUtils.getProperty("tract_client_id");

    private static Gson GSON = new GsonBuilder().setDateFormat(Url.GSON_DATE_FORMAT).create();

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
}