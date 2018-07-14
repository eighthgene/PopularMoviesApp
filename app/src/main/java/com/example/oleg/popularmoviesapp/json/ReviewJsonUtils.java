package com.example.oleg.popularmoviesapp.json;

import android.util.Log;

import com.example.oleg.popularmoviesapp.model.Review;
import com.example.oleg.popularmoviesapp.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReviewJsonUtils {
    private static final String TAG = MovieJsonUtils.class.getSimpleName();

    public static List<Review> parseReviewJson(String json) {
        List<Review> reviewList = new ArrayList<>();
        try {
            //Initialization JSONObject from String -> json
            JSONObject reviewJson = new JSONObject(json);

            JSONArray reviewsArray = reviewJson.optJSONArray(Constants.KEY_RESULTS);
            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject jsonReview = reviewsArray.optJSONObject(i);
                Review review = new Review(
                        jsonReview.optString(Constants.KEY_REVIEW_ID),
                        jsonReview.optString(Constants.KEY_REVIEW_AUTHOR),
                        jsonReview.optString(Constants.KEY_REVIEW_CONTENT),
                        jsonReview.optString(Constants.KEY_REVIEW_URL)
                );
                reviewList.add(review);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
        }
        return reviewList;
    }
}
