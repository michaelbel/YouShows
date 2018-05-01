package org.michaelbel.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Date: 26 APR 2018
 * Time: 15:42 MSK
 *
 * @author Michael Bel
 */

public class Changelog {

    public String version;
    public int build;
    public String date;
    public List<String> changes = new ArrayList<>();

    public Changelog(String version, int build, String date, String... changes) {
        this.version = version;
        this.build = build;
        this.date = date;
        Collections.addAll(this.changes, changes);
    }
}