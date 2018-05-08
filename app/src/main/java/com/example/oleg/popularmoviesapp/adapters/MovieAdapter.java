package com.example.oleg.popularmoviesapp.adapters;

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.activity.MainActivity;
import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.utulities.Constants;
import com.example.oleg.popularmoviesapp.utulities.MovieNetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private List<Movie> movieList = new ArrayList<>();
    private boolean mIsLoading;
    private LoaderManager mLoaderManager;

    public MovieAdapter(LoaderManager loaderManager) {
        this.mLoaderManager = loaderManager;
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        Picasso.with(holder.mPosterImageView.getContext())
                .load(MovieNetworkUtils.buildImageUrl(Constants.KEY_IMAGE_SIZE_W342, movie.getPosterPath()).toString())
                .into(holder.mPosterImageView);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void setMovieList(ArrayList<Movie> list) {
        movieList.addAll(list);
        mIsLoading = false;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mPosterImageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.iv_poster);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MovieAdapterViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int layoutPosition = holder.getLayoutPosition();
        if (!mIsLoading && (layoutPosition > movieList.size() - 5)) {
            mIsLoading = true;
            MovieNetworkUtils.page++;
            mLoaderManager.getLoader(MainActivity.MOVIE_LOADER_ID).forceLoad();
        }

    }
}
