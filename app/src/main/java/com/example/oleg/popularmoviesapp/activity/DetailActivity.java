package com.example.oleg.popularmoviesapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oleg.popularmoviesapp.Loaders.VideoLoader;
import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.model.Video;
import com.example.oleg.popularmoviesapp.utilities.Constants;
import com.example.oleg.popularmoviesapp.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE = "extra_movie";
    private Movie movie;
    private ImageView mBackdrop;
    private ImageView mPoster;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView mOriginalTitle;
    private TextView mMovieRating;
    private TextView mVoteCounting;
    private RatingBar mRatingBar;
    private TextView mDateRelease;
    private TextView mOverview;
    private Toolbar mToolBar;
    private List<Video> mVideos = new ArrayList<>();

    private static final int LOADER_VIDEO_ID = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent intent = getIntent();
        if (intent != null) {
            movie = intent.getExtras().getParcelable(EXTRA_MOVIE);
        }

        findAllViews();
        populateUI(movie);
        startLoadVideos();

    }

    private void findAllViews() {
        mBackdrop = findViewById(R.id.iv_detail_backdrop);
        mPoster = findViewById(R.id.iv_detail_poster);
        mCollapsingToolbarLayout = findViewById(R.id.ctb_movie_detail);
        mOriginalTitle = findViewById(R.id.tv_original_title);
        mMovieRating = findViewById(R.id.tv_movie_rating);
        mVoteCounting = findViewById(R.id.tv_vote_counting);
        mRatingBar = findViewById(R.id.rb_movie_rating);
        mDateRelease = findViewById(R.id.tv_date_release);
        mOverview = findViewById(R.id.tv_detail_overview);
        mToolBar = findViewById(R.id.tb_movie_detail);

    }

    private void startLoadVideos() {
        int loaderId = LOADER_VIDEO_ID;
        LoaderManager.LoaderCallbacks callbacks = DetailActivity.this;
        Bundle bundleForLoader = new Bundle();

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Video>> videoLoader = loaderManager.getLoader(loaderId);
        if (videoLoader == null) {
            loaderManager.initLoader(loaderId, bundleForLoader, callbacks).forceLoad();
        } else {
            loaderManager.restartLoader(loaderId, bundleForLoader, callbacks).forceLoad();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateUI(Movie movie) {
        Glide.with(this)
                .load(NetworkUtils.buildImageUrl(Constants.KEY_IMAGE_SIZE_W500, movie.getBackdropPath()).toString())
                .transition(withCrossFade())
                .into(mBackdrop);

        Glide.with(this)
                .load(NetworkUtils.buildImageUrl(Constants.KEY_IMAGE_SIZE_W500, movie.getPosterPath()).toString())
                .transition(withCrossFade())
                .into(mPoster);

        mCollapsingToolbarLayout.setTitle(movie.getTitle());

        String originalTitle = "\"" + movie.getOriginalTitle() + "\"";
        mOriginalTitle.setText(originalTitle);

        mMovieRating.setText(String.valueOf(movie.getVoteAverage()));
        mRatingBar.setRating(convertToStarRating(movie.getVoteAverage()));
        mVoteCounting.setText(String.valueOf(movie.getVoteCount() + " " + getString(R.string.detail_votes_label)));
        mDateRelease.setText(movie.getReleaseDate());

        mOverview.setText(movie.getOverview());

        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private float convertToStarRating(float num) {
        return ((num * 10) * 5) / 100;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case LOADER_VIDEO_ID:
                return new VideoLoader(this, movie.getId());
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_VIDEO_ID:
                if (data != null) {
                    mVideos.addAll((List<Video>) data);
                }
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
