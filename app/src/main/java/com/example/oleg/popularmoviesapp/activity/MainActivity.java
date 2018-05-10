package com.example.oleg.popularmoviesapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.adapters.MovieAdapter;
import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.utulities.MovieLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks,
        MovieAdapter.ListMovieClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView mRecycleView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;

    public static final int MOVIE_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Loader
        int loaderId = MOVIE_LOADER_ID;

        LoaderManager.LoaderCallbacks callbacks = MainActivity.this;
        Bundle bundleForLoader = new Bundle();

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Movie>> movieLoader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (movieLoader == null) {
            loaderManager.initLoader(loaderId, bundleForLoader, callbacks).forceLoad();
        } else {
            loaderManager.restartLoader(loaderId, bundleForLoader, callbacks).forceLoad();
        }

        mRecycleView = findViewById(R.id.rv_movies);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(loaderManager, this);
        mRecycleView.setAdapter(mMovieAdapter);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        return new MovieLoader(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        movieList.addAll((ArrayList<Movie>) data);
        mMovieAdapter.setMovieList((ArrayList<Movie>) data);
    }


    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onClickMovieListener(int clickedMovieIndex) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movieList.get(clickedMovieIndex));
        startActivity(intent);
    }
}
