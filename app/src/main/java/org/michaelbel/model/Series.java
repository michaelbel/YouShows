package org.michaelbel.model;

import java.io.Serializable;

public class Series implements Serializable {

    public int id;
    public String title;
    public int seasonCount;
    public int episodeCount;
    public String posterPath;
    public String backdropPath;

    public long seriesCountBottom;

    public Series() {}

    public Series(long count) {
        this.title = "";
        this.seriesCountBottom = count;
    }
}