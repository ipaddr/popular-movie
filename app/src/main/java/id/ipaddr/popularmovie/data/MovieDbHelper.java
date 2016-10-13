package id.ipaddr.popularmovie.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.ipaddr.popularmovie.Movie;

import id.ipaddr.popularmovie.data.MovieContract.MovieEntry;

/**
 * Created by ulfiaizzati on 10/13/16.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieEntry.COLUMN_NAME_ORIGINAL_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_NAME_IMAGE_THUMBNAIL + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_NAME_PLOT_SYNOPSIS + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_NAME_VOTE_AVERAGE + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_NAME_RELEASE_DATE + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
