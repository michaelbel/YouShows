package org.michaelbel.seriespicker;

import org.michaelbel.util.AppUtils;

public class Url {

    public static final String GSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String TMDB_API = "https://api.themoviedb.org/3/";
    public static final String TMDB_API_KEY = AppUtils.getProperty("TMDbApiKey");
    public static final String TMDB_API_TOKEN = AppUtils.getProperty("TMDbApiToken");

    public static final String en_US = "en-US";
    public static final String de_DE = "de-DE";
    public static final String ru_RU = "ru-RU";
    public static final String pt_BR = "pt-BR";
    public static final String pt_PT = "pt-PT";
    public static final String pt_US = "pt-US";
}