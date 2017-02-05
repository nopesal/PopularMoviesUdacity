package com.example.nopesal.projectmoviesudacity.database;

import android.net.Uri;
import android.util.Log;

import com.example.nopesal.projectmoviesudacity.BuildConfig;
import com.example.nopesal.projectmoviesudacity.utils.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Nico on 27/01/2017.
 */

public class MovieDatabase {
    private static String BASE_URL = "api.themoviedb.org";
    private static String API_VERSION = "3";
    private static String TYPE_MOVIE = "movie";
    private static String POSTER_BASE_URL = "image.tmdb.org";

    public ArrayList<Movie> getMoviesArray(String order) throws IOException {
        String url = generateMoviesURL(order);
        String apiResponseJSON = getJSONFromApi(url);
        JSONArray moviesJSON = null;
        ArrayList<Movie> moviesArray = new ArrayList<>();
        try {
            moviesJSON = new JSONObject(apiResponseJSON).getJSONArray("results");
            for (int i = 0; i < moviesJSON.length(); i++) {
                JSONObject movieJSON = (JSONObject) moviesJSON.get(i);
                Movie movie = new Movie(
                        movieJSON.getInt("id"),
                        movieJSON.getString("title"),
                        movieJSON.getString("overview"),
                        movieJSON.getString("vote_average"),
                        movieJSON.getString("release_date"),
                        movieJSON.getString("poster_path")
                );
                moviesArray.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviesArray;
    }

    public static String getSDPosterURL(String posterPath){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(POSTER_BASE_URL)
                .appendPath("t")
                .appendPath("p")
                .appendPath("w342");
        return builder.build().toString() + posterPath;
    }

    public static String getHDPosterURL(String posterPath){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(POSTER_BASE_URL)
                .appendPath("t")
                .appendPath("p")
                .appendPath("w500");
        return builder.build().toString() + posterPath;
    }

    private String getJSONFromApi(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String generateMoviesURL(String order) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BASE_URL)
                .appendPath(API_VERSION)
                .appendPath(TYPE_MOVIE)
                .appendPath(order)
                .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_TOKEN);
        return builder.build().toString();
    }

    private String generateCreditsURL(String id) {
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

    public String getDirectorFromMovie(int id) throws IOException {
        String url = generateCreditsURL(String.valueOf(id));
        String apiResponseJSON = getJSONFromApi(url);
        Log.i("CREDITS", "getDirectorFromMovie: "+ url);

        try {
            JSONArray crewArray = new JSONObject(apiResponseJSON).getJSONArray("crew");
            for (int i = 0; i < crewArray.length(); i++) {
                JSONObject crew = (JSONObject) crewArray.get(i);
                if (crew.getString("job").equalsIgnoreCase("Director")) {
                    return crew.getString("name");
                }
            }
        } catch (JSONException ignored) {
        }
        return null;
    }
}
