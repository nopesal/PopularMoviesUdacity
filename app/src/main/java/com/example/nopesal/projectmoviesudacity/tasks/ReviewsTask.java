package com.example.nopesal.projectmoviesudacity.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.nopesal.projectmoviesudacity.MovieDetailsActivity;
import com.example.nopesal.projectmoviesudacity.database.MovieDatabase;
import com.example.nopesal.projectmoviesudacity.utils.Review;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nopesal on 19/02/2017.
 */

public class ReviewsTask extends AsyncTask<Integer, Void, ArrayList<Review>> {

    private MovieDetailsActivity.AsyncTaskCompleteListener<ArrayList<Review>> listener;

    public ReviewsTask(MovieDetailsActivity.AsyncTaskCompleteListener<ArrayList<Review>> listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<Review> doInBackground(Integer... id) {
        MovieDatabase movieDatabase = new MovieDatabase();
        ArrayList<Review> reviewArrayList = new ArrayList<>();
        try {
            reviewArrayList = movieDatabase.getMovieReviews(id[0]);
        } catch (IOException e) {
            Log.e("REVIEWS", "doInBackground: Error getting reviews");
            e.printStackTrace();
        }
        return reviewArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Review> reviews) {
        super.onPostExecute(reviews);
        listener.onTaskCompleted(reviews);
    }
}
