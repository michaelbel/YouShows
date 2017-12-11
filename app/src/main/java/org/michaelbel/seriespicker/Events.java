package org.michaelbel.seriespicker;

@SuppressWarnings("all")
public class Events {

    public static class DeleteSeries {

        private String seriesTitle;

        public DeleteSeries(String title) {
            seriesTitle = title;
        }

        public String getSeriesTitle() {
            return seriesTitle;
        }
    }

    public static class UpdateSeries {

        private String seriesTitle;

        public UpdateSeries(String title) {
            seriesTitle = title;
        }

        public String getSeriesTitle() {
            return seriesTitle;
        }
    }

    public static class AddSeries {

        public AddSeries() {}
    }
}