package com.example.oleg.popularmoviesapp.utulities;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.oleg.popularmoviesapp.activity.MainActivity;
import com.example.oleg.popularmoviesapp.model.Movie;

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

        URL movieRequestUrl = MovieNetworkUtils.buildMovieUrl(MainActivity.currentSortOrder);
        try {
            String jsonResponse = MovieNetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            movieList = MovieJsonUtils.parseMoviesJson(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieList;
    }

}
