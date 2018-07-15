package com.example.oleg.popularmoviesapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.activity.DetailActivity;
import com.example.oleg.popularmoviesapp.activity.MainActivity;
import com.example.oleg.popularmoviesapp.data.MovieContract;
import com.example.oleg.popularmoviesapp.sync.SyncTask;
import com.example.oleg.popularmoviesapp.utilities.Constants;
import com.example.oleg.popularmoviesapp.utilities.NetworkUtils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    public interface ListMovieClickListener {
        void onClickMovieListener(int clickedMovieID);
    }

    //private List<Movie> movieList = new ArrayList<>();
    private boolean mIsLoading;
    private Context context;
    final private ListMovieClickListener mClickLister;
    private Cursor mCursor;


    public MovieAdapter(ListMovieClickListener movieClickListener) {
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
        //Movie movie = movieList.get(position);
        mCursor.moveToPosition(position);
        String posterPath = mCursor.getString(MainActivity.INDEX_MOVIE_POSTER_PATH);

        RequestOptions glideOptions = new RequestOptions()
                .error(R.drawable.ic_error_loading)
                .placeholder(R.drawable.ic_slow_loading)
                .priority(Priority.HIGH);

        Glide.with(context)
                .load(NetworkUtils.buildImageUrl(Constants.KEY_IMAGE_SIZE_W185, posterPath).toString())
                .transition(withCrossFade())
                .apply(glideOptions)
                .into(holder.mPosterImageView);

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

//    public void setMovieList(ArrayList<Movie> list) {
//        movieList.addAll(list);
//        mIsLoading = false;
//        notifyDataSetChanged();
//    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != newCursor) {
            mCursor = newCursor;
            mIsLoading = false;
            notifyDataSetChanged();
        }
    }

    public void clearMovieList() {
        mCursor = null;
        mIsLoading = false;
        notifyDataSetChanged();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private final ImageView mPosterImageView;

        MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mPosterImageView = itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mCursor.moveToPosition(clickedPosition);

            int movieID = mCursor.getInt(MainActivity.INDEX_MOVIE_ID);
            if (movieID < 0) return;
            mClickLister.onClickMovieListener(movieID);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull MovieAdapterViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int layoutPosition = holder.getLayoutPosition();

        if (!mIsLoading && (layoutPosition > mCursor.getCount() - 5)) {
            mIsLoading = true;
            SyncTask.startImmediateSyncMovies(context);
            //mLoaderManager.getLoader(MOVIE_LOADER_ID).forceLoad();
            Log.i(TAG, "Start loading new page, curent page is" + NetworkUtils.page);
        }
    }


}
