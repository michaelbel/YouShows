package org.michaelbel.app.eventbus;

import org.michaelbel.app.NotTested;

public class Events {

    public static class UpdateSeasonView {

        public UpdateSeasonView() {}
    }

    @NotTested
    public static class DeleteSeries {

        private String seriesTitle;

        public DeleteSeries(String title) {
            seriesTitle = title;
        }

        public String getSeriesTitle() {
            return seriesTitle;
        }
    }

    @NotTested
    public static class UpdateSeries {

        private String seriesTitle;

        public UpdateSeries(String title) {
            seriesTitle = title;
        }

        public String getSeriesTitle() {
            return seriesTitle;
        }
    }

    @NotTested
    public static class AddSeries {

        public AddSeries() {}
    }
}