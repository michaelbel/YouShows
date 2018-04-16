package org.michaelbel.realm;

import org.michaelbel.app.NotTested;
import org.michaelbel.rest.model.Episode;
import org.michaelbel.rest.model.Season;
import org.michaelbel.rest.model.Show;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Date: 27 MAR 2018
 * Time: 19:19 MSK
 *
 * @author Michael Bel
 */

@SuppressWarnings("all")
public class RealmDb {

//--INSERT or UPDATE--------------------------------------------------------------------------------

    public static void insertOrUpdateShow(Show showParam) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showParam.showId).findFirst();
            if (show == null) {
                show = realm.createObject(Show.class);
            }
            show.showId = showParam.showId;
            show.name = showParam.name;
            show.posterPath = showParam.posterPath;
            show.backdropPath = showParam.backdropPath;
            show.popularity = showParam.popularity;
            show.voteAverage = showParam.voteAverage;
            show.voteCount = showParam.voteCount;
            show.overview = showParam.overview;
            show.firstAirDate = showParam.firstAirDate;
            show.originalLanguage = showParam.originalLanguage;
            show.originalName = showParam.originalName;
            show.homepage = showParam.homepage;
            show.inProduction = showParam.inProduction;
            show.lastAirDate = showParam.lastAirDate;
            show.numberSeasons = showParam.numberSeasons;
            show.numberEpisodes = showParam.numberEpisodes;
            show.status = showParam.status;
            show.type = showParam.type;

            show.viewsNumber = 1;

            realm.insertOrUpdate(show);
        });
        realmDb.close();
    }

    public static void insertOrUpdateSeason(int showId, Season seasonParam) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonParam.seasonId).findFirst();
            if (season == null) {
                season = realm.createObject(Season.class);
            }

            season.seasonId = seasonParam.seasonId;
            season.name = seasonParam.name;
            season.airDate = seasonParam.airDate;
            season.overview = seasonParam.overview;
            season.posterPath = seasonParam.posterPath;
            season.episodeCount = seasonParam.episodeCount; // todo: = 0
            season.seasonNumber = seasonParam.seasonNumber;
            season._id = seasonParam._id;

            season.showId = showId;
            season.episodeWatchedCount = 0;

            realm.insertOrUpdate(season);
        });
        realmDb.close();
    }

    public static void insertOrUpdateEpisode(int showId, int seasonId, Episode episodeParam) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Episode episode = realm.where(Episode.class).equalTo("showId", showId).equalTo("seasonId", seasonId).equalTo("episodeId", episodeParam.episodeId).findFirst();
            if (episode == null) {
                episode = realm.createObject(Episode.class);
            }

            episode.airDate = episodeParam.airDate;
            episode.episodeNumber = episodeParam.episodeNumber;
            episode.name = episodeParam.name;
            episode.overview = episodeParam.overview;
            episode.episodeId = episodeParam.episodeId;
            episode.production_code = episodeParam.production_code;
            episode.season_number = episodeParam.season_number;
            episode.still_path = episodeParam.still_path;
            episode.vote_average = episodeParam.vote_average;
            episode.vote_count = episodeParam.vote_count;

            episode.showId = showId;
            episode.seasonId = seasonId;

            realm.insertOrUpdate(episode);
        });
        realmDb.close();
    }

//--GET INSTANCE------------------------------------------------------------------------------------

    public static Show getShow(int showId) {
        Realm realmDb = Realm.getDefaultInstance();
        return realmDb.where(Show.class).equalTo("showId", showId).findFirst();
    }

    public static Season getSeason(int showId, int seasonId) {
        Realm realmDb = Realm.getDefaultInstance();
        return realmDb.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
    }

    public static Episode getEpisode(int showId, int seasonId, int episodeId) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(Episode.class).equalTo("showId", showId).equalTo("seasonId", seasonId).equalTo("episodeId", episodeId).findFirst();
    }

//--IS EXIST----------------------------------------------------------------------------------------

    public static boolean isShowExist(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null;
    }

    public static boolean isSeasonExist(int showId, int seasonId) {
        Realm realm = Realm.getDefaultInstance();
        Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
        return season != null;
    }

    public static boolean isEpisodeExist(int showId, int seasonId, int episodeId) {
        Realm realm = Realm.getDefaultInstance();
        Episode episode = realm.where(Episode.class)
                .equalTo("showId", showId)
                .equalTo("seasonId", seasonId)
                .equalTo("episodeId", episodeId)
                .findFirst();
        return episode != null;
    }

//--OTHER-------------------------------------------------------------------------------------------

//--Show--------------------------------------------------------------------------------------------

    public static boolean isShowFollow(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null && show.isFollow;
    }

    public static void followShow(int showId, boolean follow) {
        Realm realmDb = Realm.getDefaultInstance();
        Show show = realmDb.where(Show.class).equalTo("showId", showId).findFirst();
        realmDb.executeTransaction(realm -> {
            if (show != null) {
                show.isFollow = follow;
            }
        });
        realmDb.close();
    }

    public static int getShowViews(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.viewsNumber : 0;
    }

    public static void updateShowViewsCount(int showId) {
        int views = getShowViews(showId);
        int newViews = views + 1;

        Realm realmDb = Realm.getDefaultInstance();
        Show show = realmDb.where(Show.class).equalTo("showId", showId).findFirst();
        realmDb.executeTransaction(realm -> {
            if (show != null) {
                show.viewsNumber = newViews;
            }
        });
        realmDb.close();
    }

//--Season------------------------------------------------------------------------------------------

    public static int getSeasonsInShow(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show.numberSeasons;
    }

    public static int getWatchedSeasonsInShow(int showId) {
        int allSeasons = RealmDb.getSeasonsInShow(showId);
        int watchedSeasons = 0;

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Season> seasons = realm.where(Season.class).equalTo("showId", showId).findAll();

        for (Season s : seasons) {
            int watchedEpisodes = getWatchedEpisodesInSeason(showId, s.seasonId);

            if (s.episodeCount != 0) {
                if (watchedEpisodes == s.episodeCount) {
                    watchedSeasons = watchedSeasons + 1;
                }
            }
        }

        if (watchedSeasons == allSeasons) {
            return -1;
        } else {
            return watchedSeasons;
        }
    }

    public static void setSeasonEpisodesCount(int showId, int seasonId, int episodes) {
        Realm realmDb = Realm.getDefaultInstance();
        Season season = realmDb.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
        realmDb.executeTransaction(realm -> {
            if (season != null) {
                season.episodeCount = episodes;
            }
        });
        realmDb.close();
    }

    public static boolean isSeasonWatched(int showId, Season season) {
        return season.episodeCount == getWatchedEpisodesInSeason(showId, season.seasonId);
    }

//--Episode-----------------------------------------------------------------------------------------

    public static boolean isEpisodeWatched(int showId, int seasonId, int episodeId) {
        Realm realmDb = Realm.getDefaultInstance();
        Episode episode = realmDb.where(Episode.class)
                .equalTo("showId", showId)
                .equalTo("seasonId", seasonId)
                .equalTo("episodeId", episodeId)
                .findFirst();
        return episode != null && episode.isWatched;
    }

    public static void markEpisodeAsWatched(int showId, int seasonId, int episodeId, boolean watching) {
        Realm realmDb = Realm.getDefaultInstance();
        Episode episode = realmDb.where(Episode.class)
                .equalTo("showId", showId)
                .equalTo("seasonId", seasonId)
                .equalTo("episodeId", episodeId)
                .findFirst();
        realmDb.executeTransaction(realm -> {
            if (episode != null) {
                episode.isWatched = watching;
            }
        });
        realmDb.close();
    }

    public static int getWatchedEpisodesInSeason(int showId, int seasonId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Episode> episodes = realm.where(Episode.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findAll();

        int watchedCount = 0;
        for (Episode episode : episodes) {
            if (episode.isWatched) {
                watchedCount++;
            }
        }

        return watchedCount;
    }

    public static int getWatchedEpisodesInShow(int showId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Episode> episodes = realm.where(Episode.class).equalTo("showId", showId).findAll();

        int watchedCount = 0;
        for (Episode episode : episodes) {
            if (episode.isWatched) {
                watchedCount++;
            }
        }

        return watchedCount;
    }

//--Not Tested--------------------------------------------------------------------------------------

    @NotTested
    public static boolean getShowStatus(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null && show.inProduction;
    }

    @NotTested
    public static void removeShow(int showId) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.deleteFromRealm();
            }
        });
        realmDb.close();
    }

    @NotTested
    public static void removeSeason(int showId, int seasonId) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
            if (season != null) {
                season.deleteFromRealm();
            }
        });
        realmDb.close();
    }

    @NotTested
    public static void removeEpisode(int showId, int seasonId, int episodeId) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Episode episode = realm.where(Episode.class).equalTo("showId", showId).equalTo("seasonId", seasonId).equalTo("episodeId", episodeId).findFirst();
            if (episode != null) {
                episode.deleteFromRealm();
            }
        });
        realmDb.close();
    }

    @NotTested
    public static void removeEpisodes(int showId, int seasonId) {
        Realm realmDb = Realm.getDefaultInstance();

        realmDb.executeTransaction(realm -> {
            RealmResults<Episode> results = realmDb.where(Episode.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findAll();
            results.deleteAllFromRealm();
        });
        realmDb.close();
    }
}