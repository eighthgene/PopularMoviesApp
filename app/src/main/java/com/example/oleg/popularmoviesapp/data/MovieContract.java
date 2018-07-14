package com.example.oleg.popularmoviesapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.oleg.popularmoviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movies";
    public static final String PATH_VIDEO = "videos";
    public static final String PATH_REVIEW = "reviews";
    public static final String PATH_FAVORITE = "favorite";

    public static final String PATH_REVIEW_BY_ID = "reviews/id";
    public static final String PATH_REVIEW_ID = "id";

    public static class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_VOTE_COUNT = "voteCount";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_FAVORITE = "favorite";



        public static Uri buildMovieUriById(int movieId){
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(movieId))
                    .build();
        }
    }

    public static class FavoriteEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_VOTE_COUNT = "voteCount";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";


        public static Uri buildFavoriteUriById(String movieId){
            return CONTENT_URI.buildUpon()
                    .appendPath(movieId)
                    .build();
        }
    }

    public static class VideoEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_VIDEO)
                .build();

        public static final String TABLE_NAME = "videos";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MOVIE_ID = "id_movie";
        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";

        public static Uri buildVideoUriById(String movieId){
            return CONTENT_URI.buildUpon()
                    .appendPath(movieId)
                    .build();
        }

    }

    public static class ReviewEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEW)
                .build();

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MOVIE_ID = "id_movie";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_URL = "url";

        public static Uri buildReviewUriById(String reviewId){
            return CONTENT_URI.buildUpon()
                    .appendPath(reviewId)
                    .build();
        }


        public static Uri buildReviewUriByIdReview(String reviewId){
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_REVIEW_ID)
                    .appendPath(reviewId)
                    .build();
        }


    }



}
