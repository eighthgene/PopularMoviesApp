package com.example.oleg.popularmoviesapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oleg.popularmoviesapp.R;
import com.example.oleg.popularmoviesapp.activity.DetailActivity;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{
    private static final String TAG = ReviewAdapter.class.getSimpleName();
    private Context mContext;
    private Cursor mCursor;
    private ReviewClickListener mClickReviewListener;

    public interface ReviewClickListener{
            void onclickReviewListener(String idReview);
    }

    public ReviewAdapter(ReviewClickListener mClickReviewListener) {
        this.mClickReviewListener = mClickReviewListener;
    }

    @NonNull
    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutForListItem, parent, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Log.i(TAG, "position: " + position);

        String contentReview = mCursor.getString(DetailActivity.INDEX_REVIEW_CONTENT);
        Log.i(TAG, "contentReview: " + contentReview);
        holder.mContentTextView.setText(contentReview);
        //holder.mReadMoreTextView.setText(R.string.review_read_more_label);
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

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mContentTextView;
        //private final TextView mReadMoreTextView;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mContentTextView = itemView.findViewById(R.id.tv_review_content);
            //mReadMoreTextView = itemView.findViewById(R.id.tv_review_read_more);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mCursor.moveToPosition(clickedPosition);

            String reviewId = mCursor.getString(DetailActivity.INDEX_REVIEW_ID);
            mClickReviewListener.onclickReviewListener(reviewId);
        }
    }
}
