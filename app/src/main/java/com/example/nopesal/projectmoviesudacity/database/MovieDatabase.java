package com.example.nopesal.projectmoviesudacity.database;

import android.net.Uri;
import android.util.Log;

import com.example.nopesal.projectmoviesudacity.BuildConfig;
import com.example.nopesal.projectmoviesudacity.utils.Movie;
import com.example.nopesal.projectmoviesudacity.utils.Review;

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
    public ArrayList<Movie> getMoviesArray(String order) throws IOException {
        String url = URLGenerator.generateMoviesURL(order);
        String apiResponseJSON = getJSONFromApi(url);
        ArrayList<Movie> moviesArray = new ArrayList<>();
        try {
            JSONArray moviesJSON = new JSONObject(apiResponseJSON).getJSONArray("results");
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

    private String getJSONFromApi(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String getDirectorFromMovie(int id) throws IOException {
        String url = URLGenerator.generateCreditsURL(String.valueOf(id));
        String apiResponseJSON = getJSONFromApi(url);
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

    public String getYoutubeTrailerURL(int id) throws IOException {
        String url = URLGenerator.generateTrailerURL(String.valueOf(id));
        String apiResponseJSON = getJSONFromApi(url);
        try {
            JSONArray trailersArray = new JSONObject(apiResponseJSON).getJSONArray("results");
            JSONObject firstTrailer = (JSONObject) trailersArray.get(0);
            return URLGenerator.generateYoutubeURL(firstTrailer.getString("key"));
        } catch (JSONException ignored) {
        }
        return null;
    }

    public ArrayList<Review> getMovieReviews(int id) throws IOException {
        String url = URLGenerator.generateReviewsURL(String.valueOf(id));
        Log.i("REVIEWS", "getMovieReviews: " + url);
        String apiResponseJSON = getJSONFromApi(url);
        ArrayList<Review> reviewsArray = new ArrayList<>();
        try {
            JSONArray reviewsJSON = new JSONObject(apiResponseJSON).getJSONArray("results");
            for (int i = 0; i < reviewsJSON.length(); i++) {
                JSONObject movieJSON = (JSONObject) reviewsJSON.get(i);
                Review review = new Review(
                        movieJSON.getString("content"),
                        movieJSON.getString("author")
                );
                reviewsArray.add(review);
            }
        } catch (JSONException e) {
            Log.e("REVIEWS", "getMovieReviews: Error parsing reviews JSON");
            e.printStackTrace();
        }
        return reviewsArray;
    }
}
