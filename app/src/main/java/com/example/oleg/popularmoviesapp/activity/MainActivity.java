package com.example.oleg.popularmoviesapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.oleg.popularmoviesapp.utilities.Constants;
import com.example.oleg.popularmoviesapp.utilities.MovieLoader;
import com.example.oleg.popularmoviesapp.utilities.MovieNetworkUtils;

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
    private LoaderManager loaderManager;
    private SharedPreferences sharedPreferences;
    public static String currentSortOrder = Constants.MOVIE_SORT_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findAllViews();
        checkConnectionInternet();
        loadSortOrder();
        startLoadMovies();

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
        mProgressBar.setVisibility(View.INVISIBLE);
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
            Toast.makeText(this, R.string.refreshed, Toast.LENGTH_LONG).show();
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
        mProgressBar = findViewById(R.id.pb_empty_list_movies);
    }


    private boolean isOnline() {
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
            Toast.makeText(getBaseContext(), R.string.toast_no_internet_connection, Toast.LENGTH_LONG).show();
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
        if (currentSortOrder.equals(Constants.MOVIE_SORT_POPULAR)) {
            menu.findItem(R.id.sortPopularMenuItem).setChecked(true);
        } else if (currentSortOrder.equals(Constants.MOVIE_SORT_TOP_RATED)) {
            menu.findItem(R.id.sortHighestMenuItem).setChecked(true);
        }
        setAppTitle();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.sortPopularMenuItem):
                item.setChecked(true);
                changeSaveMovieOrder(Constants.MOVIE_SORT_POPULAR);
                break;

            case (R.id.sortHighestMenuItem):
                item.setChecked(true);
                changeSaveMovieOrder(Constants.MOVIE_SORT_TOP_RATED);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void changeSaveMovieOrder(String sortOrder) {
        if (!currentSortOrder.equals(sortOrder)) {
            clearMovieList();
            mProgressBar.setVisibility(View.VISIBLE);
            saveSortOrder(sortOrder);
            currentSortOrder = sortOrder;
            MovieNetworkUtils.page = 1;
            setAppTitle();
            startLoadMovies();
        }
    }

    private void saveSortOrder(String sortOrder) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_MOVIE_SORT_ORDER, sortOrder);
        editor.apply();
    }

    private void loadSortOrder() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        currentSortOrder = sharedPreferences.getString(Constants.KEY_MOVIE_SORT_ORDER,
                Constants.MOVIE_SORT_POPULAR);
    }


    private void startLoadMovies() {
        int loaderId = MOVIE_LOADER_ID;
        LoaderManager.LoaderCallbacks callbacks = MainActivity.this;
        Bundle bundleForLoader = new Bundle();

        loaderManager = getSupportLoaderManager();
        Loader<List<Movie>> movieLoader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (movieLoader == null) {
            loaderManager.initLoader(loaderId, bundleForLoader, callbacks).forceLoad();
        } else {
            loaderManager.restartLoader(loaderId, bundleForLoader, callbacks).forceLoad();
        }
    }

    private void clearMovieList() {
        mMovieAdapter.clearMovieList();
        movieList.clear();
    }


    private void setAppTitle() {
        if (currentSortOrder.equals(Constants.MOVIE_SORT_POPULAR)) {
            setTitle(R.string.app_title_popular);
        } else if (currentSortOrder.equals(Constants.MOVIE_SORT_TOP_RATED)) {
            setTitle(R.string.app_title_top_rated);
        }
    }
}