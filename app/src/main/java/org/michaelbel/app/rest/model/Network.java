package org.michaelbel.app.rest.model;

import com.google.gson.annotations.SerializedName;

import org.michaelbel.app.rest.TmdbObject;

import java.io.Serializable;

/**
 * Date: 02 APR 2018
 * Time: 00:30 MSK
 *
 * @author Michael Bel
 */

public class Network extends TmdbObject implements Serializable {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;
}