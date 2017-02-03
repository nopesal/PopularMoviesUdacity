package com.example.nopesal.projectmoviesudacity.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nico on 26/01/2017.
 */

public class Movie implements Parcelable {
    private String title;
    private String synopsis;
    private String rating;
    private String releaseDate;
    private String posterPath;
    private int vibrantColor;

    public Movie(String title, String synopsis, String rating, String releaseDate, String posterPath, int vibrantColor) {
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.vibrantColor = vibrantColor;
    }

    public int getVibrantColor() {
        return vibrantColor;
    }

    public void setVibrantColor(int vibrantColor) {
        this.vibrantColor = vibrantColor;
    }

    public String getTitle() {
        return title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }


    protected Movie(Parcel in) {
        title = in.readString();
        synopsis = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        vibrantColor = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(synopsis);
        dest.writeString(rating);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeInt(vibrantColor);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}