package com.example.oleg.popularmoviesapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.adapters.MovieAdapter;
import com.example.oleg.popularmoviesapp.data.MovieContract;
import com.example.oleg.popularmoviesapp.sync.SyncTask;
import com.example.oleg.popularmoviesapp.utilities.Constants;
import com.example.oleg.popularmoviesapp.utilities.GridAutoFitLayoutManager;
import com.example.oleg.popularmoviesapp.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.ListMovieClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String[] MAIN_MOVIES_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_POSTER_PATH = 6;



    private static final int MOVIE_LOADER_ID = 0;
    //public static final int ViDEO_LOADER_ID = 1;
    //public static final int REVIEW_LOADER_ID = 2;

    //private List<Movie> movieList = new ArrayList<>();
    private RecyclerView mRecycleView;
    private static int mPosition = RecyclerView.NO_POSITION;
    private MovieAdapter mMovieAdapter;
    //private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwiOnRefreshListener;
    private TextView mErrorMessage;
    private ImageView mLostConnectionImageView;
    private ImageView mSwipeDownImageView;
    private SharedPreferences sharedPreferences;
    public static String currentSortOrder = Constants.MOVIE_SORT_POPULAR;
    private int mDataSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findAllViews();
        loadSortOrder();
        startLoadMovies();

        //RecycleView
        //GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        GridAutoFitLayoutManager autoFitLayoutManager = new GridAutoFitLayoutManager(this, Constants.COLUMN_WIDTH);
        mRecycleView.setLayoutManager(autoFitLayoutManager);
        mRecycleView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter( this);
        mRecycleView.setAdapter(mMovieAdapter);
        mSwiOnRefreshListener.setOnRefreshListener(this);

        //startLoadMovies();

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        switch (id) {
            case MOVIE_LOADER_ID:
                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;

                return new CursorLoader(this,
                        movieQueryUri,
                        MAIN_MOVIES_PROJECTION,
                        null,
                        null,
                        null);

            //case ViDEO_LOADER_ID:

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        if (data.getCount() == 0 && isOnline()) {
            SyncTask.startImmediateSyncMovies(this);
        }
        mMovieAdapter.swapCursor(data);

        //
        Log.i(TAG, "RV position " + mPosition);

        //if (data.getCount() != 0) showWeatherDataView();
        mDataSize = data.getCount();
        checkConnectionInternet();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }


    @Override
    public void onClickMovieListener(int clickedMovieID) {
        Intent intent = new Intent(this, DetailActivity.class);
        Uri uriForMovieClicked = MovieContract.MovieEntry.buildMovieUriById(clickedMovieID);
        Log.i(TAG, "Uri: " + uriForMovieClicked);
        intent.setData(uriForMovieClicked);
        //intent.putExtra(DetailActivity.EXTRA_MOVIE, movieList.get(clickedMovieIndex));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mSwiOnRefreshListener.setRefreshing(true);
        if (checkConnectionInternet() && mDataSize == 0) {
            Toast.makeText(this, R.string.refreshed, Toast.LENGTH_LONG).show();
            LoaderManager loaderManager = getSupportLoaderManager();
            LoaderManager.LoaderCallbacks callbacks = MainActivity.this;
            loaderManager.initLoader(MOVIE_LOADER_ID, new Bundle(), callbacks).forceLoad();
        }
        else if(!checkConnectionInternet() && mDataSize == 0){
            Toast.makeText(this, R.string.toast_no_internet_connection, Toast.LENGTH_LONG).show();
        }
        mSwiOnRefreshListener.setRefreshing(false);
    }

    private void findAllViews() {
        mSwiOnRefreshListener = findViewById(R.id.sr_movies);
        mRecycleView = findViewById(R.id.rv_movies);
        mErrorMessage = findViewById(R.id.tv_error_message);
        //mProgressBar = findViewById(R.id.pb_empty_list_movies);
        mLostConnectionImageView = findViewById(R.id.iv_lost_connection);
        mSwipeDownImageView = findViewById(R.id.iv_swipe_down);
    }


     private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean checkConnectionInternet() {
        if (!isOnline() && mDataSize == 0) {
            mErrorMessage.setVisibility(View.VISIBLE);
            mLostConnectionImageView.setVisibility(View.VISIBLE);
            mSwipeDownImageView.setVisibility(View.VISIBLE);
            return false;
        } else if (!isOnline() && mDataSize != 0) {
            Toast.makeText(getBaseContext(), R.string.toast_no_internet_connection, Toast.LENGTH_LONG).show();
            return false;
        } else if (isOnline()) {
            mErrorMessage.setVisibility(View.GONE);
            mLostConnectionImageView.setVisibility(View.GONE);
            mSwipeDownImageView.setVisibility(View.GONE);
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
            case (R.id.favoriteMovies):
                Intent favoriteIntent = new Intent(this, FavoriteActivity.class);
                startActivity(favoriteIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void changeSaveMovieOrder(String sortOrder) {
        if (!currentSortOrder.equals(sortOrder)) {
            clearMovieList();
            saveSortOrder(sortOrder);
            currentSortOrder = sortOrder;
            NetworkUtils.page = 1;
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
        setAppTitle();
    }


    private void startLoadMovies() {
        int loaderId = MOVIE_LOADER_ID;
        LoaderManager.LoaderCallbacks callbacks = MainActivity.this;
        Bundle bundleForLoader = new Bundle();

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Cursor> movieLoader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (movieLoader == null) {
            loaderManager.initLoader(loaderId, bundleForLoader, callbacks).forceLoad();
        } else {
            loaderManager.restartLoader(loaderId, bundleForLoader, callbacks).forceLoad();
        }
        //getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    private void clearMovieList() {
        int row_deleted = getBaseContext().getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null);
        Log.i(TAG, "clearMovieList rows deleted: " + row_deleted);
        mMovieAdapter.clearMovieList();
        mDataSize = 0;
        checkConnectionInternet();
    }


    private void setAppTitle() {
        if (currentSortOrder.equals(Constants.MOVIE_SORT_POPULAR)) {
            setTitle(R.string.app_title_popular);
        } else if (currentSortOrder.equals(Constants.MOVIE_SORT_TOP_RATED)) {
            setTitle(R.string.app_title_top_rated);
        }
    }

}