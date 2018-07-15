package com.example.oleg.popularmoviesapp.activity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.adapters.ReviewAdapter;
import com.example.oleg.popularmoviesapp.adapters.VideoAdapter;
import com.example.oleg.popularmoviesapp.data.MovieContract;
import com.example.oleg.popularmoviesapp.databinding.ActivityDetailBinding;
import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.sync.SyncTask;
import com.example.oleg.popularmoviesapp.utilities.Constants;
import com.example.oleg.popularmoviesapp.utilities.NetworkUtils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        VideoAdapter.VideoClickListener, ReviewAdapter.ReviewClickListener {

    private static final String TAG = DetailActivity.class.getSimpleName();

    public static final String LOADER_TYPE_VIDEOS = "videos";
    public static final String LOADER_TYPE_REVIEWS = "reviews";
    public static final String LOADER_TYPE_FAVORITE = "favorite";

    public static final String FAB_STATE_ENABLED = "1";
    public static final String FAB_STATE_DISABLED = "0";

    private static String FAB_STATE_CURRENT;

    private static final String[] DETAIL_MOVIES_PROJECTION = {
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
    public static final int INDEX_MOVIE_ORIGINAL_TITLE = 1;
    public static final int INDEX_MOVIE_TITLE = 2;
    public static final int INDEX_MOVIE_POPULARITY = 3;
    public static final int INDEX_MOVIE_VOTE_AVERAGE = 4;
    public static final int INDEX_MOVIE_VOTE_COUNT = 5;
    public static final int INDEX_MOVIE_POSTER_PATH = 6;
    public static final int INDEX_MOVIE_BACKDROP_PATH = 7;
    public static final int INDEX_MOVIE_OVERVIEW = 8;
    public static final int INDEX_MOVIE_RELEASE_DATE = 9;

    private static final String[] DETAIL_FAVORITE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_ID,
    };

    private static final String[] DETAIL_VIDEO_PROJECTION = {
            MovieContract.VideoEntry._ID,
            MovieContract.VideoEntry.COLUMN_PATH,
            MovieContract.VideoEntry.COLUMN_NAME,
            MovieContract.VideoEntry.COLUMN_MOVIE_ID,
    };

    public static final int INDEX_VIDEO_NAME = 2;
    public static final int INDEX_VIDEO_KEY = 1;

    private static final String[] DETAIL_REVIEW_PROJECTION = {
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
            MovieContract.ReviewEntry.COLUMN_ID,
    };

    public static final int INDEX_REVIEW_CONTENT = 1;
    public static final int INDEX_REVIEW_ID = 2;


    private static final int ID_DETAIL_MOVIE_LOADER = 300;
    private static final int ID_DETAIL_VIDEO_LOADER = 400;
    private static final int ID_DETAIL_REVIEW_LOADER = 500;
    private static final int ID_DETAIL_FAVORITE_LOADER = 600;

    private ActivityDetailBinding binding;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;

    private Uri mUri;
    private String mIdMovie;
    private Cursor mCursorMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        //findAllViews();
        initVideoRecycleView();
        initReviewRecycleView();


        binding.fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncTaskLoader syncTask = new SyncTaskLoader(getBaseContext(), mIdMovie, LOADER_TYPE_FAVORITE);
                if (syncTask.getStatus().equals(AsyncTask.Status.FINISHED) ||
                        syncTask.getStatus().equals(AsyncTask.Status.PENDING)) {
                    syncTask.execute();
                } else {
                    Toast.makeText(DetailActivity.this, "Wait please...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //startLoadVideos();
        getSupportLoaderManager().initLoader(ID_DETAIL_MOVIE_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_DETAIL_VIDEO_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_DETAIL_REVIEW_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_DETAIL_FAVORITE_LOADER, null, this);


    }

    private void initReviewRecycleView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        binding.rvReviews.setLayoutManager(layoutManager);
        binding.rvReviews.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter(this);
        binding.rvReviews.setAdapter(mReviewAdapter);
    }

    private void initVideoRecycleView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        binding.rvVideos.setLayoutManager(layoutManager);
        binding.rvVideos.setHasFixedSize(true);
        mVideoAdapter = new VideoAdapter(this);
        binding.rvVideos.setAdapter(mVideoAdapter);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void populateUI(Movie movie) {
        Log.i(TAG, "populateUI: Started");

        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_backdrop_error)
                .placeholder(R.drawable.ic_slow_loading)
                .priority(Priority.HIGH);

        Log.i(TAG, "Movie: " + movie.getBackdropPath());
        Glide.with(this)
                .load(NetworkUtils.buildImageUrl(Constants.KEY_IMAGE_SIZE_W500, movie.getBackdropPath()).toString())
                .apply(options)
                .transition(withCrossFade())
                .into(binding.ivDetailBackdrop);

        Glide.with(this)
                .load(NetworkUtils.buildImageUrl(Constants.KEY_IMAGE_SIZE_W500, movie.getPosterPath()).toString())
                .apply(options)
                .transition(withCrossFade())
                .into(binding.ivDetailPoster);

        binding.ctbMovieDetail.setTitle(movie.getTitle());

        String originalTitle = "\"" + movie.getOriginalTitle() + "\"";
        binding.tvOriginalTitle.setText(originalTitle);

        binding.tvMovieRating.setText(String.valueOf(movie.getVoteAverage()));
        binding.rbMovieRating.setRating(convertToStarRating(movie.getVoteAverage()));
        binding.tvVoteCounting.setText(String.valueOf(movie.getVoteCount() + " " + getString(R.string.detail_votes_label)));
        binding.tvDateRelease.setText(movie.getReleaseDate());

        binding.tvDetailOverview.setText(movie.getOverview());

        setSupportActionBar(binding.tbMovieDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void changeStateFavorite(String state) {
        switch (state) {
            case FAB_STATE_ENABLED:
                binding.fabFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_enabled));
                FAB_STATE_CURRENT = FAB_STATE_ENABLED;

                break;
            case FAB_STATE_DISABLED:
                binding.fabFavorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));
                FAB_STATE_CURRENT = FAB_STATE_DISABLED;
                break;
        }
    }

    private float convertToStarRating(float num) {
        return ((num * 10) * 5) / 100;
    }

    boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case ID_DETAIL_MOVIE_LOADER:
                return new CursorLoader(this,
                        mUri,
                        DETAIL_MOVIES_PROJECTION,
                        null,
                        null,
                        null);

            case ID_DETAIL_FAVORITE_LOADER:
                Uri favoriteUri = MovieContract.FavoriteEntry.buildFavoriteUriById(mUri.getPathSegments().get(1));
                return new CursorLoader(this,
                        favoriteUri,
                        DETAIL_FAVORITE_PROJECTION,
                        null,
                        null,
                        null);


            case ID_DETAIL_VIDEO_LOADER:
                mIdMovie = mUri.getPathSegments().get(1);
                Uri uriVideo = MovieContract.VideoEntry.buildVideoUriById(mIdMovie);

                return new CursorLoader(this,
                        uriVideo,
                        DETAIL_VIDEO_PROJECTION,
                        null,
                        null,
                        null);

            case ID_DETAIL_REVIEW_LOADER:
                Log.i(TAG, "onCreateLoader Review");
                if (mIdMovie == null) {
                    mIdMovie = mUri.getPathSegments().get(1);
                }
                Uri uriReview = MovieContract.ReviewEntry.buildReviewUriById(mIdMovie);

                return new CursorLoader(this,
                        uriReview,
                        DETAIL_REVIEW_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //

        int id = loader.getId();
        switch (id) {
            case ID_DETAIL_MOVIE_LOADER:
                boolean cursorHasValidData = false;
                Log.i(TAG, "testik" + data.getCount());
                if (data != null && data.moveToFirst()) {
                    /* We have valid data, continue on to bind the data to the UI */
                    cursorHasValidData = true;
                }

                if (!cursorHasValidData) {
                    /* No data to display, simply return and do nothing */
                    return;
                }

                // Save movie data for Favorite movies
                mCursorMovie = data;

                Movie movie = new Movie(
                        data.getInt(INDEX_MOVIE_ID),
                        data.getString(INDEX_MOVIE_ORIGINAL_TITLE),
                        data.getString(INDEX_MOVIE_TITLE),
                        data.getFloat(INDEX_MOVIE_POPULARITY),
                        data.getFloat(INDEX_MOVIE_VOTE_AVERAGE),
                        data.getInt(INDEX_MOVIE_VOTE_COUNT),
                        data.getString(INDEX_MOVIE_POSTER_PATH),
                        data.getString(INDEX_MOVIE_BACKDROP_PATH),
                        data.getString(INDEX_MOVIE_OVERVIEW),
                        data.getString(INDEX_MOVIE_RELEASE_DATE)
                );


                populateUI(movie);
                break;

            case ID_DETAIL_FAVORITE_LOADER:
                if (data.getCount() == 1) {
                    changeStateFavorite(FAB_STATE_ENABLED);
                    Log.i(TAG, "onLoadFinished FAB_STATE_ENABLED ");
                } else
                    changeStateFavorite(FAB_STATE_DISABLED);
                Log.i(TAG, "onLoadFinished FAB_STATE_DISABLED ");
                break;

            case ID_DETAIL_VIDEO_LOADER:
                Log.i(TAG, "selected rows: " + data.getCount());
                boolean cursorHasValidDataVideo = false;
                if (data != null && data.moveToFirst()) {
                    /* We have valid data, continue on to bind the data to the UI */
                    cursorHasValidDataVideo = true;
                }

                if (!cursorHasValidDataVideo) {
                    /* No data to display, simply return and do nothing */
                    if (isOnline()) {
                        Log.i(TAG, "Loading videos...... ");
                        SyncTaskLoader syncTask = new SyncTaskLoader(this, mIdMovie, LOADER_TYPE_VIDEOS);
                        syncTask.execute();
                    }
                } else {
                    Log.i(TAG, "swap cursor");
                    mVideoAdapter.swapCursor(data);
                }
                break;

            case ID_DETAIL_REVIEW_LOADER:
                Log.i(TAG, "onLoadFinished Review: " + data.getCount());
                boolean cursorHasValidDataReview = false;
                if (data != null && data.moveToFirst()) {
                    /* We have valid data, continue on to bind the data to the UI */
                    cursorHasValidDataReview = true;
                }
                if (!cursorHasValidDataReview) {
                    /* No data to display, simply return and do nothing */
                    if (isOnline()) {
                        Log.i(TAG, "Loading reviews...... ");
                        SyncTaskLoader syncTask = new SyncTaskLoader(this, mIdMovie, LOADER_TYPE_REVIEWS);
                        syncTask.execute();
                    }
                } else {
                    Log.i(TAG, "swap cursor");
                    mReviewAdapter.swapCursor(data);
                }
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mReviewAdapter.swapCursor(null);
    }

    @Override
    public void onClickVideoListener(String videoKey) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(NetworkUtils.YOUTUBE_VIDEO_URL + videoKey));
        try {
            this.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            this.startActivity(webIntent);
        }
    }

    @Override
    public void onclickReviewListener(String idReview) {
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewUriByIdReview(idReview);
        Intent reviewIntent = new Intent(this, ReviewActivity.class);
        reviewIntent.setData(reviewUri);
        startActivity(reviewIntent);
    }


    public class SyncTaskLoader extends AsyncTask<Void, String, Void> {
        private String mMovieId;
        private final Context mContext;
        private String mTypeTask;

        private SyncTaskLoader(Context context, String movieId, String typeTask) {
            this.mMovieId = movieId;
            this.mContext = context;
            this.mTypeTask = typeTask;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch (mTypeTask) {
                case LOADER_TYPE_VIDEOS:
                    SyncTask.syncVideos(mContext, mMovieId);
                    break;
                case LOADER_TYPE_REVIEWS:
                    SyncTask.syncReviews(mContext, mMovieId);
                    break;
                case LOADER_TYPE_FAVORITE:
//                    ContentValues values = new ContentValues();
//                    values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
                    Uri favoriteMovieUri = MovieContract.FavoriteEntry.buildFavoriteUriById(mMovieId);
                    if (FAB_STATE_CURRENT.equals(FAB_STATE_ENABLED)) {
                        int rowsDeleted = getContentResolver().delete(
                                favoriteMovieUri,
                                null,
                                null);
                        Log.i(TAG, "rows deleted: " + rowsDeleted);
                        publishProgress(FAB_STATE_DISABLED);
                    } else {
                        ContentValues values = new ContentValues();
                        if (mCursorMovie != null) {
                            DatabaseUtils.cursorRowToContentValues(mCursorMovie, values);
                            Uri inserted = getContentResolver().insert(
                                    favoriteMovieUri,
                                    values);
                            Log.i(TAG, "uri inserted: " + inserted);
                            publishProgress(FAB_STATE_ENABLED);
                        } else {
                            throw new UnsupportedOperationException("Movie data is null");
                        }
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown type task: " + mTypeTask);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            switch (values[0]){
                case FAB_STATE_ENABLED:
                    Toast.makeText(DetailActivity.this, "Movie added to favorite", Toast.LENGTH_SHORT).show();
                    break;
                case FAB_STATE_DISABLED:
                    Toast.makeText(DetailActivity.this, "Movie removed from favorites", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

}


