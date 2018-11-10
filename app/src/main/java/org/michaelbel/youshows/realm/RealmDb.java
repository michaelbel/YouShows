package org.michaelbel.youshows.realm;

import org.michaelbel.youshows.model.SearchItem;
import org.michaelbel.youshows.rest.model.Company;
import org.michaelbel.youshows.rest.model.Episode;
import org.michaelbel.youshows.rest.model.Genre;
import org.michaelbel.youshows.rest.model.Season;
import org.michaelbel.youshows.rest.model.Show;
import org.michaelbel.material.annotation.Beta;
import org.michaelbel.material.annotation.NotTested;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

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
            season.episodeCount = seasonParam.episodeCount;
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
        Episode episode = realm.where(Episode.class).equalTo("showId", showId).equalTo("seasonId", seasonId).equalTo("episodeId", episodeId).findFirst();
        return episode != null;
    }

    public static boolean isWatchedEpisodesInShowExist(int showId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Episode> episodes = realm.where(Episode.class).equalTo("showId", showId).findAll();
        int watchedCount = 0;
        if (episodes.isLoaded()) {
            for (Episode episode : episodes) {
                if (episode.isWatched) {
                    watchedCount++;
                }
            }
        }
        return watchedCount > 0;
    }

//--REMOVE------------------------------------------------------------------------------------------

    public static void deleteAllWatchedShows() {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            RealmResults<Show> results = realm.where(Show.class).findAll();
            for (Show show : results) {
                if (isWatchedEpisodesInShowExist(show.showId)) {
                    show.progress = 0;
                    show.showProgress = 0F;
                    deleteAllWatchedEpisodesFromShow(show.showId);
                    //show.deleteFromRealm();
                }
            }
        });
        realmDb.close();
    }

    public static void deleteAllWatchedEpisodesFromShow(int showId) {
        Realm realmDb = Realm.getDefaultInstance();
        RealmResults<Episode> episodes = realmDb.where(Episode.class).equalTo("showId", showId).findAll();
        for (Episode episode : episodes) {
            episode.isWatched = false;
            episode.deleteFromRealm();
        }
    }

    public static void unfollowFromAllShows() {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            RealmResults<Show> results = realm.where(Show.class).equalTo("isFollow", true).findAll();
            for (Show show : results) {
                if (show != null) {
                    show.isFollow = false;
                }
            }
        });
        realmDb.close();
    }

    public static void removeShow(int showId) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.progress = 0;
                show.showProgress = 0F;
                deleteAllWatchedEpisodesFromShow(show.showId);
            }
        });
        realmDb.close();
    }

//--GET DATA----------------------------------------------------------------------------------------

    public static int getMyWatchedShowsCount() {
        List<Show> list = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Show> results = realm.where(Show.class).findAll();
        for (Show s : results) {
            if (RealmDb.isWatchedEpisodesInShowExist(s.showId)) {
                list.add(s);
            }
        }

        return list.size();
    }

    public static int getFollowingShowsCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Show> results = realm.where(Show.class).equalTo("isFollow", true).findAll();
        return results.size();
    }

    public static int getCachedShowsCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Show> results = realm.where(Show.class).findAll();
        return results.size();
    }

//--Show--------------------------------------------------------------------------------------------

    public static boolean isShowFollow(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null && show.isFollow;
    }

    public static void followShow(int showId, boolean follow) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
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

    public static int getShowEpisodesCount(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.numberEpisodes : 0;
    }

    public static int getShowProgress(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.progress : 0;
    }

    public static float getProgress(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.showProgress : 0;
    }

    public static void updateShowViewsCount(int showId) {
        int views = getShowViews(showId);
        int newViews = views + 1;

        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.viewsNumber = newViews;
            }
        });
        realmDb.close();
    }

    public static void updateProgress(int showId, float progress) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.progress = (int) progress;
                show.showProgress = progress;
            }
        });
        realmDb.close();
    }

    public static void updateLastChangesDate(int showId, String lastChangesDate) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.lastChangesDate = lastChangesDate;
            }
        });
        realmDb.close();
    }

    public static void updateStartFollowingDate(int showId, String date) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.startFollowingDate = date;
            }
        });
        realmDb.close();
    }

    public static void updateStatus(int showId, boolean status) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.inProduction = status;
            }
        });
        realmDb.close();
    }

    public static String getOriginalName(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.originalName : null;
    }

    public static void updateOriginalName(int showId, String name) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.originalName = name;
            }
        });
        realmDb.close();
    }

    public static String getStatus(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.status : null;
    }

    public static void updateStatus(int showId, String status) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.status = status;
            }
        });
        realmDb.close();
    }

    public static String getType(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.type : null;
    }

    public static void updateType(int showId, String type) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.type = type;
            }
        });
        realmDb.close();
    }

    public static String getFirstAirDate(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.firstAirDate : null;
    }

    public static String getLastAirDate(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.lastAirDate : null;
    }

    public static void updateLastAirDate(int showId, String lastDate) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.lastAirDate = lastDate;
            }
        });
        realmDb.close();
    }

    public static String getHomepage(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.homepage : null;
    }

    public static void updateHomepage(int showId, String homepage) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.homepage = homepage;
            }
        });
        realmDb.close();
    }

    public static void updateGenres(int showId, List<Genre> genres) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.genresString.clear();
                for (Genre genre : genres) {
                    show.genresString.add(genre.name);
                }
                realm.insertOrUpdate(show);
            }
        });
        realmDb.close();
    }

    public static RealmList<String> getGenres(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.genresString.size() == 0 ? null : show.genresString : null;
    }

    public static void updateCountries(int showId, List<String> countries) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.countriesString.clear();
                for (String country : countries) {
                    show.countriesString.add(country);
                }
                realm.insertOrUpdate(show);
            }
        });
        realmDb.close();
    }

    public static RealmList<String> getCountries(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.countriesString.size() == 0 ? null : show.countriesString : null;
    }

    public static void updateCompanies(int showId, List<Company> companies) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.companiesString.clear();
                for (Company company : companies) {
                    show.companiesString.add(company.name);
                }
                realm.insertOrUpdate(show);
            }
        });
        realmDb.close();
    }

    public static RealmList<String> getCompanies(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? (show.companiesString.size() != 0 ? show.companiesString : null) : null;
    }

    public static void updateSeasonsList(int showId, List<Season> seasons) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
            if (show != null) {
                show.seasonsList.clear();
                for (Season season : seasons) {
                    show.seasonsList.add(season);
                }
                realm.insertOrUpdate(show);
            }
        });
        realmDb.close();
    }

    public static RealmList<Season> getShowSeasons(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.seasonsList != null ? show.seasonsList : null : null;
    }

    public static boolean isShowSeasonsEmpty(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.seasonsList != null ? show.seasonsList.isEmpty() : true : true;
    }

//--Season------------------------------------------------------------------------------------------

    public static int getSeasonsInShow(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null ? show.numberSeasons : 0;
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
        realmDb.executeTransaction(realm -> {
            Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
            if (season != null) {
                season.episodeCount = episodes;
            }
        });
        realmDb.close();
    }

    public static boolean isSeasonWatched(int showId, int seasonId, int episodeCount) {
        return episodeCount == getWatchedEpisodesInSeason(showId, seasonId);
    }

    public static String getSeasonOverview(int showId, int seasonId) {
        Realm realm = Realm.getDefaultInstance();
        Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
        return season != null ? season.overview : null;
    }

    public static String getSeasonAirDate(int showId, int seasonId) {
        Realm realm = Realm.getDefaultInstance();
        Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
        return season != null ? season.airDate : null;
    }

    public static int getSeasonEpisodesCount(int showId, int seasonId) {
        Realm realm = Realm.getDefaultInstance();
        Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
        return season != null ? season.episodeCount : 0;
    }

    public static void updateEpisodesList(int showId, int seasonId, List<Episode> episodes) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
            if (season != null) {
                season.episodesList.clear();
                for (Episode episode : episodes) {
                    season.episodesList.add(episode);
                }
                realm.insertOrUpdate(season);
            }
        });
        realmDb.close();
    }

    public static RealmList<Episode> getSeasonEpisodes(int showId, int seasonId) {
        Realm realm = Realm.getDefaultInstance();
        Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
        return season != null ? season.episodesList != null ? season.episodesList : null : null;
    }

    @Beta
    public static List<Episode> getShowEpisodes(int showId) {
        Realm realm = Realm.getDefaultInstance();
        List<Episode> episodes = new ArrayList<>(realm.where(Episode.class).equalTo("showId", showId).findAll());
        return episodes;
    }

    public static boolean isSeasonEpisodesEmpty(int showId, int seasonId) {
        Realm realm = Realm.getDefaultInstance();
        Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
        return season != null ? season.episodesList != null ? season.episodesList.isEmpty() : true : true;
    }

    public static void setSeasonScrollPosition(int showId, int seasonId, int scrollPosition) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
            if (season != null) {
                season.scrollPosition = scrollPosition;
            }
        });
        realmDb.close();
    }

    public static int getSeasonScrollPosition(int showId, int seasonId) {
        Realm realm = Realm.getDefaultInstance();
        Season season = realm.where(Season.class).equalTo("showId", showId).equalTo("seasonId", seasonId).findFirst();
        return season != null ? season.scrollPosition : 0;
    }

//--Episode-----------------------------------------------------------------------------------------

    public static boolean isEpisodeWatched(int showId, int seasonId, int episodeId) {
        Realm realmDb = Realm.getDefaultInstance();
        Episode episode = realmDb.where(Episode.class).equalTo("showId", showId).equalTo("seasonId", seasonId).equalTo("episodeId", episodeId).findFirst();
        return episode != null && episode.isWatched;
    }

    public static void markEpisodeAsWatched(int showId, int seasonId, int episodeId, boolean watching) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Episode episode = realm.where(Episode.class).equalTo("showId", showId).equalTo("seasonId", seasonId).equalTo("episodeId", episodeId).findFirst();
            if (episode != null) {
                episode.isWatched = watching;
            }
        });
        realmDb.close();
    }

    public static void setEpisodeWatchDate(int showId, int seasonId, int episodeId, String date) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            Episode episode = realm.where(Episode.class).equalTo("showId", showId).equalTo("seasonId", seasonId).equalTo("episodeId", episodeId).findFirst();
            if (episode != null) {
                episode.watchDate = date;
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
    public static Season getFirstSeasonInShow(int showId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Season> seasons = realm.where(Season.class).equalTo("showId", showId).findAll();
        return seasons.get(0);
    }

    @NotTested
    public static boolean getShowStatus(int showId) {
        Realm realm = Realm.getDefaultInstance();
        Show show = realm.where(Show.class).equalTo("showId", showId).findFirst();
        return show != null && show.inProduction;
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

//--SEARCH HISTORY----------------------------------------------------------------------------------

    public static void insertSearchItem(SearchItem searchItem) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            SearchItem item = realm.createObject(SearchItem.class);
            item.query = searchItem.query;
            item.date = searchItem.date;
            item.voice = searchItem.voice;
            realm.insertOrUpdate(item);
        });
        realmDb.close();
    }

    public static void removeSearchItem(String date) {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            SearchItem item = realm.where(SearchItem.class).equalTo("date", date).findFirst();
            if (item != null) {
                item.deleteFromRealm();
            }
        });
        realmDb.close();
    }

    public static boolean isSearchItemsExist() {
        Realm realmDb = Realm.getDefaultInstance();
        RealmResults<SearchItem> searchItems = realmDb.where(SearchItem.class).findAll();
        return searchItems.size() > 0;
    }

    public static void clearSearchHistory() {
        Realm realmDb = Realm.getDefaultInstance();
        realmDb.executeTransaction(realm -> {
            RealmResults<SearchItem> results = realm.where(SearchItem.class).findAll();
            results.deleteAllFromRealm();
        });
        realmDb.close();
    }

    public static RealmResults<SearchItem> getSearchItems() {
        Realm realmDb = Realm.getDefaultInstance();
        RealmResults<SearchItem> results = realmDb.where(SearchItem.class).sort("date", Sort.DESCENDING).findAll();
        return results;
    }
}