package org.michaelbel.ui.view;

import android.support.annotation.IntDef;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Date: 01 APR 2018
 * Time: 18:16 MSK
 *
 * @author Michael Bel
 */

@Retention(RetentionPolicy.CLASS)
@IntDef({
    EmptyViewMode.MODE_NO_CONNECTION,
    EmptyViewMode.MODE_NO_SHOWS,
    EmptyViewMode.MODE_NO_RESULTS
})
@Documented
@EmptyViewMode
public @interface EmptyViewMode {
    int MODE_NO_CONNECTION = 0;
    int MODE_NO_SHOWS = 1;
    int MODE_NO_RESULTS = 2;
}