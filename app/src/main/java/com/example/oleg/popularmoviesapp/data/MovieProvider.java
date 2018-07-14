package com.example.oleg.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.utilities.NetworkUtils;

public class MovieProvider extends ContentProvider {
    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;

    public static final int CODE_VIDEO = 200;
    public static final int CODE_VIDEO_WITH_ID = 201;

    public static final int CODE_REVIEW = 300;
    public static final int CODE_REVIEW_WITH_ID_MOVIE = 301;
    public static final int CODE_REVIEW_WITH_ID = 302;

    public static final int CODE_FAVORITE = 400;
    public static final int CODE_FAVORITE_WITH_ID = 401;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG = MovieProvider.class.getSimpleName();
    private MovieDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_VIDEO, CODE_VIDEO);
        matcher.addURI(authority, MovieContract.PATH_VIDEO + "/#", CODE_VIDEO_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_REVIEW, CODE_REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", CODE_REVIEW_WITH_ID_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_REVIEW_BY_ID + "/*", CODE_REVIEW_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_FAVORITE, CODE_FAVORITE);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/#", CODE_FAVORITE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MOVIE_WITH_ID:

                String id = uri.getPathSegments().get(1);
                String[] selectionArguments = new String[]{id};

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;

            case CODE_VIDEO:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_VIDEO_WITH_ID:
                String idVideo = uri.getPathSegments().get(1);
                Log.i(TAG, "ID -> : " + idVideo);
                String[] selectionArgumentsVideo = new String[]{idVideo};

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.VideoEntry.TABLE_NAME,
                        projection,
                        MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArgumentsVideo,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_REVIEW:
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_REVIEW_WITH_ID_MOVIE:
                String idMovieReview = uri.getPathSegments().get(1);
                Log.i(TAG, "ID Review -> : " + idMovieReview);
                String[] selectionArgumentsReview = new String[]{idMovieReview};

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArgumentsReview,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_REVIEW_WITH_ID:
                String idReview = uri.getPathSegments().get(2);
                Log.i(TAG, "ID Review -> : " + idReview);
                String[] selectArgs = new String[]{idReview};

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        MovieContract.ReviewEntry.COLUMN_ID + " = ? ",
                        selectArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_FAVORITE:

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_FAVORITE_WITH_ID:

                String idFavorite = uri.getPathSegments().get(1);
                String[] selectArgsFavorite = new String[]{idFavorite};

                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavoriteEntry.COLUMN_ID + " = ? ",
                        selectArgsFavorite,
                        null,
                        null,
                        sortOrder);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                int rowsInsertedMovie = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInsertedMovie++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInsertedMovie > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInsertedMovie;

            case CODE_VIDEO:
                int rowsInsertedVideo = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        Log.i(TAG, "Value: " + value.toString());
                        long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInsertedVideo++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInsertedVideo > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInsertedVideo;

            case CODE_REVIEW:
                int rowsInsertedReviews = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        Log.i(TAG, "Value review: " + value.toString());
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInsertedReviews++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInsertedReviews > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInsertedReviews;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    //TODO for Favorite movie
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                long _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME,
                        null,
                        values);
                if (_id > 0) {
                    returnUri = MovieContract.FavoriteEntry.buildFavoriteUriById(movieId);
                } else {
                    throw new SQLException("Filed to insert to database uri: " + uri);
                }
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                String[] selectAgsFavorite = new String[]{movieId};
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME,
                        MovieContract.FavoriteEntry.COLUMN_ID + " = ?",
                        selectAgsFavorite);
                break;

            case CODE_MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_WITH_ID:
                String idMovie = uri.getPathSegments().get(1);
                String[] selectionArguments = new String[]{idMovie};
                rowsUpdated =
                        db.update(MovieContract.MovieEntry.TABLE_NAME,
                                values,
                                MovieContract.MovieEntry.COLUMN_ID + " = ?",
                                selectionArguments);
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
        }
        return rowsUpdated;
    }

    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
