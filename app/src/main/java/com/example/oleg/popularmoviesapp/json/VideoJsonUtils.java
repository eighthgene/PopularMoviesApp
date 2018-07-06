package com.example.oleg.popularmoviesapp.json;

import android.util.Log;

import com.example.oleg.popularmoviesapp.model.Video;
import com.example.oleg.popularmoviesapp.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideoJsonUtils {
    private static final String TAG = MovieJsonUtils.class.getSimpleName();

    public static List<Video> parseVideoJson(String json) {
        List<Video> videoList = new ArrayList<>();
        try {
            //Initialization JSONObject from String -> json
            JSONObject videoJson = new JSONObject(json);

            JSONArray videosArray = videoJson.optJSONArray(Constants.KEY_RESULTS);
            for (int i = 0; i < videosArray.length(); i++) {
                JSONObject jsonVideo = videosArray.optJSONObject(i);
                Video video = new Video(
                        jsonVideo.optString(Constants.KEY_VIDEO_ID),
                        jsonVideo.optString(Constants.KEY_VIDEO_KEY),
                        jsonVideo.optString(Constants.KEY_VIDEO_LANGUAGE),
                        jsonVideo.optString(Constants.KEY_VIDEO_NAME),
                        jsonVideo.optString(Constants.KEY_VIDEO_SITE),
                        jsonVideo.optInt(Constants.KEY_VIDEO_SIZE),
                        jsonVideo.optString(Constants.KEY_VIDEO_TYPE)
                );
                videoList.add(video);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
        }
        return videoList;
    }
}
