package com.example.oleg.popularmoviesapp.utilities;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.oleg.popularmoviesapp.data.MovieContract;
import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.model.Review;
import com.example.oleg.popularmoviesapp.model.Video;

import java.util.List;

public class ContentValuesUtils {
    public static ContentValues[] getMovieContentValuesFromJson(List<Movie> movieList) {

        int contentSize = movieList.size();
        ContentValues[] moviesContentValues = new ContentValues[contentSize];

        for (int i = 0; i < contentSize; i++) {

            Movie movie = movieList.get(i);

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_ID, movie.getId());
            movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.getPopularity());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());

            moviesContentValues[i] = movieValues;
        }

        return moviesContentValues;
    }

    public static ContentValues[] getVideoContentValuesFromJson(List<Video> videoList, String movieId) {

        int contentSize = videoList.size();
        ContentValues[] videosContentValues = new ContentValues[contentSize];

        for (int i = 0; i < contentSize; i++) {

            Video video = videoList.get(i);

            ContentValues videoValues = new ContentValues();
            videoValues.put(MovieContract.VideoEntry.COLUMN_ID, video.getId());
            videoValues.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID, movieId);
            videoValues.put(MovieContract.VideoEntry.COLUMN_PATH, video.getKey());
            videoValues.put(MovieContract.VideoEntry.COLUMN_LANGUAGE, video.getLanguage());
            videoValues.put(MovieContract.VideoEntry.COLUMN_NAME, video.getName());
            videoValues.put(MovieContract.VideoEntry.COLUMN_SITE, video.getSite());
            videoValues.put(MovieContract.VideoEntry.COLUMN_SIZE, video.getSize());
            videoValues.put(MovieContract.VideoEntry.COLUMN_TYPE, video.getType());

            videosContentValues[i] = videoValues;
        }

        return videosContentValues;
    }

    public static ContentValues[] getReviewContentValuesFromJson(List<Review> reviewList, String movieId) {

        int contentSize = reviewList.size();
        ContentValues[] reviewsContentValues = new ContentValues[contentSize];

        for (int i = 0; i < contentSize; i++) {

            Review review = reviewList.get(i);

            ContentValues reviewValues = new ContentValues();
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_ID, review.getId());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_URL, review.getUrl());

            reviewsContentValues[i] = reviewValues;
        }

        return reviewsContentValues;
    }

}
