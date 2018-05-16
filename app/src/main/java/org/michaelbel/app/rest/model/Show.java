package org.michaelbel.app.rest.model;

import com.google.gson.annotations.SerializedName;

import org.michaelbel.app.realm.Realmed;
import org.michaelbel.app.rest.Response;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.RealmClass;

/**
 * Date: 27 MAR 2018
 * Time: 20:53 MSK
 *
 * @author Michael Bel
 */

@SuppressWarnings("all")
@RealmClass
public class Show extends RealmObject /*implements Parcelable*/ {

    @Response
    @SerializedName("id")
    public int showId;

    @Response
    @SerializedName("name")
    public String name;

    @Response
    @SerializedName("poster_path")
    public String posterPath;

    @Response
    @SerializedName("backdrop_path")
    public String backdropPath;

    @Response
    @SerializedName("popularity")
    public float popularity;

    @Response
    @SerializedName("vote_average")
    public float voteAverage;

    @Response
    @SerializedName("overview")
    public String overview;

    @Response
    @SerializedName("first_air_date")
    public String firstAirDate;

    @Response
    @SerializedName("original_language")
    public String originalLanguage;

    @Response
    @SerializedName("vote_count")
    public int voteCount;

    @Response
    @SerializedName("original_name")
    public String originalName;

    @Ignore
    @Response
    @SerializedName("origin_country")
    public List<String> countries;

    @Ignore
    @Response
    @SerializedName("genre_ids")
    public List<Integer> genreIds;

    @Ignore
    @SerializedName("created_by")
    public List<Creator> crew;

    @Ignore
    @SerializedName("episode_run_time")
    public List<Integer> episodeRunTime;

    @Ignore
    @SerializedName("genres")
    public List<Genre> genres;

    @SerializedName("homepage")
    public String homepage;

    @SerializedName("in_production")
    public boolean inProduction;

    @Ignore
    @SerializedName("languages")
    public List<String> languages;

    @SerializedName("last_air_date")
    public String lastAirDate;

    @Ignore
    @SerializedName("networks")
    public List<Network> networks;

    @SerializedName("number_of_episodes")
    public int numberEpisodes;

    @SerializedName("number_of_seasons")
    public int numberSeasons;

    @Ignore
    @SerializedName("production_companies")
    public List<Company> companies;

    @Ignore
    @SerializedName("seasons")
    public List<Season> seasons;

    @SerializedName("status")
    public String status;

    @SerializedName("type")
    public String type;

    @Realmed
    public boolean isFollow;

    @Realmed
    public int viewsNumber;

    @Realmed
    public int lastWatchDate;

    @Realmed
    public int progress;

    @Realmed
    public RealmList<String> genresString;

    @Realmed
    public RealmList<String> countriesString;

    @Realmed
    public RealmList<String> companiesString;

    @Realmed
    public float showProgress;

    //@Realmed
    //public String lastViewedTime;

    /*@New
    @Realmed
    public String genresString;

    @New
    @Realmed
    public String counriesString;

    @New
    @Realmed
    public String companiesString;*/

    /*public Show() {}

    protected Show(Parcel in) {
        showId = in.readInt();
        name = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        popularity = in.readFloat();
        voteAverage = in.readFloat();
        overview = in.readString();
        firstAirDate = in.readString();
        originalLanguage = in.readString();
        voteCount = in.readInt();
        originalName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(showId);
        dest.writeString(name);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeFloat(popularity);
        dest.writeFloat(voteAverage);
        dest.writeString(overview);
        dest.writeString(firstAirDate);
        dest.writeString(originalLanguage);
        dest.writeInt(voteCount);
        dest.writeString(originalName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Show> CREATOR = new Parcelable.Creator<Show>() {
        @Override
        public Show createFromParcel(Parcel in) {
            return new Show(in);
        }

        @Override
        public Show[] newArray(int size) {
            return new Show[size];
        }
    };*/
}