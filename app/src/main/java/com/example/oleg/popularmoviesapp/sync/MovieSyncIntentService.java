package com.example.oleg.popularmoviesapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class MovieSyncIntentService extends IntentService {

    public MovieSyncIntentService() {
        super("MovieSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SyncTask.syncMovies(this);
    }
}
