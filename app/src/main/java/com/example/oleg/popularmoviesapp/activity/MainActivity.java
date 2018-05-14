package com.example.oleg.popularmoviesapp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.adapters.MovieAdapter;
import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.utulities.MovieLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks,
        MovieAdapter.ListMovieClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView mRecycleView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwiOnRefreshListener;
    private TextView mErrorMessage;
    public static final int MOVIE_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findAllViews();
        checkConnectionInternet();

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

        //RecycleView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(loaderManager, this);
        mRecycleView.setAdapter(mMovieAdapter);

        mSwiOnRefreshListener.setOnRefreshListener(this);

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
        //TODO
    }

    @Override
    public void onClickMovieListener(int clickedMovieIndex) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movieList.get(clickedMovieIndex));
        startActivity(intent);
    }


    @Override
    public void onRefresh() {
        mSwiOnRefreshListener.setRefreshing(true);
        if (checkConnectionInternet() && movieList.size() == 0) {
            Toast.makeText(this, "Refreshed", Toast.LENGTH_LONG).show();
            LoaderManager loaderManager = getSupportLoaderManager();
            LoaderManager.LoaderCallbacks callbacks = MainActivity.this;
            loaderManager.initLoader(MOVIE_LOADER_ID, new Bundle(), callbacks).forceLoad();
        }
        mSwiOnRefreshListener.setRefreshing(false);
    }

    private void findAllViews() {
        mSwiOnRefreshListener = findViewById(R.id.sr_movies);
        mRecycleView = findViewById(R.id.rv_movies);
        mErrorMessage = findViewById(R.id.tv_error_message);
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean checkConnectionInternet() {
        if (!isOnline() && movieList.size() == 0) {
            mErrorMessage.setVisibility(View.VISIBLE);
            return false;
        } else if (!isOnline() && movieList.size() != 0) {
            Toast.makeText(getBaseContext(), "No internet connection", Toast.LENGTH_LONG).show();
            return false;
        } else if (isOnline()) {
            mErrorMessage.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.sortPopularMenuItem):

                break;

            case (R.id.sortHighestMenuItem):
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}