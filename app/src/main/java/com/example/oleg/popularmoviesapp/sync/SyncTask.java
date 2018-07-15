package com.example.oleg.popularmoviesapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.oleg.popularmoviesapp.activity.MainActivity;
import com.example.oleg.popularmoviesapp.data.MovieContract;
import com.example.oleg.popularmoviesapp.json.MovieJsonUtils;
import com.example.oleg.popularmoviesapp.json.ReviewJsonUtils;
import com.example.oleg.popularmoviesapp.json.VideoJsonUtils;
import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.model.Review;
import com.example.oleg.popularmoviesapp.model.Video;
import com.example.oleg.popularmoviesapp.utilities.ContentValuesUtils;
import com.example.oleg.popularmoviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SyncTask {
    private static final String TAG = SyncTask.class.getSimpleName();

    public static void startImmediateSyncMovies(final Context context) {
        Intent intentToSyncImmediately = new Intent(context, MovieSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

    synchronized public static void syncMovies(Context context) {
        List<Movie> movieList = new ArrayList<>();

        try {
            URL movieRequestUrl = NetworkUtils.buildMovieUrl(MainActivity.currentSortOrder);
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                movieList = MovieJsonUtils.parseMoviesJson(jsonResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ContentValues[] moviesValues = ContentValuesUtils.getMovieContentValuesFromJson(movieList);


            if (moviesValues != null && moviesValues.length != 0) {
                ContentResolver moviesContentResolver = context.getContentResolver();
                int moviesInserted = moviesContentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI,
                        moviesValues);

                Log.i(TAG, "inserted movies to DB: " + moviesInserted);
                Log.i(TAG, "Page loading " + NetworkUtils.page);
                if (moviesInserted != 0) {
                    NetworkUtils.page++;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized public static void syncVideos(Context context, String id) {
        List<Video> videos = new ArrayList<>();
        if (id == null) return;
        URL videosRequestUrl = NetworkUtils.buildVideoURL(id);
        try {
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(videosRequestUrl);
            videos = VideoJsonUtils.parseVideoJson(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContentValues[] videoValues = ContentValuesUtils.getVideoContentValuesFromJson(videos, id);

        if (videoValues != null && videoValues.length != 0) {
            ContentResolver contentResolver = context.getContentResolver();
            int videosInserted = contentResolver.bulkInsert(MovieContract.VideoEntry.CONTENT_URI,
                    videoValues);

            Log.i(TAG, "inserted movies to DB: " + videosInserted);
        }
    }

    synchronized public static void syncReviews(Context context, String id){
        List<Review> reviews = new ArrayList<>();
        if (id == null) return;
        URL reviewRequestUrl = NetworkUtils.buildReviewURL(id);
        Log.i(TAG, "reviewRequestUrl: " + reviewRequestUrl.toString());
        try {
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(reviewRequestUrl);
            reviews = ReviewJsonUtils.parseReviewJson(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Reviews number: " + reviews.size());

        ContentValues[] reviewValues = ContentValuesUtils.getReviewContentValuesFromJson(reviews, id);
        Log.i(TAG, "reviewValues size: " + reviewValues.length);

        if (reviewValues != null && reviewValues.length != 0) {
            ContentResolver contentResolver = context.getContentResolver();
            int reviewsInserted = contentResolver.bulkInsert(MovieContract.ReviewEntry.CONTENT_URI,
                    reviewValues);

            Log.i(TAG, "inserted reviews to DB: " + reviewsInserted);
        }
    }
}