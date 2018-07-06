package com.example.oleg.popularmoviesapp.Loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import com.example.oleg.popularmoviesapp.json.VideoJsonUtils;
import com.example.oleg.popularmoviesapp.model.Video;
import com.example.oleg.popularmoviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VideoLoader extends AsyncTaskLoader<List<Video>> {
    private int id;
    public VideoLoader(Context context, int id) {
        super(context);
        this.id = id;
    }

    @Override
    public List<Video> loadInBackground() {
        List<Video> videos = new ArrayList<>();

        URL videosRequestUrl = NetworkUtils.buildVideoURL(String.valueOf(id));
        try {
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(videosRequestUrl);
            videos = VideoJsonUtils.parseVideoJson(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videos;
    }
}
