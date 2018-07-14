package com.example.oleg.popularmoviesapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View.OnClickListener;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.activity.DetailActivity;
import com.example.oleg.popularmoviesapp.utilities.NetworkUtils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {


    public interface VideoClickListener{
        void onClickVideoListener(String videoKey);
    }


    private static final String TAG = VideoAdapter.class.getSimpleName();
    private Cursor mCursor;
    private Context mContext;
    private VideoClickListener mClickVideoListener;

    public VideoAdapter(VideoClickListener clickListener) {
        this.mClickVideoListener = clickListener;
    }

    @NonNull
    @Override
    public VideoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutForListItem = R.layout.video_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutForListItem, parent, false);
        return new VideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String thumbnailVideoKey = mCursor.getString(DetailActivity.INDEX_VIDEO_KEY);
        Log.i(TAG, "thumbnailVideoKey: " + thumbnailVideoKey);
        String urlStringVideoThumbnail = NetworkUtils.buildVideoThumbnail(thumbnailVideoKey);
        Log.i(TAG, "onBindViewHolder: " + urlStringVideoThumbnail);

        String videoTitle = mCursor.getString(DetailActivity.INDEX_VIDEO_NAME);

        RequestOptions glideOptions = new RequestOptions()
                .error(R.drawable.ic_error_loading)
                .placeholder(R.drawable.ic_slow_loading)
                .centerCrop()
                .priority(Priority.HIGH);

        Glide.with(mContext)
                .load(urlStringVideoThumbnail)
                .transition(withCrossFade())
                .apply(glideOptions)
                .into(holder.mThumbnailImageView);

        //holder.mThumbnailImageView.setImageResource(R.drawable.ic_error);
        holder.mVideoTitleTextView.setText(videoTitle);
        holder.mYoutubePlayImageView.setImageResource(R.drawable.ic_youtube_play);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != newCursor) {
            mCursor = newCursor;
            //mIsLoading = false;
            notifyDataSetChanged();
        }
    }

    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        private final ImageView mThumbnailImageView;
        private final TextView mVideoTitleTextView;
        private final ImageView mYoutubePlayImageView;

        VideoAdapterViewHolder(View itemView) {
            super(itemView);
            mThumbnailImageView = itemView.findViewById(R.id.iv_video);
            mVideoTitleTextView = itemView.findViewById(R.id.tv_video_title);
            mYoutubePlayImageView = itemView.findViewById(R.id.iv_youtube_play);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mCursor.moveToPosition(clickedPosition);

            String videoKey = mCursor.getString(DetailActivity.INDEX_VIDEO_KEY);
            mClickVideoListener.onClickVideoListener(videoKey);
        }
    }
}
