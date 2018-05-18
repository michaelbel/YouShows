package org.michaelbel.app.model;

/**
 * Date: 08 MAY 2018
 * Time: 14:20 MSK
 *
 * @author Michael Bel
 */

public class Source {

    public String url;
    public String name;
    public String license;

    public Source(String name, String url, String license) {
        this.url = url;
        this.name = name;
        this.license = license;
    }
}