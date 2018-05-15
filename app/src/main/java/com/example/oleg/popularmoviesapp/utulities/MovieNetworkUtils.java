package com.example.oleg.popularmoviesapp.utulities;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class MovieNetworkUtils {
    private static final String TAG = MovieNetworkUtils.class.getSimpleName();

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String apiKey = ApiKeyUtils.API_KEY;
    public static int page = 1;

    private static final String API_KEY_PARAM = "api_key";
    private static final String LANGUAGE_PARAM = "language";
    private static final String PAGE_PARAM = "page";


    public static URL buildMovieUrl(String sortOrder) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Built movie URL " + url);
        return url;
    }

    public static URL buildImageUrl(String size, String posterPath){
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(size)
                .appendEncodedPath(posterPath)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Built image URL " + url);
        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
