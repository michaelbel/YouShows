package org.michaelbel.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.michaelbel.rest.model.Series;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION_CURRENT = 2;
    public static final String DATABASE_NAME = "SERIES_DATABASE";

    private static final String SERIES_TABLE = "SERIES";
    private static final String KEY_SERIES_ID = "_id";
    private static final String KEY_SERIES_TITLE = "title";
    private static final String KEY_SERIES_SEASON_COUNT = "seasons";
    private static final String KEY_SERIES_EPISODE_COUNT = "episodes";
    private static final String KEY_SERIES_POSTER_PATH = "poster_path";
    private static final String KEY_SERIES_BACKDROP_PATH = "backdrop_path";

    private static volatile DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }

        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION_CURRENT);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + SERIES_TABLE + " ("
                + KEY_SERIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_SERIES_TITLE + " TEXT, "
                + KEY_SERIES_SEASON_COUNT + " INTEGER, "
                + KEY_SERIES_EPISODE_COUNT + " INTEGER, "
                + KEY_SERIES_POSTER_PATH + " TEXT, "
                + KEY_SERIES_BACKDROP_PATH + " TEXT) "
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SERIES_TABLE);
        onCreate(db);
    }

    public void addSeries(Series series) {
        ContentValues values = new ContentValues();
        values.put(KEY_SERIES_TITLE, series.title);
        values.put(KEY_SERIES_SEASON_COUNT, series.seasonCount);
        values.put(KEY_SERIES_EPISODE_COUNT, series.episodeCount);
        values.put(KEY_SERIES_POSTER_PATH, series.posterPath);
        values.put(KEY_SERIES_BACKDROP_PATH, series.backdropPath);

        SQLiteDatabase database = this.getWritableDatabase();
        database.insert(SERIES_TABLE, null, values);
        database.close();
    }

    public void removeSeries(Series series) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(SERIES_TABLE, KEY_SERIES_ID + " = ?", new String[]{String.valueOf(series.id)});
        database.close();
    }

    public void updateSeries(Series series) {
        ContentValues values = new ContentValues();
        values.put(KEY_SERIES_TITLE, series.title);
        values.put(KEY_SERIES_SEASON_COUNT, series.seasonCount);
        values.put(KEY_SERIES_EPISODE_COUNT, series.episodeCount);
        values.put(KEY_SERIES_POSTER_PATH, series.posterPath);
        values.put(KEY_SERIES_BACKDROP_PATH, series.backdropPath);

        SQLiteDatabase database = this.getWritableDatabase();
        database.update(SERIES_TABLE, values, KEY_SERIES_ID + " = ? ", new String[]{String.valueOf(series.id)});
        database.close();
    }

    public void clearTable() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + SERIES_TABLE);
        database.close();
    }

    public long getCount() {
        SQLiteDatabase database = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(database, SERIES_TABLE);
        database.close();
        return count;
    }

    public Series getSeries(int id) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + SERIES_TABLE + " WHERE " +
                KEY_SERIES_ID + " = ?", new String[]{String.valueOf(id)});
        cursor.moveToFirst();

        Series series = new Series();
        series.id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SERIES_ID));
        series.title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SERIES_TITLE));
        series.seasonCount = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SERIES_SEASON_COUNT));
        series.episodeCount = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SERIES_EPISODE_COUNT));
        series.posterPath = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SERIES_POSTER_PATH));
        series.backdropPath = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SERIES_BACKDROP_PATH));

        cursor.close();
        database.close();
        return series;
    }

    public List<Series> getList() {
        List<Series> list = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();

        try (Cursor cursor = database.rawQuery("SELECT * FROM " + SERIES_TABLE, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Series series = new Series();
                    series.id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SERIES_ID));
                    series.title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SERIES_TITLE));
                    series.seasonCount = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SERIES_SEASON_COUNT));
                    series.episodeCount = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SERIES_EPISODE_COUNT));
                    series.posterPath = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SERIES_POSTER_PATH));
                    series.backdropPath = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SERIES_BACKDROP_PATH));

                    list.add(series);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        database.close();
        return list;
    }
}