package com.example.nopesal.projectmoviesudacity.tasks;

import android.os.AsyncTask;

import com.example.nopesal.projectmoviesudacity.MovieDetailsActivity;
import com.example.nopesal.projectmoviesudacity.database.TheMovieDatabase;

import java.io.IOException;

/**
 * Created by nopesal on 04/02/2017.
 */

public class TrailerTask extends AsyncTask<Integer, Void, String> {

    private MovieDetailsActivity.AsyncTaskCompleteListener<String> listener;

    public TrailerTask(MovieDetailsActivity.AsyncTaskCompleteListener<String> listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Integer... id) {
        TheMovieDatabase theMovieDatabase = new TheMovieDatabase();
        String youtubeTrailerURL = null;
        try {
            youtubeTrailerURL = theMovieDatabase.getYoutubeTrailerURL(id[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return youtubeTrailerURL;
    }

    @Override
    protected void onPostExecute(String youtubeTrailerURL) {
        super.onPostExecute(youtubeTrailerURL);
        listener.onTaskCompleted(youtubeTrailerURL);
    }
}
