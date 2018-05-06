package org.michaelbel.app;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Date: 26 APR 2018
 * Time: 15:42 MSK
 *
 * @author Michael Bel
 */

public class Changelog {

    @SerializedName("version")
    public String version;

    @SerializedName("build")
    public int build;

    @SerializedName("date")
    public String date;

    @SerializedName("changes")
    public ArrayList<String> changes = new ArrayList<>();
}