package com.example.oleg.popularmoviesapp.Loaders;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.oleg.popularmoviesapp.data.MovieContract;
import com.example.oleg.popularmoviesapp.json.VideoJsonUtils;
import com.example.oleg.popularmoviesapp.model.Video;
import com.example.oleg.popularmoviesapp.utilities.ContentValuesUtils;
import com.example.oleg.popularmoviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VideoLoader extends AsyncTaskLoader<Void> {
    private static final String TAG = VideoLoader.class.getSimpleName();
    private String id;

    public VideoLoader(Context context, String id) {
        super(context);
        this.id = id;
    }


    @Nullable
    @Override
    public Void loadInBackground() {
        Log.i(TAG, "loadInBackground: ");
        List<Video> videos = new ArrayList<>();

        URL videosRequestUrl = NetworkUtils.buildVideoURL(id);
        try {
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(videosRequestUrl);
            videos = VideoJsonUtils.parseVideoJson(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContentValues[] videoValues = ContentValuesUtils.getVideoContentValuesFromJson(videos, id);

        if (videoValues != null && videoValues.length != 0) {
            ContentResolver contentResolver = getContext().getContentResolver();
            int videosInserted = contentResolver.bulkInsert(MovieContract.VideoEntry.CONTENT_URI,
                    videoValues);

            Log.i(TAG, "inserted movies to DB: " + videosInserted);
        }
        return null;
    }


}
