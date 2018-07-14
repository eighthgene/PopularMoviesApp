package com.example.oleg.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private int id;
    private String originalTitle;
    private String title;

    private float popularity;
    private float voteAverage;
    private int voteCount;

    private String posterPath;
    private String backdropPath;

    private String overview;
    private String releaseDate;

    public Movie(int id, String originalTitle, String title, float popularity,
                 float voteAverage, int voteCount, String posterPath, String backdropPath,
                 String overview, String releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.title = title;
        this.popularity = popularity;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }


    public Movie(Parcel in) {
        id = in.readInt();
        originalTitle = in.readString();
        title = in.readString();
        popularity = in.readFloat();
        voteAverage = in.readFloat();
        voteCount = in.readInt();
        posterPath = in.readString();
        backdropPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(originalTitle);
        dest.writeString(title);
        dest.writeFloat(popularity);
        dest.writeFloat(voteAverage);
        dest.writeInt(voteCount);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public float getPopularity() {
        return popularity;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}