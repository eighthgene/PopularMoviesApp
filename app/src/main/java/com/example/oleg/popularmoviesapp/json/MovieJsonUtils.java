package com.example.oleg.popularmoviesapp.json;

import android.util.Log;

import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieJsonUtils {
    private static final String TAG = MovieJsonUtils.class.getSimpleName();

    public static List<Movie> parseMoviesJson(String json) {
        List<Movie> movieList = new ArrayList<>();
        try {
            //Initialization JSONObject from String -> json
            JSONObject moviesJson = new JSONObject(json);

            JSONArray moviesArray = moviesJson.optJSONArray(Constants.KEY_RESULTS);
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject jsonMovie = moviesArray.optJSONObject(i);
                Movie movie = new Movie(
                        jsonMovie.optInt(Constants.KEY_MOVIE_ID),
                        jsonMovie.optString(Constants.KEY_MOVIE_ORIGINAL_TITLE),
                        jsonMovie.optString(Constants.KEY_MOVIE_TITLE),
                        (float) jsonMovie.optDouble(Constants.KEY_MOVIE_POPULARITY),
                        (float) jsonMovie.optDouble(Constants.KEY_MOVIE_VOTE_AVERAGE),
                        jsonMovie.optInt(Constants.KEY_MOVIE_VOTE_COUNT),
                        jsonMovie.optString(Constants.KEY_MOVIE_POSTER_PATH),
                        jsonMovie.optString(Constants.KEY_MOVIE_BACKDROP_PATH),
                        jsonMovie.optString(Constants.KEY_MOVIE_OVERVIEW),
                        jsonMovie.optString(Constants.KEY_MOVIE_RELEASE_DATE)
                );
                movieList.add(movie);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
        }
        return movieList;
    }
}
