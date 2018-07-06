package com.example.oleg.popularmoviesapp.Loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import com.example.oleg.popularmoviesapp.activity.MainActivity;
import com.example.oleg.popularmoviesapp.json.MovieJsonUtils;
import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    public MovieLoader(Context context) {
        super(context);
    }


    @Override
    public List<Movie> loadInBackground() {
        List<Movie> movieList = new ArrayList<>();

        URL movieRequestUrl = NetworkUtils.buildMovieUrl(MainActivity.currentSortOrder);
        try {
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            movieList = MovieJsonUtils.parseMoviesJson(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieList;
    }

}
