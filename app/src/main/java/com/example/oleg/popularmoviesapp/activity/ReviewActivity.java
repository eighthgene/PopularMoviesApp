package com.example.oleg.popularmoviesapp.activity;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.data.MovieContract;
import com.example.oleg.popularmoviesapp.databinding.ActivityReviewBinding;

public class ReviewActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mUri;
    private ActivityReviewBinding binding;

    public static final int ID_REVIEW_LOADER = 100;

    private static final String[] REVIEW_PROJECTION = {
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
    };

    public static final int INDEX_REVIEW_AUTHOR = 0;
    public static final int INDEX_REVIEW_CONTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review);

        mUri = getIntent().getData();

        getSupportLoaderManager().initLoader(ID_REVIEW_LOADER, null, this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                mUri,
                REVIEW_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        populateUI(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        populateUI(null);
    }

    private void populateUI(Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            binding.tvAuthorLabel.setText(R.string.review_author_label);

            binding.tvReviewAuthorName.setText(" ");
            binding.tvReviewComment.setText(" ");

        } else {
            binding.tvAuthorLabel.setText(R.string.review_author_label);

            binding.tvReviewAuthorName.setText(data.getString(INDEX_REVIEW_AUTHOR));
            binding.tvReviewComment.setText(data.getString(INDEX_REVIEW_CONTENT));

        }

    }
}
