package com.example.oleg.popularmoviesapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.activity.MainActivity;
import com.example.oleg.popularmoviesapp.model.Movie;
import com.example.oleg.popularmoviesapp.utulities.Constants;
import com.example.oleg.popularmoviesapp.utulities.MovieNetworkUtils;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    public interface ListMovieClickListener {
        void onClickMovieListener(int clickedMovieIndex);
    }

    private List<Movie> movieList = new ArrayList<>();
    private boolean mIsLoading;
    private LoaderManager mLoaderManager;
    private Context context;
    final private ListMovieClickListener mClickLister;


    public MovieAdapter(LoaderManager loaderManager, ListMovieClickListener movieClickListener) {
        this.mLoaderManager = loaderManager;
        this.mClickLister = movieClickListener;
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        Glide.with(context)
                .load(MovieNetworkUtils.buildImageUrl(Constants.KEY_IMAGE_SIZE_W185, movie.getPosterPath()).toString())
                .transition(withCrossFade())
                .into(holder.mPosterImageView);
        holder.mProgressBar.setVisibility(View.INVISIBLE);

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

    public void clearMovieList() {
        movieList.clear();
        mIsLoading = false;
        notifyDataSetChanged();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final ImageView mPosterImageView;
        private final ProgressBar mProgressBar;

        MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.iv_poster);
            mProgressBar = itemView.findViewById(R.id.pb_movie_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mClickLister.onClickMovieListener(clickedPosition);
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
