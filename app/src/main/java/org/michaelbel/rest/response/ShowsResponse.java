package org.michaelbel.rest.response;

import com.google.gson.annotations.SerializedName;

import org.michaelbel.rest.TmdbObject;
import org.michaelbel.rest.model.Show;

import java.util.List;

/**
 * Date: 19 MAR 2018
 * Time: 14:50 MSK
 *
 * @author Michael Bel
 */

public class ShowsResponse extends TmdbObject {

    @SerializedName("page")
    public int page;

    @SerializedName("total_pages")
    public int totalPages;

    @SerializedName("total_results")
    public int totalResults;

    @SerializedName("results")
    public List<Show> shows;
}