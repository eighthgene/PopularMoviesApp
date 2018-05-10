package com.example.oleg.popularmoviesapp.activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.utulities.Constants;
import com.example.oleg.popularmoviesapp.utulities.MovieNetworkUtils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "extra_movie";
    private Movie movie;
    private ImageView poster;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        poster = findViewById(R.id.iv_detail_poster);
        mCollapsingToolbarLayout = findViewById(R.id.ctb_movie_detail);

        Intent intent = getIntent();
        if (intent != null) {
            movie = intent.getExtras().getParcelable(EXTRA_MOVIE);
        }

        Glide.with(this)
                .load(MovieNetworkUtils.buildImageUrl(Constants.KEY_IMAGE_SIZE_W500, movie.getBackdropPath()).toString())
                .transition(withCrossFade())
                .into(poster);

        mCollapsingToolbarLayout.setTitle(movie.getTitle());



    }


}
