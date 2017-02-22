package com.example.nopesal.projectmoviesudacity.database;

import android.net.Uri;

import com.example.nopesal.projectmoviesudacity.BuildConfig;

/**
 * Created by nopesal on 19/02/2017.
 */

public class URLGenerator {
    private static String POSTER_BASE_URL = "image.tmdb.org";
    private static String BASE_URL = "api.themoviedb.org";
    private static String API_VERSION = "3";
    private static String TYPE_MOVIE = "movie";

    public static String getSDPosterURL(String posterPath) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(POSTER_BASE_URL)
                .appendPath("t")
                .appendPath("p")
                .appendPath("w342");
        return builder.build().toString() + posterPath;
    }

    public static String getHDPosterURL(String posterPath) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(POSTER_BASE_URL)
                .appendPath("t")
                .appendPath("p")
                .appendPath("w500");
        return builder.build().toString() + posterPath;
    }

    static String generateMoviesURL(String order) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(API_VERSION)
                .appendPath(TYPE_MOVIE)
                .appendPath(order)
                .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_TOKEN);
        return builder.build().toString();
    }

    static String generateCreditsURL(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(API_VERSION)
                .appendPath(TYPE_MOVIE)
                .appendPath(id)
                .appendPath("credits")
                .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_TOKEN);
        return builder.build().toString();
    }

    static String generateTrailerURL(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(API_VERSION)
                .appendPath(TYPE_MOVIE)
                .appendPath(id)
                .appendPath("videos")
                .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_TOKEN);
        return builder.build().toString();
    }

    static String generateYoutubeURL(String youtubeKey) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", youtubeKey);
        return builder.build().toString();
    }

    static String generateReviewsURL(String id){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(API_VERSION)
                .appendPath(TYPE_MOVIE)
                .appendPath(id)
                .appendPath("reviews")
                .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_TOKEN);
        return builder.build().toString();
    }
}
