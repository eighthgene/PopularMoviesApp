package com.example.oleg.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.oleg.popularmoviesapp.data.MovieContract.MovieEntry;
import com.example.oleg.popularmoviesapp.data.MovieContract.VideoEntry;
import com.example.oleg.popularmoviesapp.data.MovieContract.ReviewEntry;
import com.example.oleg.popularmoviesapp.data.MovieContract.FavoriteEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 11;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

                        MovieEntry._ID                          + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_ID                    + " INTEGER NOT NULL, " +
                        MovieEntry.COLUMN_ORIGINAL_TITLE        + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_TITLE                 + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_POPULARITY            + " REAL NOT NULL, " +
                        MovieEntry.COLUMN_VOTE_AVERAGE          + " REAL NOT NULL, " +
                        MovieEntry.COLUMN_VOTE_COUNT            + " INTEGER NOT NULL, " +
                        MovieEntry.COLUMN_POSTER_PATH           + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_BACKDROP_PATH         + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_OVERVIEW              + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_RELEASE_DATE          + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_FAVORITE              + " INTEGER DEFAULT 0, " +

                        " UNIQUE (" + MovieEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FAVORITE_TABLE =

                "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +

                        FavoriteEntry._ID                          + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoriteEntry.COLUMN_ID                    + " INTEGER NOT NULL, " +
                        FavoriteEntry.COLUMN_ORIGINAL_TITLE        + " TEXT NOT NULL, " +
                        FavoriteEntry.COLUMN_TITLE                 + " TEXT NOT NULL, " +
                        FavoriteEntry.COLUMN_POPULARITY            + " REAL NOT NULL, " +
                        FavoriteEntry.COLUMN_VOTE_AVERAGE          + " REAL NOT NULL, " +
                        FavoriteEntry.COLUMN_VOTE_COUNT            + " INTEGER NOT NULL, " +
                        FavoriteEntry.COLUMN_POSTER_PATH           + " TEXT NOT NULL, " +
                        FavoriteEntry.COLUMN_BACKDROP_PATH         + " TEXT NOT NULL, " +
                        FavoriteEntry.COLUMN_OVERVIEW              + " TEXT NOT NULL, " +
                        FavoriteEntry.COLUMN_RELEASE_DATE          + " TEXT NOT NULL, " +

                        " UNIQUE (" + FavoriteEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_VIDEO_TABLE =

                "CREATE TABLE " + MovieContract.VideoEntry.TABLE_NAME + " (" +

                        VideoEntry._ID                                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        VideoEntry.COLUMN_ID                              + " TEXT NOT NULL, " +
                        VideoEntry.COLUMN_MOVIE_ID                        + " INTEGER NOT NULL, " +
                        VideoEntry.COLUMN_PATH                            + " TEXT NOT NULL, " +
                        VideoEntry.COLUMN_LANGUAGE                        + " TEXT NOT NULL, " +
                        VideoEntry.COLUMN_NAME                            + " TEXT NOT NULL, " +
                        VideoEntry.COLUMN_SITE                            + " TEXT NOT NULL, " +
                        VideoEntry.COLUMN_SIZE                            + " INTEGER NOT NULL, " +
                        VideoEntry.COLUMN_TYPE                            + " TEXT NOT NULL, " +

                        " UNIQUE (" + VideoEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEW_TABLE =

                "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +

                        ReviewEntry._ID                 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ReviewEntry.COLUMN_ID           + " TEXT NOT NULL, " +
                        ReviewEntry.COLUMN_MOVIE_ID     + " INTEGER NOT NULL, " +
                        ReviewEntry.COLUMN_AUTHOR       + " TEXT NOT NULL, " +
                        ReviewEntry.COLUMN_CONTENT      + " TEXT NOT NULL, " +
                        ReviewEntry.COLUMN_URL          + " TEXT NOT NULL, " +

                        " UNIQUE (" + ReviewEntry.COLUMN_ID + ") ON CONFLICT REPLACE);";


        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_VIDEO_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
            onCreate(db);
    }
}
