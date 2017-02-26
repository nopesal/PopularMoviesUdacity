package com.example.nopesal.projectmoviesudacity.tasks;

import android.os.AsyncTask;

import com.example.nopesal.projectmoviesudacity.MainActivity;
import com.example.nopesal.projectmoviesudacity.database.TheMovieDatabase;
import com.example.nopesal.projectmoviesudacity.utils.Movie;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nico on 30/01/2017.
 */

public class PopularMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private MainActivity.AsyncTaskCompleteListener<ArrayList<Movie>> listener;

    public PopularMoviesTask(MainActivity.AsyncTaskCompleteListener<ArrayList<Movie>> listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... order) {
        TheMovieDatabase theMovieDatabase = new TheMovieDatabase();
        ArrayList<Movie> movieArrayList = new ArrayList<>();
        try {
            movieArrayList = theMovieDatabase.getMoviesArray(order[0]);
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
