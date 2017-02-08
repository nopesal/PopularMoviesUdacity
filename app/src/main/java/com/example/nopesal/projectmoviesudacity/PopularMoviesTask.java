package com.example.nopesal.projectmoviesudacity;

import android.content.Context;
import android.os.AsyncTask;

import com.example.nopesal.projectmoviesudacity.database.MovieDatabase;
import com.example.nopesal.projectmoviesudacity.utils.Movie;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nico on 30/01/2017.
 */

public class PopularMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
    private Context mContext;
    private MainActivity.AsyncTaskCompleteListener<ArrayList<Movie>> listener;

    public PopularMoviesTask(Context mContext, MainActivity.AsyncTaskCompleteListener<ArrayList<Movie>> listener) {
        this.mContext = mContext;
        this.listener = listener;
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... order) {
        MovieDatabase movieDatabase = new MovieDatabase();
        ArrayList<Movie> movieArrayList = new ArrayList<>();
        try {
            movieArrayList = movieDatabase.getMoviesArray(order[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        super.onPostExecute(movies);
        listener.onTaskCompleted(movies);
    }
}
