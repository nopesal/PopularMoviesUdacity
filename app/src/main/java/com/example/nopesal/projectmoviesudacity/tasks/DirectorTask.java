package com.example.nopesal.projectmoviesudacity.tasks;

import android.os.AsyncTask;

import com.example.nopesal.projectmoviesudacity.MovieDetailsActivity;
import com.example.nopesal.projectmoviesudacity.database.TheMovieDatabase;

import java.io.IOException;

/**
 * Created by nopesal on 04/02/2017.
 */

public class DirectorTask extends AsyncTask<Integer, Void, String> {

    private MovieDetailsActivity.AsyncTaskCompleteListener<String> listener;

    public DirectorTask(MovieDetailsActivity.AsyncTaskCompleteListener<String> listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Integer... id) {
        TheMovieDatabase theMovieDatabase = new TheMovieDatabase();
        String director = null;
        try {
            director = theMovieDatabase.getDirectorFromMovie(id[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return director;
    }

    @Override
    protected void onPostExecute(String director) {
        super.onPostExecute(director);
        listener.onTaskCompleted(director);
    }
}
