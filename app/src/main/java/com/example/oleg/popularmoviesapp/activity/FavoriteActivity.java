package com.example.oleg.popularmoviesapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.adapters.MovieAdapter;
import com.example.oleg.popularmoviesapp.data.MovieContract;
import com.example.oleg.popularmoviesapp.sync.SyncTask;
import com.example.oleg.popularmoviesapp.utilities.Constants;
import com.example.oleg.popularmoviesapp.utilities.GridAutoFitLayoutManager;

public class FavoriteActivity extends AppCompatActivity implements MovieAdapter.ListMovieClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private MovieAdapter mMovieAdapter;

    public static final int MOVIE_FAVORITE_LOADER = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        setTitle(R.string.app_title_favorite);


        //GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        GridAutoFitLayoutManager gridAutoFitLayoutManager = new GridAutoFitLayoutManager(this, Constants.COLUMN_WIDTH);
        RecyclerView mRecycleView = findViewById(R.id.rv_favorite);
        mRecycleView.setLayoutManager(gridAutoFitLayoutManager);
        mRecycleView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecycleView.setAdapter(mMovieAdapter);


        getSupportLoaderManager().initLoader(MOVIE_FAVORITE_LOADER, null, this);
    }

    @Override
    public void onClickMovieListener(int clickedMovieID) {
        Intent intent = new Intent(this, DetailActivity.class);
        Uri uriForMovieClicked = MovieContract.FavoriteEntry.buildFavoriteUriById(String.valueOf(clickedMovieID));
        intent.setData(uriForMovieClicked);
        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case MOVIE_FAVORITE_LOADER:
                return new CursorLoader(this,
                        MovieContract.FavoriteEntry.CONTENT_URI,
                        MainActivity.MAIN_MOVIES_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.getCount() == 0) {
            SyncTask.startImmediateSyncMovies(this);
        }
        mMovieAdapter.swapCursor(data);

        //

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }
}
